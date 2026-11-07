import { expect, test, type Page } from "@playwright/test";

const ADMIN = {
  identifiant: process.env.E2E_ADMIN_IDENTIFIANT ?? "admin@immogest.local",
  motDePasse: process.env.E2E_ADMIN_MOT_DE_PASSE ?? "Admin1234!",
};

async function connecterAdmin(page: Page) {
  await page.goto("/login");
  await page.getByLabel("Téléphone ou e-mail").fill(ADMIN.identifiant);
  await page.getByLabel("Mot de passe", { exact: true }).fill(ADMIN.motDePasse);
  await page.getByRole("button", { name: "Se connecter" }).click();
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
}

const telephone = () => `+2376${Math.floor(Math.random() * 1e8).toString().padStart(8, "0")}`;

test("Paramètres > Utilisateurs : création d'un compte avec mot de passe généré, puis mot de passe visible une seule fois", async ({ page }) => {
  await connecterAdmin(page);
  await page.goto("/parametres");
  await page.getByRole("link", { name: /Utilisateurs/ }).click();
  await expect(page.getByRole("heading", { name: "Utilisateurs", level: 1 })).toBeVisible();

  const nom = `Compte E2E ${Date.now().toString(36)}`;
  const tel = telephone();
  await page.getByRole("button", { name: "Nouvel utilisateur" }).click();
  await page.getByLabel("Nom", { exact: true }).fill(nom);
  await page.getByLabel("Téléphone").fill(tel);
  await page.getByLabel("Rôle").selectOption("LOCATAIRE");
  await page.getByRole("button", { name: "Générer un mot de passe" }).click();
  await page.getByRole("button", { name: "Créer le compte" }).click();

  const mdp = page.getByTestId("recap-mot-de-passe");
  await expect(mdp).toHaveText(/^\S{12}$/);
  await expect(page.getByTestId("recap-identifiant")).toHaveText(tel);
  await page.getByRole("button", { name: "Fermer" }).click();
  await expect(mdp).toHaveCount(0);
});

test("fiche locataire : créer l'accès crée le compte et le rattache à la fiche", async ({ page }) => {
  await connecterAdmin(page);
  const tel = telephone();
  const creation = await page.request.post("/api/proxy/api/v1/locataires", { data: { nom: `Locataire Accès ${Date.now().toString(36)}`, telephone: tel } });
  expect(creation.status()).toBe(201);
  const { id } = (await creation.json()) as { id: string };

  await page.goto(`/locataires/${id}`);
  await expect(page.getByText("Accès à l'application")).toBeVisible();
  await page.getByRole("button", { name: "Générer un mot de passe" }).click();
  await page.getByRole("button", { name: "Créer l'accès" }).click();
  await expect(page.getByTestId("recap-mot-de-passe")).toHaveText(/^\S{12}$/);
  await expect(page.getByText("Un compte est rattaché à ce locataire.")).toBeVisible();

  const fiche = await page.request.get(`/api/proxy/api/v1/locataires/${id}`);
  expect(((await fiche.json()) as { utilisateurId?: string }).utilisateurId).toBeTruthy();
});
