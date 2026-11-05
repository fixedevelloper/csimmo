package com.immogest.app.securite.infra;

import com.immogest.app.shared.Acteur;
import com.immogest.app.shared.ActeurCourant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/** Fournit l'acteur de la requête à partir du JWT authentifié. */
@Component
class ActeurCourantSecurite implements ActeurCourant {

    @Override
    public Optional<Acteur> acteur() {
        var authentification = SecurityContextHolder.getContext().getAuthentication();
        if (authentification instanceof JwtAuthenticationToken jwt && jwt.isAuthenticated()) {
            Set<String> roles = jwt.getAuthorities().stream()
                    .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
                    .collect(Collectors.toSet());
            return Optional.of(new Acteur(UUID.fromString(jwt.getName()), roles));
        }
        return Optional.empty();
    }
}
