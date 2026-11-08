# CLAUDE.md — ImmoGest (gestion immobilière, licence annuelle on-premise)

> Nom de code provisoire : **ImmoGest**. Remplacer partout si un nom commercial est choisi.

## 1. Le produit en bref

Application de gestion locative **installée chez le client** (propriétaire ou agence), **pas un SaaS**.
Le client achète une **licence annuelle**, installe le logiciel sur son serveur/VPS/PC, et reste
propriétaire de ses données. L'éditeur fournit : l'installeur, les mises à jour et un **serveur de licences**.

Marché : Cameroun puis zone CEMAC. Monnaie **XAF (FCFA, sans décimales)**. Langues : **fr** (défaut) puis **en**.
Fuseau horaire par défaut : `Africa/Douala`.

Document de référence fonctionnel : `docs/ANALYSE.md` (exporté de l'analyse). En cas de doute sur une règle métier, il fait foi.

## 2. Structure du monorepo

```
/
├── CLAUDE.md
├── docs/                    # ANALYSE.md, PLAN.md, ADR (docs/adr/NNNN-titre.md)
├── backend/                 # Maven multi-module (Java 21, Spring Boot 3.x)
│   ├── pom.xml              # parent
│   ├── licensing-core/      # lib partagée : modèle de licence, signature/vérification Ed25519
│   ├── app/                 # application installée chez le client (API REST)
│   └── license-server/      # serveur de licences de l'éditeur (API REST)
├── web/                     # Next.js (App Router, TypeScript) — app du client
├── license-web/             # Next.js — portail éditeur (gestion des licences)
├── mobile/                  # Flutter — app locataire / agent / prestataire
├── api/                     # openapi.yaml généré par le backend (source des clients TS et Dart)
└── deploy/                  # docker-compose, Caddyfile, install.sh, update.sh, backup.sh
```

## 3. Stack imposée

| Couche | Choix |
| --- | --- |
| Backend | Java 21, Spring Boot 3.x, Maven, Spring Web, Spring Data JPA (Hibernate), Spring Security (JWT access + refresh), Bean Validation |
| Modularité | **Spring Modulith** (BOM `spring-modulith-bom`) : `spring-modulith-starter-core`, `spring-modulith-starter-jpa` (registre de publication d'événements = outbox), `spring-modulith-starter-test`, `spring-modulith-actuator`, `spring-modulith-docs` — voir §5 |
| Base | PostgreSQL 16, migrations **Flyway** (`backend/app/src/main/resources/db/migration`) |
| Tâches planifiées | `db-scheduler` ou Quartz JDBC (persistant en base — **pas de Redis**, pour simplifier l'installation) |
| PDF | Thymeleaf + OpenHTMLtoPDF (quittances, reçus, baux, relevés de gérance) |
| Fichiers | **Stockage objet compatible S3**, via AWS SDK for Java v2 (`software.amazon.awssdk:s3`, `forcePathStyle(true)`, endpoint configurable). Serveur livré chez le client : **SeaweedFS** (Apache 2.0) en mode `weed server -s3`. Le code ne dépend que de l'API S3 : MinIO, RustFS, Garage, AWS S3 ou Backblaze B2 se branchent par configuration. Voir §5 « Stockage » et `docs/adr/0002-stockage-s3.md` |
| Doc API | springdoc-openapi → `api/openapi.yaml` |
| Tests backend | JUnit 5, AssertJ, Testcontainers (PostgreSQL + conteneur SeaweedFS S3), MockMvc / RestAssured, `@ApplicationModuleTest` + `Scenario` (Spring Modulith) |
| Web | Next.js 16 (App Router), TypeScript strict, Tailwind CSS, shadcn/ui, TanStack Query, react-hook-form + zod, next-intl, client API généré par `openapi-typescript` + `openapi-fetch` |
| Tests web | Vitest + Testing Library, Playwright (parcours clés) |
| Mobile | Flutter 3.x (Android d'abord), Riverpod, go_router, dio, drift (SQLite, hors ligne), client généré via `openapi-generator` (dart-dio), flutter_localizations |
| Tests mobile | flutter_test, integration_test |
| Packaging | Docker Compose (postgres, seaweedfs, app, web, caddy) + scripts `install.sh` / `update.sh` / `backup.sh` ; obfuscation du jar par ProGuard en build release |
| CI | GitHub Actions : build + tests des 5 projets, génération OpenAPI, build des images ; `images.yml` publie et signe les images sur GHCR (voir `docs/CICD.md`) |

Ne pas introduire d'autre framework ou de dépendance lourde sans créer un ADR dans `docs/adr/`.

## 4. Commandes

```bash
# Backend
cd backend && ./mvnw verify                    # build + tests (Testcontainers : Docker requis)
cd backend && ./mvnw -q install -DskipTests -pl licensing-core -am && ./mvnw -pl app spring-boot:run   # API sur http://localhost:8080 (profil dev)
cd backend && ./mvnw -pl app -am -Popenapi verify  # régénère api/openapi.yaml

# Serveur de licences de l'éditeur (étape 9.1) : base PostgreSQL propre, port 8090
cd backend && LICENSE_SERVER_CLE_PRIVEE_CHEMIN=/chemin/hors/depot/licence-private.key LICENSE_SERVER_JWT_SECRET=<32+ car.> \
  LICENSE_SERVER_ADMIN_EMAIL=... LICENSE_SERVER_ADMIN_MOT_DE_PASSE=<12+ car.> ./mvnw -pl license-server spring-boot:run   # aussi LICENSE_SERVER_DB_URL/USER/PASSWORD

# Portail éditeur (étape 9.2) : port 3000 par défaut ; LICENSE_SERVER_URL = serveur de licences (défaut http://localhost:8090)
cd license-web && pnpm install && LICENSE_SERVER_URL=http://localhost:8090 pnpm dev
cd license-web && pnpm lint && pnpm typecheck && pnpm test

# Licences (outil de dev, voir LicenceCli ; la clé privée reste HORS du dépôt)
cd backend && ./mvnw -q -pl licensing-core exec:java -Dexec.args="generer-cles --sortie /chemin/hors/depot"   # aussi : signer | verifier | empreinte

# Web
cd web && pnpm install && pnpm dev             # http://localhost:3000
cd web && pnpm gen:api                         # régénère src/lib/api/schema.d.ts depuis api/openapi.yaml
cd web && pnpm lint && pnpm typecheck && pnpm test
cd web && pnpm test:e2e                        # Playwright : exige le backend (profil dev) sur :8080

# Mobile (espace locataire : lib/features/locataire ; espace agent hors ligne : lib/features/agent — file drift `encaissements_en_attente`, clé d'idempotence UUID générée à la saisie et jamais changée, envoi par `SynchroEncaissements` ; push facultatif : lib/features/push, voir ADR 0004)
cd mobile && flutter pub get && flutter run
cd mobile && dart run build_runner build -d    # drift, riverpod, client API
cd mobile && flutter analyze && flutter test   # inclut test/flow (parcours d'intégration sur la VM, sans appareil)
cd mobile && ./tool/gen-api.sh                 # régénère packages/immogest_api (openapi-generator dart-dio) depuis api/openapi.yaml
cd mobile && flutter test integration_test -d <appareil|émulateur>   # même parcours sur Android (attention : installe l'app sur l'appareil)

# Production (voir deploy/README.md)
cd deploy && ./install.sh && ./backup.sh && ./update.sh && ./restore.sh <id> && ./diagnostic.sh

# Environnement de dev complet
docker compose -f deploy/docker-compose.dev.yml up -d   # postgres + seaweedfs (S3 sur :8333) + mailpit
# Données de démonstration (base VIDE de préférence ; jamais en production) : un compte par rôle, mot de passe Demo1234!, codes SMS dans la console
cd backend && SPRING_PROFILES_ACTIVE=dev,demo ./mvnw -pl app spring-boot:run

# Spring Modulith
cd backend && ./mvnw -pl app -am test -Dtest=ModularityTests -Dsurefire.failIfNoSpecifiedTests=false   # vérifie les frontières + régénère docs/modules/
```

## 5. Architecture backend (module `app`)

### 5.1 Modules Spring Modulith

Classe principale : `com.immogest.app.ImmoGestApplication`. **Chaque sous-package direct de `com.immogest.app` est un module applicatif** :

| Module | Contenu | Dépend de (autorisé) |
| --- | --- | --- |
| `shared` | types communs : `Montant` (long FCFA), `Periode`, erreurs métier, `FileStorage`, `ControleAccesFichier`, `Acteur`, `ActeurCourant` (interfaces) | — (module `@ApplicationModule(type = OPEN)`) |
| `stockage` | implémentation S3 de `FileStorage` | `shared` |
| `audit` | `audit_log`, écoute des événements de tous les modules | `shared` |
| `securite` | utilisateurs, rôles, auth, OTP | `shared` |
| `parametres` | paramètres de l'installation, modèles de documents | `shared` |
| `licence` | installation (assistant de premier lancement), vérification, mode lecture seule, palier ; expose `licence::api` (`ControlePalier`) | `shared`, `securite` (l'assistant crée l'ADMIN) |
| `patrimoine` | immeubles (rattachés à un propriétaire tiers, facultatif), lots, photos ; statut du lot suivant `BailActive`/`BailTermine` (types définis dans `shared`, pour éviter un cycle avec `bail`) ; « supprimer » = archiver ; un PROPRIETAIRE_TIERS lit seulement ses biens (404 sinon) | `shared`, `licence::api`, `stockage`, `tiers::api` |
| `tiers` | propriétaires tiers, mandats (taux en points de base, sans chevauchement), locataires, garants, prestataires ; pièces d'identité et numéros de CNI chiffrés (AES-GCM) ; expose `tiers::api` (`LecteurTiers`) | `shared`, `stockage`, `securite` (rôle du compte rattaché à une fiche) |
| `bail` | baux (BROUILLON/ACTIF/RESILIE/TERMINE), exclusion « un bail en vigueur par lot » en base, PDF de bail (mise en page Thymeleaf fixe, textes modifiables = données échappées, jamais un gabarit) ; expose `bail::api` (`LecteurBaux`) | `shared`, `patrimoine::api`, `tiers::api`, `stockage` |
| `finance` | échéances (clé unique `bail_id + période`, job quotidien 03:00 + à l'activation du bail via `BailActive`), avance imputée, statuts `A_VENIR/DUE/PARTIELLE/PAYEE/EN_RETARD/ANNULEE` ; loyer et charges du bail = **mensuels**, une échéance = loyer×mois de la périodicité (dernière période au prorata de la sortie) ; paiements manuels (`POST /api/v1/paiements`, `Idempotency-Key` ; ESPECES/VIREMENT/MTN_MOMO/ORANGE_MONEY, référence obligatoire hors espèces) imputés oldest-first, surplus = crédit réimputé par le moteur ; **contre-passation** = paiement `CONTRE_PASSATION` + imputations négatives, `fin_paiement`/`fin_imputation` immuables en base (trigger) ; caisse du jour par agent ; compte locataire ; **quittances** (une par échéance soldée, événement `EcheancePayee`) et **reçus** (paiement qui ne solde pas tout ce qu'il touche, `RecuAEmettre`) en PDF Thymeleaf+OpenHTMLtoPDF, numérotation continue par année et par type (`QUI-AAAA-NNNNNN`, `REC-…`) via `fin_compteur_numero` incrémenté dans la transaction du document, annulés (jamais supprimés) à la contre-passation, téléchargement par `/api/v1/fichiers/{id}` ; **dépenses** imputées au propriétaire (`fin_depense`, annulées avec motif jamais supprimées) et **reversements** mensuels (`fin_reversement` : encaissé = imputations du mois à la date d'imputation en `Africa/Douala`, nettes des contre-passations ; loyers/charges au prorata du bail ; honoraires = taux du mandat en vigueur le jour de l'encaissement × part loyers ; net = encaissé − honoraires − dépenses) CALCULE → VALIDE (figé par trigger, relevé PDF `REV-AAAA-NNNNNN`, mois terminé et mandat requis) → PAYE ; job quotidien 6 h qui recalcule le mois écoulé | `shared`, `bail::api`, `tiers::api`, `securite`, `patrimoine::api`, `stockage` |
| `relance` | niveaux paramétrables (défaut -5, 3, 10, 30 ; table `rel_parametres`), job quotidien 8 h idempotent (unique `echeance_id + niveau`), rattrapage sans inonder (seul le niveau le plus haut atteint part, les autres sont `IGNOREE` ; au-delà de 30 jours de dépassement du niveau : plus de relance auto), un seul canal par message (WhatsApp → push → SMS, via `notification` `StrategieCanaux.PREFERENCE`), historique par échéance, relance manuelle, liste des arriérés (recherche par locataire), **mise en demeure** PDF numérotée `MED-AAAA-NNNNNN` (possible une fois le dernier niveau atteint, une par échéance, récapitule tous les arriérés du bail) | `shared`, `finance`, `bail::api`, `tiers::api`, `patrimoine::api`, `notification`, `stockage` |
| `notification` | interface `CanalNotification` (SMS HTTP configurable, WhatsApp Cloud API, SMTP, push FCM ; **simulés** si `immogest.notification.simuler=true`, activé en dev et en test) ; configuration et secrets (chiffrés, jamais renvoyés) saisis dans Paramètres > Notifications ; file `notif_message` persistante (`FOR UPDATE SKIP LOCKED`, 1 essai + 3 réessais 1/5/30 min, dédoublonnage) servant aussi de journal ; modèles fr/en `notification/messages_*.properties` ; réagit à `PaiementEnregistre` (reçu SMS espèces) et `DocumentEmis` de `finance` ; OTP envoyés en direct par SMS (jamais en file) ; appareils push `/api/v1/notifications/appareils`. Voir `docs/adr/0003-notifications.md` | `shared`, `finance`, `tiers::api`, `stockage`, `securite` |
| `paiementenligne` | agrégateurs Mobile Money, webhooks ; appelle `finance::api` pour enregistrer un paiement confirmé (`finance` ne dépend **jamais** de `paiementenligne`) | `shared`, `finance::api` |
| `tableaudebord` | agrégats en lecture seule, sans table propre : occupation (lots non archivés, taux = occupés / (libres + occupés)), attendu vs encaissé **par mois d'échéance**, arriérés par immeuble et par locataire, baux ACTIF/RESILIE arrivant à échéance sous 60 j ; taux en points de base ; `/api/v1/tableau-de-bord/*` réservé ADMIN/GESTIONNAIRE | `shared`, `patrimoine::api`, `finance`, `bail::api`, `tiers::api` |
| `maintenance` | **signalements de pannes** par les locataires (`POST /api/v1/signalements` en multipart : description et bail dans le corps, jamais dans l'URL ; photo facultative via `StockageService`), suivi par les gestionnaires (NOUVEAU → EN_COURS → RESOLU/REJETE, terminaux définitifs) ; devis, prestataires et imputation : V2 | `shared`, `bail::api`, `tiers::api`, `patrimoine::api`, `stockage` |
| `paiementenligne` | **Mobile Money par agrégateur** : interface `AgregateurPaiement` (initier, vérifier le statut, valider un webhook) ; seul `AgregateurSimule` (`immogest.paiementenligne.simuler=true`, dev/test) est implémenté, un agrégateur réel s'ajoute en une classe. Règle n°8 : transaction `EN_ATTENTE` créée à l'initiation, **paiement créé et imputé uniquement à la confirmation** (webhook signé `POST /api/v1/webhooks/{fournisseur}` ou job de vérification toutes les 2 min), via `finance` avec la clé `enligne-<id>` ; confirmation idempotente (verrou par transaction, webhooks dédoublonnés `ol_webhook_recu`), montant différent → `ANOMALIE` sans paiement, confirmation tardive d'une transaction `EXPIREE` quand même enregistrée ; `Idempotency-Key` obligatoire à l'initiation ; identifiants marchands chiffrés dans Paramètres > Paiement en ligne ; le job n'écrit rien si la licence n'autorise pas les écritures (`ControlePalier.ecrituresAutorisees`) | `shared`, `finance`, `bail::api`, `tiers::api`, `licence::api`, `stockage` |
| `sauvegarde` | écran Paramètres > Sauvegardes (ADMIN) : lit les manifestes écrits par le conteneur `sauvegarde` (`immogest.sauvegarde.repertoire`, lecture seule), dépose des demandes (`sauvegarde.demande`, `restauration-<id>.demande`) dans `immogest.sauvegarde.demandes`, expose l'état (`etat.json`) et le diagnostic ; restauration limitée à la même version, confirmée par `RESTAURER` ; redémarrage de l'API sur `redemarrer-app` (prod). L'application ne touche jamais elle-même la base ni les fichiers à restaurer. Voir `deploy/README.md` | `shared`, `licence` |
| `importation` | **import Excel (.xlsx, Apache POI) / CSV** (le package ne s'appelle pas `import`, mot réservé Java) : `GET /api/v1/import/modele` (classeur ou CSV d'un type), `POST /previsualisation` (aucune écriture, toutes les erreurs ligne par ligne, empreinte SHA-256), `POST /api/v1/import` (même fichier + empreinte ; **tout ou rien** en une transaction, 422 + rapport sinon) ; immeubles, lots, locataires, baux **en cours**, soldes d'ouverture ; jamais de doublon (`DEJA_EXISTANT`) ; ADMIN seul ; journal `imp_journal` en écriture seule. **Reprise** : `finance.RepriseService` crée les échéances d'avant la mise en service déjà réglées sauf le solde (aucun paiement créé), colonne `fin_echeance.reprise`. Voir `docs/adr/0006-import-donnees.md` | `shared`, `patrimoine`, `tiers`, `bail`, `finance`, `licence::api` |
| `conformite` | **loi n°2024/017** : registre des traitements (`conformite/registre-traitements.json`), modèles de consentement fr/en, consentements **en ajout seul** (`conf_consentement`), demandes d'anonymisation (`conf_demande` : reçue → exécutée ou refusée avec motif ; obstacles : bail en cours, impayés, biens rattachés ; exécution ADMIN seul). L'anonymisation (`tiers.AnonymisationService`) efface les données personnelles et garde écritures et documents comptables ; l'événement `shared.PersonneAnonymisee` fait effacer comptes (`securite`), messages (`notification`) et données personnelles du journal (`audit`, seule modification de `audit_log` permise, sous l'indicateur `immogest.anonymisation`). Voir `docs/adr/0007-conformite-et-securite.md` | `shared`, `tiers`, `bail::api`, `finance`, `patrimoine` |
| `document` | (lot ultérieur) | à déclarer à sa création |

Organisation interne d'un module (exemple `bail`) :

```
com.immogest.app.bail
├── package-info.java          # @ApplicationModule(displayName = "Baux", allowedDependencies = {"shared", "patrimoine :: api", "tiers :: api"})
├── BailService.java           # API publique du module (package racine = exposé)
├── BailDto.java, ...          # DTO publics (records)
├── BailCree.java              # événements publiés (records, au passé : BailCree, BailResilie)
├── api/                       # @NamedInterface("api") si d'autres modules en ont besoin
├── web/                       # controllers REST (internes au module)
├── domain/                    # entités JPA, règles (internes)
└── infra/                     # repositories, adaptateurs (internes)
```

Règles Modulith (vérifiées par `ModularityTests`, la CI échoue sinon) :
- Seuls le **package racine** du module et ses `@NamedInterface` sont visibles des autres modules. `domain/`, `infra/`, `web/` sont internes.
- **Aucune dépendance cyclique** entre modules ; `allowedDependencies` déclaré dans chaque `package-info.java`.
- Communication **inter-modules par événements** de préférence : publication via `ApplicationEventPublisher` dans la transaction, écoute avec `@ApplicationModuleListener` (asynchrone, après commit, nouvelle transaction). Le registre JPA des publications garantit la livraison (outbox) ; `spring.modulith.events.republish-outstanding-events-on-restart=true`.
  Exemples : `finance` publie `EcheancePayee` → `finance` (quittance) et `notification` réagissent ; `bail` publie `BailActive` → `patrimoine` passe le lot en OCCUPE ; `licence` publie `LicenceExpiree`.
- Appel synchrone direct seulement pour une **lecture** nécessaire dans la même transaction (ex. `finance` lit les conditions d'un bail via `bail::api`).
- Une entité JPA ne référence **jamais** l'entité d'un autre module : seulement son identifiant (`UUID lotId`).
- Tables préfixées par module si ambiguïté (`fin_paiement`, `fin_imputation`) ; une migration Flyway ne modifie que les tables de son module.
- Tests : `ModularityTests` (`ApplicationModules.of(ImmoGestApplication.class).verify()` + `new Documenter(modules).writeDocumentation()` → `docs/modules/`), et un `@ApplicationModuleTest` par module avec `Scenario` pour tester les événements publiés/consommés.
- Actuator : endpoint `/actuator/modulith` activé (profil `dev` uniquement).

### 5.2 Stockage de fichiers (S3)

- Interface `FileStorage` dans `shared` : `put(cle, flux, typeMime, taille)`, `get(cle)`, `delete(cle)`, `urlTemporaire(cle, duree)`. Implémentation `S3FileStorage` dans `stockage`.
- Configuration (`immogest.stockage.*`) : `endpoint`, `region` (défaut `us-east-1`), `bucket` (défaut `immogest`), `access-key`, `secret-key`, `path-style=true`. Bucket créé au démarrage s'il n'existe pas.
- Clés d'objets : `{module}/{entite}/{id}/{uuid}.{ext}` (ex. `bail/baux/<bailId>/<uuid>.pdf`). Jamais le nom de fichier fourni par l'utilisateur dans la clé ; il est gardé en base.
- Le **contrôle d'accès est fait par l'API** : le navigateur et le mobile ne reçoivent jamais d'identifiants S3. Téléchargement via `GET /api/v1/fichiers/{id}` qui vérifie les droits puis **diffuse le flux** (défaut, fonctionne même si le stockage n'est pas exposé) ou redirige vers une URL présignée de 5 min si `immogest.stockage.url-presignee=true`.
- Pièces d'identité et documents sensibles : **chiffrés côté application (AES-GCM)** avant envoi au stockage.
- Les autres modules utilisent `StockageService` (package racine de `stockage`) : `enregistrer(module, entite, entiteId, nom, flux, sensible)` et `supprimer(id)` ; ils déclarent `stockage` dans leurs `allowedDependencies` et fournissent un bean `ControleAccesFichier` (sans lui, l'accès est refusé). `securite` fournira `ActeurCourant` (sans lui, `GET /fichiers/{id}` répond 401).
- Limites : 10 Mo par fichier, types autorisés (jpeg, png, webp, pdf) vérifiés sur le contenu, pas seulement l'extension. Photos redimensionnées (max 1600 px) avant stockage.
- Le serveur S3 n'est **pas exposé sur Internet** : seul le conteneur `app` y accède sur le réseau Docker interne.

### 5.3 Règles générales

- Les controllers n'exposent **jamais** les entités JPA : DTO (records Java) + mappers (MapStruct).
- Erreurs : `ProblemDetail` (RFC 7807) avec un `code` métier stable (ex. `BAIL_LOT_DEJA_OCCUPE`) traduit côté front.
- Idempotence : tout endpoint qui crée un paiement accepte l'en-tête `Idempotency-Key` (clé unique en base, rejeu = même réponse). Prévu dès le lot 4, utilisé par le mobile hors ligne (lot 7) et les webhooks.
- Pagination : `page`, `size`, `sort` ; réponses `{ content, page, size, totalElements }`.
- Toutes les routes sous `/api/v1`. Webhooks sous `/api/v1/webhooks/{fournisseur}` (non authentifiés JWT, signature vérifiée).

### 5.4 Serveur de licences (`backend/license-server`)

Application Spring Boot séparée (JdbcTemplate, Flyway `db/migration`, tables `ls_*`), qui dépend de `licensing-core` et jamais de `app`. Clé privée Ed25519 lue depuis `LICENSE_SERVER_CLE_PRIVEE_CHEMIN` (démarrage refusé sans elle ; jamais journalisée ni renvoyée). Connexion éditeur : `POST /api/v1/auth/login` → JWT HS256 8 h (`LICENSE_SERVER_JWT_SECRET`), verrouillage 15 min après 5 échecs ; compte initial créé depuis `LICENSE_SERVER_ADMIN_*` si la table est vide. Une licence = un `licenseId` stable ; chaque fichier signé est une ligne **immuable** de `ls_emission` (trigger) : EMISSION, RENOUVELLEMENT (expiration strictement plus tardive), TRANSFERT (nouvelle empreinte, `license-server.transferts-max`=3). Révocation avec motif (définitive). `POST /api/v1/verification` (public, contrat §6 ter) : `INCONNUE` si licence inconnue ou identifiant mal formé ; `REVOQUEE` si révoquée **ou si l'empreinte ne correspond pas** (copie ou ancien serveur après transfert) ; sinon `VALIDE`, avec `licenceRenouvelee` (le dernier `.lic`) une seule fois après un renouvellement. Paiements de licence (`ls_paiement`, écriture seule) et versions publiées (`ls_version`, sha256 + URL https) ; `GET /api/v1/versions/derniere` est public. Fichier `.lic` : `GET /api/v1/licences/{id}/fichier`.

### 5.5 Portail éditeur (`license-web`)

Next.js sans dépendance supplémentaire (pas de next-intl : outil interne, **français seulement**, textes dans `src/lib/messages.ts`). Pas de client OpenAPI : le serveur de licences n'en publie pas, les types sont écrits à la main dans `src/lib/licences.ts` (à garder alignés avec `Modeles.java`). Le jeton JWT de l'éditeur vit dans un cookie httpOnly/SameSite=Strict `ls_jeton` posé par `/api/auth/login` ; le navigateur n'appelle que `/api/proxy/api/v1/{clients,licences,versions}/**` (liste blanche ; la vérification des installations et la connexion n'y passent pas ; origine contrôlée sur les écritures). Le fichier `.lic` est téléchargé à l'émission, au renouvellement, au transfert, et à tout moment depuis la fiche licence.

## 6. Règles métier non négociables

Argent et dates :
- Montants en **`long` (FCFA entiers)**, jamais `double`. Colonnes `BIGINT`. Pourcentages en points de base (`int`, 1000 = 10 %).
- Dates métier en `LocalDate`, horodatages en `OffsetDateTime` UTC en base, affichés en `Africa/Douala`.

Cycle locatif :
1. Un lot a **au plus un bail actif** à une date donnée (contrainte vérifiée en service **et** en base, ex. contrainte d'exclusion sur la période).
2. Bail : loyer, charges, périodicité (MENSUELLE, TRIMESTRIELLE, ANNUELLE), jour d'échéance, caution, nombre de mois d'avance.
3. Les échéances sont générées par un job quotidien idempotent (clé unique `bail_id + periode`).
4. L'avance versée à la signature solde automatiquement les premières échéances.
5. Statuts d'échéance : `A_VENIR → DUE → PARTIELLE → PAYEE`, et `EN_RETARD` après le délai de grâce (paramétrable, défaut 5 jours).
6. Un paiement est imputé **sur l'échéance la plus ancienne d'abord** (table `imputation`).
7. **Quittance** uniquement pour une échéance entièrement payée ; paiement partiel → **reçu**.
8. Paiement Mobile Money validé **uniquement** après confirmation de l'opérateur (webhook signé ou vérification de statut), jamais sur déclaration.
9. Honoraires = pourcentage du mandat × loyers encaissés. Reversement = encaissé − honoraires − dépenses imputées.
10. Caution restituée = caution − retenues justifiées − arriérés.
11. **Aucune suppression** de paiement, imputation, quittance ou reversement : annulation par **contre-passation** (écriture inverse avec motif). Tout est tracé dans `audit_log` (auteur, action, avant/après, date), conservé **10 ans** (jamais purgé automatiquement).
12. Relances : rappel J-5, puis J+3, J+10, J+30 (paramétrables) ; mise en demeure après la dernière.

Licence :
1. Fichier de licence JSON signé **Ed25519** (clé privée chez l'éditeur, clé publique embarquée dans `app`). Contenu : `licenseId`, `client`, `palierLots`, `dateDebut`, `dateExpiration`, `empreinteServeur`, `fonctionnalites`.
2. Vérification de signature **hors ligne** au démarrage et chaque jour ; vérification en ligne au plus 1 fois/semaine ; **30 jours de grâce** sans réseau.
3. Alertes à 30, 15 et 7 jours de l'expiration.
4. Licence expirée ou invalide → **mode lecture seule** : un filtre bloque toutes les requêtes d'écriture (sauf auth, activation de licence, export, sauvegarde) avec le code `LICENCE_EXPIREE`. Aucune donnée supprimée.
5. Nombre de lots > palier → création de lot refusée (`LICENCE_PALIER_ATTEINT`).
6. Ne jamais journaliser la clé de licence complète ni de secrets.
6bis. États (`EtatLicence`) : `AUCUNE`, `VALIDE`, `EXPIREE`, `INVALIDE`, `REVOQUEE`, `VERIFICATION_REQUISE` ; seul `VALIDE` autorise les écritures. Le filtre répond 403 `LICENCE_EXPIREE` (avec la propriété `etat`) à toute écriture hors exceptions : `/api/v1/auth`, `/api/v1/licence`, `/api/v1/installation`, `/api/v1/exports`, `/api/v1/sauvegardes` (les futurs endpoints d'export et de sauvegarde doivent vivre sous ces préfixes). Les webhooks sont bloqués : un encaissement ne s'enregistre pas sous une licence expirée.
6ter. Un module qui crée des lots appelle `ControlePalier.verifierCreationLot(nombreActuel)` (409 `LICENCE_PALIER_ATTEINT`). Vérification en ligne : `POST {IMMOGEST_LICENCE_SERVEUR_URL}/api/v1/verification` avec `{licenseId, empreinteServeur, versionApplication}` → `{statut: VALIDE|REVOQUEE|INCONNUE, licenceRenouvelee?}` (contrat à implémenter par `license-server`, étape 9.1). Une tentative réussie par semaine, nouvel essai le lendemain après un échec ; la grâce de 30 jours court depuis la dernière confirmation `VALIDE` ; sans URL configurée, ni vérification en ligne ni grâce. Importer deux fois le même fichier ne rallonge pas la grâce ; une licence qui expire avant la courante est refusée (`LICENCE_PLUS_ANCIENNE`). Événements : `LicenceImportee`, `LicenceBientotExpiree` (une fois par seuil 30/15/7), `LicenceExpiree`, `LicenceRevoquee`, `InstallationTerminee`.
6quater. Configuration : `IMMOGEST_LICENCE_CLE_PUBLIQUE` (sinon `src/main/resources/licence/cle-publique-editeur.txt`, **à remplir avant la première livraison** ; sans clé, toute licence est refusée), `IMMOGEST_LICENCE_SERVEUR_URL`, `IMMOGEST_MACHINE_ID`. Profil dev : `immogest.dev.installation-auto=true` crée l'ADMIN de dev et une licence de dev signée par une clé de développement (jamais celle de l'éditeur) ; les tests d'intégration du module licence le désactivent pour tester l'assistant. L'état est mis en cache jusqu'à la fin de la journée ; ne jamais le mettre en cache pendant une transaction (le cache est invalidé à la fin de toute transaction qui importe une licence).
7. Format du fichier `.lic` : `{"format":1,"licence":{…},"signature":"<base64url>"}`. Les octets signés sont le **JSON canonique** (clés triées, sans espaces, UTF-8, texte en NFC) de `{"format":1,"licence":{…}}` reconstruit depuis les champs lus. **Ne jamais modifier `CanonicalJson` ni les champs signés** sans nouveau numéro de `format` : toutes les licences émises deviendraient invalides (le test `CanonicalJsonTest` fige le format). `VerificateurLicence.verifier(contenu, clePublique, empreinteLocale, aujourdhui)` contrôle dans l'ordre format → signature → empreinte → dates ; le contenu n'est renvoyé que si la signature est valide.
8. Empreinte serveur = SHA-256 de (identifiant machine, identifiant d'installation généré au premier démarrage et conservé en base). L'identifiant machine vient de `IMMOGEST_MACHINE_ID` (obligatoire dans Docker : `install.sh` y place l'identifiant de l'hôte), sinon `/etc/machine-id`, sinon le registre Windows.

Sécurité transversale (étape 11.2) : tout endpoint déclare ses droits par `@PreAuthorize` (sinon figurer, justifié, dans `MatriceDroitsTest`) ; limite de débit par IP sur `/api/v1/auth/**` (`LimiteDebitFilter`, réglable par `immogest.securite.limite-auth-par-minute`) ; pages plafonnées à 100.

Sécurité (module `securite`) : JWT HS256 signé avec `IMMOGEST_JWT_SECRET`, refresh opaque stocké haché (rotation, réutilisation = révocation de toutes les sessions), verrouillage 15 min après 5 échecs, OTP SMS via l'interface `OtpSender`, 2FA optionnelle pour ADMIN/GESTIONNAIRE. ADMIN et GESTIONNAIRE ne peuvent jamais se connecter par OTP seul. Routes publiques : `/api/v1/auth/{login,refresh,logout,otp/*}`, `/api/v1/health`, `/api/v1/webhooks/**` ; tout le reste exige un JWT ; `/api/v1/utilisateurs/**` est réservé à ADMIN. Un module qui limite l'accès par rôle utilise `@PreAuthorize` (renvoie 403 `ACCES_REFUSE`) et lit l'utilisateur via `ActeurCourant`.

Sessions : access JWT 15 min, refresh 30 jours ; le web déconnecte après **30 min d'inactivité** (le refresh n'est plus renouvelé au-delà). Notation de solvabilité des locataires : V2.

Mises à jour : Flyway n'est pas réversible ; le retour arrière de `update.sh` = restauration de la sauvegarde faite juste avant la migration + ancienne image.

Rôles : `ADMIN`, `GESTIONNAIRE` (accès total), `PROPRIETAIRE_TIERS`, `LOCATAIRE`, `PRESTATAIRE`, `ENCAISSEUR`, `GESTIONNAIRE_BIENS`, `COMPTABLE`. Un locataire ne voit que ses baux, échéances et documents ; un propriétaire tiers que ses biens. **`ENCAISSEUR`**, **`GESTIONNAIRE_BIENS`** et **`COMPTABLE`** (employés restreints, `docs/adr/0009-equipe-et-perimetres.md`) : refus par défaut, il ne voit et n'encaisse que dans les immeubles qui lui sont affectés (`sec_affectation`, `shared.PerimetreAcces` ; sans affectation, rien ; hors périmètre : 404), double authentification obligatoire. Un module qui ouvre une route à un rôle restreint filtre par périmètre et ajoute un test « interdit hors périmètre ».

## 7. Conventions de code

- Langue des identifiants : **français pour le domaine** (`Bail`, `Echeance`, `Quittance`, `Lot`, tables `bail`, `echeance`) car le vocabulaire métier est francophone ; **anglais pour la technique** (`Controller`, `Repository`, `Service`). Pas d'accents dans les identifiants.
- Mobile hors ligne : tout encaissement passe par la file locale (un seul chemin, en ligne ou non) ; une saisie déjà présentée au serveur ne se supprime jamais (elle a pu y être créée) ; la file n'est envoyée que sous le compte qui l'a saisie ; la copie locale des locataires est vidée à la déconnexion.
- API : toute méthode d'un contrôleur appelée par le mobile porte un `@Operation(operationId = "…")` explicite (sinon la numérotation générée — `lister9`, `creer1`… — glisse dès qu'on ajoute un contrôleur et casse le client Dart) ; deux records de contrôleurs ne portent jamais le même nom (collision de schémas OpenAPI).
- Textes affichés : jamais en dur ; fichiers de traduction (`messages_fr.properties`, `web/messages/fr.json`, `mobile/lib/l10n/app_fr.arb`).
- Java : records pour DTO, `final` par défaut, pas de Lombok sur les entités sauf `@Getter`, injection par constructeur.
- TypeScript : `strict`, pas de `any`, composants serveur par défaut, `"use client"` seulement si nécessaire.
- Flutter : Riverpod (`@riverpod` codegen), écrans dans `lib/features/<feature>/`, pas de logique métier dans les widgets.
- Commits : Conventional Commits (`feat(bail): …`, `fix(finance): …`).

## 8. Définition de « terminé » pour chaque tâche

- [ ] Code + migrations Flyway + tests (unitaires pour les règles, intégration Testcontainers pour les endpoints).
- [ ] `./mvnw verify`, `pnpm lint typecheck test`, `flutter analyze test` passent pour les projets touchés.
- [ ] `api/openapi.yaml` régénéré et clients TS/Dart régénérés si l'API change.
- [ ] Textes en fr (et clé en en, même si non traduite).
- [ ] Contrôle d'accès par rôle testé (au moins un test « interdit » par endpoint sensible).
- [ ] Case cochée dans `docs/PLAN.md` et une ligne dans `CHANGELOG.md`.

## 9. Ce qu'il ne faut pas faire

- Ne pas ajouter de logique multi-tenant (`agence_id`) : une installation = un client.
- Ne pas appeler de service externe dans les tests (simuler agrégateur, SMS, WhatsApp, serveur de licences).
- Ne pas modifier une migration Flyway déjà commitée : en créer une nouvelle.
- Ne pas stocker de pièces d'identité en clair : chiffrement au repos (AES-GCM, clé dans la configuration de l'installation).
- Ne pas écrire de fichiers sur le disque local de l'application (hors temporaires) : tout passe par `FileStorage`.
- Ne pas utiliser les images Docker officielles MinIO (plus publiées depuis octobre 2025) ; MinIO reste utilisable par configuration si un client le possède déjà.
- Ne pas contourner Spring Modulith (pas d'accès à `..domain..` ou `..infra..` d'un autre module, pas de `@Autowired` de repository étranger).
