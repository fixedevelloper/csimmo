import { createPublicKey, verify } from "node:crypto";
import { readFileSync } from "node:fs";

import { expect, test, type Page } from "@playwright/test";

const ADMIN = { email: process.env.EDITEUR_EMAIL ?? "editeur@immogest.local", motDePasse: process.env.EDITEUR_MOT_DE_PASSE ?? "Editeur-Local-2026!" };
const SERVEUR = process.env.LICENCE_SERVEUR_URL ?? "http://localhost:8091";
const CLE_PUBLIQUE = process.env.CLE_PUBLIQUE_FICHIER ?? "../deploy/generated/cles-dev/licence-public.key";
const CAPTURES = process.env.E2E_CAPTURES ?? "test-results/captures-editeur";

const hex64 = () => Array.from({ length: 64 }, () => "0123456789abcdef"[Math.floor(Math.random() * 16)]).join("");
const aujourdhui = () => new Date().toISOString().slice(0, 10);
const dans = (jours: number) => new Date(Date.now() + jours * 86_400_000).toISOString().slice(0, 10);

/** JSON canonique de CLAUDE.md §6 licence n°7 : clés triées, sans espaces. Réimplémenté ici pour vérifier la signature SANS le code Java. */
function canonique(v: unknown): string {
  if (Array.isArray(v)) return `[${v.map(canonique).join(",")}]`;
  if (v && typeof v === "object") return `{${Object.keys(v).sort().map((k) => `${JSON.stringify(k)}:${canonique((v as Record<string, unknown>)[k])}`).join(",")}}`;
  return JSON.stringify(v);
}

function signatureValide(contenu: string): boolean {
  const f = JSON.parse(contenu) as { format: number; licence: unknown; signature: string };
  const cle = createPublicKey({ key: Buffer.from(readFileSync(CLE_PUBLIQUE, "utf8").trim(), "base64"), format: "der", type: "spki" });
  return verify(null, Buffer.from(canonique({ format: f.format, licence: f.licence })), cle, Buffer.from(f.signature, "base64url"));
}

async function seConnecter(page: Page) {
  await page.goto("/login");
  await page.getByLabel("Adresse e-mail").fill(ADMIN.email);
  await page.getByLabel("Mot de passe", { exact: true }).fill(ADMIN.motDePasse);
  await page.getByRole("button", { name: "Se connecter" }).click();
  await expect(page.getByRole("heading", { name: "Tableau de bord", level: 1 })).toBeVisible();
}

async function telechargement(page: Page, action: () => Promise<void>): Promise<string> {
  const attente = page.waitForEvent("download");
  await action();
  const d = await attente;
  expect(d.suggestedFilename()).toMatch(/^licence-[0-9a-f-]{36}\.lic$/);
  return readFileSync((await d.path())!, "utf8");
}

test.describe.configure({ mode: "serial" });

const suffixe = Date.now().toString(36);
const nomClient = `Agence Chrome ${suffixe}`;
const empreinte = hex64();
const empreinte2 = hex64();
let idLicence = "";

test("accès protégé, mauvais mot de passe refusé, connexion réussie, aucun jeton lisible par la page", async ({ page, context }) => {
  await page.goto("/licences");
  await expect(page).toHaveURL(/\/login$/);
  const entetes = (await page.goto("/login"))!.headers();
  expect(entetes["content-security-policy"]).toContain("frame-ancestors 'none'");
  expect(entetes["x-frame-options"]).toBe("DENY");

  await page.getByLabel("Adresse e-mail").fill(ADMIN.email);
  await page.getByLabel("Mot de passe", { exact: true }).fill("mauvais-mot-de-passe");
  await page.getByRole("button", { name: "Se connecter" }).click();
  await expect(page.locator('p[role="alert"]')).toContainText("Identifiants invalides");
  await expect(page).toHaveURL(/\/login$/);
  await page.screenshot({ path: `${CAPTURES}/01-login-refuse.png` });

  await seConnecter(page);
  const cookies = await context.cookies();
  const jeton = cookies.find((c) => c.name === "ls_jeton");
  expect(jeton?.httpOnly).toBe(true);
  expect(jeton?.sameSite).toBe("Strict");
  expect(await page.evaluate(() => document.cookie)).not.toContain("ls_jeton");
  await page.screenshot({ path: `${CAPTURES}/02-tableau-de-bord.png`, fullPage: true });
});

