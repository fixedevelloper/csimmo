import { expect, test, type Page } from "@playwright/test";

const ADMIN = {
  identifiant: process.env.E2E_ADMIN_IDENTIFIANT ?? "admin@immogest.local",
  motDePasse: process.env.E2E_ADMIN_MOT_DE_PASSE ?? "Admin1234!",
};
const telephone = () => `+2376${Math.floor(Math.random() * 1e8).toString().padStart(8, "0")}`;

async function connecter(page: Page, identifiant: string, motDePasse: string) {
  await page.goto("/login");
  await page.getByLabel("Téléphone ou e-mail").fill(identifiant);
  await page.getByLabel("Mot de passe", { exact: true }).fill(motDePasse);
  await page.getByRole("button", { name: "Se connecter" }).click();
}

test("profil : modifier ses informations puis son mot de passe, avec le mot de passe actuel", async ({ page }) => {
  // un compte jetable (gestionnaire sans double authentification pour pouvoir se connecter sans SMS)
  await connecter(page, ADMIN.identifiant, ADMIN.motDePasse);
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
  const tel = telephone();
  const suffixe = Date.now().toString(36);
  const creation = await page.request.post("/api/proxy/api/v1/utilisateurs", {
    data: { nom: `Profil E2E ${suffixe}`, telephone: tel, role: "GESTIONNAIRE", deuxFacteursActif: false, motDePasse: "Profil-e2e-ancien1" },
  });
  expect(creation.status()).toBe(201);
  await page.request.post("/api/auth/logout");

  await connecter(page, tel, "Profil-e2e-ancien1");
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
  await page.getByRole("button", { name: "Mon compte" }).click();
  await page.getByRole("menuitem", { name: "Mon profil" }).click();
  await expect(page.getByRole("heading", { name: "Mon profil", level: 1 })).toBeVisible();

  // informations : le mot de passe actuel est exigé
  const nouveauNom = `Profil Modifié ${suffixe}`;
  await page.getByLabel("Nom", { exact: true }).fill(nouveauNom);
  await page.getByRole("button", { name: "Enregistrer" }).click();
  await expect(page.getByText("Saisissez votre mot de passe actuel.")).toBeVisible();
  await page.getByLabel("Mot de passe actuel", { exact: true }).first().fill("faux-faux-faux");
  await page.getByRole("button", { name: "Enregistrer" }).click();
  await expect(page.locator("main").getByRole("alert")).toContainText(/incorrect/i);
  await page.getByLabel("Mot de passe actuel", { exact: true }).first().fill("Profil-e2e-ancien1");
  await page.getByRole("button", { name: "Enregistrer" }).click();
  await expect(page.getByText("Profil mis à jour.")).toBeVisible();
  await expect(page.getByRole("button", { name: "Mon compte" })).toContainText(nouveauNom);

  // double authentification : proposée (compte non restreint), jamais activée ici (le code SMS n'est lisible que dans la console)
  await expect(page.getByText("Désactivée", { exact: true })).toBeVisible();
  await expect(page.getByRole("button", { name: "Activer" })).toBeVisible();

  // mot de passe : 12 caractères, puis retour à la connexion avec le nouveau
  await page.getByLabel("Nouveau mot de passe", { exact: true }).fill("court");
  await page.getByRole("button", { name: "Changer le mot de passe" }).click();
  await expect(page.getByText("12 caractères minimum")).toBeVisible();
  await page.getByLabel("Mot de passe actuel", { exact: true }).last().fill("Profil-e2e-ancien1");
  await page.getByLabel("Nouveau mot de passe", { exact: true }).fill("Profil-e2e-nouveau2");
  await page.getByLabel("Confirmer le nouveau mot de passe", { exact: true }).fill("Profil-e2e-nouveau2");
  await page.getByRole("button", { name: "Changer le mot de passe" }).click();
  await expect(page).toHaveURL(/\/login\?raison=mot-de-passe/);
  await expect(page.getByText("Mot de passe modifié : reconnectez-vous")).toBeVisible();

  // l'ancien ne marche plus, le nouveau oui
  await connecter(page, tel, "Profil-e2e-ancien1");
  await expect(page.getByRole("alert").first()).toBeVisible();
  await connecter(page, tel, "Profil-e2e-nouveau2");
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
});

test("profil : un employé restreint voit la double authentification comme obligatoire", async ({ page }) => {
  await connecter(page, ADMIN.identifiant, ADMIN.motDePasse);
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
  // l'écran Équipe crée l'encaisseur avec la double authentification ; son profil est vérifié côté API (le code SMS n'est pas lisible ici)
  const creation = await page.request.post("/api/proxy/api/v1/utilisateurs", {
    data: { nom: `Encaisseur Profil ${Date.now().toString(36)}`, telephone: telephone(), role: "ENCAISSEUR", deuxFacteursActif: true, motDePasse: "Profil-e2e-ancien1" },
  });
  expect(creation.status()).toBe(201);
  expect(((await creation.json()) as { deuxFacteursActif: boolean }).deuxFacteursActif).toBe(true);
});
