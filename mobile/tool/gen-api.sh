#!/usr/bin/env bash
# Régénère le client Dart (openapi-generator, dart-dio + json_serializable) depuis api/openapi.yaml.
# Usage : cd mobile && ./tool/gen-api.sh      (Java 17+ requis ; le jar est téléchargé dans un dossier temporaire)
set -euo pipefail

VERSION="7.27.0"
RACINE="$(cd "$(dirname "$0")/.." && pwd)"
CIBLE="$RACINE/packages/immogest_api"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

curl -fsSL -o "$TMP/oag.jar" \
  "https://repo.maven.apache.org/maven2/org/openapitools/openapi-generator-cli/$VERSION/openapi-generator-cli-$VERSION.jar"

rm -rf "$CIBLE"
(cd "$TMP" && java -jar oag.jar generate -g dart-dio \
  -i "$RACINE/../api/openapi.yaml" -o "$CIBLE" \
  --additional-properties=pubName=immogest_api,pubVersion=0.1.0,serializationLibrary=json_serializable)

# json_serializable génère une syntaxe qui exige Dart >= 3.8 (le générateur déclare 3.5)
sed -i "s|sdk: '>=3.5.0 <4.0.0'|sdk: '>=3.8.0 <4.0.0'|" "$CIBLE/pubspec.yaml"
# Fichiers de documentation et tests d'exemple générés : inutiles dans le dépôt
rm -rf "$CIBLE/doc" "$CIBLE/test" "$CIBLE/README.md" "$CIBLE/.openapi-generator" "$CIBLE/.openapi-generator-ignore"

(cd "$CIBLE" && flutter pub get && dart run build_runner build -d)
