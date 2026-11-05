# ADR 0006 — Import de données (étape 11.1)

Statut : accepté.

## Décisions

- **Apache POI (`poi-ooxml` 5.4.1)** : seule dépendance ajoutée, nécessaire pour lire et produire des `.xlsx` (le plan la nomme). Les anciens `.xls` sont refusés avec un message clair. Le CSV est lu par un lecteur maison (RFC 4180, séparateur `;` `,` ou tabulation détecté, UTF-8 avec ou sans BOM, repli Windows-1252) : pas de seconde bibliothèque.
- **Nom du module : `importation`** (`import` est un mot réservé de Java ; le tableau de `CLAUDE.md` le mentionnait sous l'ancien nom).
- **Prévisualisation sans écriture, import sans état serveur** : l'import reçoit de nouveau le fichier, vérifie son empreinte SHA-256 (celle de la prévisualisation) et rejoue l'analyse. Rien n'est gardé entre deux appels (pas de fichier sur disque, pas de table de brouillons).
- **Tout ou rien** : une seule transaction, un verrou d'import à la fois. À la première erreur d'écriture, tout est annulé et la ligne en cause est rapportée. La prévisualisation collecte *toutes* les erreurs de format, de liens et de doublons ; seules les règles qui ne se voient qu'à l'écriture (par ex. lot existant déjà loué) apparaissent à l'import.
- **Pas de doublons, jamais** : un immeuble (nom), un lot (immeuble + référence) ou un locataire (téléphone) déjà enregistré est une erreur `DEJA_EXISTANT`. Réimporter le même fichier ne crée rien. Les lots et baux peuvent référencer des immeubles et locataires déjà enregistrés.
- **Services publics des autres modules, jamais leurs tables** : les règles métier (palier de licence, un bail en vigueur par lot, chiffrement de la CNI) s'appliquent comme à la saisie manuelle.
- **Reprise des soldes d'ouverture** (`finance.RepriseService`, colonne `fin_echeance.reprise`) : pour un bail déjà en cours, les échéances dont la date précède la reprise sont créées **déjà réglées**, sauf le solde indiqué, imputé aux échéances les plus récentes. **Aucun paiement ni imputation n'est créé** : ni la caisse, ni les reversements, ni les quittances ne voient l'historique comme un encaissement d'aujourd'hui. Un bail sans ligne « Soldes » est considéré à jour. Un solde supérieur aux loyers dus est refusé. Seuls les baux en cours sont importés.

## Limites connues

- L'activation d'un bail génère son PDF dans le stockage S3 ; si l'import échoue après cela, ces PDF restent orphelins (inoffensifs).
- Pas de crédit d'ouverture (locataire en avance) : à saisir sur le bail après l'import.
- 2 000 lignes par onglet, 10 Mo par fichier. Les messages d'erreur du rapport sont en français (produits par le serveur).
- Test de charge d'un gros import (milliers de baux avec PDF) : non fait.
