#!/usr/bin/env bash
# Construit l'archive d'une version à publier : immogest-<version>.tar.gz (images Docker + fichiers de déploiement) et sa somme SHA-256.
#   ./package.sh 1.4.0      → dist/immogest-1.4.0.tar.gz et dist/immogest-1.4.0.tar.gz.sha256
# L'archive et la somme sont ensuite publiées via le portail éditeur (Versions).
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"
VERSION="${1:?version requise, ex. 1.4.0}"
[[ "$VERSION" =~ ^[0-9A-Za-z][0-9A-Za-z.+-]*$ ]] || { echo "version invalide" >&2; exit 2; }
export IMMOGEST_VERSION="$VERSION"
# variables factices : seule la construction nous intéresse
export POSTGRES_PASSWORD=x IMMOGEST_JWT_SECRET=x IMMOGEST_CLE_CHIFFREMENT=x IMMOGEST_S3_ACCESS_KEY=x IMMOGEST_S3_SECRET_KEY=x IMMOGEST_MACHINE_ID=x IMMOGEST_SITE=x
docker compose build app web sauvegarde
TMP=$(mktemp -d); trap 'rm -rf "${TMP:?}"' EXIT
docker save "immogest/app:$VERSION" "immogest/web:$VERSION" "immogest/sauvegarde:$VERSION" -o "$TMP/images.tar"
mkdir -p "$TMP/deploy"
cp -r docker-compose.yml Caddyfile.* install.sh update.sh backup.sh restore.sh diagnostic.sh Dockerfile.* "$TMP/deploy/"
mkdir -p dist
tar -czf "dist/immogest-$VERSION.tar.gz" -C "$TMP" images.tar deploy
( cd dist && sha256sum "immogest-$VERSION.tar.gz" > "immogest-$VERSION.tar.gz.sha256" )
echo "dist/immogest-$VERSION.tar.gz  $(cut -d' ' -f1 "dist/immogest-$VERSION.tar.gz.sha256")"
