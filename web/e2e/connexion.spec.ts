import { expect, test, type Page } from "@playwright/test";

// Compte créé par le profil dev du backend (AdminDeveloppement).
const ADMIN = {
  identifiant: process.env.E2E_ADMIN_IDENTIFIANT ?? "admin@immogest.local",
  motDePasse: process.env.E2E_ADMIN_MOT_DE_PASSE ?? "Admin1234!",
};

async function seConnecter(page: Page, identifiant: string, motDePasse: string) {
  await page.getByLabel("Téléphone ou e-mail").fill(identifiant);
  await page.getByLabel("Mot de passe").fill(motDePasse);
  await page.getByRole("button", { name: "Se connecter" }).click();
}

test("connexion → tableau de bord vide → déconnexion", async ({ page }) => {
  // Sans session, une page protégée renvoie vers la connexion en mémorisant la destination
  await page.goto("/tableau-de-bord");
  await expect(page).toHaveURL(/\/login\?next=%2Ftableau-de-bord/);

  await seConnecter(page, ADMIN.identifiant, ADMIN.motDePasse);

  await expect(page).toHaveURL(/\/tableau-de-bord$/);
  await expect(page.getByRole("heading", { name: "Tableau de bord", level: 1 })).toBeVisible();
  await expect(page.getByText("Bienvenue, Admin Dev")).toBeVisible();
  // Indicateurs de pilotage réservés aux gestionnaires
  await expect(page.getByTestId("taux-occupation")).toBeVisible();
  await expect(page.getByTestId("arrieres-total")).toBeVisible();
  await expect(page.getByRole("img", { name: /Loyers attendus et encaissés/ })).toBeVisible();

  // Menu latéral complet pour un administrateur
  const menu = page.getByRole("navigation", { name: "Navigation principale" });
  for (const entree of ["Tableau de bord", "Patrimoine", "Locataires", "Baux", "Encaissements", "Relances", "Propriétaires", "Paramètres"]) {
    await expect(menu.getByRole("link", { name: entree })).toBeVisible();
  }

  // Les jetons ne sont jamais accessibles au JavaScript de la page
  const cookies = await page.context().cookies();
  expect(cookies.find((c) => c.name === "immogest_access")?.httpOnly).toBe(true);
  expect(cookies.find((c) => c.name === "immogest_refresh")?.httpOnly).toBe(true);
  expect(await page.evaluate(() => document.cookie)).not.toContain("immogest_");

  await page.getByRole("button", { name: "Mon compte" }).click();
  await page.getByRole("menuitem", { name: "Se déconnecter" }).click();
  await expect(page).toHaveURL(/\/login/);

  // La session est bien terminée : retour forcé vers la connexion
  await page.goto("/tableau-de-bord");
  await expect(page).toHaveURL(/\/login/);
  expect((await page.context().cookies()).filter((c) => c.name.startsWith("immogest_"))).toHaveLength(0);
});

test("un mot de passe incorrect affiche une erreur traduite", async ({ page }) => {
  await page.goto("/login");
  await seConnecter(page, ADMIN.identifiant, "mauvais-mot-de-passe");

  await expect(page.locator("form").getByRole("alert")).toHaveText("Identifiant ou mot de passe incorrect.");
  await expect(page).toHaveURL(/\/login/);
});

test("retour à la page demandée après connexion, et changement de langue", async ({ page }) => {
  await page.goto("/baux");
  await expect(page).toHaveURL(/\/login\?next=%2Fbaux/);
  await seConnecter(page, ADMIN.identifiant, ADMIN.motDePasse);
  await expect(page).toHaveURL(/\/baux$/);
  await expect(page.getByRole("heading", { name: "Baux", level: 1 })).toBeVisible();

  await page.getByRole("button", { name: "EN", exact: true }).click();
  await expect(page.getByRole("heading", { name: "Leases", level: 1 })).toBeVisible();
  await expect(page.getByRole("button", { name: "My account" })).toBeVisible();
});

test("une session expirée côté API renvoie vers la connexion", async ({ page }) => {
  await page.goto("/login");
  await seConnecter(page, ADMIN.identifiant, ADMIN.motDePasse);
  await expect(page).toHaveURL(/\/tableau-de-bord$/);

  // On supprime les deux cookies : le prochain appel API échoue en 401
  await page.context().clearCookies();
  await page.reload();
  await expect(page).toHaveURL(/\/login/);
});
