import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const put = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { PUT: (...a: unknown[]) => put(...a) } }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));
vi.mock("@/hooks/use-signalements", () => ({
  useSignalements: () => ({
    isPending: false,
    error: null,
    refetch: vi.fn(),
    data: {
      content: [
        { id: "s-1", locataireNom: "Paul Mbarga", logement: "Palmiers — A1", description: "Fuite sous l'évier", statut: "NOUVEAU", photoFichierId: "f-1", creeLe: "2026-11-04T10:00:00Z" },
        { id: "s-2", locataireNom: "Marie Ngo", logement: "Palmiers — B2", description: "Porte bloquée", statut: "RESOLU", noteGestion: "Réparée" },
      ],
      page: 0, size: 20, totalElements: 2,
    },
  }),
}));

import { SignalementsListe } from "./signalements-liste";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 200 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });
const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient()}><SignalementsListe /></QueryClientProvider>, "fr");

describe("SignalementsListe", () => {
  beforeEach(() => vi.clearAllMocks());

  it("liste les pannes avec la photo (lien protégé) et n'offre des actions que sur les signalements ouverts", () => {
    rendu();
    expect(screen.getByText("Fuite sous l'évier")).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "Voir la photo" })).toHaveAttribute("href", "/api/proxy/api/v1/fichiers/f-1");
    expect(screen.getAllByRole("button", { name: "Prendre en charge" })).toHaveLength(1);
    expect(screen.getAllByRole("button", { name: "Marquer résolu" })).toHaveLength(1);
    expect(screen.getByText("Réparée")).toBeInTheDocument();
  });

  it("prend en charge avec une note", async () => {
    put.mockReturnValueOnce(ok({}));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Prendre en charge" }));
    await userEvent.type(screen.getByLabelText("Note pour le locataire (facultative)"), "Plombier prévenu");
    await userEvent.click(screen.getByRole("button", { name: "Confirmer la prise en charge" }));
    await waitFor(() => expect(put).toHaveBeenCalledWith("/api/v1/signalements/{id}/statut", { params: { path: { id: "s-1" } }, body: { statut: "EN_COURS", note: "Plombier prévenu" } }));
  });

  it("affiche l'erreur métier traduite", async () => {
    put.mockReturnValueOnce(ko(409, "SIGNALEMENT_TRANSITION_INVALIDE"));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Rejeter" }));
    await userEvent.click(screen.getByRole("button", { name: "Confirmer le rejet" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("définitif");
  });
});
