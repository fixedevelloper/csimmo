import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const post = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { POST: (...a: unknown[]) => post(...a) } }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));
vi.mock("@/hooks/use-relances", () => ({
  useArrieres: () => ({
    isPending: false,
    error: null,
    refetch: vi.fn(),
    data: {
      content: [
        { echeanceId: "e-1", bailId: "b-1", bailNumero: "BAIL-2026-0001", locataireNom: "Paul Mbarga", logement: "Palmiers — A1", dateEcheance: "2026-09-25", reste: 160000, joursRetard: 40, peutMiseEnDemeure: true, derniereRelance: { dateRelance: "2026-10-25", canal: "WHATSAPP" } },
        { echeanceId: "e-2", bailId: "b-2", bailNumero: "BAIL-2026-0002", locataireNom: "Marie Ngo", logement: "Palmiers — B2", dateEcheance: "2026-10-30", reste: 80000, joursRetard: 5, peutMiseEnDemeure: false },
      ],
      page: 0, size: 20, totalElements: 2,
    },
  }),
  useHistoriqueRelances: () => ({ isPending: false, data: { content: [{ id: "r-1", dateRelance: "2026-10-25", type: "AUTO", niveau: 30, resultat: "EN_FILE", canal: "WHATSAPP", statutEnvoi: "ENVOYE" }] } }),
}));

import { ArrieresListe } from "./arrieres-liste";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 201 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });
const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient()}><ArrieresListe /></QueryClientProvider>, "fr");

describe("ArrieresListe", () => {
  beforeEach(() => vi.clearAllMocks());

  it("affiche les arriérés, la dernière relance, et n'offre la mise en demeure qu'à partir du dernier niveau", () => {
    rendu();
    expect(screen.getByText("Paul Mbarga")).toBeInTheDocument();
    expect(screen.getByText("40 j")).toBeInTheDocument();
    expect(screen.getByText(/WHATSAPP/)).toBeInTheDocument();
    expect(screen.getByText("Aucune")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Mise en demeure pour Paul Mbarga" })).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Mise en demeure pour Marie Ngo" })).not.toBeInTheDocument();
  });

  it("relance manuellement une échéance", async () => {
    post.mockReturnValueOnce(ok({ id: "r-9" }));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Relancer Marie Ngo" }));
    await waitFor(() => expect(post).toHaveBeenCalledWith("/api/v1/relances", { body: { echeanceId: "e-2" } }));
  });

  it("demande confirmation avant d'émettre une mise en demeure, puis propose le téléchargement", async () => {
    post.mockReturnValueOnce(ok({ numero: "MED-2026-000001", fichierId: "f-9" }));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Mise en demeure pour Paul Mbarga" }));
    expect(post).not.toHaveBeenCalled();
    const dialogue = await screen.findByRole("alertdialog");
    await userEvent.click(within(dialogue).getByRole("button", { name: "Mise en demeure" }));
    await waitFor(() => expect(post).toHaveBeenCalledWith("/api/v1/mises-en-demeure", { body: { echeanceId: "e-1" } }));
  });

  it("affiche l'erreur métier traduite", async () => {
    post.mockReturnValueOnce(ko(409, "RELANCE_ECHEANCE_SOLDEE"));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Relancer Paul Mbarga" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("soldée");
  });

  it("ouvre l'historique d'une échéance", async () => {
    rendu();
    await userEvent.click(screen.getAllByRole("button", { name: "Historique" })[0]);
    expect(await screen.findByText(/niveau J30/)).toBeInTheDocument();
    expect(screen.getByText(/envoyée/)).toBeInTheDocument();
  });
});
