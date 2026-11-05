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

test("encaissement espèces → caisse → contre-passation → compte locataire", async ({ page }) => {
  test.setTimeout(120_000);
  const suffixe = Date.now().toString(36);
  const nomLocataire = `Paul Caisse ${suffixe}`;
  await connecterAdmin(page);

  const immeuble = await api(page, "/api/v1/immeubles", { nom: `Résidence Caisse ${suffixe}`, adresse: "1 rue", ville: "Douala" });
  const lot = await api(page, "/api/v1/lots", { immeubleId: immeuble.id, reference: "C1", type: "STUDIO" });
  const locataire = await api(page, "/api/v1/locataires", { nom: nomLocataire, telephone: telephone() });
  const debut = new Date();
  debut.setMonth(debut.getMonth() - 2);
  debut.setDate(1);
  const bail = await api(page, "/api/v1/baux", {
    lotId: lot.id, locataireId: locataire.id, dateDebut: debut.toISOString().slice(0, 10), loyer: 150000, charges: 10000,
    periodicite: "MENSUELLE", jourEcheance: 1,
  });
  await api(page, `/api/v1/baux/${bail.id}/activer`, {});

  await page.goto("/encaissements");
  // la liste des échéances est générée par l'événement d'activation : on la voit dans le compte
  await page.getByLabel("Rechercher un locataire").fill(nomLocataire);
  await page.locator("#p-locataire").selectOption({ label: nomLocataire });
  await expect(page.locator("#p-bail")).toHaveValue(bail.id); // un seul bail : choisi d'office
  await page.getByLabel("Montant (FCFA)").fill("160000");
  await page.getByRole("button", { name: "Enregistrer l'encaissement" }).click();
  await expect(page.getByText("Encaissement enregistré.")).toBeVisible();
  const ligne = page.getByRole("row").filter({ hasText: nomLocataire });
  await expect(ligne).toContainText("Valide");
  await expect(page.getByTestId("total-caisse").or(page.getByText("Caisse du jour (espèces)"))).toBeVisible();

  // la quittance est émise en arrière-plan : visible dans le compte, PDF téléchargeable
  await page.goto(`/locataires/${locataire.id}/compte`);
  const quittance = page.getByRole("row").filter({ hasText: "Quittance" });
  await expect(async () => {
    await page.reload();
    await expect(quittance.first()).toBeVisible({ timeout: 3000 });
  }).toPass({ timeout: 30_000 });
  const href = await quittance.first().getByRole("link", { name: /Télécharger QUI-/ }).getAttribute("href");
  const pdf = await page.request.get(href ?? "");
  expect(pdf.status()).toBe(200);
  expect((await pdf.body()).subarray(0, 4).toString()).toBe("%PDF");
  await page.goto("/encaissements");
  await page.getByLabel("Rechercher un locataire").fill(nomLocataire);
  await page.locator("#p-locataire").selectOption({ label: nomLocataire });

  // mobile money sans référence : refusé côté formulaire
  await page.getByLabel("Montant (FCFA)").fill("1000");
  await page.locator("#p-mode").selectOption("MTN_MOMO");
  await page.getByRole("button", { name: "Enregistrer l'encaissement" }).click();
  await expect(page.getByText("La référence est obligatoire pour ce mode.")).toBeVisible();

  // contre-passation : motif obligatoire
  await ligne.first().getByRole("button", { name: "Contre-passer" }).click();
  await expect(page.getByRole("button", { name: "Confirmer la contre-passation" })).toBeDisabled();
  await page.getByLabel("Motif de la contre-passation").fill(`Erreur ${suffixe}`);
  await page.getByRole("button", { name: "Confirmer la contre-passation" }).click();
  await expect(page.getByText("Paiement contre-passé.")).toBeVisible();
  await expect(page.getByRole("row").filter({ hasText: nomLocataire }).filter({ hasText: "Annulé" })).toBeVisible();
  await expect(page.getByRole("row").filter({ hasText: `Erreur ${suffixe}` })).toBeVisible();

  // compte locataire : arriérés = toutes les échéances échues, aucun crédit
  await page.goto(`/locataires/${locataire.id}/compte`);
  await expect(page.getByRole("heading", { name: "Compte locataire" })).toBeVisible();
  await expect(async () => {
    const texte = (await page.getByTestId("arrieres").textContent()) ?? "";
    expect(texte.replace(/\D/g, "")).not.toBe("0");
  }).toPass({ timeout: 20_000 });
  await expect(page.getByTestId("credit")).toContainText("0");
  // la contre-passation a annulé la quittance (conservée, marquée annulée)
  await expect(page.getByRole("row").filter({ hasText: "Quittance" }).filter({ hasText: "Annulé" }).first()).toBeVisible();
});
