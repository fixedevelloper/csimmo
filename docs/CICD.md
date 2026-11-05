# CI/CD d'ImmoGest

> **État** : les workflows sont poussés sur `fixedevelloper/csimmo` (privé) et passent `actionlint` ; **leurs premiers résultats réels n'ont pas encore été relus** (dépôt privé : non lisibles sans authentification). Les Dockerfiles, eux, ont été construits et démarrés localement. Les premières exécutions réelles révéleront probablement des ajustements (cache, droits, noms de paquets).

## Vue d'ensemble

| Workflow | Quand | Fait quoi |
| --- | --- | --- |
| `ci.yml` | PR et fusions sur `main` | Backend (`mvnw verify`), web et portail éditeur (lint, types, tests, build), **audit des dépendances** (HIGH/CRITICAL bloquants), scénarios Playwright, mobile, **construction des images + installation de bout en bout** (`packaging`), construction des images de l'éditeur, mise à jour N-1 → N |
| `images.yml` | fusion sur `main`, tag `vX.Y.Z` | Construit, **analyse (Trivy)**, **publie sur GHCR**, **signe (cosign)** les 5 images, puis **installe les images publiées** et vérifie qu'elles servent l'application |
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

Dans **Settings > Actions > General** : droits du `GITHUB_TOKEN` « lecture » par défaut (les workflows demandent eux-mêmes ce qu'il leur faut). Dans **Settings > Packages** : conserver les paquets privés ; donner l'accès en lecture aux comptes qui installent.

Aucun secret n'est nécessaire à ce stade : la publication utilise le `GITHUB_TOKEN` et la signature l'identité OIDC du workflow. **La clé privée de licence n'est jamais dans la CI** : elle ne sert qu'au serveur de licences de l'éditeur.

## Publier une version

1. **Renseigner la clé publique de l'éditeur** dans `backend/app/src/main/resources/licence/cle-publique-editeur.txt` (sinon le tag est refusé par `garde-release`).
2. Mettre `CHANGELOG.md` à jour, fusionner sur `main`, attendre la CI verte.
3. `git tag v1.4.0 && git push origin v1.4.0`.
4. `images.yml` : contrôle de la clé → construction → **analyse bloquante** (CRITICAL/HIGH corrigeables) → publication → signature → installation des images publiées.
5. Si une faille connue est acceptée, l'inscrire dans `.trivyignore` **avec sa raison et sa date de revue**.

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
`--images-prebuilt` **ne construit rien** (`up --no-build`). Pour un client **sans accès à GHCR ou sans Internet**, la voie reste l'archive `immogest-X.tar.gz` + somme SHA-256 de `update.sh` (`deploy/package.sh`).

## Points d'attention des premières exécutions

- **CodeQL** : constaté sur ce dépôt, le code scanning n'est pas disponible (dépôt privé, sans GitHub Advanced Security) : le workflow est passé en déclenchement manuel. Dependabot et l'audit des dépendances du CI, eux, fonctionnent.
- **`images.yml` tourne dès la première fusion sur `main`** et publie `edge` et `sha-…` dans GHCR (paquets privés par défaut).
- Les contrôles obligatoires de la protection de branche doivent porter les noms exacts affichés par GitHub après une première exécution : ajustez la liste du § Mise en place.

## Ce qui n'est pas encore fait

- **Archive de release et publication de la version dans le serveur de licences** (phase 3) : `package.sh` est à rejouer dans un workflow de release, qui attache l'archive et sa somme à la GitHub Release et appelle l'API de publication des versions. Il faut décider d'un jeton d'API pour le serveur de licences (il n'a aujourd'hui qu'une connexion par mot de passe).
- **Déploiement du staging et de l'éditeur** (phase 4) : pas de serveur ni de compose pour `license-server` / `license-web`.
- **Application mobile** (phase 5) : pas de build Android en CI.
- **Audit des dépendances Java** (OWASP dependency-check) : seul Dependabot les surveille.
- **Actions épinglées par condensé** (`uses: …@<sha>`) plutôt que par version majeure : durcissement à faire une fois le dépôt en place (Dependabot tient alors les condensés à jour).
- **arm64** : non construit.
