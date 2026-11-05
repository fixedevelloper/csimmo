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

const telephone = () => `+2376${Math.floor(Math.random() * 1e8).toString().padStart(8, "0")}`;

async function creer(page: Page, chemin: string, corps: Record<string, unknown>): Promise<{ id: string }> {
  const r = await page.request.post(`/api/proxy${chemin}`, { data: corps });
  expect(r.status(), await r.text()).toBe(201);
  return (await r.json()) as { id: string };
}

test("bail : assistant en 4 étapes → PDF → activation → chevauchement refusé → résiliation avec préavis", async ({ page }) => {
  test.setTimeout(120_000);
  const suffixe = Date.now().toString(36);
  const nomImmeuble = `Résidence Baux ${suffixe}`;
  const nomLocataire = `Paul Baux ${suffixe}`;
  await connecterAdmin(page);

  const immeuble = await creer(page, "/api/v1/immeubles", { nom: nomImmeuble, adresse: "1 rue du Bail", ville: "Douala" });
  await creer(page, "/api/v1/lots", { immeubleId: immeuble.id, reference: "A1", type: "APPARTEMENT", loyerIndicatif: 150000 });
  const locataire = await creer(page, "/api/v1/locataires", { nom: nomLocataire, telephone: telephone(), numeroCni: "CNI-E2E-1" });
  await creer(page, `/api/v1/locataires/${locataire.id}/garants`, { nom: "Marie Garant", lien: "Mère", telephone: telephone() });

  async function remplirAssistant(debut: string, fin: string) {
    await page.goto("/baux/nouveau");
    await page.getByLabel("Rechercher un lot libre").fill(nomImmeuble);
    await page.getByRole("button", { name: `${nomImmeuble} — A1` }).click();
    await page.getByRole("button", { name: "Suivant" }).click();
    await page.getByLabel("Rechercher un locataire").fill(nomLocataire);
    await page.getByRole("button", { name: new RegExp(nomLocataire) }).click();
    await page.getByLabel(/Marie Garant/).check();
    await page.getByRole("button", { name: "Suivant" }).click();
    await page.getByLabel("Date de début").fill(debut);
    if (fin) await page.getByLabel("Date de fin (facultative)").fill(fin);
    await page.getByLabel("Mois d'avance").fill("2");
    await page.getByRole("button", { name: "Suivant" }).click();
  }

  await remplirAssistant("2026-01-01", "2026-12-31");
  await expect(page.getByText(nomLocataire).first()).toBeVisible();
  await page.getByRole("button", { name: "Enregistrer le brouillon" }).click();
  await expect(page.getByTestId("statut-bail")).toHaveText("Brouillon");
  const premierId = page.url().split("/").pop() ?? "";

  // aperçu PDF servi par l'API
  const urlPdf = await page.getByRole("link", { name: "Aperçu PDF" }).getAttribute("href");
  const pdf = await page.request.get(urlPdf ?? "");
  expect(pdf.status()).toBe(200);
  expect(pdf.headers()["content-type"]).toContain("application/pdf");
  expect((await pdf.body()).subarray(0, 4).toString()).toBe("%PDF");

  await page.getByRole("button", { name: "Activer", exact: true }).click();
  await page.getByRole("alertdialog").getByRole("button", { name: "Activer" }).click();
  await expect(page.getByTestId("statut-bail")).toHaveText("Actif");
  await expect(page.getByText("Exemplaire n° 1")).toBeVisible();

  // échéances générées à l'activation (événement asynchrone) : les 2 mois d'avance soldent les deux premières
  await expect(async () => {
    await page.reload();
    await expect(page.getByTestId("statut-echeance").first()).toHaveText("Payée", { timeout: 3000 });
  }).toPass({ timeout: 30_000 });
  await expect(page.getByTestId("statut-echeance").nth(1)).toHaveText("Payée");
  await expect(page.getByTestId("statut-echeance").nth(2)).toHaveText("En retard");

  // le lot occupé n'est plus proposé par l'assistant ; un second brouillon créé par l'API est refusé à l'activation
  const lotId = (await (await page.request.get(`/api/proxy/api/v1/lots?immeubleId=${immeuble.id}`)).json()).content[0].id as string;
  const second = await creer(page, "/api/v1/baux", {
    lotId, locataireId: locataire.id, dateDebut: "2026-06-01", dateFin: "2027-05-31", loyer: 150000, periodicite: "MENSUELLE", jourEcheance: 5,
  });
  await page.goto(`/baux/${second.id}`);
  await page.getByRole("button", { name: "Activer", exact: true }).click();
  await page.getByRole("alertdialog").getByRole("button", { name: "Activer" }).click();
  await expect(page.getByRole("alert").filter({ hasText: "déjà un bail en vigueur" })).toBeVisible();
  await expect(page.getByTestId("statut-bail")).toHaveText("Brouillon");

  // résiliation : préavis de 3 mois non respecté, puis accepté
  await page.goto(`/baux/${premierId}`);
  await page.getByRole("button", { name: "Résilier", exact: true }).click();
  await page.getByLabel("Date de notification du préavis").fill("2026-03-01");
  await page.getByLabel(/Date de sortie/).fill("2026-04-01");
  await page.getByLabel("Motif de la résiliation").fill("Départ");
  await page.getByRole("button", { name: "Confirmer la résiliation" }).click();
  await expect(page.getByRole("alert").filter({ hasText: "préavis n'est pas respecté" })).toBeVisible();
  await page.getByLabel("Les parties acceptent un préavis réduit").check();
  await page.getByRole("button", { name: "Confirmer la résiliation" }).click();
  await expect(page.getByTestId("statut-bail")).toHaveText("Résilié");
});
