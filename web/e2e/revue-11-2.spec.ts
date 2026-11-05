import { expect, test, type Page } from "@playwright/test";

// Revue 11.2 en navigateur réel : en-têtes et CSP sans violation, import, conformité, sauvegardes, limite de débit.
const ADMIN = { identifiant: process.env.E2E_ADMIN_IDENTIFIANT ?? "admin@immogest.local", motDePasse: process.env.E2E_ADMIN_MOT_DE_PASSE ?? "Admin1234!" };
const CAPTURES = process.env.E2E_CAPTURES ?? "test-results/captures";

async function seConnecter(page: Page) {
  await page.goto("/login");
  await page.getByLabel("Téléphone ou e-mail").fill(ADMIN.identifiant);
  await page.getByLabel("Mot de passe").fill(ADMIN.motDePasse);
  await page.getByRole("button", { name: "Se connecter" }).click();
  await expect(page).toHaveURL(/\/tableau-de-bord$/);
}

const unique = () => `${Date.now() % 1_000_000_000}`;

test.describe.configure({ mode: "serial" });

test("en-têtes de sécurité et aucune violation de CSP sur les pages de l'application", async ({ page }) => {
  await page.addInitScript(() => {
    document.addEventListener("securitypolicyviolation", (e) => {
      const w = window as unknown as { __csp?: string[] };
      (w.__csp = w.__csp ?? []).push(`${e.violatedDirective} ← ${e.blockedURI}`);
    });
  });
  const consoleCsp: string[] = [];
  page.on("console", (m) => {
    if (/Content Security Policy|Refused to (load|execute|apply|connect)/i.test(m.text())) consoleCsp.push(m.text());
  });

  const login = await page.goto("/login");
  const h = login!.headers();
  expect(h["content-security-policy"]).toContain("frame-ancestors 'none'");
  expect(h["content-security-policy"]).toContain("default-src 'self'");
  expect(h["x-frame-options"]).toBe("DENY");
  expect(h["x-content-type-options"]).toBe("nosniff");
  expect(h["referrer-policy"]).toBe("strict-origin-when-cross-origin");
  expect(h["x-powered-by"]).toBeUndefined();

  await seConnecter(page);
  const pages = ["/tableau-de-bord", "/patrimoine", "/locataires", "/baux", "/encaissements", "/relances", "/parametres", "/parametres/licence", "/parametres/notifications",
    "/parametres/paiement-en-ligne", "/parametres/sauvegardes", "/parametres/import", "/parametres/conformite"];
  for (const chemin of pages) {
    await page.goto(chemin);
    await expect(page.getByRole("heading", { level: 1 }).first()).toBeVisible();
    await page.waitForLoadState("networkidle");
    const violations = await page.evaluate(() => (window as unknown as { __csp?: string[] }).__csp ?? []);
    expect(violations, `violations CSP sur ${chemin}`).toEqual([]);
  }
  expect(consoleCsp).toEqual([]);
  await page.goto("/parametres");
  for (const carte of ["Sauvegardes", "Import de données", "Conformité", "Paiement en ligne", "Licence"]) {
    await expect(page.getByRole("link", { name: new RegExp(carte) }).first()).toBeVisible();
  }
  await page.screenshot({ path: `${CAPTURES}/01-parametres.png`, fullPage: true });
});

test("import : modèle Excel téléchargé, erreurs ligne par ligne, import CSV puis réimport refusé", async ({ page }) => {
  await seConnecter(page);
  await page.goto("/parametres/import");

  const telechargement = page.waitForEvent("download");
  await page.getByRole("link", { name: /Modèle Excel/ }).click();
  const fichier = await telechargement;
  expect(fichier.suggestedFilename()).toBe("modele-import-immogest.xlsx");
  const chemin = await fichier.path();
  expect((await import("node:fs")).statSync(chemin!).size).toBeGreaterThan(2000);

  // fichier avec une colonne obligatoire absente : le rapport nomme la colonne
  await page.getByLabel(/Fichier \(\.xlsx ou \.csv\)/).setInputFiles({ name: "immeubles.csv", mimeType: "text/csv", buffer: Buffer.from("nom;adresse\nX;rue\n") });
  await page.getByLabel("Contenu du CSV").selectOption("IMMEUBLES");
  await page.getByRole("button", { name: "Analyser le fichier" }).click();
  await expect(page.getByRole("heading", { name: /erreur\(s\) à corriger/ })).toBeVisible();
  await expect(page.getByTestId("erreurs-import")).toContainText("Colonne obligatoire absente");
  await expect(page.getByRole("button", { name: "Importer" })).toHaveCount(0);
  await page.screenshot({ path: `${CAPTURES}/02-import-erreurs.png`, fullPage: true });

  // fichier correct (accents, guillemets)
  const nom = `Résidence Chrome ${unique()}`;
  const csv = `nom;adresse;ville\n"${nom}";"12 rue ""du Port""";Douala\n`;
  await page.getByLabel(/Fichier \(\.xlsx ou \.csv\)/).setInputFiles({ name: "immeubles.csv", mimeType: "text/csv", buffer: Buffer.from(csv, "utf-8") });
  await page.getByLabel("Contenu du CSV").selectOption("IMMEUBLES");
  await page.getByRole("button", { name: "Analyser le fichier" }).click();
  await expect(page.getByRole("status")).toContainText("Aucune erreur");
  await page.getByRole("button", { name: "Importer" }).click();
  await page.getByRole("alertdialog").getByRole("button", { name: "Importer" }).click();
  await expect(page.getByTestId("resultat-import")).toContainText("Immeubles créé(s) : 1");
  await page.screenshot({ path: `${CAPTURES}/03-import-ok.png`, fullPage: true });

  const liste = await page.request.get(`/api/proxy/api/v1/immeubles?q=${encodeURIComponent(nom)}`);
  const corps = (await liste.json()) as { content: { nom: string; adresse: string }[] };
  expect(corps.content.map((i) => i.nom)).toContain(nom);
  expect(corps.content[0].adresse).toBe('12 rue "du Port"');

  // le même fichier une seconde fois : refusé, aucun doublon
  await page.reload();
  await page.getByLabel(/Fichier \(\.xlsx ou \.csv\)/).setInputFiles({ name: "immeubles.csv", mimeType: "text/csv", buffer: Buffer.from(csv, "utf-8") });
  await page.getByLabel("Contenu du CSV").selectOption("IMMEUBLES");
  await page.getByRole("button", { name: "Analyser le fichier" }).click();
  await expect(page.getByTestId("erreurs-import")).toContainText("existe déjà");
  const encore = await page.request.get(`/api/proxy/api/v1/immeubles?q=${encodeURIComponent(nom)}`);
  expect(((await encore.json()) as { content: unknown[] }).content).toHaveLength(1);
});

