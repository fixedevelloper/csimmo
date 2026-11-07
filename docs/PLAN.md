# PLAN.md — Plan de réalisation avec Claude Code

Backend **Java 21 / Spring Boot**, web **Next.js**, mobile **Flutter**, licence annuelle installée chez le client.
Le contexte, la stack et les règles métier sont dans `CLAUDE.md` : Claude Code le lit automatiquement à chaque session.

## Comment utiliser ce plan

1. Placer `CLAUDE.md` à la racine du dépôt, ce fichier dans `docs/PLAN.md`, l'analyse exportée dans `docs/ANALYSE.md`.
2. **Une étape = une session Claude Code** (ou un `/clear` entre deux étapes) pour garder un contexte propre.
3. Pour chaque étape : passer en **mode plan** (Shift+Tab deux fois), coller le prompt de l'étape, relire le plan proposé, puis valider.
4. Exiger les tests : chaque prompt se termine par la vérification de la « définition de terminé » de `CLAUDE.md`.
5. Faire un commit (ou une PR) par étape ; cocher la case ici.
6. **Spring Modulith** : chaque étape qui crée un module backend crée aussi son `package-info.java` (`@ApplicationModule` + `allowedDependencies`), ses événements publiés, un `@ApplicationModuleTest`, et met à jour le tableau `CLAUDE.md` §5.1. `ModularityTests` doit rester vert : c'est le garde-fou qui empêche le monolithe de devenir un plat de spaghettis au fil des sessions.
7. Les étapes d'un même lot peuvent être confiées en parallèle à plusieurs sessions **seulement** si elles touchent des projets différents (ex. 7.x mobile pendant 5.x backend), chacune sur sa branche ou son worktree (`claude --worktree`).

Ordre : 0 → 1 → 2 → 3 → 4 → 5 → 6, puis 7 (mobile) et 8 (paiement) en parallèle, puis 9 → 10 → 11.
Durée indicative alignée sur l'analyse : ~23 semaines jusqu'au pilote.

---

## Lot 0 — Fondations (semaines 1-3)

### [x] 0.1 Monorepo et outillage
**Objectif** : squelette des 5 projets qui compilent, CI verte.
**Livrables** : arborescence de `CLAUDE.md` §2, `.gitignore`, `.editorconfig`, `deploy/docker-compose.dev.yml` (PostgreSQL 16 + SeaweedFS en mode S3 + Mailpit), workflow GitHub Actions qui build et teste chaque projet, `CHANGELOG.md`, `docs/adr/0001-stack.md`, `docs/adr/0002-stockage-s3.md`.
**Critères d'acceptation** : `./mvnw verify`, `pnpm build` (web et license-web) et `flutter test` passent en CI.

```text
Lis CLAUDE.md. Crée le squelette du monorepo décrit en §2 : backend Maven multi-module
(parent + licensing-core + app + license-server, Java 21, Spring Boot 3.x, wrapper Maven),
web et license-web en Next.js 15 App Router TypeScript strict avec Tailwind et shadcn/ui (pnpm),
mobile en Flutter (Android, package com.immogest.mobile) avec Riverpod, go_router et flutter_localizations.
Ajoute deploy/docker-compose.dev.yml (postgres:16, chrislusf/seaweedfs avec la commande
"server -s3" exposant le S3 sur le port 8333 et une config d'identité S3 de dev, mailpit),
.editorconfig, .gitignore, CHANGELOG.md, docs/adr/0001-stack.md, docs/adr/0002-stockage-s3.md
(pourquoi S3 + SeaweedFS plutôt que MinIO, voir CLAUDE.md §3 et §5.2), et un workflow
GitHub Actions qui build et teste les 5 projets.
Chaque projet doit avoir un test minimal qui passe. Vérifie toutes les commandes de CLAUDE.md §4.
```

