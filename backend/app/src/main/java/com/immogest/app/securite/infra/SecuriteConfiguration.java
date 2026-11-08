package com.immogest.app.securite.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

/** API stateless protégée par JWT ; erreurs 401/403 en ProblemDetail avec code stable. */
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
@EnableConfigurationProperties(SecuriteProperties.class)
class SecuriteConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    Clock horloge() {
        return Clock.systemUTC();
    }

    @Bean
    JwtDecoder jwtDecoder(JwtService jwtService) {
        return jwtService.decoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper mapper, SecuriteProperties proprietes, Clock horloge) throws Exception {
        AuthenticationEntryPoint nonAuthentifie = (requete, reponse, e) -> {
            boolean expire = e.getMessage() != null && e.getMessage().toLowerCase().contains("expired");
            ecrire(reponse, mapper, HttpStatus.UNAUTHORIZED, expire ? "TOKEN_EXPIRE" : "NON_AUTHENTIFIE",
                    "Authentification requise");
        };
        AccessDeniedHandler refuse = (requete, reponse, e) ->
                ecrire(reponse, mapper, HttpStatus.FORBIDDEN, "ACCES_REFUSE", "Accès refusé");

        http
                .addFilterBefore(new LimiteDebitFilter(proprietes, mapper, horloge), org.springframework.security.web.authentication.www.BasicAuthenticationFilter.class)
                .headers(h -> h
                        // API JSON : aucune ressource à charger, aucun cadre autorisé (réponses jamais interprétées comme des pages)
                        .contentSecurityPolicy(c -> c.policyDirectives("default-src 'none'; frame-ancestors 'none'"))
                        .frameOptions(f -> f.deny())
                        .referrerPolicy(r -> r.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                        .permissionsPolicyHeader(p -> p.policy("camera=(), microphone=(), geolocation=(), payment=()"))
                        .httpStrictTransportSecurity(s -> s.maxAgeInSeconds(31_536_000).includeSubDomains(true)))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/logout",
                                "/api/v1/auth/otp/demander", "/api/v1/auth/otp/verifier",
                                "/api/v1/auth/deux-facteurs/verifier", "/api/v1/auth/deux-facteurs/enrolement/demarrer",
                                "/api/v1/auth/deux-facteurs/enrolement/confirmer").permitAll()
                        .requestMatchers("/api/v1/health", "/actuator/health", "/api/v1/webhooks/**").permitAll()
                        // Assistant de premier lancement (module licence) : public, une seule fois, 409 ensuite
                        .requestMatchers("/api/v1/installation").permitAll()
                        .requestMatchers("/v3/api-docs", "/v3/api-docs.yaml", "/v3/api-docs/**", "/swagger-ui/**",
                                "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/utilisateurs/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint(nonAuthentifie).accessDeniedHandler(refuse))
                .oauth2ResourceServer(o -> o
                        .authenticationEntryPoint(nonAuthentifie)
                        .accessDeniedHandler(refuse)
                        .jwt(j -> j.jwtAuthenticationConverter(SecuriteConfiguration::convertir)));
        return http.build();
    }

    private static AbstractAuthenticationToken convertir(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList(JwtService.CLAIM_ROLES);
        Collection<GrantedAuthority> autorites = (roles == null ? List.<String>of() : roles).stream()
                .<GrantedAuthority>map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
        return new JwtAuthenticationToken(jwt, autorites, jwt.getSubject());
    }

    private static void ecrire(jakarta.servlet.http.HttpServletResponse reponse, ObjectMapper mapper,
            HttpStatus status, String code, String detail) throws IOException {
        reponse.setStatus(status.value());
        reponse.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        mapper.writeValue(reponse.getOutputStream(), Map.<String, Object>of(
                "type", "about:blank", "title", status.getReasonPhrase(), "status", status.value(),
                "detail", detail, "code", code));
    }
}
