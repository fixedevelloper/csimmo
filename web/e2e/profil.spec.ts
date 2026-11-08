import { expect, test, type Page } from "@playwright/test";

import { codeTotp } from "./totp";

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
  // un compte jetable (gestionnaire sans double authentification : elle est facultative et désactivée par défaut)
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

  // double authentification : proposée, désactivée par défaut (l'activation par application est couverte par le test suivant)
  await expect(page.getByText("Désactivée", { exact: true })).toBeVisible();
  await expect(page.getByRole("button", { name: "Configurer" })).toBeVisible();

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

test("double authentification par application : activation depuis le profil, puis connexion avec le code", async ({ page }) => {
  await connecter(page, ADMIN.identifiant, ADMIN.motDePasse);
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
  const tel = telephone();
  const creation = await page.request.post("/api/proxy/api/v1/utilisateurs", {
    data: { nom: `Totp E2E ${Date.now().toString(36)}`, telephone: tel, role: "GESTIONNAIRE", deuxFacteursActif: false, motDePasse: "Totp-e2e-ancien1" },
  });
  expect(creation.status()).toBe(201);
  expect(((await creation.json()) as { deuxFacteursActif: boolean }).deuxFacteursActif).toBe(false);
  await page.request.post("/api/auth/logout");

  // connexion directe (pas de second facteur par défaut), puis activation depuis le profil : mot de passe, clé, premier code
  await connecter(page, tel, "Totp-e2e-ancien1");
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
  await page.getByRole("button", { name: "Mon compte" }).click();
  await page.getByRole("menuitem", { name: "Mon profil" }).click();
  await page.getByRole("button", { name: "Configurer" }).click();
  await page.locator("#df-mdp").fill("Totp-e2e-ancien1");
  await page.getByRole("button", { name: "Continuer" }).click();
  const cle = (await page.getByTestId("cle-application").textContent()) ?? "";
  expect(cle.replace(/\s+/g, "")).toMatch(/^[A-Z2-7]{32}$/);
  await page.locator("#df-code").fill("000000");
  await page.getByRole("button", { name: "Confirmer et activer" }).click();
  await expect(page.locator("main").getByRole("alert")).toBeVisible(); // code faux refusé
  await page.locator("#df-code").fill(codeTotp(cle));
  await page.getByRole("button", { name: "Confirmer et activer" }).click();
  await expect(page.getByText("Double authentification activée.")).toBeVisible();
  await expect(page.getByText("Activée", { exact: true })).toBeVisible();

  // nouvelle connexion : le mot de passe seul ne suffit plus ; le code (pas suivant : un pas déjà accepté ne rejoue pas) ouvre la session
  await page.request.post("/api/auth/logout");
  await connecter(page, tel, "Totp-e2e-ancien1");
  await expect(page).toHaveURL(/\/login/);
  await page.getByLabel("Code de l'application").fill("000000");
  await page.getByRole("button", { name: "Vérifier" }).click();
  await expect(page.getByRole("alert").first()).toBeVisible();
  await page.getByLabel("Code de l'application").fill(codeTotp(cle, 1));
  await page.getByRole("button", { name: "Vérifier" }).click();
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
});

test("double authentification : première connexion avec enrôlement (compte créé avec l'option activée)", async ({ page }) => {
  await connecter(page, ADMIN.identifiant, ADMIN.motDePasse);
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
  const tel = telephone();
  const creation = await page.request.post("/api/proxy/api/v1/utilisateurs", {
    data: { nom: `Enrolement E2E ${Date.now().toString(36)}`, telephone: tel, role: "ENCAISSEUR", deuxFacteursActif: true, motDePasse: "Totp-e2e-ancien1" },
  });
  expect(creation.status()).toBe(201);
  await page.request.post("/api/auth/logout");

  await connecter(page, tel, "Totp-e2e-ancien1");
  await expect(page.getByText("Configurez votre application d'authentification")).toBeVisible();
  const cle = (await page.getByTestId("cle-application").textContent()) ?? "";
  await page.getByLabel("Code de l'application").fill(codeTotp(cle));
  await page.getByRole("button", { name: "Confirmer et se connecter" }).click();
  await expect(page).not.toHaveURL(/\/login/);
});
