#!/usr/bin/env bash
# Regroupe les commandes pour lancer les tests en local.
# Usage : ./tests-local.sh [cible ...]   (sans argument : "rapide")
#
# Cibles :
#   env          démarre postgres + seaweedfs + mailpit (docker-compose.dev.yml)
#   env-stop     arrête cet environnement
#   backend      ./mvnw verify (Docker requis : Testcontainers)
#   modulith     ModularityTests seul (frontières + docs/modules/)
#   web          lint + typecheck + vitest
#   license-web  lint + typecheck + vitest (portail éditeur)
#   mobile       flutter analyze + flutter test
#   e2e          Playwright web (exige le backend en profil dev sur :8080)
#   editeur-up   démarre serveur de licences (:8091) + portail éditeur (:3200) en arrière-plan (logs : deploy/generated/logs-dev)
#   e2e-editeur  Playwright du portail éditeur dans Chrome (exige editeur-up ou équivalent)
#   openapi      régénère api/openapi.yaml
#   rapide       web + license-web + mobile + modulith (sans Docker lourd)
#   tout         env + backend + web + license-web + mobile
set -euo pipefail
RACINE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$RACINE"

# pnpm absent du PATH : repli sur npx
if ! command -v pnpm >/dev/null 2>&1; then
  pnpm() { npx -y pnpm "$@"; }
fi

etape() { printf '\n\033[1;34m==> %s\033[0m\n' "$*"; }

env_up()   { etape "Environnement de dev"; docker compose -f deploy/docker-compose.dev.yml up -d; }
env_stop() { etape "Arrêt de l'environnement"; docker compose -f deploy/docker-compose.dev.yml down; }

backend() {
  etape "Backend : ./mvnw verify"
  (cd backend && ./mvnw verify)
}

modulith() {
  etape "Spring Modulith : ModularityTests"
  (cd backend && ./mvnw -pl app -am test -Dtest=ModularityTests -Dsurefire.failIfNoSpecifiedTests=false)
}

web() {
  etape "Web : lint, typecheck, tests"
  (cd web && pnpm install --frozen-lockfile && pnpm lint && pnpm typecheck && pnpm test)
}

license_web() {
  etape "Portail éditeur : lint, typecheck, tests"
  (cd license-web && pnpm install --frozen-lockfile && pnpm lint && pnpm typecheck && pnpm test)
}

mobile() {
  etape "Mobile : analyze + tests"
  (cd mobile && flutter pub get && flutter analyze && flutter test)
}

e2e() {
  etape "Web : Playwright (backend profil dev requis sur :8080)"
  (cd web && pnpm test:e2e)
}

# Valeurs de DEV (surchargeables). Le mot de passe n'existe nulle part ailleurs que dans ces défauts et dans web/e2e-editeur.
EDITEUR_EMAIL="${EDITEUR_EMAIL:-editeur@immogest.local}"
EDITEUR_MOT_DE_PASSE="${EDITEUR_MOT_DE_PASSE:-Editeur-Local-2026!}"
CLES_DEV="${CLES_DEV:-$RACINE/deploy/generated/cles-dev}"
LOGS_DEV="$RACINE/deploy/generated/logs-dev"

editeur_up() {
  etape "Serveur de licences :8091 + portail éditeur :3200"
  mkdir -p "$LOGS_DEV"
  [ -f "$CLES_DEV/licence-private.key" ] || { echo "Clé absente : $CLES_DEV (voir licence generer-cles)" >&2; exit 1; }
  (cd backend && LICENSE_SERVER_PORT=8091 \
    LICENSE_SERVER_CLE_PRIVEE_CHEMIN="$CLES_DEV/licence-private.key" \
    LICENSE_SERVER_JWT_SECRET="${LICENSE_SERVER_JWT_SECRET:-secret-de-dev-pour-les-tests-locaux-0123456789}" \
    LICENSE_SERVER_ADMIN_EMAIL="$EDITEUR_EMAIL" LICENSE_SERVER_ADMIN_MOT_DE_PASSE="$EDITEUR_MOT_DE_PASSE" \
    nohup ./mvnw -q -pl license-server spring-boot:run >"$LOGS_DEV/license-server.log" 2>&1 &)
  (cd license-web && LICENSE_SERVER_URL=http://localhost:8091 nohup pnpm dev -p 3200 >"$LOGS_DEV/license-web.log" 2>&1 &)
  for i in $(seq 1 90); do
    curl -fs http://localhost:8091/api/v1/versions/derniere >/dev/null && curl -fs http://localhost:3200/login >/dev/null && { echo "prêts"; return; }
    sleep 2
  done
  echo "Démarrage trop long, voir $LOGS_DEV" >&2; exit 1
}

e2e_editeur() {
  etape "Portail éditeur : Playwright dans Chrome"
  (cd web && EDITEUR_EMAIL="$EDITEUR_EMAIL" EDITEUR_MOT_DE_PASSE="$EDITEUR_MOT_DE_PASSE" E2E_CHANNEL=chrome \
    pnpm exec playwright test -c playwright.editeur.config.ts)
}

openapi() {
  etape "Régénération de api/openapi.yaml"
  (cd backend && ./mvnw -pl app -am -Popenapi verify)
}

lancer() {
  case "$1" in
    env) env_up ;;
    env-stop) env_stop ;;
    backend) backend ;;
    modulith) modulith ;;
    web) web ;;
    license-web) license_web ;;
    mobile) mobile ;;
    e2e) e2e ;;
    editeur-up) editeur_up ;;
    e2e-editeur) e2e_editeur ;;
    openapi) openapi ;;
    rapide) web; license_web; mobile; modulith ;;
    tout) env_up; backend; web; license_web; mobile ;;
    -h|--help|help) sed -n '2,21p' "$0" ;;
    *) echo "Cible inconnue : $1 (voir --help)" >&2; exit 1 ;;
  esac
}

[ $# -eq 0 ] && set -- rapide
for cible in "$@"; do lancer "$cible"; done
etape "Terminé"
