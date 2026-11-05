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

test("tableau de bord : un impayé et un bail qui se termine apparaissent dans les indicateurs", async ({ page }) => {
  test.setTimeout(90_000);
  const suffixe = Date.now().toString(36);
  const nomLocataire = `Paul Pilotage ${suffixe}`;
  await connecterAdmin(page);

  const avant = Number(((await page.getByTestId("arrieres-total").textContent()) ?? "0").replace(/\D/g, ""));
  const immeuble = await api(page, "/api/v1/immeubles", { nom: `Résidence Pilotage ${suffixe}`, adresse: "1 rue", ville: "Douala" });
  const lot = await api(page, "/api/v1/lots", { immeubleId: immeuble.id, reference: "P1", type: "STUDIO" });
  const locataire = await api(page, "/api/v1/locataires", { nom: nomLocataire, telephone: telephone() });
  const debut = new Date();
  debut.setDate(debut.getDate() - 40);
  const fin = new Date();
  fin.setDate(fin.getDate() + 20);
  const bail = await api(page, "/api/v1/baux", {
    lotId: lot.id, locataireId: locataire.id, dateDebut: debut.toISOString().slice(0, 10), dateFin: fin.toISOString().slice(0, 10),
    loyer: 90000000, charges: 0, periodicite: "MENSUELLE", jourEcheance: 1, // loyer démesuré : en tête des listes malgré les arriérés d'autres parcours
  });
  await api(page, `/api/v1/baux/${bail.id}/activer`, {});

  await expect(async () => {
    await page.reload();
    const apres = Number(((await page.getByTestId("arrieres-total").textContent({ timeout: 3000 })) ?? "0").replace(/\D/g, ""));
    expect(apres).toBeGreaterThanOrEqual(avant + 90000000);
  }).toPass({ timeout: 30_000 });

  // le locataire figure dans les arriérés, avec un lien vers son compte
  await expect(page.getByRole("link", { name: nomLocataire })).toBeVisible();
  // le bail à échéance sous 60 jours est listé
  await expect(page.getByText(`Résidence Pilotage ${suffixe} — P1`)).toBeVisible();
  // le graphique existe (Recharts) et son tableau accessible porte les 12 mois
  await expect(page.getByRole("img", { name: /Loyers attendus et encaissés/ })).toBeVisible();
  await expect(page.locator("figure table tbody tr")).toHaveCount(12);

  await page.getByRole("link", { name: nomLocataire }).click();
  await expect(page.getByRole("heading", { name: "Compte locataire" })).toBeVisible();
});