test("client créé puis retrouvé par la recherche", async ({ page }) => {
  await seConnecter(page);
  await page.goto("/clients");
  await page.getByLabel("Nom *").fill(nomClient);
  await page.getByLabel("E-mail").first().fill("contact@agence-chrome.cm");
  await page.getByLabel("Pays").fill("CM");
  await page.getByRole("button", { name: "Ajouter le client" }).click();
  await page.getByLabel("Rechercher un client").fill(nomClient);
  await expect(page.getByRole("link", { name: nomClient })).toBeVisible();
  await page.screenshot({ path: `${CAPTURES}/03-client.png`, fullPage: true });
});

test("émission : fichier .lic téléchargé, signature vérifiée hors Java, validations côté navigateur", async ({ page, request }) => {
  await seConnecter(page);
  await page.goto("/licences/nouvelle");
  await page.getByLabel("Client *").selectOption({ label: nomClient });
  await page.getByLabel("Expiration *").fill(dans(10));

  // empreinte invalide : refus immédiat dans le navigateur, rien n'est envoyé
  await page.getByLabel("Empreinte du serveur *").fill("pas-une-empreinte");
  await page.getByRole("button", { name: "Émettre la licence" }).click();
  await expect(page.locator('p[role="alert"]')).toContainText("64 caractères hexadécimaux");
  await page.screenshot({ path: `${CAPTURES}/04-emission-refusee.png` });

  await page.getByLabel("Palier (nombre de lots) *").fill("20");
  await page.getByLabel("Empreinte du serveur *").fill(empreinte.toUpperCase()); // majuscules acceptées, normalisées en minuscules
  await page.getByLabel("Fonctionnalités").fill("PAIEMENT_EN_LIGNE, BASE");
  const lic = await telechargement(page, () => page.getByRole("button", { name: "Émettre la licence" }).click());
  await expect(page.getByRole("heading", { level: 1 })).toContainText(nomClient);
  idLicence = page.url().split("/").pop()!;

  const fichier = JSON.parse(lic) as { format: number; licence: { licenseId: string; client: string; palierLots: number; empreinteServeur: string; fonctionnalites: string[]; dateExpiration: string }; signature: string };
  expect(fichier.format).toBe(1);
  expect(fichier.licence).toMatchObject({ licenseId: idLicence, client: nomClient, palierLots: 20, empreinteServeur: empreinte, fonctionnalites: ["BASE", "PAIEMENT_EN_LIGNE"], dateExpiration: dans(10) });
  expect(signatureValide(lic), "la signature Ed25519 du fichier téléchargé doit être valide").toBe(true);
  // un fichier retouché d'un seul chiffre n'est plus valide
  const retouche = lic.replace(/"palierLots"\s*:\s*20/, '"palierLots" : 2000');
  expect(retouche, "la retouche doit réellement changer le fichier").not.toBe(lic);
  expect(signatureValide(retouche)).toBe(false);

  const v = await request.post(`${SERVEUR}/api/v1/verification`, { data: { licenseId: idLicence, empreinteServeur: empreinte, versionApplication: "chrome" } });
  expect(await v.json()).toEqual({ statut: "VALIDE", licenceRenouvelee: null });
  await page.screenshot({ path: `${CAPTURES}/05-fiche-licence.png`, fullPage: true });
});

