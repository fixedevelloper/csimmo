#!/usr/bin/env bash
# Fonctions communes aux scripts de sauvegarde / restauration (exécutés dans le conteneur « sauvegarde »).
set -euo pipefail

SAUVEGARDES=${SAUVEGARDES:-/sauvegardes}
DEMANDES=${DEMANDES:-/demandes}
BUCKET=${IMMOGEST_S3_BUCKET:-immogest}
RETENTION_JOURS=${SAUVEGARDE_RETENTION_JOURS:-30}
export PGHOST=${PGHOST:-postgres} PGUSER=${PGUSER:-immogest} PGDATABASE=${PGDATABASE:-immogest}

# Remote rclone « src » = le stockage S3 de l'application ; « ext » = copie externe (si configurée)
export RCLONE_CONFIG_SRC_TYPE=s3 RCLONE_CONFIG_SRC_PROVIDER=Other RCLONE_CONFIG_SRC_ENDPOINT="${IMMOGEST_S3_ENDPOINT:-http://seaweedfs:8333}"
export RCLONE_CONFIG_SRC_ACCESS_KEY_ID="${IMMOGEST_S3_ACCESS_KEY:-}" RCLONE_CONFIG_SRC_SECRET_ACCESS_KEY="${IMMOGEST_S3_SECRET_KEY:-}"
export RCLONE_CONFIG_SRC_REGION="${IMMOGEST_S3_REGION:-us-east-1}" RCLONE_CONFIG_SRC_FORCE_PATH_STYLE=true
if [ -n "${SAUVEGARDE_EXTERNE_S3_ENDPOINT:-}" ]; then
  export RCLONE_CONFIG_EXT_TYPE=s3 RCLONE_CONFIG_EXT_PROVIDER=Other RCLONE_CONFIG_EXT_ENDPOINT="$SAUVEGARDE_EXTERNE_S3_ENDPOINT"
  export RCLONE_CONFIG_EXT_ACCESS_KEY_ID="${SAUVEGARDE_EXTERNE_S3_ACCESS_KEY:-}" RCLONE_CONFIG_EXT_SECRET_ACCESS_KEY="${SAUVEGARDE_EXTERNE_S3_SECRET_KEY:-}"
  export RCLONE_CONFIG_EXT_REGION="${SAUVEGARDE_EXTERNE_S3_REGION:-us-east-1}" RCLONE_CONFIG_EXT_FORCE_PATH_STYLE=true
fi

export RCLONE_CONFIG=/tmp/rclone.conf; : > "$RCLONE_CONFIG"   # configuration par variables d'environnement uniquement
# Copie externe : une destination injoignable doit échouer vite (par défaut rclone réessaie ~10 min), la sauvegarde locale est déjà valide
RCLONE_EXTERNE=(--retries 1 --low-level-retries 2 --contimeout 10s --timeout 60s)

journal() { echo "[$(date -u +%FT%TZ)] $*"; }

# Destination externe : « s3 » (bucket distinct), « disque » (volume monté sur /externe, marqué par /externe/.actif) ou rien
externe_cible() {
  if [ -n "${SAUVEGARDE_EXTERNE_S3_ENDPOINT:-}" ]; then echo "ext:${SAUVEGARDE_EXTERNE_S3_BUCKET:?SAUVEGARDE_EXTERNE_S3_BUCKET manquant}/${SAUVEGARDE_EXTERNE_S3_PREFIXE:-immogest}"
  elif [ -f /externe/.actif ]; then echo "/externe"
  fi
}

# etat_ecrire OPERATION STATUT ID MESSAGE : lu par l'interface (écran Paramètres > Sauvegardes)
etat_ecrire() {
  local op="$1" statut="$2" id="${3:-}" msg="${4:-}"
  local debut="${ETAT_DEBUT:-$(date -u +%FT%TZ)}"; export ETAT_DEBUT="$debut"
  local fin="null"; [ "$statut" != EN_COURS ] && fin="\"$(date -u +%FT%TZ)\""
  msg=${msg//\\/\\\\}; msg=${msg//\"/\\\"}
  printf '{"operation":"%s","statut":"%s","id":"%s","debut":"%s","fin":%s,"message":"%s"}\n' "$op" "$statut" "$id" "$debut" "$fin" "$msg" > "$DEMANDES/etat.json.tmp"
  mv "$DEMANDES/etat.json.tmp" "$DEMANDES/etat.json"; chmod 0644 "$DEMANDES/etat.json"
}

id_valide() { [[ "$1" =~ ^[0-9]{8}-[0-9]{6}(-[a-z0-9-]{1,40})?$ ]]; }

# Date ISO du manifeste de la dernière sauvegarde complète (vide si aucune)
derniere_sauvegarde_date() {
  local m; m=$(ls -1d "$SAUVEGARDES"/[0-9]*/MANIFEST.json 2>/dev/null | sort | tail -n1) || true
  if [ -n "$m" ]; then sed -n 's/.*"date":"\([^"]*\)".*/\1/p' "$m"; fi
}
