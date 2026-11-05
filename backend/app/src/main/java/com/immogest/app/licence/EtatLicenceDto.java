package com.immogest.app.licence;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * État de la licence pour l'écran Paramètres et le bandeau d'alerte. Les champs du contenu (client, palier, dates…)
 * ne sont renseignés que si la signature de la licence est valide.
 *
 * @param alerteJours              seuil d'alerte atteint (30, 15 ou 7 jours avant l'expiration), sinon {@code null}
 * @param empreinteServeur         empreinte de CE serveur, à communiquer à l'éditeur pour obtenir une licence
 * @param joursDeGraceRestants     jours avant d'exiger une vérification en ligne ({@code null} si elle est désactivée)
 */
public record EtatLicenceDto(
        EtatLicence etat,
        boolean lectureSeule,
        String detail,
        String licenseId,
        String client,
        Integer palierLots,
        LocalDate dateDebut,
        LocalDate dateExpiration,
        Long joursRestants,
        Integer alerteJours,
        List<String> fonctionnalites,
        String empreinteServeur,
        boolean verificationEnLigneActive,
        OffsetDateTime derniereVerificationEnLigne,
        Long joursDeGraceRestants) {
}
