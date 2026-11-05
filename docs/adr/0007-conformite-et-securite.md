# ADR 0007 — Conformité (loi 2024/017) et durcissement (étape 11.2)

Statut : accepté. Ne remplace pas un avis juridique : le registre et les modèles sont des **points de départ** à faire valider.

## Conformité

- **Anonymisation plutôt que suppression** : la fiche (locataire, garant, propriétaire, prestataire) est conservée, car baux, quittances et écritures y renvoient par identifiant, mais nom, contacts, adresse, profession, date de naissance, numéro et copies de pièces d'identité, coordonnées de reversement et compte lié sont effacés. Les écritures financières et documents comptables sont conservés (obligation légale, 10 ans) : la règle « aucune suppression » (CLAUDE.md §6.11) est respectée.
- **Une demande, jamais un effacement automatique** : `conf_demande` (reçue → exécutée ou refusée avec motif). Les **obstacles** sont exposés avant exécution : bail en cours, impayés (la créance justifie la conservation), biens encore rattachés à un propriétaire. Seul un ADMIN exécute ou refuse ; un GESTIONNAIRE peut enregistrer la demande reçue.
- **Effacement lié par événement** (`shared.PersonneAnonymisee`, livré par le registre de publication même après redémarrage) : `securite` (compte, sessions, OTP), `notification` (file de messages, appareils), `audit` (journal). Modules découplés : l'événement vit dans `shared`, ce qui évite le cycle `securite → conformite → tiers → securite`.
- **Le journal d'audit reste en ajout seul**, avec **une** exception contrôlée : le déclencheur de base accepte de modifier les colonnes `avant`/`apres` (et seulement elles) sous l'indicateur `immogest.anonymisation`, posé pour la seule transaction de l'anonymisation, pour y remplacer nom, téléphone, e-mail, adresse par « ANONYME ». Aucune suppression, jamais.
- **Consentements en ajout seul** (`conf_consentement`) : chaque ligne est une décision (accord, refus ou retrait) avec version du modèle, mode de recueil (papier, électronique, oral), référence du document et auteur ; la plus récente fait foi. Finalités soumises à consentement : celles qui ne découlent pas du contrat (messageries, copie de pièce d'identité, communication au propriétaire, compte mobile).
- **Registre des traitements** : ressource versionnée (`conformite/registre-traitements.json`, 9 traitements), affichée dans Paramètres > Conformité.

## Limites connues (à dire au client)

- Les **PDF déjà émis** (bail signé, quittances, reçus, mises en demeure) contiennent le nom de la personne et sont conservés comme documents comptables. Ils ne sont pas réécrits.
- Les **sauvegardes** antérieures conservent la personne jusqu'à leur rotation (30 jours) ; une restauration depuis une sauvegarde antérieure à l'anonymisation la ferait **réapparaître** : à ré-anonymiser après restauration.
- Pas de portail de demande côté personne concernée ni de droit d'accès/de portabilité automatisé : la demande est saisie par un gestionnaire. Les messages d'obstacles et le registre sont en français.

## Durcissement de l'API

- **Limite de débit par adresse IP** sur `POST /api/v1/auth/**` (20/min ; renouvellement 60/min), en plus du verrouillage par compte. En mémoire (une installation = une instance). Derrière Caddy, `server.forward-headers-strategy=native` fait lire l'adresse réelle dans `X-Forwarded-For`.
- **En-têtes** : API (CSP `default-src 'none'`, `frame-ancestors 'none'`, `nosniff`, `X-Frame-Options: DENY`, `Referrer-Policy: no-referrer`, `Permissions-Policy`, HSTS sur HTTPS) ; interfaces web (CSP mêmes-origines, aucun cadre, `nosniff`, `poweredByHeader` retiré).
- **Matrice des droits testée** (`MatriceDroitsTest`) : chaque endpoint doit déclarer `@PreAuthorize` ou figurer dans une liste d'exceptions justifiées ; un nouvel endpoint sans déclaration fait échouer la CI. Par défaut, la configuration HTTP n'exige qu'un jeton valide, c'est-à-dire n'importe quel rôle.
- Taille de page plafonnée à 100 (déjà appliquée par `Pagination`, désormais aussi par Spring Data), jetons falsifiés (`alg: none`, mauvaise signature) refusés, erreurs sans pile ni chemins.
