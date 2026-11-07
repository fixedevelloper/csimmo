import { expect, test, type Page } from "@playwright/test";

const ADMIN = {
  identifiant: process.env.E2E_ADMIN_IDENTIFIANT ?? "admin@immogest.local",
  motDePasse: process.env.E2E_ADMIN_MOT_DE_PASSE ?? "Admin1234!",
};
const PNG = Buffer.from(
  "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR4nGNgYGD4DwABBAEAwS2OUAAAAABJRU5ErkJggg==",
  "base64",
);

async function connecterAdmin(page: Page) {
  await page.goto("/login");
  await page.getByLabel("Téléphone ou e-mail").fill(ADMIN.identifiant);
  await page.getByLabel("Mot de passe", { exact: true }).fill(ADMIN.motDePasse);
  await page.getByRole("button", { name: "Se connecter" }).click();
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
}

const telephone = () => `+2376${Math.floor(Math.random() * 1e8).toString().padStart(8, "0")}`;

test("propriétaire → mandats sans chevauchement → immeuble rattaché", async ({ page }) => {
  test.setTimeout(90_000);
  const suffixe = Date.now().toString(36);
  const nom = `Mme Ngono ${suffixe}`;
  await connecterAdmin(page);

  await page.goto("/proprietaires");
  await page.getByRole("link", { name: "Nouveau propriétaire" }).click();
  await page.getByLabel("Nom", { exact: true }).fill(nom);
  await page.getByLabel("Téléphone").fill(telephone());
  await page.getByLabel("Mode de reversement").selectOption("MOBILE_MONEY");
  await page.getByRole("button", { name: "Enregistrer", exact: true }).click();
  await expect(page.getByRole("heading", { name: nom, level: 1 })).toBeVisible();

  // Mandat à 10 % puis un mandat qui le chevauche
  await page.getByLabel("Taux d'honoraires (%)").fill("10");
  await page.getByLabel("Début").fill("2026-01-01");
  await page.getByLabel("Fin (facultative)").fill("2026-12-31");
  await page.getByRole("button", { name: "Ajouter un mandat" }).click();
  await expect(page.getByRole("cell", { name: "10 %" })).toBeVisible();

  await page.getByLabel("Taux d'honoraires (%)").fill("7,5");
  await page.getByLabel("Début").fill("2026-06-01");
  await page.getByRole("button", { name: "Ajouter un mandat" }).click();
  await expect(page.getByRole("alert").filter({ hasText: "déjà un mandat" })).toBeVisible();

  await page.getByLabel("Début").fill("2027-01-01");
  await page.getByRole("button", { name: "Ajouter un mandat" }).click();
  await expect(page.getByRole("cell", { name: "7,5 %" })).toBeVisible();

  // Un immeuble rattaché à ce propriétaire
  await page.goto("/patrimoine/nouveau");
  await page.getByLabel("Nom", { exact: true }).fill(`Immeuble ${suffixe}`);
  await page.getByLabel("Adresse").fill("1 rue des Tests");
  await page.getByLabel("Ville").fill("Douala");
  await page.getByLabel("Propriétaire (facultatif)").selectOption({ label: nom });
  await page.getByRole("button", { name: "Enregistrer", exact: true }).click();
  await expect(page.getByRole("heading", { name: `Immeuble ${suffixe}`, level: 1 })).toBeVisible();
});

test("locataire → CNI → garant → pièce chiffrée → recherche", async ({ page }) => {
  test.setTimeout(90_000);
  const suffixe = Date.now().toString(36);
  const nom = `Paul Mbarga ${suffixe}`;
  const tel = telephone();
  await connecterAdmin(page);

  await page.goto("/locataires/nouveau");
  await page.getByLabel("Nom", { exact: true }).fill(nom);
  await page.getByLabel("Téléphone").fill(tel);
  await page.getByLabel("Numéro de CNI").fill("CNI-E2E-998877");
  await page.getByRole("button", { name: "Enregistrer", exact: true }).click();
  await expect(page.getByRole("heading", { name: nom, level: 1 })).toBeVisible();
  await expect(page.getByTestId("numero-cni")).toHaveText("CNI-E2E-998877");

  // Garant (onglet « Garants et pièces »)
  await page.getByRole("tab", { name: "Garants et pièces" }).click();
  await page.getByRole("button", { name: "Ajouter un garant" }).click();
  await page.getByLabel("Nom", { exact: true }).fill("Marie Mbarga");
  await page.getByLabel("Lien avec le locataire").fill("Mère");
  await page.getByLabel("Téléphone").fill(telephone());
  await page.getByRole("button", { name: "Enregistrer", exact: true }).click();
  await expect(page.getByText("Marie Mbarga")).toBeVisible();

  // Pièce d'identité : envoyée chiffrée, rouverte en clair par l'API
  await page.getByLabel("Type de pièce").selectOption("CNI_RECTO");
  await page.getByLabel("Choisir un fichier").setInputFiles({ name: "cni.png", mimeType: "image/png", buffer: PNG });
  await page.getByRole("button", { name: "Ajouter la pièce" }).click();
  const lien = page.getByRole("link", { name: "Ouvrir" });
  await expect(lien).toBeVisible();
  const reponse = await page.request.get((await lien.getAttribute("href"))!);
  expect(reponse.status()).toBe(200);
  expect(reponse.headers()["content-type"]).toBe("image/png");
  expect((await reponse.body()).equals(PNG)).toBe(true);

  // Recherche par téléphone dans la liste
  await page.goto("/locataires");
  await page.getByLabel("Rechercher (nom, téléphone)").fill(tel.slice(-6));
  await page.getByRole("button", { name: "Rechercher" }).click();
  await expect(page.getByRole("link", { name: nom })).toBeVisible();
  await page.getByLabel("Rechercher (nom, téléphone)").fill("zzz-introuvable");
  await page.getByRole("button", { name: "Rechercher" }).click();
  await expect(page.getByText("Aucun résultat pour ces critères.")).toBeVisible();
});

test("prestataire : création puis archivage", async ({ page }) => {
  test.setTimeout(60_000);
  const nom = `Plomberie ${Date.now().toString(36)}`;
  await connecterAdmin(page);
  await page.goto("/prestataires/nouveau");
  await page.getByLabel("Nom", { exact: true }).fill(nom);
  await page.getByLabel("Métier").fill("Plombier");
  await page.getByLabel("Téléphone").fill(telephone());
  await page.getByRole("button", { name: "Enregistrer", exact: true }).click();
  await expect(page).toHaveURL(/\/prestataires$/);
  await expect(page.getByRole("link", { name: nom })).toBeVisible();

  await page.getByRole("link", { name: nom }).click();
  await page.getByRole("button", { name: "Archiver" }).click();
  await page.getByRole("alertdialog").getByRole("button", { name: "Archiver" }).click();
  await expect(page).toHaveURL(/\/prestataires$/);
  await expect(page.getByRole("link", { name: nom })).toHaveCount(0);
});
