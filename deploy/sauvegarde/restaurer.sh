#!/usr/bin/env bash
# Restaure une sauvegarde : base (recréée) + fichiers du bucket (synchronisés, les fichiers postérieurs sont supprimés).
#   restaurer.sh <id> [--depuis-ui]
# Une sauvegarde de sécurité « avant-restauration » est faite d'abord. Refuse une sauvegarde d'une autre version de l'application
# (le retour arrière d'une mise à jour restaure aussi l'ancienne image : voir update.sh), sauf FORCER_VERSION=1.
. "$(dirname "$0")/lib.sh"
ID="${1:-}"; DEPUIS_UI="${2:-}"
id_valide "$ID" || { echo "identifiant de sauvegarde invalide" >&2; exit 2; }
DIR="${SAUVEGARDES:?}/$ID"
[ -f "$DIR/MANIFEST.json" ] || { echo "sauvegarde introuvable ou incomplète : $ID" >&2; exit 2; }
VERSION_SAUVEGARDE=$(sed -n 's/.*"version":"\([^"]*\)".*/\1/p' "$DIR/MANIFEST.json")
if [ "${FORCER_VERSION:-0}" != 1 ] && [ -n "${IMMOGEST_VERSION:-}" ] && [ "$VERSION_SAUVEGARDE" != "$IMMOGEST_VERSION" ]; then
  echo "cette sauvegarde vient de la version $VERSION_SAUVEGARDE (installée : $IMMOGEST_VERSION)" >&2; exit 4
fi
ATTENDU=$(sed -n 's/.*"sha256Base":"\([^"]*\)".*/\1/p' "$DIR/MANIFEST.json")
[ "$(sha256sum "$DIR/base.dump" | cut -d' ' -f1)" = "$ATTENDU" ] || { echo "la somme de contrôle de la base ne correspond pas : sauvegarde corrompue" >&2; exit 5; }

export ETAT_DEBUT="$(date -u +%FT%TZ)"
etat_ecrire restauration EN_COURS "$ID" "sauvegarde de sécurité"
journal "Restauration de $ID : sauvegarde de sécurité d'abord"
RC=0; "$(dirname "$0")/sauvegarder.sh" avant-restauration || RC=$?
if [ $RC -ne 0 ] && [ $RC -ne 3 ]; then etat_ecrire restauration ECHEC "$ID" "la sauvegarde de sécurité a échoué : rien n'a été modifié"; exit 1; fi

exec 9>"$SAUVEGARDES/.verrou"; flock -n 9 || { echo "opération en cours" >&2; exit 1; }
export ETAT_DEBUT="$(date -u +%FT%TZ)"
trap 'etat_ecrire restauration ECHEC "$ID" "échec : restaurez la sauvegarde avant-restauration la plus récente"; journal "ÉCHEC de la restauration"' ERR
etat_ecrire restauration EN_COURS "$ID" "restauration de la base"
PGDATABASE=postgres psql -v ON_ERROR_STOP=1 -q -c "DROP DATABASE IF EXISTS immogest WITH (FORCE)" -c "CREATE DATABASE immogest OWNER immogest"
PGDATABASE=immogest pg_restore --no-owner --exit-on-error --dbname=immogest "$DIR/base.dump"
etat_ecrire restauration EN_COURS "$ID" "restauration des fichiers"
rclone sync "$DIR/fichiers" "src:$BUCKET" --transfers 4 --quiet
trap - ERR
# L'application surveille ce fichier et redémarre (sa connexion à l'ancienne base est coupée) ; ignoré s'il est antérieur à son démarrage
if [ "$DEPUIS_UI" = "--depuis-ui" ]; then date +%s > "$DEMANDES/redemarrer-app"; chmod 0666 "$DEMANDES/redemarrer-app"; fi
etat_ecrire restauration OK "$ID" ""
journal "Restauration de $ID terminée"
