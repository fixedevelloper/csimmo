import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const get = vi.fn();
const post = vi.fn();
let role = "ADMIN";
vi.mock("@/lib/api/client", () => ({ api: { GET: (...a: unknown[]) => get(...a), POST: (...a: unknown[]) => post(...a) } }));
vi.mock("@/hooks/use-me", () => ({ useMe: () => ({ data: { role } }) }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

import { ConformitePage } from "./conformite-page";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 200 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });

const demande = (extra: object = {}) => ({ id: "d-1", type: "LOCATAIRE", personneId: "p-1", statut: "RECUE", motif: "Demande écrite", recueLe: "2026-11-04T10:00:00Z", obstacles: [], ...extra });

function serveur(demandes: unknown[]) {
  get.mockImplementation((chemin: string) => {
    if (chemin.endsWith("/demandes")) return ok(demandes);
    if (chemin.endsWith("/registre")) return ok({ version: "1.0", responsable: "Le client est le responsable.", note: "À faire valider.", traitements: [{ id: "T1", nom: "Gestion locative", finalite: "Contrat", baseLegale: "Contrat", donnees: ["Nom"], personnes: ["Locataires"], destinataires: ["Gestionnaires"], conservation: "10 ans", mesures: ["Chiffrement"] }] });
    return ok([{ finalite: "NOTIFICATIONS", version: "1.0", titre: "Messages", texte: "J'accepte {RESPONSABLE}" }]);
  });
}
const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient({ defaultOptions: { queries: { retry: false } } })}><ConformitePage /></QueryClientProvider>, "fr");

describe("ConformitePage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    role = "ADMIN";
  });

  it("affiche le registre et les modèles", async () => {
    serveur([]);
    rendu();
    expect(await screen.findByText(/T1 — Gestion locative/)).toBeInTheDocument();
    expect(screen.getByText("Le client est le responsable.")).toBeInTheDocument();
    expect(await screen.findByText("J'accepte {RESPONSABLE}")).toBeInTheDocument();
    expect(screen.getByText("Aucune demande.")).toBeInTheDocument();
  });

  it("une demande bloquée par des obstacles ne peut pas être exécutée et dit pourquoi", async () => {
    serveur([demande({ obstacles: [{ code: "BAIL_EN_COURS", message: "x" }, { code: "IMPAYES", message: "y" }] })]);
    rendu();
    expect(await screen.findByText("Un bail est en cours : attendez sa fin.")).toBeInTheDocument();
    expect(screen.getByText(/Des impayés subsistent/)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Anonymiser" })).toBeDisabled();
  });

  it("exécute après confirmation et rappelle ce qui est conservé", async () => {
    serveur([demande()]);
    post.mockReturnValueOnce(ok({ demandeId: "d-1", piecesSupprimees: 2, conserve: ["Baux et quittances (obligation légale)"] }));
    rendu();
    await userEvent.click(await screen.findByRole("button", { name: "Anonymiser" }));
    expect(await screen.findByText(/Cette action est irréversible/)).toBeInTheDocument();
    await userEvent.click(screen.getAllByRole("button", { name: "Anonymiser" }).at(-1)!);
    await waitFor(() => expect(post).toHaveBeenCalledWith("/api/v1/conformite/demandes/{id}/execution", { params: { path: { id: "d-1" } } }));
    expect(await screen.findByText("Baux et quittances (obligation légale)")).toBeInTheDocument();
  });

  it("le refus exige un motif", async () => {
    serveur([demande()]);
    post.mockReturnValueOnce(ok({}));
    rendu();
    await userEvent.click(await screen.findByRole("button", { name: "Refuser" }));
    const confirmer = screen.getByRole("button", { name: "Confirmer le refus" });
    expect(confirmer).toBeDisabled();
    await userEvent.type(screen.getByLabelText(/Motif du refus/), "Identité non vérifiée");
    await userEvent.click(confirmer);
    await waitFor(() => expect(post).toHaveBeenCalledWith("/api/v1/conformite/demandes/{id}/refus", { params: { path: { id: "d-1" } }, body: { motif: "Identité non vérifiée" } }));
  });

  it("un gestionnaire voit les demandes mais pas les boutons d'exécution", async () => {
    role = "GESTIONNAIRE";
    serveur([demande()]);
    rendu();
    expect(await screen.findByTestId("demande")).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Anonymiser" })).not.toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Refuser" })).not.toBeInTheDocument();
  });

  it("affiche l'erreur métier traduite", async () => {
    serveur([demande()]);
    post.mockReturnValueOnce(ko(409, "ANONYMISATION_IMPOSSIBLE"));
    rendu();
    await userEvent.click(await screen.findByRole("button", { name: "Anonymiser" }));
    await userEvent.click(screen.getAllByRole("button", { name: "Anonymiser" }).at(-1)!);
    expect(await screen.findByRole("alert")).toHaveTextContent("Anonymisation impossible");
  });
});
