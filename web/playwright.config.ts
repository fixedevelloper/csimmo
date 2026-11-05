import { defineConfig, devices } from "@playwright/test";

/**
 * Parcours de bout en bout : nécessite le backend (profil dev, ADMIN de développement) sur API_URL,
 * lui-même relié à PostgreSQL et SeaweedFS (deploy/docker-compose.dev.yml).
 */
const PORT = Number(process.env.E2E_PORT ?? 3100);

export default defineConfig({
  testDir: "./e2e",
  fullyParallel: false,
  workers: 1,
  // La première compilation d'une page en mode dev peut dépasser les 5 s par défaut
  expect: { timeout: 20_000 },
  retries: process.env.CI ? 1 : 0,
  reporter: process.env.CI ? [["github"], ["html", { open: "never" }]] : "list",
  use: {
    baseURL: `http://localhost:${PORT}`,
    locale: "fr-FR",
    trace: "retain-on-failure",
  },
  // E2E_CHANNEL=chrome : pilote le Google Chrome installé (et non le Chromium de Playwright)
  projects: [{ name: "chromium", use: { ...devices["Desktop Chrome"], channel: process.env.E2E_CHANNEL || undefined } }],
  webServer: {
    command: process.env.CI
      ? `pnpm build && PORT=${PORT} COOKIE_SECURE=false pnpm start`
      : `PORT=${PORT} pnpm dev`,
    url: `http://localhost:${PORT}/login`,
    reuseExistingServer: !process.env.CI,
    timeout: 240_000,
    env: { API_URL: process.env.API_URL ?? "http://localhost:8080", COOKIE_SECURE: "false" },
  },
});