### [x] 0.2 Socle backend `app` et Spring Modulith
**Objectif** : API démarrable avec base, migrations, erreurs, audit, OpenAPI, et frontières de modules vérifiées dès le premier jour.
**Livrables** : interface `ControleAccesFichier` dans `shared`, conservation de l'audit 10 ans (aucune purge), config par profils (`dev`, `test`, `prod`), Flyway `V1__init.sql` (inclut la table `event_publication` de Spring Modulith), `ProblemDetail` global + codes d'erreur, entité de base (id UUID, `created_at`, `updated_at`, `version`), modules `shared` (OPEN) et `audit` avec leur `package-info.java`, `ModularityTests` (vérification + génération de `docs/modules/` en PlantUML/C4), registre d'événements JPA avec republication au redémarrage, `/actuator/modulith` en dev, springdoc avec export vers `api/openapi.yaml`, endpoint `/api/v1/health`.
**Critères** : `ModularityTests` passe et échoue si on ajoute volontairement un accès à un package interne d'un autre module ; un `@ApplicationModuleTest` sur `audit` vérifie avec `Scenario` qu'un événement publié est bien journalisé.

```text
Lis CLAUDE.md §3, §5.1, §5.3 et §6. Dans backend/app, mets en place le socle : profils dev/test/prod,
PostgreSQL + Flyway (V1__init.sql, avec la table event_publication de Spring Modulith),
classe d'entité de base (UUID, created_at, updated_at, version), ProblemDetail global avec un champ
"code" métier. Configure Spring Modulith via le BOM : starter-core, starter-jpa (registre de
publication d'événements, republication au redémarrage), starter-test, actuator (profil dev), docs.
Crée les modules shared (@ApplicationModule type OPEN : Montant, Periode, erreurs, interface
FileStorage) et audit (table audit_log : auteur, action, entite, entite_id, avant JSONB, apres JSONB,
date ; alimenté par un @ApplicationModuleListener générique sur les événements métier).
Ajoute ModularityTests (verify + Documenter vers docs/modules/) et un @ApplicationModuleTest avec
Scenario pour audit. springdoc-openapi avec un profil Maven "openapi" qui écrit api/openapi.yaml.
Test d'intégration Testcontainers sur /api/v1/health.
```

### [x] 0.2b Module `stockage` (S3)
**Objectif** : stockage de fichiers S3 utilisable par tous les modules.
**Livrables** : module `stockage` implémentant `FileStorage` avec AWS SDK v2 (endpoint configurable, path-style, création du bucket au démarrage), entité `Fichier` (id, clé, nom d'origine, type MIME, taille, sha256, propriétaire module/entité), endpoint `GET /api/v1/fichiers/{id}` qui vérifie les droits (via l'interface `ControleAccesFichier` définie dans `shared`, que chaque module propriétaire implémente) puis diffuse le flux, option URL présignée, validation du type par le contenu (Apache Tika), limite de taille, redimensionnement des images, utilitaire de chiffrement AES-GCM pour les fichiers sensibles.
**Critères** : tests Testcontainers sur un conteneur SeaweedFS : envoi, lecture, suppression, refus d'un faux PDF, refus d'accès sans droit, fichier chiffré illisible directement dans le bucket.

```text
Lis CLAUDE.md §3 et §5.2. Crée le module Spring Modulith stockage (dépend uniquement de shared)
qui implémente FileStorage avec AWS SDK for Java v2 (S3Client avec endpointOverride, forcePathStyle,
région us-east-1 par défaut, S3Presigner pour l'option URL présignée), configuré par
immogest.stockage.* et qui crée le bucket au démarrage s'il manque. Ajoute l'entité Fichier
(métadonnées + sha256), l'endpoint GET /api/v1/fichiers/{id} qui délègue la vérification de droits à
une interface ControleAccesFichier (une implémentation par module propriétaire) puis diffuse le flux,
la détection du type par contenu avec Apache Tika (jpeg, png, webp, pdf ; 10 Mo max), le
redimensionnement des images à 1600 px, et un service ChiffrementFichier AES-GCM.
Tests d'intégration avec Testcontainers GenericContainer("chrislusf/seaweedfs") en "server -s3".
Le code ne doit dépendre d'aucune API propre à SeaweedFS : uniquement S3.
```

