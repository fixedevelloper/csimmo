package com.immogest.app.securite.infra;

import com.immogest.app.securite.domain.Utilisateur;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, UUID> {

    Optional<Utilisateur> findByTelephone(String telephone);

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByTelephone(String telephone);

    boolean existsByEmail(String email);

    /** Comptes d'un des rôles donnés (page Équipe : employés). */
    org.springframework.data.domain.Page<com.immogest.app.securite.domain.Utilisateur> findByRoleIn(
            java.util.Collection<com.immogest.app.securite.Role> roles, org.springframework.data.domain.Pageable pageable);
}