test("conformité : consentement, demande d'effacement, anonymisation par l'administrateur", async ({ page }) => {
  await seConnecter(page);
  const tel = `+2376${(Date.now() % 90_000_000) + 10_000_000}`;
  const creation = await page.request.post("/api/proxy/api/v1/locataires", {
    data: { nom: "Paul Chrome Secret", telephone: tel, email: "paul.chrome@secret.cm", profession: "Médecin", adresse: "12 rue Cachée" },
  });
  expect(creation.status()).toBe(201);
  const { id } = (await creation.json()) as { id: string };

  await page.goto(`/locataires/${id}`);
  await expect(page.getByText("Paul Chrome Secret").first()).toBeVisible();
  await page.getByRole("tab", { name: "Conformité" }).click();
  await expect(page.getByText("Données personnelles et consentements")).toBeVisible();
  await page.getByRole("button", { name: "Enregistrer l'accord" }).first().click();
  const ligne = page.locator("li", { hasText: "Messages (SMS, WhatsApp, e-mail)" });
  await expect(ligne.getByText("Accordé")).toBeVisible();
  await page.screenshot({ path: `${CAPTURES}/04-consentements.png`, fullPage: true });

  await page.getByRole("button", { name: "Enregistrer une demande d'effacement" }).click();
  await page.goto("/parametres/conformite");
  const demande = page.getByTestId("demande").first();
  await expect(demande).toContainText("Locataire");
  await expect(demande).toContainText("Reçue");
  await page.screenshot({ path: `${CAPTURES}/05-demande.png`, fullPage: true });
  await demande.getByRole("button", { name: "Anonymiser" }).click();
  await expect(page.getByRole("alertdialog")).toContainText("irréversible");
  await page.getByRole("alertdialog").getByRole("button", { name: "Anonymiser" }).click();
  await expect(page.getByText("À communiquer à la personne : ce qui est conservé")).toBeVisible();
  await expect(page.getByTestId("demande").first()).toContainText("Anonymisée");
  await page.screenshot({ path: `${CAPTURES}/06-anonymisee.png`, fullPage: true });

  const fiche = await page.request.get(`/api/proxy/api/v1/locataires/${id}`);
  const l = (await fiche.json()) as { nom: string; telephone: string | null; email: string | null; adresse: string | null; archive: boolean };
  expect(l.nom).toBe("Locataire anonymisé");
  expect(l.telephone ?? null).toBeNull();
  expect(l.email ?? null).toBeNull();
  expect(l.adresse ?? null).toBeNull();
  expect(l.archive).toBe(true);
  expect(JSON.stringify(l)).not.toContain("Secret");
});

test("sauvegardes : l'écran s'affiche et explique l'absence du service en développement", async ({ page }) => {
  await seConnecter(page);
  await page.goto("/parametres/sauvegardes");
  await expect(page.getByRole("heading", { name: "Sauvegardes et diagnostic", level: 1 })).toBeVisible();
  await expect(page.getByText(/PostgreSQL/)).toBeVisible();
  await expect(page.getByText(/Aucune sauvegarde n'existe/)).toBeVisible();
  await page.getByRole("button", { name: "Sauvegarder maintenant" }).click();
  await expect(page.locator('p[role="alert"]')).toContainText(/service de sauvegarde n'est pas joignable|déjà/);
  await page.screenshot({ path: `${CAPTURES}/07-sauvegardes.png`, fullPage: true });
});

// Désactivé par défaut : la limite (par adresse IP) bloque toute connexion pendant une minute et ferait échouer les scénarios suivants.
// À lancer seul : E2E_LIMITE_DEBIT=1 (avec le backend à sa limite par défaut, 20 connexions par minute).
test("limite de débit : trop d'essais de connexion → message clair dans le navigateur", async ({ page }) => {
  test.skip(!process.env.E2E_LIMITE_DEBIT, "E2E_LIMITE_DEBIT non défini");
  // à exécuter en dernier : la limite (par adresse IP) bloque toute connexion pendant une minute
  for (let i = 0; i < 25; i++) {
    await page.request.post("/api/auth/login", { data: { identifiant: "inconnu@exemple.cm", motDePasse: "faux" } });
  }
  await page.goto("/login");
  await page.getByLabel("Téléphone ou e-mail").fill(ADMIN.identifiant);
  await page.getByLabel("Mot de passe").fill(ADMIN.motDePasse);
  await page.getByRole("button", { name: "Se connecter" }).click();
  await expect(page.locator('[role="alert"]:not(#__next-route-announcer__)')).toContainText(/Trop de requêtes/);
  await expect(page).toHaveURL(/\/login/);
  await page.screenshot({ path: `${CAPTURES}/08-limite-debit.png`, fullPage: true });
});
