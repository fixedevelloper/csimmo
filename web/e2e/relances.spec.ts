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

async function api(page: Page, chemin: string, corps: Record<string, unknown>): Promise<{ id: string }> {
  const r = await page.request.post(`/api/proxy${chemin}`, { data: corps });
  expect(r.ok(), await r.text()).toBe(true);
  return (await r.json().catch(() => ({}))) as { id: string };
}

test("arriérés → relance manuelle → historique → mise en demeure → paramètres", async ({ page }) => {
  test.setTimeout(120_000);
  const suffixe = Date.now().toString(36);
  const nom = `Paul Impaye ${suffixe}`;
  await connecterAdmin(page);

  const immeuble = await api(page, "/api/v1/immeubles", { nom: `Résidence Relance ${suffixe}`, adresse: "1 rue", ville: "Douala" });
  const lot = await api(page, "/api/v1/lots", { immeubleId: immeuble.id, reference: "I1", type: "STUDIO" });
  const locataire = await api(page, "/api/v1/locataires", { nom, telephone: telephone() });
  const debut = new Date();
  debut.setDate(debut.getDate() - 45);
  const bail = await api(page, "/api/v1/baux", {
    lotId: lot.id, locataireId: locataire.id, dateDebut: debut.toISOString().slice(0, 10), loyer: 150000, charges: 10000,
    periodicite: "MENSUELLE", jourEcheance: 1,
  });
  await api(page, `/api/v1/baux/${bail.id}/activer`, {});

  // les échéances sont créées par l'événement d'activation
  await page.goto("/relances");
  await page.getByLabel("Rechercher un locataire").fill(nom);
  await page.getByRole("button", { name: "Rechercher" }).click();
  const ligne = page.getByRole("row").filter({ hasText: nom }).first();
  await expect(async () => {
    await page.reload();
    await page.getByLabel("Rechercher un locataire").fill(nom);
    await page.getByRole("button", { name: "Rechercher" }).click();
    await expect(ligne).toBeVisible({ timeout: 3000 });
  }).toPass({ timeout: 30_000 });
  await expect(ligne).toContainText("160");
  await expect(ligne).toContainText("45 j");

  // relance manuelle : aucun canal actif en dev par défaut → « aucun canal », mais tracée
  await ligne.getByRole("button", { name: `Relancer ${nom}` }).click();
  await expect(page.getByText("Relance enregistrée.")).toBeVisible();
  await ligne.getByRole("button", { name: "Historique" }).click();
  await expect(page.getByRole("list", { name: "Historique des relances" })).toContainText("relance manuelle");

  // mise en demeure avec confirmation
  await ligne.getByRole("button", { name: `Mise en demeure pour ${nom}` }).click();
  await page.getByRole("alertdialog").getByRole("button", { name: "Mise en demeure" }).click();
  await expect(page.getByText(/Mise en demeure MED-\d{4}-\d{6} émise\./)).toBeVisible();
  const lien = page.getByRole("link", { name: "Télécharger la mise en demeure" }).first();
  const pdf = await page.request.get((await lien.getAttribute("href")) ?? "");
  expect(pdf.status()).toBe(200);
  expect((await pdf.body()).subarray(0, 4).toString()).toBe("%PDF");

  // paramètres (ADMIN) : niveaux invalides refusés, valides enregistrés puis rétablis
  await page.goto("/parametres/relances");
  await page.getByLabel("Niveaux (jours)").fill("abc");
  await page.getByRole("button", { name: "Enregistrer" }).click();
  await expect(page.getByText("Saisissez des nombres entiers séparés par des virgules.")).toBeVisible();
  await page.getByLabel("Niveaux (jours)").fill("-5, 3, 10, 30");
  await page.getByRole("button", { name: "Enregistrer" }).click();
  await expect(page.getByText("Paramètres enregistrés.")).toBeVisible();
});
