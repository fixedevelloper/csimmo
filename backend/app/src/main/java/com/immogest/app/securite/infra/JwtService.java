package com.immogest.app.securite.infra;

import com.immogest.app.securite.Role;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

/** Émission et validation des jetons d'accès (JWT HS256, 15 min par défaut). */
@Component
public class JwtService {

    static final String EMETTEUR = "immogest";
    static final String CLAIM_ROLES = "roles";

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final SecuriteProperties proprietes;

    JwtService(SecuriteProperties proprietes) {
        this.proprietes = proprietes;
        SecretKey cle = cle(proprietes.jwtSecret());
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(cle));
        this.decoder = NimbusJwtDecoder.withSecretKey(cle).macAlgorithm(MacAlgorithm.HS256).build();
    }

    private static SecretKey cle(String base64) {
        if (base64 == null || base64.isBlank()) {
            throw new IllegalStateException("immogest.securite.jwt-secret (IMMOGEST_JWT_SECRET) est obligatoire");
        }
        byte[] octets = Base64.getDecoder().decode(base64.trim());
        if (octets.length < 32) {
            throw new IllegalStateException("immogest.securite.jwt-secret doit contenir au moins 32 octets (base64)");
        }
        return new SecretKeySpec(octets, "HmacSHA256");
    }

    public String creerAcces(UUID utilisateurId, Role role, Instant maintenant) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(EMETTEUR)
                .subject(utilisateurId.toString())
                .issuedAt(maintenant)
                .expiresAt(maintenant.plusSeconds(proprietes.accesDureeMinutes() * 60L))
                .id(UUID.randomUUID().toString())
                .claim(CLAIM_ROLES, List.of(role.name()))
                .build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }

    public long dureeAccesSecondes() {
        return proprietes.accesDureeMinutes() * 60L;
    }

    JwtDecoder decoder() {
        return decoder;
    }
}
