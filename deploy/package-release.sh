#!/usr/bin/env bash
# Archive de release à partir d'images DÉJÀ construites et publiées (jamais reconstruites : on livre exactement ce qui a été analysé et signé).
#   ./package-release.sh 1.4.0 --prefixe ghcr.io/fixedevelloper/immogest [--source-tag 1.4.0]   # tire <prefixe>-app|web|sauvegarde:<étiquette>
#   ./package-release.sh 0.0.1 --local --source-tag test                                         # images locales immogest/<nom>:<étiquette> (essais)
# Sortie : dist/immogest-<version>.tar.gz et sa somme SHA-256, au format attendu par update.sh (--archive / --sha256) :
#   images.tar (docker save, étiquetées immogest/<nom>:<version>) + deploy/ (compose, Caddyfile, scripts) + RELEASE.txt (origine exacte).
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"
VERSION="${1:?version requise, ex. 1.4.0}"; shift
[[ "$VERSION" =~ ^[0-9A-Za-z][0-9A-Za-z.+-]*$ ]] || { echo "version invalide" >&2; exit 2; }
PREFIXE=""; SOURCE="$VERSION"; LOCAL=0
while [ $# -gt 0 ]; do
  case "$1" in
    --prefixe) PREFIXE="${2:-}"; shift 2 ;;
    --source-tag) SOURCE="${2:-}"; shift 2 ;;
    --local) LOCAL=1; shift ;;
    *) echo "option inconnue : $1" >&2; exit 2 ;;
  esac
done
[ "$LOCAL" = 1 ] || [ -n "$PREFIXE" ] || { echo "--prefixe est requis (ou --local)" >&2; exit 2; }

IMAGES=(app web sauvegarde)
TMP=$(mktemp -d); trap 'rm -rf "${TMP:?}"' EXIT
{
  echo "version: $VERSION"
  echo "source: ${GITHUB_SERVER_URL:-}/${GITHUB_REPOSITORY:-local}@${GITHUB_SHA:-inconnu}"
  echo "date: $(date -u +%FT%TZ)"
} > "$TMP/RELEASE.txt"
REFS=()
for img in "${IMAGES[@]}"; do
  if [ "$LOCAL" = 1 ]; then SRC="immogest/$img:$SOURCE"; else SRC="${PREFIXE}-$img:$SOURCE"; docker pull -q "$SRC" >/dev/null; fi
  docker tag "$SRC" "immogest/$img:$VERSION"
  REFS+=("immogest/$img:$VERSION")
  echo "image $img: $SRC $(docker image inspect --format '{{if .RepoDigests}}{{index .RepoDigests 0}}{{else}}(locale){{end}} {{.Id}}' "$SRC")" >> "$TMP/RELEASE.txt"
done
docker save "${REFS[@]}" -o "$TMP/images.tar"
mkdir -p "$TMP/deploy"
cp -r docker-compose.yml Caddyfile.* install.sh update.sh backup.sh restore.sh diagnostic.sh package-release.sh "$TMP/deploy/"
mkdir -p dist
tar -czf "dist/immogest-$VERSION.tar.gz" -C "$TMP" images.tar deploy RELEASE.txt
( cd dist && sha256sum "immogest-$VERSION.tar.gz" > "immogest-$VERSION.tar.gz.sha256" )
echo "dist/immogest-$VERSION.tar.gz  $(cut -d' ' -f1 "dist/immogest-$VERSION.tar.gz.sha256")"
