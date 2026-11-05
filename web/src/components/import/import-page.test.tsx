import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

import { ImportPage } from "./import-page";

const fetchMock = vi.fn();
beforeEach(() => vi.stubGlobal("fetch", fetchMock));
afterEach(() => {
  fetchMock.mockReset();
  vi.unstubAllGlobals();
});

const reponse = (corps: unknown, status = 200) => Promise.resolve(new Response(JSON.stringify(corps), { status, headers: { "content-type": "application/json" } }));
const fichier = (nom = "import.xlsx") => new File(["contenu"], nom);

const previsu = (extra: object = {}) => ({
  empreinte: "e".repeat(64),
  feuilles: [{ type: "IMMEUBLES", feuille: "Immeubles", lignes: 2 }, { type: "LOTS", feuille: "Lots", lignes: 5 }],
  erreurs: [],
  apercu: {},
  importable: true,
  ...extra,
});

async function choisir(f: File) {
  await userEvent.upload(screen.getByLabelText(/Fichier \(\.xlsx ou \.csv\)/), f);
}

describe("ImportPage", () => {
  it("propose les modèles à télécharger", () => {
    renderIntl(<ImportPage />, "fr");
    expect(screen.getByRole("link", { name: /Modèle Excel/ })).toHaveAttribute("href", "/api/proxy/api/v1/import/modele?format=xlsx");
    expect(screen.getByRole("link", { name: "CSV : Locataires" })).toHaveAttribute("href", "/api/proxy/api/v1/import/modele?format=csv&type=LOCATAIRES");
  });

  it("analyse puis n'autorise l'import qu'en l'absence d'erreur", async () => {
    fetchMock.mockReturnValueOnce(reponse(previsu()));
    renderIntl(<ImportPage />, "fr");
    expect(screen.getByRole("button", { name: "Analyser le fichier" })).toBeDisabled();
    await choisir(fichier());
    await userEvent.click(screen.getByRole("button", { name: "Analyser le fichier" }));
    expect(await screen.findByText("Lots : 5 ligne(s)")).toBeInTheDocument();
    expect(screen.getByRole("status")).toHaveTextContent("Aucune erreur");
    expect(screen.getByRole("button", { name: "Importer" })).toBeEnabled();
    const [url, init] = fetchMock.mock.calls[0];
    expect(url).toBe("/api/proxy/api/v1/import/previsualisation");
    expect((init.body as FormData).get("fichier")).toBeInstanceOf(File);
    expect((init.body as FormData).get("type")).toBeNull(); // le type n'est envoyé que pour un CSV
  });

  it("affiche les erreurs ligne par ligne et masque le bouton d'import", async () => {
    fetchMock.mockReturnValueOnce(
      reponse(previsu({ importable: false, erreurs: [{ feuille: "Baux", ligne: 7, colonne: "date_debut", code: "VALEUR_INVALIDE", message: "date invalide" }, { feuille: "Lots", ligne: 0, colonne: null, code: "LICENCE_PALIER_ATTEINT", message: "palier dépassé" }] })),
    );
    renderIntl(<ImportPage />, "fr");
    await choisir(fichier());
    await userEvent.click(screen.getByRole("button", { name: "Analyser le fichier" }));
    expect(await screen.findByRole("heading", { name: "2 erreur(s) à corriger" })).toBeInTheDocument();
    const lignes = screen.getByTestId("erreurs-import").querySelectorAll("tr");
    expect(lignes[0]).toHaveTextContent("Baux7date_debutdate invalide");
    expect(lignes[1]).toHaveTextContent("Lots——palier dépassé");
    expect(screen.queryByRole("button", { name: "Importer" })).not.toBeInTheDocument();
  });

  it("un CSV demande son type et l'envoie", async () => {
    fetchMock.mockReturnValueOnce(reponse(previsu()));
    renderIntl(<ImportPage />, "fr");
    await choisir(fichier("lots.csv"));
    await userEvent.selectOptions(screen.getByLabelText("Contenu du CSV"), "LOTS");
    await userEvent.click(screen.getByRole("button", { name: "Analyser le fichier" }));
    await waitFor(() => expect(fetchMock).toHaveBeenCalled());
    expect((fetchMock.mock.calls[0][1].body as FormData).get("type")).toBe("LOTS");
  });

  it("importe avec l'empreinte analysée, après confirmation, et affiche les comptes", async () => {
    fetchMock.mockReturnValueOnce(reponse(previsu())).mockReturnValueOnce(reponse({ importe: true, crees: { IMMEUBLES: 2, LOTS: 5 }, echeancesReprises: 31, erreurs: [] }));
    renderIntl(<ImportPage />, "fr");
    await choisir(fichier());
    await userEvent.click(screen.getByRole("button", { name: "Analyser le fichier" }));
    await userEvent.click(await screen.findByRole("button", { name: "Importer" }));
    await userEvent.click(await screen.findByRole("button", { name: "Importer", hidden: false }).then(() => screen.getAllByRole("button", { name: "Importer" }).at(-1)!));
    expect(await screen.findByTestId("resultat-import")).toHaveTextContent("Lots créé(s) : 5");
    expect(screen.getByTestId("resultat-import")).toHaveTextContent("Échéances d'historique reprises : 31");
    const [url, init] = fetchMock.mock.calls[1];
    expect(url).toBe("/api/proxy/api/v1/import");
    expect((init.body as FormData).get("empreinte")).toBe("e".repeat(64));
  });

  it("un import refusé (422) montre le rapport et dit que rien n'est écrit", async () => {
    fetchMock.mockReturnValueOnce(reponse(previsu())).mockReturnValueOnce(
      reponse({ importe: false, crees: {}, echeancesReprises: 0, erreurs: [{ feuille: "Baux", ligne: 3, code: "BAIL_LOT_DEJA_OCCUPE", message: "Ce lot est déjà loué" }] }, 422),
    );
    renderIntl(<ImportPage />, "fr");
    await choisir(fichier());
    await userEvent.click(screen.getByRole("button", { name: "Analyser le fichier" }));
    await userEvent.click(await screen.findByRole("button", { name: "Importer" }));
    await userEvent.click(screen.getAllByRole("button", { name: "Importer" }).at(-1)!);
    expect(await screen.findByText(/Rien n'a été enregistré/)).toBeInTheDocument();
    expect(screen.getByText("Ce lot est déjà loué")).toBeInTheDocument();
  });

  it("traduit une erreur d'API (fichier illisible)", async () => {
    fetchMock.mockReturnValueOnce(reponse({ code: "IMPORT_FICHIER_ILLISIBLE" }, 400));
    renderIntl(<ImportPage />, "fr");
    await choisir(fichier());
    await userEvent.click(screen.getByRole("button", { name: "Analyser le fichier" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("illisible ou corrompu");
  });
});
