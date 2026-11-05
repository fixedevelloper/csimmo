package com.immogest.app.tiers.infra;

import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurService;
import com.immogest.app.shared.ErreurMetier;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Vérifie qu'un compte utilisateur peut être rattaché à une fiche (il existe et a le bon rôle). */
@Component
public class LiensUtilisateur {

    private final UtilisateurService utilisateurs;

    LiensUtilisateur(UtilisateurService utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    /** @throws ErreurMetier 404 si le compte n'existe pas, 422 si son rôle n'est pas {@code attendu} */
    public void verifier(UUID utilisateurId, Role attendu) {
        if (utilisateurId == null) {
            return;
        }
        Role role = utilisateurs.obtenir(utilisateurId).role();
        if (role != attendu) {
            throw new ErreurMetier("TIERS_UTILISATEUR_ROLE_INVALIDE",
                    "Ce compte n'a pas le rôle %s".formatted(attendu), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public static ErreurMetier dejaLie() {
        return new ErreurMetier("TIERS_UTILISATEUR_DEJA_LIE", "Ce compte est déjà rattaché à une autre fiche", HttpStatus.CONFLICT);
    }
}