### [x] 0.3 Sécurité et utilisateurs
**Objectif** : authentification et rôles.
**Livrables** : module `securite` : utilisateurs, rôles (`CLAUDE.md` §6), login par téléphone/e-mail + mot de passe (BCrypt), JWT access (15 min) + refresh (30 jours, rotation, révocation), déconnexion web après 30 min d'inactivité, OTP SMS (interface `OtpSender`, implémentation console en dev), 2FA optionnelle pour ADMIN/GESTIONNAIRE, CRUD utilisateurs (ADMIN), changement de mot de passe, verrouillage après 5 échecs.
**Critères** : tests pour login, refresh, révocation, accès interdit par rôle.

```text
Lis CLAUDE.md. Implémente le module securite dans backend/app : entité Utilisateur (nom, telephone,
email, role, mot de passe BCrypt, actif, tentatives_echouees), Spring Security stateless avec JWT access
15 min + refresh 30 jours stocké en base (rotation et révocation), endpoints /api/v1/auth/login,
/refresh, /logout, /otp/demander, /otp/verifier, CRUD /api/v1/utilisateurs réservé à ADMIN,
verrouillage après 5 échecs. OtpSender est une interface (implémentation console en dev).
Tests d'intégration pour chaque endpoint, y compris des cas 401/403.
```

### [x] 0.4 Socle web Next.js
**Objectif** : coquille de l'app du client connectée à l'API.
**Livrables** : génération du client (`pnpm gen:api`), écran de connexion, gestion des jetons (cookies httpOnly via route handlers Next.js), middleware de protection des routes, layout (menu latéral, en-tête, sélecteur de langue), next-intl fr/en, TanStack Query, composants de base (table paginée, formulaire, toasts, confirmation), page d'erreur qui traduit les `code` métier.
**Critères** : Playwright : connexion → tableau de bord vide → déconnexion.

```text
Lis CLAUDE.md. Dans web/, mets en place : client API typé généré depuis api/openapi.yaml
(openapi-typescript + openapi-fetch, script pnpm gen:api), authentification via route handlers
Next.js qui stockent les jetons en cookies httpOnly et rafraîchissent le jeton d'accès,
middleware qui protège /(app)/*, layout avec menu latéral (Tableau de bord, Patrimoine,
Locataires, Baux, Encaissements, Relances, Propriétaires, Paramètres), next-intl fr/en,
TanStack Query, composants DataTable paginée, Form (react-hook-form + zod), Toast, ConfirmDialog,
et une fonction qui traduit les codes d'erreur ProblemDetail. Test Playwright du parcours de connexion.
```

### [ ] 0.6 Jeu de données de démonstration
**Livrables** : profil `demo` qui charge un jeu cohérent (immeubles, lots, locataires, baux, échéances, paiements) pour développer, présenter et tester de bout en bout.

### [x] 0.5 Socle mobile Flutter
**Objectif** : coquille de l'app mobile.
**Livrables** : écran « configurer le serveur » (saisie d'URL ou scan de QR code), connexion, stockage sécurisé des jetons (`flutter_secure_storage`), intercepteur dio de rafraîchissement, client Dart généré, navigation par rôle (locataire / agent / prestataire), base drift vide, l10n fr/en.
**Critères** : test d'intégration : configuration serveur → connexion → accueil selon le rôle.

```text
Lis CLAUDE.md. Dans mobile/, mets en place : client API Dart généré depuis api/openapi.yaml,
premier lancement avec écran de configuration de l'adresse du serveur du client (saisie ou
scan QR via mobile_scanner), connexion, jetons dans flutter_secure_storage, intercepteur dio
de refresh, go_router avec redirection selon le rôle (LOCATAIRE, GESTIONNAIRE, PRESTATAIRE),
base drift initialisée, l10n fr/en. Ajoute un test d'intégration du parcours.
```

---

## Lot 1 — Licence côté client (semaine 4)

La licence est faite tôt car elle conditionne toutes les écritures.

### [x] 1.1 `licensing-core`
**Livrables** : modèle `Licence` (record), sérialisation JSON canonique, signature et vérification **Ed25519** (JDK natif), calcul d'empreinte serveur (identifiant machine + identifiant d'installation généré au premier démarrage), outil CLI de dev pour générer une paire de clés et signer une licence de test.
**Critères** : tests : licence valide, signature altérée, champ modifié, expirée, mauvaise empreinte.

