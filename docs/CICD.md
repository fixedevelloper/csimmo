# CI/CD d'ImmoGest

> **État** : les workflows sont poussés sur `fixedevelloper/csimmo` (privé) et passent `actionlint` ; **leurs premiers résultats réels n'ont pas encore été relus** (dépôt privé : non lisibles sans authentification). Les Dockerfiles, eux, ont été construits et démarrés localement. Les premières exécutions réelles révéleront probablement des ajustements (cache, droits, noms de paquets).

## Vue d'ensemble

| Workflow | Quand | Fait quoi |
| --- | --- | --- |
| `ci.yml` | PR et fusions sur `main` | Backend (`mvnw verify`), web et portail éditeur (lint, types, tests, build), **audit des dépendances** (HIGH/CRITICAL bloquants), scénarios Playwright, mobile, **construction des images + installation de bout en bout** (`packaging`), construction des images de l'éditeur, mise à jour N-1 → N |
| `images.yml` | fusion sur `main` ; appelé par `release.yml` sur un tag | Construit, **analyse (Trivy)**, **publie sur GHCR**, **signe (cosign)** les 5 images, puis **installe les images publiées** et vérifie qu'elles servent l'application |
| `release.yml` | tag `vX.Y.Z` ; manuel (répétition) | Appelle `images.yml`, fabrique l'**archive** à partir des images publiées, la **signe**, **teste la mise à jour** avec cette archive depuis la release précédente, puis crée la **GitHub Release** |
| `codeql.yml` | **manuel seulement** (désactivé) | Analyse statique Java et TypeScript : indisponible sur un dépôt privé sans GitHub Advanced Security (« Code scanning is not enabled »). Déclencheurs à remettre si le dépôt devient public ou si GHAS est activé |
| `dependabot.yml` | chaque semaine | PR groupées de mises à jour (Maven, npm ×2, pub, Docker, Actions) |

Images publiées (`ghcr.io/fixedevelloper/immogest-<nom>`) : `app` (jar obfusqué), `web`, `sauvegarde`, `license-server`, `license-web`. Une seule architecture : **linux/amd64**.

Étiquettes : `edge` et `sha-xxxxxxx` à chaque fusion sur `main` ; `X.Y.Z`, `X.Y` et `latest` sur un tag. Une étiquette peut bouger, **le condensé (digest) signé non** : c'est lui qui fait foi.

## Mise en place (une seule fois)

Ces commandes agissent hors de cette machine : à lancer par un humain, après relecture.

```bash
# 1. Dépôt déjà créé et poussé : https://github.com/fixedevelloper/csimmo (privé, branche main)

# 2. Protection de la branche principale : la CI doit être verte
gh api -X PUT repos/fixedevelloper/csimmo/branches/main/protection --input - <<'JSON'
{ "required_status_checks": { "strict": true, "contexts": ["backend", "web (web)", "web (license-web)", "web-e2e", "audit-dependances (web)", "audit-dependances (license-web)", "mobile", "packaging", "images-editeur (license-server)", "images-editeur (license-web)"] },
  "enforce_admins": true,
  "required_pull_request_reviews": { "required_approving_review_count": 1 },
  "restrictions": null }
JSON
```

