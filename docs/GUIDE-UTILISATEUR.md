# Guide utilisateur d'ImmoGest

ImmoGest gère la location : immeubles, lots, locataires, baux, loyers, quittances, relances, reversements aux propriétaires.
Monnaie : **FCFA**, sans décimales. Fuseau : Douala. Langues : français et anglais (sélecteur en haut à droite).

## 1. Qui voit quoi

| Rôle | Peut faire |
| --- | --- |
| **Administrateur** | Tout, y compris utilisateurs, licence, sauvegardes, import, conformité (exécuter une anonymisation). |
| **Gestionnaire** | Patrimoine, tiers, baux, encaissements, relances, reversements, tableau de bord. Pas les réglages sensibles. |
| **Propriétaire tiers** | Voit uniquement ses propres biens et ses reversements. |
| **Locataire** (application mobile) | Voit ses baux, échéances, quittances ; paie par Mobile Money ; signale des pannes. |
| **Prestataire** | Accès limité aux interventions qui lui sont confiées. |

## 2. Patrimoine

- **Immeubles** : nom, adresse, ville, propriétaire tiers (facultatif). Photos. « Supprimer » = **archiver** (rien n'est effacé).
- **Lots** : référence unique dans l'immeuble, type (appartement, studio, boutique, bureau, terrain), surface, loyer indicatif, équipements, compteurs. Le statut (libre, occupé, en travaux) suit les baux.
- Le nombre de lots est limité par le **palier de votre licence**.

## 3. Locataires, garants, propriétaires

- Fiche locataire : identité, contacts, profession, pièce d'identité (numéro et copie **chiffrés**), garants.
- Propriétaires tiers : coordonnées de reversement et **mandats** (taux d'honoraires, périodes sans chevauchement).

## 4. Baux

1. Créez un bail en **brouillon** : lot, locataire, dates, loyer et charges **mensuels**, périodicité (mensuelle, trimestrielle, annuelle), jour d'échéance, caution, mois d'avance.
2. **Activez-le** : le lot devient occupé et un exemplaire PDF du bail est figé. Un lot ne peut avoir qu'un seul bail en vigueur.
3. Les **échéances** sont créées automatiquement chaque nuit et à l'activation. L'avance versée à la signature solde les premières.
4. **Résiliation** (avec préavis), **renouvellement** : depuis la fiche du bail.

## 5. Encaisser

- Écran **Encaissements** : choisissez le bail, le montant, le mode (espèces, virement, Mobile Money), la référence (obligatoire hors espèces). Un paiement s'impute sur l'**échéance la plus ancienne d'abord**.
- Échéance entièrement payée → **quittance** PDF numérotée. Paiement partiel → **reçu**.
- **Aucune suppression** : pour corriger, faites une **contre-passation** avec motif (écriture inverse). Tout est tracé.
- Paiement **Mobile Money** par le locataire : le paiement n'est enregistré **qu'après confirmation de l'opérateur**, jamais sur simple déclaration.
- Statuts d'une échéance : à venir → due → partielle → payée ; **en retard** après le délai de grâce (5 jours par défaut).

## 6. Relances

Rappels automatiques à J-5, J+3, J+10, J+30 (réglables), un seul canal par message (WhatsApp, notification, puis SMS). Écran **Relances** : arriérés par locataire, historique, relance manuelle, **mise en demeure** PDF une fois le dernier niveau atteint.

## 7. Propriétaires : dépenses et reversements

- Enregistrez les **dépenses** imputées au propriétaire (annulées avec motif, jamais supprimées).
- Chaque mois : reversement = loyers encaissés − honoraires du mandat − dépenses. États : calculé → **validé** (relevé PDF, figé) → payé.

## 8. Tableau de bord

Taux d'occupation, attendu et encaissé par mois, arriérés par immeuble et par locataire, baux arrivant à échéance sous 60 jours.

## 9. Reprendre vos données existantes

Paramètres > **Import de données** : téléchargez le modèle Excel, remplissez les onglets (Immeubles, Lots, Locataires, Baux, Soldes), puis **Analysez** : toutes les erreurs sont listées ligne par ligne. L'import est **tout ou rien**. Seuls les baux **en cours** sont repris ; l'historique antérieur est considéré payé, sauf le **solde d'ouverture** que vous indiquez (impayés à la date de reprise).

## 10. Licence

Paramètres > **Licence** : date d'expiration, palier, empreinte du serveur, import d'une licence renouvelée. Alertes à 30, 15 et 7 jours. **Expirée = lecture seule** : plus aucune écriture, mais toutes les données restent consultables, exportables et sauvegardables.

## 11. Sauvegardes et diagnostic

Paramètres > **Sauvegardes** : liste, sauvegarde immédiate, restauration (saisie de « RESTAURER » requise), diagnostic et alertes (sauvegarde ancienne, pas de copie externe, disque faible…). Voir le guide d'installation.

## 12. Données personnelles (loi n°2024/017)

- **Consentements** : texte à faire signer ou à lire à la personne (Paramètres > Conformité > Modèles), puis enregistrez sa décision sur sa fiche (papier, électronique ou oral). Un retrait est un nouvel enregistrement : l'historique est conservé.
- **Registre des traitements** : Paramètres > Conformité > Registre (à compléter et faire valider par votre conseil juridique).
- **Demande d'effacement** : enregistrez la demande ; l'écran indique ce qui l'empêche (bail en cours, impayés, biens gérés). Un administrateur l'**exécute** ou la **refuse avec motif**. L'**anonymisation est irréversible** : nom, contacts, adresse, pièces d'identité sont effacés ; les documents comptables (quittances, bail signé) et les écritures sont **conservés 10 ans** (obligation légale), rattachés à une fiche anonymisée. Les sauvegardes antérieures conservent la personne jusqu'à leur rotation (30 jours).

## 13. Bonnes pratiques

- Ne partagez jamais un compte. Activez la double authentification pour les administrateurs.
- Vérifiez chaque semaine l'alerte « copie externe » des sauvegardes.
- Ne contournez pas la contre-passation : c'est elle qui garantit des comptes fiables.
- Déconnexion automatique après 30 minutes d'inactivité.
