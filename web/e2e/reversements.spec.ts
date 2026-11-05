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

test("reversement : calcul du mois, dépense déduite, mois en cours non validable, aperçu du relevé", async ({ page }) => {
  test.setTimeout(120_000);
  const suffixe = Date.now().toString(36);
  const nomProprietaire = `Mme Mandante ${suffixe}`;
  await connecterAdmin(page);

  const proprietaire = await api(page, "/api/v1/proprietaires-tiers", { nom: nomProprietaire, telephone: telephone() });
  await api(page, `/api/v1/proprietaires-tiers/${proprietaire.id}/mandats`, { tauxHonorairesBp: 1000, dateDebut: "2020-01-01" });
  const immeuble = await api(page, "/api/v1/immeubles", { nom: `Résidence Mandat ${suffixe}`, adresse: "1 rue", ville: "Douala", proprietaireTiersId: proprietaire.id });
  const lot = await api(page, "/api/v1/lots", { immeubleId: immeuble.id, reference: "M1", type: "STUDIO" });
  const locataire = await api(page, "/api/v1/locataires", { nom: `Paul Locataire ${suffixe}`, telephone: telephone() });
  const debut = new Date();
  debut.setDate(debut.getDate() - 20);
  const bail = await api(page, "/api/v1/baux", {
    lotId: lot.id, locataireId: locataire.id, dateDebut: debut.toISOString().slice(0, 10), loyer: 200000, charges: 0, periodicite: "MENSUELLE", jourEcheance: 1,
  });
  await api(page, `/api/v1/baux/${bail.id}/activer`, {});
  await expect(async () => {
    const r = await page.request.get(`/api/proxy/api/v1/echeances?bailId=${bail.id}`);
    expect(((await r.json()) as { totalElements: number }).totalElements).toBeGreaterThan(0);
  }).toPass({ timeout: 20_000 });
  await api(page, "/api/v1/paiements", { bailId: bail.id, montant: 200000, mode: "VIREMENT", reference: `V-${suffixe}` });

  // calcul du mois courant, filtré sur ce propriétaire
  await page.goto(`/reversements?proprietaire=${proprietaire.id}`);
  const mois = new Date();
  // Le champ est rempli par React avec le mois précédent à l'hydratation : un fill() trop tôt est écrasé et le calcul porte sur le mauvais mois
  // (échec aléatoire selon la vitesse de la machine). On attend la page, puis on réessaie jusqu'à ce que la valeur tienne.
  const valeurMois = `${mois.getFullYear()}-${String(mois.getMonth() + 1).padStart(2, "0")}`;
  const champMois = page.getByLabel("Mois", { exact: true }).first();
  await page.waitForLoadState("networkidle");
  await expect(async () => {
    await champMois.fill(valeurMois);
    await expect(champMois).toHaveValue(valeurMois, { timeout: 1000 });
  }).toPass({ timeout: 15_000 });
  await page.getByRole("button", { name: "Calculer le mois" }).click();
  const ligne = page.getByRole("row").filter({ hasText: nomProprietaire });
  await expect(ligne).toBeVisible();
  await expect(ligne.getByTestId("net-reversement")).toContainText("180"); // 200 000 − 10 % d'honoraires
  await expect(ligne.getByTestId("statut-reversement")).toHaveText("Calculé");

  // une dépense imputée à l'immeuble réduit le net après nouveau calcul
  await page.getByLabel("Rechercher un immeuble").fill(`Résidence Mandat ${suffixe}`);
  await page.locator("#d-immeuble").selectOption({ label: `Résidence Mandat ${suffixe}` });
  await page.locator("#d-date").fill(mois.toISOString().slice(0, 10));
  await page.locator("#d-nature").fill("Réparation toiture");
  await page.locator("#d-montant").fill("30000");
  await page.getByRole("button", { name: "Enregistrer la dépense" }).click();
  await expect(page.getByText("Dépense enregistrée.")).toBeVisible();
  await page.getByRole("button", { name: "Calculer le mois" }).click();
  await expect(ligne.getByTestId("net-reversement")).toContainText("150"); // 180 000 − 30 000

  // aperçu PDF provisoire
  const href = await ligne.getByRole("link", { name: "Aperçu" }).getAttribute("href");
  const pdf = await page.request.get(href ?? "");
  expect(pdf.status()).toBe(200);
  expect((await pdf.body()).subarray(0, 4).toString()).toBe("%PDF");

  // le mois n'est pas terminé : validation refusée avec un message clair, après confirmation
  await ligne.getByRole("button", { name: `Valider le reversement de ${nomProprietaire}` }).click();
  await page.getByRole("alertdialog").getByRole("button", { name: "Valider" }).click();
  await expect(page.getByText("Le mois n'est pas terminé : validez à partir du mois suivant.")).toBeVisible();
  await expect(ligne.getByTestId("statut-reversement")).toHaveText("Calculé");

  // la dépense peut être annulée (mois ouvert), motif obligatoire
  await page.getByRole("button", { name: "Annuler la dépense Réparation toiture" }).click();
  await expect(page.getByRole("button", { name: "Confirmer l'annulation" })).toBeDisabled();
  await page.getByLabel("Motif de l'annulation").fill("Facture doublon");
  await page.getByRole("button", { name: "Confirmer l'annulation" }).click();
  await expect(page.getByText("Dépense annulée.")).toBeVisible();
});