test("alertes d'expiration : la licence à 10 jours est signalée sur le tableau de bord et dans la liste", async ({ page }) => {
  await seConnecter(page);
  await expect(page.getByRole("link", { name: nomClient })).toBeVisible();
  await expect(page.getByText("Expire dans 30 jours ou moins").first()).toBeVisible();
  await page.goto("/licences");
  await page.getByLabel("Expirées ou expirant sous 30 jours").check();
  await expect(page.getByRole("link", { name: nomClient })).toBeVisible();
  await page.screenshot({ path: `${CAPTURES}/06-alertes.png`, fullPage: true });
});

test("renouvellement : nouveau fichier signé, remis une seule fois à l'installation, historique complet", async ({ page, request }) => {
  await seConnecter(page);
  await page.goto(`/licences/${idLicence}`);
  const formulaire = page.getByRole("form", { name: "Renouveler" });
  await formulaire.getByLabel("Nouvelle expiration *").fill(dans(400));
  await formulaire.getByLabel("Nouveau palier").fill("50");
  const lic = await telechargement(page, () => formulaire.getByRole("button", { name: "Renouveler" }).click());
  await expect(page.getByRole("status")).toContainText("Licence renouvelée");
  expect(signatureValide(lic)).toBe(true);
  expect(JSON.parse(lic).licence).toMatchObject({ licenseId: idLicence, palierLots: 50, dateExpiration: dans(400) }); // même identifiant : l'installation y reconnaît son renouvellement
  await expect(page.getByText(/RENOUVELLEMENT — 50 lots/)).toBeVisible();

  const premier = await (await request.post(`${SERVEUR}/api/v1/verification`, { data: { licenseId: idLicence, empreinteServeur: empreinte, versionApplication: "chrome" } })).json();
  expect(premier.statut).toBe("VALIDE");
  expect(signatureValide(premier.licenceRenouvelee)).toBe(true); // remis à l'installation lors de sa vérification
  const second = await (await request.post(`${SERVEUR}/api/v1/verification`, { data: { licenseId: idLicence, empreinteServeur: empreinte, versionApplication: "chrome" } })).json();
  expect(second.licenceRenouvelee).toBeNull(); // une seule fois
  await page.screenshot({ path: `${CAPTURES}/07-renouvellement.png`, fullPage: true });
});

test("transfert vers un autre serveur : empreinte invalide refusée, ancien serveur révoqué, nouveau reconnu", async ({ page, request }) => {
  await seConnecter(page);
  await page.goto(`/licences/${idLicence}`);
  const formulaire = page.getByRole("form", { name: "Transférer" });
  await formulaire.getByLabel("Nouvelle empreinte *").fill("abc");
  await formulaire.getByRole("button", { name: "Transférer" }).click();
  await expect(page.locator('p[role="alert"]')).toContainText("64 caractères hexadécimaux");

  await formulaire.getByLabel("Nouvelle empreinte *").fill(empreinte2);
  const lic = await telechargement(page, () => formulaire.getByRole("button", { name: "Transférer" }).click());
  expect(JSON.parse(lic).licence.empreinteServeur).toBe(empreinte2);
  expect(signatureValide(lic)).toBe(true);
  await expect(page.getByText("Transferts effectués")).toBeVisible();

  expect((await (await request.post(`${SERVEUR}/api/v1/verification`, { data: { licenseId: idLicence, empreinteServeur: empreinte, versionApplication: "c" } })).json()).statut).toBe("REVOQUEE");
  expect((await (await request.post(`${SERVEUR}/api/v1/verification`, { data: { licenseId: idLicence, empreinteServeur: empreinte2, versionApplication: "c" } })).json()).statut).toBe("VALIDE");
});

