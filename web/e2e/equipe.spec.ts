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

test("Équipe : créer un encaisseur avec ses immeubles, puis modifier ses affectations", async ({ page }) => {
  await connecterAdmin(page);
  const suffixe = Date.now().toString(36);
  const creation = await page.request.post("/api/proxy/api/v1/immeubles", { data: { nom: `Immeuble Équipe ${suffixe}`, adresse: "1 rue de l'Équipe", ville: "Douala" } });
  expect(creation.status()).toBe(201);
  const immeuble = (await creation.json()) as { id: string };

  await page.getByRole("link", { name: "Équipe" }).click();
  await expect(page.getByRole("heading", { name: "Équipe", level: 1 })).toBeVisible();

  const nom = `Encaisseur E2E ${suffixe}`;
  const tel = telephone();
  await page.getByRole("button", { name: "Nouvel employé" }).click();
  await page.getByLabel("Nom", { exact: true }).fill(nom);
  await page.getByLabel("Téléphone").fill(tel);
  await page.getByRole("button", { name: "Générer un mot de passe" }).click();
  await page.getByLabel("Rechercher un immeuble").fill(`Immeuble Équipe ${suffixe}`);
  await page.getByText(`Immeuble Équipe ${suffixe}`, { exact: true }).click();
  await expect(page.getByText("1 immeuble sélectionné")).toBeVisible();
  await page.getByRole("button", { name: "Créer l'employé" }).click();

  await expect(page.getByTestId("recap-mot-de-passe")).toHaveText(/^\S{12}$/);
  await expect(page.getByTestId("recap-identifiant")).toHaveText(tel);

  const ligne = page.getByRole("row").filter({ hasText: nom });
  await expect(ligne.getByText("Encaisseur", { exact: true })).toBeVisible();
  await expect(ligne.getByText("1 immeuble", { exact: true })).toBeVisible();

  // l'API confirme l'affectation
  const liste = await page.request.get("/api/proxy/api/v1/utilisateurs?roles=ENCAISSEUR&size=100");
  const employe = ((await liste.json()) as { content: { id: string; nom: string }[] }).content.find((u) => u.nom === nom);
  expect(employe).toBeTruthy();
  const aff = await page.request.get(`/api/proxy/api/v1/utilisateurs/${employe!.id}/affectations`);
  expect(((await aff.json()) as { immeubleIds: string[] }).immeubleIds).toEqual([immeuble.id]);

  // retirer l'immeuble : l'employé ne voit plus rien
  await ligne.getByRole("button", { name: "Immeubles" }).click();
  await expect(page.getByText(`Immeubles affectés à ${nom}`)).toBeVisible();
  await page.getByLabel("Rechercher un immeuble").fill(`Immeuble Équipe ${suffixe}`);
  await page.getByText(`Immeuble Équipe ${suffixe}`, { exact: true }).click(); // décoche
  await expect(page.getByText("Sans immeuble, cet employé ne voit rien")).toBeVisible();
  await page.getByRole("button", { name: "Enregistrer", exact: true }).click();
  await expect(ligne.getByText("Aucun", { exact: true })).toBeVisible();
  const apres = await page.request.get(`/api/proxy/api/v1/utilisateurs/${employe!.id}/affectations`);
  expect(((await apres.json()) as { immeubleIds: string[] }).immeubleIds).toEqual([]);
});

test("Équipe : le gestionnaire (accès total) n'a pas de choix d'immeubles et n'apparaît pas avec un nombre", async ({ page }) => {
  await connecterAdmin(page);
  const nom = `Gestionnaire E2E ${Date.now().toString(36)}`;
  await page.goto("/equipe");
  await page.getByRole("button", { name: "Nouvel employé" }).click();
  await page.getByLabel("Rôle").selectOption("GESTIONNAIRE");
  await expect(page.getByText("Immeubles affectés")).toHaveCount(0);
  await page.getByLabel("Nom", { exact: true }).fill(nom);
  await page.getByLabel("Téléphone").fill(telephone());
  await page.getByRole("button", { name: "Générer un mot de passe" }).click();
  await page.getByRole("button", { name: "Créer l'employé" }).click();
  await expect(page.getByTestId("recap-mot-de-passe")).toBeVisible();
  const ligne = page.getByRole("row").filter({ hasText: nom });
  await expect(ligne.getByText("Gestionnaire (accès total)")).toBeVisible();
  await expect(ligne.getByText("Tous", { exact: true })).toBeVisible();
});

test("Équipe : créer un gestionnaire de biens avec ses immeubles", async ({ page }) => {
  await connecterAdmin(page);
  const suffixe = Date.now().toString(36);
  const creation = await page.request.post("/api/proxy/api/v1/immeubles", { data: { nom: `Immeuble Biens ${suffixe}`, adresse: "3 rue des Biens", ville: "Douala" } });
  expect(creation.status()).toBe(201);
  const immeuble = (await creation.json()) as { id: string };

  const nom = `Gestionnaire Biens E2E ${suffixe}`;
  await page.goto("/equipe");
  await page.getByRole("button", { name: "Nouvel employé" }).click();
  await page.getByLabel("Rôle").selectOption("GESTIONNAIRE_BIENS");
  await page.getByLabel("Nom", { exact: true }).fill(nom);
  await page.getByLabel("Téléphone").fill(telephone());
  await page.getByRole("button", { name: "Générer un mot de passe" }).click();
  await page.getByLabel("Rechercher un immeuble").fill(`Immeuble Biens ${suffixe}`);
  await page.getByText(`Immeuble Biens ${suffixe}`, { exact: true }).click();
  await page.getByRole("button", { name: "Créer l'employé" }).click();
  await expect(page.getByTestId("recap-mot-de-passe")).toBeVisible();

  const ligne = page.getByRole("row").filter({ hasText: nom });
  await expect(ligne.getByText("Gestionnaire de biens", { exact: true })).toBeVisible();
  await expect(ligne.getByText("1 immeuble", { exact: true })).toBeVisible();

  const liste = await page.request.get("/api/proxy/api/v1/utilisateurs?roles=GESTIONNAIRE_BIENS&size=100");
  const employe = ((await liste.json()) as { content: { id: string; nom: string }[] }).content.find((u) => u.nom === nom);
  expect(employe).toBeTruthy();
  const aff = await page.request.get(`/api/proxy/api/v1/utilisateurs/${employe!.id}/affectations`);
  expect(((await aff.json()) as { immeubleIds: string[] }).immeubleIds).toEqual([immeuble.id]);
});
