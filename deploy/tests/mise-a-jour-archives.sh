#!/usr/bin/env bash
# Test de mise à jour avec de VRAIES archives de release (celles que les clients utilisent, y compris hors ligne) :
#   installe l'archive ANCIENNE, crée des données et un fichier, met à jour avec l'archive NOUVELLE (update.sh de l'ancienne version, comme chez un client),
#   vérifie santé, version, base et fichiers ; refuse une archive falsifiée ; vérifie le retour arrière sur un échec simulé.
#   deploy/tests/mise-a-jour-archives.sh dist/immogest-0.0.1.tar.gz dist/immogest-0.0.2.tar.gz
# Variables : PORT_HTTP / PORT_HTTPS (défaut 80 / 443), DOSSIER_ESSAI (défaut : deploy/generated/essai-mise-a-jour).
# Détruit son installation de test (projet Docker « immogest-essai », volumes compris) : n'efface jamais une installation réelle.
set -euo pipefail
ANC="$(readlink -f "${1:?archive ancienne requise}")"; NOUV="$(readlink -f "${2:?archive nouvelle requise}")"
for f in "$ANC" "$ANC.sha256" "$NOUV" "$NOUV.sha256"; do [ -f "$f" ] || { echo "introuvable : $f" >&2; exit 2; }; done
V_ANC=$(basename "$ANC" .tar.gz); V_ANC=${V_ANC#immogest-}; V_NOUV=$(basename "$NOUV" .tar.gz); V_NOUV=${V_NOUV#immogest-}
SHA_NOUV=$(cut -d' ' -f1 "$NOUV.sha256")
RACINE="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ESSAI="${DOSSIER_ESSAI:-$RACINE/generated/essai-mise-a-jour}"
export COMPOSE_PROJECT_NAME=immogest-essai   # isole l'essai de toute installation réelle (volumes, réseaux, conteneurs)

nettoyer() {
  ( cd "$ESSAI/deploy" 2>/dev/null && docker compose down -v >/dev/null 2>&1 ) || true
  rm -rf "${ESSAI:?}"
}
trap nettoyer EXIT
rm -rf "${ESSAI:?}"; mkdir -p "$ESSAI"
etape() { echo; echo "==> $*"; }
psql_() { ( cd "$ESSAI/deploy" && docker compose exec -T sauvegarde psql -At "$@" ); }

etape "Installation de la version $V_ANC (depuis son archive, images chargées, aucun accès Internet nécessaire)"
tar -xzf "$ANC" -C "$ESSAI"
docker load -i "$ESSAI/images.tar" >/dev/null
( cd "$ESSAI/deploy" && ./install.sh --mode local --site localhost --stockage local --version "$V_ANC" --images-chargees --yes \
    --port-http "${PORT_HTTP:-80}" --port-https "${PORT_HTTPS:-443}" )

etape "Données témoins (base et fichiers)"
psql_ -c "CREATE TABLE test_integrite(v text); INSERT INTO test_integrite SELECT 'ligne-' || g FROM generate_series(1, 50) g" >/dev/null
( cd "$ESSAI/deploy" && docker compose exec -T sauvegarde bash -c '. /opt/sauvegarde/lib.sh; echo piece > /tmp/p.txt; rclone copyto /tmp/p.txt src:$BUCKET/test/p.txt' )
AVANT=$(psql_ -c "SELECT count(*) FROM public.test_integrite")

verifier() { # verifier VERSION_ATTENDUE
  [ "$(grep ^IMMOGEST_VERSION= "$ESSAI/deploy/.env" | cut -d= -f2)" = "$1" ] || { echo "ÉCHEC : version attendue $1" >&2; exit 1; }
  [ "$(psql_ -c "SELECT count(*) FROM public.test_integrite")" = "$AVANT" ] || { echo "ÉCHEC : lignes perdues" >&2; exit 1; }
  ( cd "$ESSAI/deploy" && docker compose exec -T sauvegarde bash -c '. /opt/sauvegarde/lib.sh; rclone cat src:$BUCKET/test/p.txt' ) | grep -q piece || { echo "ÉCHEC : fichier perdu" >&2; exit 1; }
  ( cd "$ESSAI/deploy" && docker compose exec -T app curl -fsS http://localhost:8080/api/v1/health ) | grep -q UP || { echo "ÉCHEC : API en panne" >&2; exit 1; }
}
verifier "$V_ANC"

etape "Archive falsifiée : doit être refusée avant toute modification"
FAUSSE="$ESSAI/falsifiee.tar.gz"; cp "$NOUV" "$FAUSSE"; printf 'x' | dd of="$FAUSSE" bs=1 seek=100 conv=notrunc status=none
if ( cd "$ESSAI/deploy" && ./update.sh --archive "$FAUSSE" --sha256 "$SHA_NOUV" --yes ) > "$ESSAI/falsifiee.log" 2>&1; then echo "ÉCHEC : une archive falsifiée a été acceptée" >&2; exit 1; fi
grep -q "SOMME DE CONTRÔLE INVALIDE" "$ESSAI/falsifiee.log" || { echo "ÉCHEC : refus pour une mauvaise raison"; cat "$ESSAI/falsifiee.log"; exit 1; }
verifier "$V_ANC"

# Jusqu'à 0.1.0, update.sh n'était pas protégé contre sa propre réécriture : la mise à jour recopie les fichiers de la nouvelle version, lui compris,
# pendant qu'il tourne, et bash reprend alors sa lecture au milieu du NOUVEAU fichier. Depuis 0.2.0 tout le script est dans main() (lu en entier avant
# exécution). Pour une version installée non protégée, la procédure documentée (deploy/README.md) est de prendre d'abord update.sh de l'archive.
if ! grep -q '^main "\$@"' "$ESSAI/deploy/update.sh"; then
  etape "update.sh $V_ANC non protégé contre sa réécriture : on prend celui de l'archive $V_NOUV (procédure documentée)"
  tar -xzf "$NOUV" -C "$ESSAI" deploy/update.sh
fi

etape "Mise à jour $V_ANC → $V_NOUV avec l'archive (update.sh de la version installée)"
( cd "$ESSAI/deploy" && ./update.sh --archive "$NOUV" --sha256 "$SHA_NOUV" --yes )
verifier "$V_NOUV"

if [ "${AVEC_RETOUR_ARRIERE:-1}" = 1 ]; then
  etape "Retour arrière : échec simulé après migration, la version courante ($V_NOUV) doit revenir avec ses données"
  psql_ -c "INSERT INTO test_integrite VALUES ('ajoutee-apres-mise-a-jour')" >/dev/null
  AVANT=$((AVANT + 1))
  if ( cd "$ESSAI/deploy" && IMMOGEST_TEST_ECHEC=apres-migration ./update.sh --archive "$NOUV" --sha256 "$SHA_NOUV" --yes ) > "$ESSAI/retour.log" 2>&1; then
    echo "ÉCHEC : la mise à jour simulée en échec a réussi" >&2; exit 1; fi
  grep -q "Retour arrière réussi" "$ESSAI/retour.log" || { echo "ÉCHEC : pas de retour arrière réussi"; tail -20 "$ESSAI/retour.log"; exit 1; }
  verifier "$V_NOUV"
fi
echo; echo "OK : $V_ANC → $V_NOUV, données et fichiers intacts, archive falsifiée refusée, retour arrière vérifié"
