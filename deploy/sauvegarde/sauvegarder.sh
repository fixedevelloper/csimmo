#!/usr/bin/env bash
# Sauvegarde complète : base PostgreSQL (pg_dump), fichiers du bucket S3 (rclone), clé de chiffrement (chiffrée, séparée), copie externe, rotation.
#   sauvegarder.sh [étiquette]      (étiquette facultative : [a-z0-9-], ex. « avant-mise-a-jour »)
# Code de sortie : 0 ok ; 1 échec (rien d'incomplet n'est conservé) ; 3 sauvegarde locale ok mais copie externe en échec.
. "$(dirname "$0")/lib.sh"
ETIQUETTE="${1:-}"
[ -z "$ETIQUETTE" ] || [[ "$ETIQUETTE" =~ ^[a-z0-9-]{1,40}$ ]] || { echo "étiquette invalide" >&2; exit 2; }
ID="$(date -u +%Y%m%d-%H%M%S)${ETIQUETTE:+-$ETIQUETTE}"
PARTIEL="${SAUVEGARDES:?}/$ID.partiel"
mkdir -p "$SAUVEGARDES/cles" "$DEMANDES"

# Une seule opération à la fois (sauvegarde, restauration)
exec 9>"$SAUVEGARDES/.verrou"
flock -n 9 || { echo "une autre opération de sauvegarde ou de restauration est en cours" >&2; exit 1; }

export ETAT_DEBUT="$(date -u +%FT%TZ)"
nettoyer() { rc=$?; if [ $rc -ne 0 ]; then rm -rf "${PARTIEL:?}"; etat_ecrire sauvegarde ECHEC "$ID" "${ECHEC_MSG:-échec (voir le journal du conteneur sauvegarde)}"; journal "ÉCHEC de la sauvegarde $ID"; fi; }
trap nettoyer EXIT
etat_ecrire sauvegarde EN_COURS "$ID" ""
journal "Sauvegarde $ID"

mkdir -p "$PARTIEL/fichiers"
ECHEC_MSG="pg_dump a échoué"
pg_dump --format=custom --no-owner --file="$PARTIEL/base.dump"
ECHEC_MSG="copie du stockage de fichiers impossible"
rclone copy "src:$BUCKET" "$PARTIEL/fichiers" --transfers 4 --quiet
ECHEC_MSG="écriture de la sauvegarde impossible"
SHA=$(sha256sum "$PARTIEL/base.dump" | cut -d' ' -f1)
OBJETS=$(find "$PARTIEL/fichiers" -type f | wc -l)

# Clé de chiffrement des pièces d'identité : fichier SÉPARÉ, chiffré, à ranger ailleurs que les données (sans elle, ces fichiers sont perdus)
if [ -n "${IMMOGEST_CLE_CHIFFREMENT:-}" ] && [ -n "${SAUVEGARDE_PASSPHRASE:-}" ]; then
  printf '%s' "$IMMOGEST_CLE_CHIFFREMENT" | openssl enc -aes-256-cbc -pbkdf2 -iter 200000 -salt -pass env:SAUVEGARDE_PASSPHRASE -base64 > "$SAUVEGARDES/cles/cle-$ID.enc"
  ls -1t "$SAUVEGARDES"/cles/cle-*.enc | tail -n +6 | xargs -r rm -f   # on garde les 5 plus récentes
fi

# Copie externe (disque ou autre S3) : un échec n'invalide pas la sauvegarde locale
EXTERNE=NON_CONFIGURE; RC=0
CIBLE=$(externe_cible || true)
if [ -n "$CIBLE" ]; then
  if rclone copy "$PARTIEL" "$CIBLE/$ID" --transfers 4 --quiet "${RCLONE_EXTERNE[@]}" && { [ ! -f "$SAUVEGARDES/cles/cle-$ID.enc" ] || rclone copy "$SAUVEGARDES/cles/cle-$ID.enc" "$CIBLE/cles" --quiet "${RCLONE_EXTERNE[@]}"; }; then
    EXTERNE=OK
  else
    EXTERNE=ECHEC; RC=3; journal "AVERTISSEMENT : copie externe en échec"
  fi
fi

TAILLE=$(du -sb "$PARTIEL" | cut -f1)
printf '{"id":"%s","date":"%s","version":"%s","etiquette":"%s","tailleOctets":%s,"objets":%s,"sha256Base":"%s","externe":"%s","statut":"COMPLETE"}\n' \
  "$ID" "$(date -u +%FT%TZ)" "${IMMOGEST_VERSION:-inconnue}" "$ETIQUETTE" "$TAILLE" "$OBJETS" "$SHA" "$EXTERNE" > "$PARTIEL/MANIFEST.json"
if [ "$EXTERNE" = OK ]; then rclone copyto "$PARTIEL/MANIFEST.json" "$CIBLE/$ID/MANIFEST.json" --quiet "${RCLONE_EXTERNE[@]}" || true; fi
chmod -R a+rX "$PARTIEL"
mv "$PARTIEL" "$SAUVEGARDES/$ID"   # la sauvegarde n'existe (manifeste visible) qu'une fois complète

# Rotation : au-delà de RETENTION_JOURS, en gardant toujours les 3 plus récentes
journal "Rotation (rétention ${RETENTION_JOURS} jours)"
ls -1d "$SAUVEGARDES"/[0-9]*/ 2>/dev/null | sort -r | tail -n +4 | while read -r d; do
  if [ -n "$(find "$d" -maxdepth 0 -mtime +"$RETENTION_JOURS")" ]; then journal "suppression de $(basename "$d")"; rm -rf "${d:?}"; fi
done || true

trap - EXIT
if [ $RC -eq 3 ]; then etat_ecrire sauvegarde ECHEC "$ID" "sauvegarde locale réussie, copie externe en échec"; else etat_ecrire sauvegarde OK "$ID" ""; fi
journal "Sauvegarde $ID terminée ($OBJETS fichiers, $TAILLE octets, copie externe : $EXTERNE)"
exit $RC