```text
Lis CLAUDE.md §6 (Licence). Implémente backend/licensing-core : record Licence (licenseId, client,
palierLots, dateDebut, dateExpiration, empreinteServeur, fonctionnalites), sérialisation JSON
canonique (ordre des clés stable), signature et vérification Ed25519 avec l'API JDK,
classe EmpreinteServeur, et une petite CLI (main) pour générer une paire de clés et signer une
licence de test. Aucune dépendance Spring. Tests unitaires de tous les cas d'invalidité.
```

### [x] 1.2 Module `licence` dans `app`
**Livrables** : assistant de premier lancement (création du compte ADMIN + import du fichier de licence), endpoint d'activation et d'état (`/api/v1/licence`), vérification au démarrage + job quotidien, vérification en ligne hebdomadaire (client HTTP vers le serveur de licences, simulé en test), grâce de 30 jours, alertes 30/15/7 jours, filtre **lecture seule**, contrôle du palier de lots, écran web « Licence » (état, date d'expiration, import d'une nouvelle licence) et bandeau d'alerte.
**Critères** : tests d'intégration : écriture refusée avec `LICENCE_EXPIREE`, lecture et export autorisés, création de lot refusée au-delà du palier, grâce hors ligne.

```text
Lis CLAUDE.md §6 (Licence). Implémente le module licence dans backend/app en utilisant
licensing-core : assistant de premier lancement (/api/v1/installation : crée l'ADMIN et importe la
licence ; utilisable une seule fois), /api/v1/licence (état, import), vérification au démarrage et
job quotidien, vérification en ligne hebdomadaire via un client ServeurLicencesClient (interface,
simulée en test), 30 jours de grâce, notifications à J-30/15/7, filtre qui met l'API en lecture
seule si la licence est expirée ou invalide (exceptions : auth, licence, exports, sauvegarde).
Côté web : écran d'installation, écran Paramètres > Licence et bandeau d'alerte global.
Tests d'intégration de chaque règle.
```

---

## Lot 2 — Patrimoine et tiers (semaines 5-6)

### [x] 2.1 Patrimoine (backend + web)
**Livrables** : immeubles (adresse, quartier, ville, GPS, photos) et lots (référence, type, surface, pièces, loyer indicatif, statut LIBRE/OCCUPE/EN_TRAVAUX, équipements, compteurs) ; photos envoyées dans le stockage S3 via `FileStorage` ; listes filtrables, fiches, formulaires web. Le statut du lot change en écoutant les événements `BailActive` / `BailTermine` (pas d'appel direct depuis `bail`).
**Critères** : CRUD testé, palier de licence respecté, photos servies avec contrôle d'accès.

```text
Lis CLAUDE.md. Implémente le module Spring Modulith patrimoine (backend + web) : Immeuble et Lot selon
docs/ANALYSE.md (section Modèle de données), photos stockées via FileStorage (module stockage)
avec une implémentation de ControleAccesFichier, package-info.java avec allowedDependencies
et une @NamedInterface("api") exposant la lecture des lots, endpoints CRUD paginés et filtrables (ville, quartier, statut, type), contrôle du
palier de licence à la création d'un lot. Côté web : liste des immeubles, fiche immeuble avec ses lots,
formulaires de création/modification, galerie photo. Tests backend et un test Playwright.
```

### [x] 2.2 Tiers : propriétaires tiers, mandats, locataires, garants, prestataires
**Livrables** : fiches (notation de solvabilité reportée en V2), pièces d'identité **chiffrées au repos**, mandats (taux d'honoraires en points de base), rattachement immeuble → propriétaire tiers, recherche rapide (nom, téléphone).
**Critères** : un PROPRIETAIRE_TIERS ne voit que ses immeubles (test) ; CNI illisible en base.

```text
Lis CLAUDE.md. Implémente le module tiers (backend + web) : ProprietaireTiers, Mandat (taux en points
de base, dates), Locataire, Garant, Prestataire. Les fichiers de pièces d'identité sont chiffrés
AES-GCM avant stockage. Rattachement Immeuble -> ProprietaireTiers (optionnel). Recherche par nom
ou téléphone. Règles de visibilité par rôle (un propriétaire tiers ne voit que ses biens).
Écrans web correspondants. Tests, dont les tests de visibilité.
```

---

## Lot 3 — Baux (semaines 7-8)

