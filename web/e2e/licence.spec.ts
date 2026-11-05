import { expect, test, type Page } from "@playwright/test";

// Compte et licence de développement créés au démarrage du backend (profil dev, installation-auto).
const ADMIN = {
  identifiant: process.env.E2E_ADMIN_IDENTIFIANT ?? "admin@immogest.local",
  motDePasse: process.env.E2E_ADMIN_MOT_DE_PASSE ?? "Admin1234!",
};

async function connecterAdmin(page: Page) {
  await page.goto("/login");
  await page.getByLabel("Téléphone ou e-mail").fill(ADMIN.identifiant);
  await page.getByLabel("Mot de passe").fill(ADMIN.motDePasse);
  await page.getByRole("button", { name: "Se connecter" }).click();
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
}

test("Paramètres > Licence affiche l'état de la licence et l'empreinte du serveur", async ({ page }) => {
  await connecterAdmin(page);

  await page.getByRole("navigation", { name: "Navigation principale" }).getByRole("link", { name: "Paramètres" }).click();
  await page.getByRole("link", { name: /Licence/ }).click();

  await expect(page).toHaveURL(/\/parametres\/licence$/);
  await expect(page.getByRole("heading", { name: "Licence", level: 1 })).toBeVisible();
  await expect(page.getByTestId("etat-licence")).toHaveText("Valide");
  await expect(page.getByText("Développement", { exact: true })).toBeVisible();
  await expect(page.getByText("100 lots")).toBeVisible();
  await expect(page.getByTestId("empreinte")).toHaveText(/^[0-9a-f]{64}$/);

  // Licence valide et loin de l'expiration : aucun bandeau d'alerte
  await expect(page.getByTestId("bandeau-licence")).toHaveCount(0);
});

test("importer un fichier qui n'est pas une licence affiche une erreur traduite", async ({ page }) => {
  await connecterAdmin(page);
  await page.goto("/parametres/licence");

  await page.getByLabel("Contenu de la licence").fill("ceci n'est pas une licence");
  await page.getByRole("button", { name: "Importer la licence" }).click();

  await expect(page.getByText("Ce fichier n'est pas une licence ImmoGest lisible.")).toBeVisible();
  await expect(page.getByTestId("etat-licence")).toHaveText("Valide");
});

test("une licence falsifiée est refusée par le serveur", async ({ page }) => {
  await connecterAdmin(page);
  await page.goto("/parametres/licence");

  const falsifiee = JSON.stringify({
    format: 1,
    licence: {
      licenseId: "L-FAUX", client: "Faussaire", palierLots: 5000, dateDebut: "2026-01-01", dateExpiration: "2099-12-31",
      empreinteServeur: "ab".repeat(32), fonctionnalites: [],
    },
    signature: "AAAA",
  });
  await page.getByLabel("Contenu de la licence").fill(falsifiee);
  await page.getByRole("button", { name: "Importer la licence" }).click();

  await expect(page.getByText(/signature de la licence est invalide/)).toBeVisible();
  await expect(page.getByText("Faussaire", { exact: true })).toHaveCount(0); // jamais affiché comme client
  await expect(page.getByTestId("etat-licence")).toHaveText("Valide");
});

test("l'assistant d'installation n'est plus accessible une fois l'application installée", async ({ page }) => {
  await page.goto("/installation");
  await expect(page).toHaveURL(/\/login/);
});
