import { defineConfig, devices } from "@playwright/test";

/**
 * Portail éditeur (license-web) dans un vrai navigateur, contre un serveur de licences réel. Rien n'est démarré par ce fichier :
 *   serveur de licences (ex. :8091) + portail (ex. :3200) à lancer avant ; EDITEUR_URL, LICENCE_SERVEUR_URL, CLE_PUBLIQUE_FICHIER, E2E_CHANNEL=chrome.
 */
export default defineConfig({
  testDir: "./e2e-editeur",
  fullyParallel: false,
  workers: 1,
  timeout: 60_000,
  expect: { timeout: 15_000 },
  reporter: "list",
  use: { baseURL: process.env.EDITEUR_URL ?? "http://localhost:3200", locale: "fr-FR", trace: "retain-on-failure", acceptDownloads: true },
  projects: [{ name: "chromium", use: { ...devices["Desktop Chrome"], channel: process.env.E2E_CHANNEL || undefined } }],
});