### [x] 3.1 Baux et génération PDF
**Livrables** : création de bail (lot, locataire, garants, dates, loyer, charges, périodicité, jour d'échéance, caution, mois d'avance), contrainte « un bail actif par lot » (service + base), statut du lot mis à jour, renouvellement, résiliation avec préavis, modèle de bail Thymeleaf → PDF, assistant web en étapes.
**Critères** : tests des chevauchements de dates, PDF généré et téléchargeable.

```text
Lis CLAUDE.md §6 (règles 1 et 2). Implémente le module bail (backend + web) : entité Bail et ses
statuts (BROUILLON, ACTIF, RESILIE, TERMINE), contrainte d'un seul bail actif par lot vérifiée en
service et en base (contrainte d'exclusion PostgreSQL sur daterange), mise à jour du statut du lot,
renouvellement et résiliation avec préavis, génération du bail en PDF (Thymeleaf + OpenHTMLtoPDF)
à partir d'un modèle modifiable dans Paramètres. Côté web : assistant de création en 4 étapes
(lot, locataire et garants, conditions financières, récapitulatif + PDF). Tests des chevauchements.
```

---

## Lot 4 — Échéances et encaissements (semaines 9-10)

### [x] 4.1 Moteur d'échéances
**Livrables** : job quotidien idempotent (clé `bail_id + periode`), statuts `A_VENIR/DUE/PARTIELLE/PAYEE/EN_RETARD`, délai de grâce paramétrable, imputation automatique de l'avance.
**Critères** : tests sur périodicités mensuelle/trimestrielle/annuelle, mois courts, relance du job sans doublon.

### [x] 4.2 Paiements manuels, imputation, contre-passation
**Livrables** : saisie d'un paiement (espèces, virement, MoMo/OM manuel avec référence), en-tête `Idempotency-Key` (CLAUDE.md §5.3), reçu SMS automatique au locataire pour tout encaissement espèces, rapprochement de caisse (total espèces saisies par agent et par jour), imputation sur l'échéance la plus ancienne, paiements partiels, contre-passation avec motif (jamais de suppression), journal d'audit, écran web « Encaissements » et fiche « compte locataire » (solde, historique).
**Critères** : tests d'imputation sur plusieurs échéances, contre-passation qui rétablit les statuts.

```text
Lis CLAUDE.md §6 (règles 3 à 6 et 11). Implémente dans le module finance : (1) un job quotidien
idempotent qui génère les échéances de chaque bail actif selon sa périodicité, gère le délai de grâce
et le statut EN_RETARD, et impute l'avance versée ; (2) la saisie des paiements manuels avec
imputation automatique sur l'échéance la plus ancienne (table imputation), les paiements partiels,
et l'annulation par contre-passation avec motif, sans aucune suppression. Montants en long (FCFA).
Côté web : écran Encaissements (saisie rapide, liste filtrable) et compte locataire (solde, historique).
Tests unitaires des calculs et tests d'intégration de bout en bout.
```

---

## Lot 5 — Quittances et notifications (semaine 11)

### [x] 5.1 Quittances et reçus
**Livrables** : génération PDF automatique (événement « échéance payée » → quittance ; paiement partiel → reçu), numérotation continue par année, téléchargement web et mobile.

### [x] 5.2 Module `notification`
**Livrables** : interface `CanalNotification` avec implémentations SMS (fournisseur HTTP configurable), WhatsApp Cloud API, e-mail (SMTP), push (Firebase) ; clés saisies par le client dans Paramètres ; file d'envoi persistante avec réessais ; modèles de messages fr/en ; journal des envois.
**Critères** : en test, tous les canaux sont simulés ; un échec d'envoi est réessayé 3 fois.

```text
Lis CLAUDE.md §6 (règle 7). Implémente (1) la génération automatique des quittances (échéance
entièrement payée) et des reçus (paiement partiel) en PDF avec numérotation continue par année,
déclenchée par événement ; (2) le module notification avec une interface CanalNotification et les
implémentations SMS (fournisseur HTTP configurable), WhatsApp Cloud API, SMTP et Firebase push,
une file d'envoi persistante en base avec 3 réessais, des modèles de messages traduits, et un écran
Paramètres > Notifications pour saisir les identifiants. Les canaux sont simulés en test.
```

