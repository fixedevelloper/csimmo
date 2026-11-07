import { expect, test, type Page } from "@playwright/test";

const ADMIN = {
  identifiant: process.env.E2E_ADMIN_IDENTIFIANT ?? "admin@immogest.local",
  motDePasse: process.env.E2E_ADMIN_MOT_DE_PASSE ?? "Admin1234!",
};

// PNG 1×1 valide
const PNG = Buffer.from(
  "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR4nGNgYGD4DwABBAEAwS2OUAAAAABJRU5ErkJggg==",
  "base64",
);

/** Ouvre la confirmation d'archivage puis la valide (dans la boîte de dialogue, pas sur la page). */
async function archiver(page: Page) {
  await page.getByRole("button", { name: "Archiver" }).click();
  await page.getByRole("alertdialog").getByRole("button", { name: "Archiver" }).click();
}

async function connecterAdmin(page: Page) {
  await page.goto("/login");
  await page.getByLabel("Téléphone ou e-mail").fill(ADMIN.identifiant);
  await page.getByLabel("Mot de passe", { exact: true }).fill(ADMIN.motDePasse);
  await page.getByRole("button", { name: "Se connecter" }).click();
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
}

test("immeuble → lot → photo → statut → archivage", async ({ page }) => {
  test.setTimeout(90_000); // parcours long : ~25 actions contre un vrai backend
  const suffixe = Date.now().toString(36);
  const nom = `Résidence E2E ${suffixe}`;
  await connecterAdmin(page);

  // Création de l'immeuble
  await page.goto("/patrimoine");
  await page.getByRole("link", { name: "Nouvel immeuble" }).click();
  await page.getByLabel("Nom", { exact: true }).fill(nom);
  await page.getByLabel("Adresse").fill("12 rue des Palmiers");
  await page.getByLabel("Ville").fill(`Douala${suffixe}`);
  await page.getByLabel("Quartier").fill("Bonanjo");
  await page.getByRole("button", { name: "Enregistrer" }).click();
  await expect(page.getByRole("heading", { name: nom, level: 1 })).toBeVisible();
  await expect(page.getByTestId("resume-lots")).toHaveText("0 libres · 0 occupés · 0 en travaux");

  // Création d'un lot avec équipements et compteur
  await page.getByRole("link", { name: "Nouveau lot" }).click();
  await page.getByLabel("Référence").fill("A101");
  await page.getByLabel("Type", { exact: true }).selectOption("STUDIO");
  await page.getByLabel("Surface (m²)").fill("35,5");
  await page.getByLabel("Loyer indicatif (FCFA)").fill("90000");
  await page.getByLabel("Équipements").fill("Parking\nBalcon");
  await page.getByRole("button", { name: "Ajouter un compteur" }).click();
  await page.getByLabel("Numéro").fill("E-123");
  await page.getByRole("button", { name: "Enregistrer" }).click();

  await expect(page.getByRole("heading", { name: /A101/, level: 1 })).toBeVisible();
  await expect(page.getByTestId("statut-lot")).toHaveText("Libre");
  await expect(page.getByText("Studio")).toBeVisible();
  await expect(page.getByText("Parking", { exact: true })).toBeVisible();
  await expect(page.getByText("Eau E-123")).toBeVisible();

  // Photo : envoyée puis relue par l'API (contrôle d'accès), pas par S3
  await page.getByLabel("Choisir une photo").setInputFiles({ name: "salon.png", mimeType: "image/png", buffer: PNG });
  await page.getByLabel("Légende (facultative)").fill("Salon");
  await page.getByRole("button", { name: "Ajouter une photo" }).click();
  const image = page.getByAltText("Salon");
  await expect(image).toBeVisible();
  await expect.poll(() => image.evaluate((el: HTMLImageElement) => el.complete && el.naturalWidth > 0)).toBe(true);
  expect(await image.getAttribute("src")).toMatch(/^\/api\/proxy\/api\/v1\/fichiers\/[0-9a-f-]{36}$/);

  // Statut manuel
  await page.getByRole("button", { name: "Mettre en travaux" }).click();
  await expect(page.getByTestId("statut-lot")).toHaveText("En travaux");
  await page.getByRole("button", { name: "Marquer libre" }).click();
  await expect(page.getByTestId("statut-lot")).toHaveText("Libre");

  // La liste des immeubles se filtre
  await page.goto("/patrimoine");
  await page.getByLabel("Ville").fill(`Douala${suffixe}`);
  await page.getByRole("button", { name: "Rechercher" }).click();
  await expect(page.getByRole("link", { name: nom })).toBeVisible();
  await page.getByLabel("Ville").fill("Ville qui n'existe pas");
  await page.getByRole("button", { name: "Rechercher" }).click();
  await expect(page.getByText("Aucun immeuble pour ces critères.")).toBeVisible();

  // Archivage : le lot d'abord (l'immeuble le refuse tant qu'il a des lots), puis l'immeuble
  await page.getByLabel("Ville").fill(`Douala${suffixe}`);
  await page.getByRole("button", { name: "Rechercher" }).click();
  await page.getByRole("link", { name: nom }).click();
  await archiver(page);
  await expect(page.getByRole("alert").filter({ hasText: "Archivez" })).toHaveText("Archivez d'abord les lots de cet immeuble.");

  await page.getByRole("link", { name: "A101" }).click();
  await archiver(page);
  await expect(page).toHaveURL(/\/patrimoine\/[0-9a-f-]{36}$/);
  await archiver(page);
  await expect(page).toHaveURL(/\/patrimoine$/);
});

test("un doublon de référence affiche l'erreur traduite", async ({ page }) => {
  const suffixe = Date.now().toString(36);
  await connecterAdmin(page);
  await page.goto("/patrimoine/nouveau");
  await page.getByLabel("Nom", { exact: true }).fill(`Doublons ${suffixe}`);
  await page.getByLabel("Adresse").fill("1 rue X");
  await page.getByLabel("Ville").fill("Douala");
  await page.getByRole("button", { name: "Enregistrer" }).click();
  await expect(page).toHaveURL(/\/patrimoine\/[0-9a-f-]{36}$/);
  const fiche = page.url();

  for (let fois = 0; fois < 2; fois++) {
    await page.goto(fiche);
    await page.getByRole("link", { name: "Nouveau lot" }).click();
    await page.getByLabel("Référence").fill("D1");
    await page.getByRole("button", { name: "Enregistrer" }).click();
    if (fois === 0) await expect(page).toHaveURL(/\/patrimoine\/lots\/[0-9a-f-]{36}$/);
  }
  await expect(page.locator("form").getByRole("alert")).toHaveText("Un lot de cet immeuble porte déjà cette référence.");
});
