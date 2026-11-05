#!/usr/bin/env bash
# Processus du conteneur « sauvegarde » : sauvegarde quotidienne (avec rattrapage si le serveur était éteint) et traitement des demandes
# déposées par l'application dans /demandes (sauvegarde immédiate, restauration).
. "$(dirname "$0")/lib.sh"
HEURE=${SAUVEGARDE_HEURE:-02:30}
mkdir -p "$SAUVEGARDES/cles" "$DEMANDES"; chmod 0777 "$DEMANDES"; chmod 0755 "$SAUVEGARDES"
derniere_tentative=0
journal "Service de sauvegarde démarré (quotidienne à $HEURE ${TZ:-UTC}, rétention ${RETENTION_JOURS} jours)"
while true; do
  maintenant=$(date +%s)
  seuil=$(date -d "$(date +%F) $HEURE" +%s)
  derniere=$(derniere_sauvegarde_date); derniere_s=0; if [ -n "$derniere" ]; then derniere_s=$(date -d "$derniere" +%s); fi
  if [ "$maintenant" -ge "$seuil" ] && [ "$derniere_s" -lt "$seuil" ] && [ $((maintenant - derniere_tentative)) -gt 3600 ]; then
    derniere_tentative=$maintenant
    /opt/sauvegarde/sauvegarder.sh quotidienne || journal "la sauvegarde quotidienne a échoué (nouvel essai dans 1 h)"
  fi
  for f in "$DEMANDES"/*.demande; do
    [ -e "$f" ] || continue
    nom=$(basename "$f" .demande); rm -f "$f"   # « réclamée » avant traitement : jamais exécutée deux fois
    case "$nom" in
      sauvegarde) /opt/sauvegarde/sauvegarder.sh manuelle || true ;;
      restauration-*) id="${nom#restauration-}"; /opt/sauvegarde/restaurer.sh "$id" --depuis-ui || true ;;
      *) journal "demande inconnue ignorée : $nom" ;;
    esac
  done
  sleep 15
done
