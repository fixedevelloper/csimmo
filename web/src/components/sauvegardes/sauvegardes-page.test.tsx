import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const get = vi.fn();
const post = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { GET: (...a: unknown[]) => get(...a), POST: (...a: unknown[]) => post(...a) } }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

import { SauvegardesPage } from "./sauvegardes-page";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 200 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });

const s = (id: string, extra: object = {}) => ({ id, date: "2026-11-03T01:30:00Z", version: "1.2.0", etiquette: "quotidienne", tailleOctets: 5 * 1024 * 1024, objets: 3, externe: "OK", restaurable: true, ...extra });
const diag = (alertes: string[] = []) => ({ versionApplication: "1.2.0", versionJava: "21", versionBase: "16.4", tailleBaseOctets: 1048576, disqueTotalOctets: 100 * 1024 ** 3, disqueLibreOctets: 40 * 1024 ** 3, etatLicence: "VALIDE", joursLicenceRestants: 200, derniereSauvegarde: "2026-11-03T01:30:00Z", ageDerniereSauvegardeHeures: 5, copieExterne: "OK", alertes });

function route(etat: object, diagnostic: object) {
  get.mockImplementation((chemin: string) => ok(chemin.endsWith("/diagnostic") ? diagnostic : etat));
}
const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}><SauvegardesPage /></QueryClientProvider>, "fr");

describe("SauvegardesPage", () => {
  beforeEach(() => vi.clearAllMocks());

  it("liste les sauvegardes et le diagnostic", async () => {
    route({ sauvegardes: [s("20261103-013000-quotidienne")], operation: null, heureQuotidienne: "02:30", versionInstallee: "1.2.0" }, diag());
    rendu();
    expect(await screen.findByText("Quotidienne")).toBeInTheDocument();
    expect(screen.getByText("5 Mo")).toBeInTheDocument();
    expect(await screen.findByText(/PostgreSQL 16.4/)).toBeInTheDocument();
    expect(screen.getByText(/chaque jour à 02:30/)).toBeInTheDocument();
  });

  it("traduit les alertes du diagnostic, les graves en tête de l'attention", async () => {
    route({ sauvegardes: [], operation: null, heureQuotidienne: "02:30" }, diag(["SAUVEGARDE_ABSENTE", "COPIE_EXTERNE_ABSENTE"]));
    rendu();
    expect(await screen.findByText(/vos données ne sont pas protégées/)).toBeInTheDocument();
    expect(screen.getByText(/Aucune copie externe/)).toBeInTheDocument();
    expect(screen.getByText("Aucune sauvegarde pour le moment.")).toBeInTheDocument();
  });

  it("« Sauvegarder maintenant » envoie la demande", async () => {
    route({ sauvegardes: [], operation: null, heureQuotidienne: "02:30" }, diag());
    post.mockReturnValueOnce(ok(undefined));
    rendu();
    await userEvent.click(await screen.findByRole("button", { name: "Sauvegarder maintenant" }));
    await waitFor(() => expect(post).toHaveBeenCalledWith("/api/v1/sauvegardes"));
  });

  it("désactive les actions pendant une opération et l'annonce", async () => {
    route({ sauvegardes: [s("20261103-013000")], operation: { operation: "restauration", statut: "EN_COURS", id: "x", debut: "2026-11-03T02:00:00Z", message: "restauration de la base" }, heureQuotidienne: "02:30" }, diag());
    rendu();
    expect(await screen.findByTestId("operation-en-cours")).toHaveTextContent("Restauration en cours");
    expect(screen.getByRole("button", { name: "Sauvegarder maintenant" })).toBeDisabled();
    expect(screen.getByRole("button", { name: "Restaurer" })).toBeDisabled();
  });

  it("une sauvegarde d'une autre version ne peut pas être restaurée depuis l'écran", async () => {
    route({ sauvegardes: [s("20261101-013000", { version: "1.1.0", restaurable: false })], operation: null, heureQuotidienne: "02:30" }, diag());
    rendu();
    expect(await screen.findByRole("button", { name: "Restaurer" })).toBeDisabled();
  });

  it("la restauration exige de taper RESTAURER", async () => {
    route({ sauvegardes: [s("20261103-013000-quotidienne")], operation: null, heureQuotidienne: "02:30" }, diag());
    post.mockReturnValueOnce(ok(undefined));
    rendu();
    await userEvent.click(await screen.findByRole("button", { name: "Restaurer" }));
    const confirmer = screen.getByRole("button", { name: "Restaurer cette sauvegarde" });
    expect(confirmer).toBeDisabled();
    await userEvent.type(screen.getByLabelText(/tapez RESTAURER/), "restaurer");
    expect(confirmer).toBeDisabled(); // la casse compte
    await userEvent.clear(screen.getByLabelText(/tapez RESTAURER/));
    await userEvent.type(screen.getByLabelText(/tapez RESTAURER/), "RESTAURER");
    await userEvent.click(confirmer);
    await waitFor(() => expect(post).toHaveBeenCalledWith("/api/v1/sauvegardes/{id}/restauration", { params: { path: { id: "20261103-013000-quotidienne" } }, body: { confirmation: "RESTAURER" } }));
  });

  it("affiche l'erreur métier traduite", async () => {
    route({ sauvegardes: [], operation: null, heureQuotidienne: "02:30" }, diag());
    post.mockReturnValueOnce(ko(409, "SAUVEGARDE_OPERATION_EN_COURS"));
    rendu();
    await userEvent.click(await screen.findByRole("button", { name: "Sauvegarder maintenant" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("déjà en cours");
  });
});