test("paiement de licence enregistré, formaté en FCFA, et la clé privée ne sort jamais", async ({ page }) => {
  await seConnecter(page);
  await page.goto(`/licences/${idLicence}`);
  const formulaire = page.getByRole("form", { name: "Enregistrer un paiement" });
  await formulaire.getByLabel("Montant (FCFA) *").fill("500000");
  await formulaire.getByLabel("Référence").fill("VIR-CHROME-1");
  await formulaire.getByLabel("Date *").fill(aujourdhui());
  await formulaire.getByRole("button", { name: "Enregistrer" }).click();
  await expect(page.getByText(/500\s?000 FCFA/)).toBeVisible();
  await expect(page.getByText(/VIR-CHROME-1/)).toBeVisible();
  const cle = readFileSync(CLE_PUBLIQUE.replace("licence-public", "licence-private"), "utf8").trim();
  expect(await page.content()).not.toContain(cle);
  await page.screenshot({ path: `${CAPTURES}/08-paiement.png`, fullPage: true });
});

test("révocation : confirmation demandée, plus aucune action, le serveur du client est refusé", async ({ page, request }) => {
  await seConnecter(page);
  await page.goto(`/licences/${idLicence}`);
  let confirmation = "";
  page.once("dialog", async (d) => {
    confirmation = d.message();
    await d.accept();
  });
  const formulaire = page.getByRole("form", { name: "Révoquer" });
  await formulaire.getByLabel("Motif *").fill("Impayé (essai Chrome)");
  await formulaire.getByRole("button", { name: "Révoquer" }).click();
  await expect(page.getByText("Licence révoquée.")).toBeVisible();
  expect(confirmation).toContain("définitive");
  await expect(page.getByText("Révoquée").first()).toBeVisible();
  await expect(page.getByText("Impayé (essai Chrome)")).toBeVisible();
  await expect(page.getByRole("form", { name: "Renouveler" })).toHaveCount(0); // plus d'actions sur une licence révoquée
  expect((await (await request.post(`${SERVEUR}/api/v1/verification`, { data: { licenseId: idLicence, empreinteServeur: empreinte2, versionApplication: "c" } })).json()).statut).toBe("REVOQUEE");
  await page.screenshot({ path: `${CAPTURES}/09-revoquee.png`, fullPage: true });
});

test("versions : https et somme SHA-256 exigés, version publique lisible sans connexion par update.sh", async ({ page, request }) => {
  await seConnecter(page);
  await page.goto("/versions");
  const numero = `9.${Date.now() % 100000}.0`;
  await page.getByLabel("Numéro de version *").fill(numero);
  await page.getByLabel("Adresse de téléchargement (https) *").fill("https://dl.exemple.cm/immogest.tar.gz");
  await page.getByLabel("Somme de contrôle SHA-256 *").fill("zz");
  await page.getByRole("button", { name: "Publier la version" }).click();
  await expect(page.locator('p[role="alert"]')).toContainText("SHA-256");
  await page.getByLabel("Somme de contrôle SHA-256 *").fill("a".repeat(64));
  await page.getByLabel("Notes de version").fill("Correctifs de test");
  await page.getByRole("button", { name: "Publier la version" }).click();
  await expect(page.getByText(numero).first()).toBeVisible();
  const derniere = await request.get(`${SERVEUR}/api/v1/versions/derniere`); // sans aucun jeton
  expect((await derniere.json()).numero).toBe(numero);
  await page.screenshot({ path: `${CAPTURES}/10-versions.png`, fullPage: true });
});

test("sécurité du portail : routes sensibles non exposées par le proxy, déconnexion effective", async ({ page }) => {
  await seConnecter(page);
  for (const chemin of ["/api/proxy/api/v1/verification", "/api/proxy/api/v1/auth/login", "/api/proxy/actuator/env"]) {
    expect((await page.request.post(chemin, { data: {} })).status(), chemin).toBe(404);
  }
  await page.getByRole("button", { name: "Se déconnecter" }).click();
  await expect(page).toHaveURL(/\/login$/);
  await page.goto("/clients");
  await expect(page).toHaveURL(/\/login$/);
  expect((await page.request.get("/api/proxy/api/v1/clients")).status()).toBe(401);
});
