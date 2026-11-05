import { expect, test, type Page } from "@playwright/test";

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

test("paramètres notifications : secrets masqués, test d'un canal, journal", async ({ page }) => {
  test.setTimeout(90_000);
  await connecterAdmin(page);
  await page.goto("/parametres");
  await page.getByRole("link", { name: /Notifications/ }).click();
  await expect(page.getByRole("heading", { name: "Notifications", level: 1 })).toBeVisible();

  // Push : jamais configuré par ce parcours → champs obligatoires manquants, activation refusée
  const push = page.locator("div[data-slot=card]").filter({ hasText: "Firebase Cloud Messaging" });
  await push.getByLabel("Activer Notification push").check();
  await push.getByRole("button", { name: "Enregistrer" }).click();
  await expect(push.getByRole("alert")).toContainText("Renseignez tous les champs obligatoires");

  // WhatsApp : configuré puis désactivé ; le parcours reste rejouable
  const carte = page.locator("div[data-slot=card]").filter({ hasText: "WhatsApp Cloud API" });
  await carte.getByLabel("Activer WhatsApp").check();

  // complété : le secret n'est plus jamais réaffiché
  await carte.getByLabel(/Identifiant du numéro WhatsApp/).fill("123456");
  await carte.getByLabel(/Jeton d'accès/).fill("EAAG-secret-e2e");
  await carte.getByRole("button", { name: "Enregistrer" }).click();
  await expect(page.getByText("Canal enregistré.")).toBeVisible();
  await expect(page.getByText("EAAG-secret-e2e")).toHaveCount(0);
  await expect(carte.getByLabel(/Jeton d'accès/)).toHaveValue("");
  await expect(carte.getByLabel(/Jeton d'accès/)).toHaveAttribute("placeholder", /Renseigné/);

  // test d'un canal (simulé en développement)
  await carte.getByLabel("Numéro de test").fill("+237699112233");
  await carte.getByRole("button", { name: "Envoyer un test" }).click();
  await expect(carte.getByRole("status")).toContainText("Test envoyé avec succès.");

  // désactivation pour ne pas influencer les autres parcours
  await carte.getByLabel("Activer WhatsApp").uncheck();
  await carte.getByRole("button", { name: "Enregistrer" }).click();
  await expect(page.getByText("Canal enregistré.").first()).toBeVisible();

  await expect(page.getByRole("heading", { name: "Journal des envois" })).toBeVisible();
});