---

## Lot 6 — Relances, tableaux de bord, reversements (semaines 12-13)

### [x] 6.1 Relances et impayés
**Livrables** : job de relances (J-5, J+3, J+10, J+30 paramétrables), canal par ordre de préférence (WhatsApp → push → SMS), liste des arriérés, mise en demeure PDF, historique des relances par échéance.

### [x] 6.2 Tableaux de bord
**Livrables** : endpoints agrégés (taux d'occupation, encaissé vs attendu par mois, arriérés par bien et par locataire, baux arrivant à échéance sous 60 jours) ; page d'accueil web avec indicateurs et graphiques.

### [x] 6.3 Reversements et relevés de gérance
**Livrables** : calcul mensuel par propriétaire tiers (encaissé − honoraires − dépenses), relevé PDF, statut du reversement (CALCULE, VALIDE, PAYE), saisie simple des dépenses imputées.

```text
Lis CLAUDE.md §6 (règles 9 et 12). Implémente : (1) le module relance avec un job quotidien
paramétrable (J-5, J+3, J+10, J+30), le choix de canal WhatsApp puis push puis SMS, la liste des
arriérés et la mise en demeure en PDF ; (2) des endpoints de tableau de bord agrégés et la page
d'accueil web avec indicateurs et graphiques ; (3) le calcul mensuel des reversements par
propriétaire tiers avec relevé de gérance PDF et saisie des dépenses imputées. Tests des calculs.
```

---

## Lot 7 — Application mobile (semaines 14-17, en parallèle du lot 8)

### [x] 7.1 Espace locataire
Mes baux, mes échéances et mon solde, téléchargement des quittances, bouton « Payer » (branché au lot 8), notifications push, signalement d'une panne avec photo (prépare le module maintenance V2).

### [x] 7.2 Espace gestionnaire terrain et mode hors ligne
Liste des lots et locataires, saisie d'encaissement espèces **hors ligne** (file drift synchronisée au retour du réseau, identifiant idempotent généré côté mobile), reçu SMS envoyé à la synchronisation.
**Critères** : test d'intégration : saisie hors ligne → reconnexion → paiement créé une seule fois côté serveur.

```text
Lis CLAUDE.md. Dans mobile/, implémente l'espace locataire (baux, échéances, solde, quittances PDF,
push Firebase, signalement de panne avec photo) et l'espace gestionnaire terrain avec saisie
d'encaissement hors ligne : file locale drift, clé d'idempotence générée sur le téléphone et
acceptée par l'API (en-tête Idempotency-Key, déjà fourni par l'étape 4.2), synchronisation automatique
au retour du réseau. Tests unitaires des providers et un test d'intégration du scénario hors ligne.
```

---

## Lot 8 — Paiement Mobile Money (semaines 14-17)

### [x] 8.1 Module `paiementenligne`
**Livrables** : interface `AgregateurPaiement` (initier, vérifier le statut, valider un webhook), première implémentation pour l'agrégateur choisi (CinetPay, Campay ou NotchPay — **à confirmer avant de démarrer**), identifiants marchands du client dans Paramètres, endpoint webhook signé, job de vérification des transactions en attente, création du paiement + imputation **uniquement** après confirmation.
**Critères** : tests avec un faux agrégateur : succès, échec, webhook rejoué (idempotence), webhook à signature invalide.

```text
Lis CLAUDE.md §6 (règle 8). Implémente le module paiementenligne : interface AgregateurPaiement
(initierPaiement, verifierStatut, validerWebhook), implémentation pour <AGREGATEUR CHOISI> d'après
sa documentation officielle, saisie des identifiants marchands du client dans Paramètres,
endpoint POST /api/v1/webhooks/{fournisseur} avec vérification de signature et idempotence,
job qui revérifie les transactions EN_ATTENTE, et création du Paiement + imputation seulement
après confirmation. Branche le bouton Payer de l'app mobile et du portail web locataire.
Tests avec un faux agrégateur, dont rejeu de webhook et signature invalide.
```

---

## Lot 9 — Serveur et portail de licences de l'éditeur (semaines 18-19)

### [x] 9.1 `license-server` (backend)
Clients, licences (émission, renouvellement, transfert vers un nouveau serveur, révocation), signature avec la clé privée (lue depuis un fichier ou une variable protégée, jamais commitée), endpoint de vérification hebdomadaire appelé par les installations, historique des paiements de licence, versions publiées (numéro, notes, URL de téléchargement, somme de contrôle).

### [x] 9.2 `license-web` (portail Next.js)
Connexion éditeur, liste des clients et licences, émission/renouvellement avec téléchargement du fichier `.lic`, alertes de licences expirant sous 30 jours, publication d'une version.

```text
Lis CLAUDE.md. Implémente backend/license-server (Spring Boot, sa propre base PostgreSQL, réutilise
licensing-core) : clients, licences (émission, renouvellement, transfert d'empreinte, révocation),
signature Ed25519 avec une clé privée chargée depuis un chemin configuré (jamais dans le dépôt),
endpoint POST /api/v1/verification appelé par les installations, paiements de licence, versions
publiées. Puis license-web en Next.js : connexion, clients, licences, téléchargement du fichier .lic,
alertes d'expiration, publication de version. Tests, y compris la vérification croisée avec app.
```

---

## Lot 10 — Packaging, installation, mises à jour (semaines 18-20)

### [x] 10.1 Images et installeur
**Livrables** : Dockerfiles multi-étapes (jar obfusqué ProGuard, Next.js `output: "standalone"`), `deploy/docker-compose.yml` de production (postgres, seaweedfs en S3 sur le réseau interne uniquement, app, web, caddy avec HTTPS automatique ou certificat local), option « stockage S3 externe » (AWS, Backblaze B2, MinIO existant du client) qui désactive le conteneur seaweedfs, mode « réseau local » (sans Internet : paiement en ligne et app locataire désactivés) et option tunnel Cloudflare, `install.sh` (vérifie Docker et la configuration minimale, génère les secrets, demande le domaine ou l'IP, démarre, affiche l'URL et le QR code de configuration mobile).

### [x] 10.2 Mises à jour et sauvegardes
**Livrables** : `update.sh` (sauvegarde → télécharge la version → vérifie la somme de contrôle → migre → redémarre ; en cas d'échec, retour arrière = restauration de la sauvegarde d'avant migration + ancienne image, Flyway n'étant pas réversible), sauvegarde séparée de la clé AES-GCM, `backup.sh` + job de sauvegarde quotidienne (pg_dump + copie du bucket S3 avec rclone, rotation 30 jours, copie externe configurable vers un disque ou un autre S3), restauration depuis l'écran Paramètres, outil de diagnostic (versions, espace disque, état licence, dernière sauvegarde).
**Critères** : test CI qui installe la version N-1, met à jour vers N et vérifie les données.

```text
Lis CLAUDE.md. Dans deploy/, crée le packaging de production : Dockerfiles multi-étapes (jar
obfusqué ProGuard pour app, Next.js standalone pour web), docker-compose.yml (postgres, seaweedfs
en "server -s3" non exposé, app, web, caddy) avec une option de stockage S3 externe, install.sh (contrôles préalables, génération des secrets, domaine ou IP, démarrage, URL et
QR code de configuration mobile), update.sh (sauvegarde, téléchargement, somme de contrôle,
migration, redémarrage, retour arrière automatique), backup.sh (pg_dump + rclone du bucket S3) et
sauvegarde quotidienne avec rotation. Ajoute un écran Paramètres > Sauvegardes et un diagnostic. Ajoute un job CI qui installe
la version précédente, met à jour et vérifie l'intégrité des données.
```

---

## Lot 11 — Pilote (semaines 20-23)

### [x] 11.1 Import des données existantes
Import Excel/CSV (immeubles, lots, locataires, baux en cours, soldes d'ouverture) avec modèle téléchargeable, prévisualisation, rapport d'erreurs ligne par ligne.

### [~] 11.2 Durcissement et conformité (partiel : voir CHANGELOG)
Conformité loi camerounaise n°2024/017 : modèles de consentement, registre des traitements, procédure de demande de suppression/anonymisation compatible avec « aucune suppression » (anonymisation des données personnelles, écritures financières conservées).

Traduction anglaise complète, revue de sécurité (OWASP : injections, contrôle d'accès, en-têtes, limites de débit sur l'auth), tests de charge (5 000 lots, 3G simulée), accessibilité de base, guide d'installation et guide utilisateur dans `docs/`.

```text
Lis CLAUDE.md. Implémente le module import (Excel/CSV via Apache POI) : modèles téléchargeables,
prévisualisation, validation ligne par ligne avec rapport d'erreurs, import transactionnel des
immeubles, lots, locataires, baux en cours et soldes d'ouverture. Puis fais une revue de sécurité
de l'API (contrôle d'accès sur chaque endpoint, en-têtes, limitation de débit sur /auth) et
corrige ce que tu trouves, en listant chaque correction dans CHANGELOG.md.
```

---

## Lot 12 — Équipe et périmètres d'accès (voir `docs/adr/0009-equipe-et-perimetres.md`)

Un employé n'accède qu'aux immeubles qui lui sont affectés ; sans affectation, il ne voit rien. Les employés restreints sont de **nouveaux rôles** (refus par défaut) ; `GESTIONNAIRE` reste à accès total.

### [x] 12.1 Socle : affectations et périmètre (phase A, backend)
Migration `sec_affectation` ; rôle `ENCAISSEUR` (double authentification obligatoire, révocation des sessions à la désactivation) ; `shared.PerimetreAcces` et `shared.LocatairesDuPerimetre` ; filtrage de patrimoine, bail, finance (paiements, échéances, caisse, documents), tiers (locataires) ; ouverture des seules routes de l'encaisseur ; audit des affectations ; OpenAPI et clients TS/Dart régénérés. Tests : « interdit hors périmètre » par route, non-régression ADMIN/GESTIONNAIRE, matrice des droits.

### [x] 12.2 Page Équipe (phase A, web) et espace agent (mobile) — fiche employé détaillée (encaissements récents, audit) reportée
Menu Administration > Équipe : liste, création, affectation multiple, fiche employé (encaissements, caisse, audit). Mobile : espace agent pour `ENCAISSEUR`, cache limité au périmètre, vidé quand les affectations changent. e2e : un encaisseur n'encaisse que dans son périmètre.

### [x] 12.3 Gestionnaire de biens (phase B)
Rôle `GESTIONNAIRE_BIENS` : baux, locataires, relances, signalements sur son périmètre ; un locataire est visible avec un bail dans le périmètre ou s'il l'a créé (décidé, V24).

### [ ] 12.4 Comptable (phase C)
Rôle `COMPTABLE` : lecture des paiements, dépenses et reversements du périmètre, validation des reversements.

---

## Après le pilote — V2 (≈ 12 semaines)

Une étape par module, même format : maintenance (tickets, devis, prestataires), états des lieux mobiles avec photos et signature, charges et régularisation, GED, signature électronique, annonces et prospects, messagerie, exports comptables SYSCOHADA.

## Décisions à prendre avant certaines étapes

| Avant l'étape | Décision |
| --- | --- |
| 0.1 | ~~Nom du produit et package~~ : décidé, **ImmoGest** / `com.immogest` |
| 8.1 | ~~Agrégateur Mobile Money retenu~~ : socle livré avec un agrégateur **simulé** ; l'implémentation réelle (CinetPay, Campay ou NotchPay) reste à choisir et à ajouter comme une classe `AgregateurPaiement` |
| 5.2 | Fournisseur SMS retenu |
| 10.1 | Cible d'installation principale : VPS Linux (recommandé) ou PC Windows |
| 10.1 | Serveur S3 livré par défaut : SeaweedFS (proposé) ou RustFS une fois stable ; MinIO seulement s'il est déjà chez le client |
| 1.2 | ~~Comportement à l'expiration~~ : lecture seule (implémenté) |
| 1ère livraison | Générer la paire de clés de l'éditeur, coller la clé publique dans `backend/app/src/main/resources/licence/cle-publique-editeur.txt` ; garder la clé privée hors dépôt |
| 10.1 | `docker-compose.yml` de production : toujours `SPRING_PROFILES_ACTIVE=prod` (le profil par défaut est `dev`, qui installe un ADMIN et une licence de développement) ; passer `IMMOGEST_MACHINE_ID` de l'hôte |