Dans **Settings > Actions > General** : droits du `GITHUB_TOKEN` « lecture » par défaut (les workflows demandent eux-mêmes ce qu'il leur faut). Dans **Settings > Packages** : passer les 5 paquets `immogest-*` en **public** (le dépôt reste privé), sinon `docker pull` échoue chez les clients.

Aucun secret n'est nécessaire à ce stade : la publication utilise le `GITHUB_TOKEN` et la signature l'identité OIDC du workflow. **La clé privée de licence n'est jamais dans la CI** : elle ne sert qu'au serveur de licences de l'éditeur.

## Publier une version

1. **Renseigner la clé publique de l'éditeur** dans `backend/app/src/main/resources/licence/cle-publique-editeur.txt` (sinon le tag est refusé par le contrôle de `images.yml`).
2. Mettre `CHANGELOG.md` à jour, fusionner sur `main`, attendre la CI verte.
3. `git tag v1.4.0 && git push origin v1.4.0`. **`release.yml`** enchaîne :
   1. `images.yml` : contrôle de la clé → construction → **analyse bloquante** (CRITICAL/HIGH corrigeables) → publication → signature → installation des images publiées ;
   2. **archive** `immogest-1.4.0.tar.gz` (`deploy/package-release.sh`) à partir des images **publiées** (jamais reconstruites : on livre exactement ce qui a été analysé et signé), avec `RELEASE.txt` (révision git, condensés des images) et sa somme `.sha256` ; signature de l'archive (`.bundle`, cosign sans clé) ;
   3. **test de mise à jour réel** (`deploy/tests/mise-a-jour-archives.sh`) : installe l'archive de la release précédente, crée des données et un fichier, met à jour avec la nouvelle archive, vérifie santé, base et fichiers, **refuse une archive falsifiée**, vérifie le **retour arrière** sur un échec simulé. Ignoré (avec un message) pour la toute première version ;
   4. **GitHub Release** : archive, somme et signature en pièces jointes (version contenant un tiret, ex. `1.4.0-rc1` : « pré-release »).
4. Si une faille connue est acceptée, l'inscrire dans `.trivyignore` **avec sa raison et sa date de revue**.

**Répétition sans rien publier** : Actions > Release > Run workflow, version `0.0.0-essai`, étiquette source `edge` (ou `sha-xxxxxxx`). Fabrique et signe l'archive à partir de ces images et la garde 30 jours en artefact ; ne crée **jamais** de Release (réservé à un vrai tag).

**Test local sans GitHub** : `deploy/package-release.sh 0.0.1 --local --source-tag <étiquette locale>` (deux fois, deux versions), puis `PORT_HTTP=18081 PORT_HTTPS=18443 deploy/tests/mise-a-jour-archives.sh dist/immogest-0.0.1.tar.gz dist/immogest-0.0.2.tar.gz` (projet Docker isolé `immogest-essai`, détruit à la fin).

## Vérifier une image avant de l'installer

```bash
cosign verify ghcr.io/fixedevelloper/immogest-app:1.4.0 \
  --certificate-identity-regexp '^https://github.com/fixedevelloper/csimmo/\.github/workflows/images\.yml@refs/tags/v' \
  --certificate-oidc-issuer https://token.actions.githubusercontent.com
```
Réussit seulement si l'image a été produite par `images.yml` de CE dépôt sur un tag. Une image modifiée à la main, ou construite ailleurs, échoue. Chaque image porte aussi sa **provenance** (révision git, workflow) et son **SBOM** (liste des composants), consultables avec `docker buildx imagetools inspect`.

## Installer et mettre à jour à partir des images publiées

```bash
docker login ghcr.io
export IMMOGEST_IMAGE_APP=ghcr.io/fixedevelloper/immogest-app IMMOGEST_IMAGE_WEB=ghcr.io/fixedevelloper/immogest-web IMMOGEST_IMAGE_SAUVEGARDE=ghcr.io/fixedevelloper/immogest-sauvegarde
cd deploy && ./install.sh --mode domaine --site immo.exemple.cm --version 1.4.0 --images-prebuilt --yes
```
`--images-prebuilt` **ne construit rien** (`up --no-build`) et écrit `IMMOGEST_IMAGE_*` dans `.env` (préfixe par défaut `ghcr.io/fixedevelloper/immogest`, option `--registre`). Les images sont **publiques** (décision : le jar est obfusqué, aucun jeton chez le client) : le `docker login` et les variables exportées ne sont plus nécessaires. Mise à jour : `./update.sh --version 1.4.0 [--verifier-signature]` (voir `deploy/README.md`). Le serveur de l'éditeur se met à jour à la main (`deploy/editeur/README.md`). Pour un client **sans accès à GHCR ou sans Internet**, la voie reste l'archive `immogest-X.tar.gz` + somme SHA-256 de `update.sh` (`deploy/package-release.sh`).

## Points d'attention des premières exécutions

- **CodeQL** : constaté sur ce dépôt, le code scanning n'est pas disponible (dépôt privé, sans GitHub Advanced Security) : le workflow est passé en déclenchement manuel. Dependabot et l'audit des dépendances du CI, eux, fonctionnent.
- **`images.yml` tourne dès la première fusion sur `main`** et publie `edge` et `sha-…` dans GHCR (paquets privés par défaut).
- Les contrôles obligatoires de la protection de branche doivent porter les noms exacts affichés par GitHub après une première exécution : ajustez la liste du § Mise en place.

## Ce qui n'est pas encore fait

- **Téléchargement par les clients** : dans un dépôt **privé**, les pièces jointes d'une Release ne se téléchargent pas sans authentification GitHub, or `update.sh --url` fait un `curl` simple. Il faut décider où héberger l'archive pour les clients : un stockage objet (S3, B2…) en lecture publique ou par lien signé, alimenté par le workflow, **ou** continuer à remettre l'archive (clé USB, lien d'envoi) avec sa somme. Tant que ce n'est pas tranché, `update.sh --archive` fonctionne, pas la mise à jour « en un clic » depuis le serveur de licences.
- **Publication de la version dans le serveur de licences** (`POST /api/v1/versions`) : dépend du point précédent (URL à enregistrer) et d'un jeton d'API (il n'a aujourd'hui qu'une connexion par mot de passe).
- **Déploiement du staging** (phase 4) : pas de serveur de staging. Le compose de l'éditeur existe (`deploy/editeur/` : PostgreSQL, `license-server`, `license-web`, Caddy ; images GHCR) mais n'a pas encore été déployé sur un serveur réel.
- **Application mobile** (phase 5) : pas de build Android en CI.
- **Audit des dépendances Java** (OWASP dependency-check) : seul Dependabot les surveille.
- **Actions épinglées par condensé** (`uses: …@<sha>`) plutôt que par version majeure.
- **arm64** : non construit.
- **Première release réelle** : jamais exécutée (bloquée par la clé publique de l'éditeur) ; seule la répétition et l'essai local ont été vérifiés.
