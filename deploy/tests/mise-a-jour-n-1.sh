#!/usr/bin/env bash
# Test de mise à jour : installe la version précédente (tag git), crée des données témoins, met à jour vers la version du dépôt courant avec
# le update.sh COURANT, puis vérifie santé, données et fichiers. Usage (depuis la racine du dépôt) : deploy/tests/mise-a-jour-n-1.sh <tag-précédent>
# Détruit l'installation de test à la fin (docker compose down -v) : à n'exécuter que sur une machine de CI ou de test.
set -euo pipefail
PREV="${1:?tag précédent requis}"
RACINE=$(git rev-parse --show-toplevel)
PREV_DIR=$(mktemp -d)
git worktree add "$PREV_DIR" "$PREV"
cleanup() { (cd "$RACINE/deploy" && docker compose down -v >/dev/null 2>&1 || true); git worktree remove --force "$PREV_DIR" >/dev/null 2>&1 || true; }
trap cleanup EXIT

echo "==> Installation de la version précédente ($PREV)"
( cd "$PREV_DIR/deploy" && ./install.sh --mode local --site localhost --stockage local --version prev --yes )
cp "$PREV_DIR/deploy/.env" "$RACINE/deploy/.env"; mkdir -p "$RACINE/deploy/generated"; cp -r "$PREV_DIR/deploy/generated/." "$RACINE/deploy/generated/"
cd "$RACINE/deploy"

echo "==> Données témoins"
docker compose exec -T sauvegarde psql -q -c "CREATE TABLE test_integrite(v text); INSERT INTO test_integrite SELECT 'ligne-' || g FROM generate_series(1, 50) g"
docker compose exec -T sauvegarde bash -c '. /opt/sauvegarde/lib.sh; echo piece > /tmp/p.txt; rclone copyto /tmp/p.txt src:$BUCKET/test/p.txt'
AVANT=$(docker compose exec -T sauvegarde psql -At -c "SELECT count(*) FROM public.test_integrite")

echo "==> Mise à jour vers la version courante"
./update.sh --depuis-les-sources --version courante --yes

echo "==> Vérifications"
APRES=$(docker compose exec -T sauvegarde psql -At -c "SELECT count(*) FROM public.test_integrite")
[ "$AVANT" = "$APRES" ] || { echo "ÉCHEC : $AVANT lignes avant, $APRES après"; exit 1; }
docker compose exec -T sauvegarde bash -c '. /opt/sauvegarde/lib.sh; rclone cat src:$BUCKET/test/p.txt' | grep -q piece || { echo "ÉCHEC : fichier perdu"; exit 1; }
docker compose exec -T app curl -fsS http://localhost:8080/api/v1/health | grep -q UP || { echo "ÉCHEC : API en panne"; exit 1; }
echo "OK : mise à jour $PREV → courante, données et fichiers intacts"
