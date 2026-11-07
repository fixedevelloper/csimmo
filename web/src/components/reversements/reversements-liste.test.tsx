import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

vi.mock("next/navigation", () => ({ useSearchParams: () => new URLSearchParams() }));
const post = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { POST: (...a: unknown[]) => post(...a) } }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));
let role = "GESTIONNAIRE";
vi.mock("@/hooks/use-me", () => ({ useMe: () => ({ data: { role } }) }));
vi.mock("@/hooks/use-reversements", () => ({
  useReversements: () => ({
    isPending: false,
    error: null,
    refetch: vi.fn(),
    data: {
      content: [
        { id: "r-1", proprietaireNom: "Mme Ngono", brut: 400000, honoraires: 30000, depenses: 50000, net: 320000, statut: "CALCULE" },
        { id: "r-2", proprietaireNom: "M. Fotso", brut: 100000, honoraires: 5000, depenses: 0, net: 95000, statut: "VALIDE", numero: "REV-2026-000001", fichierId: "f-2" },
        { id: "r-3", proprietaireNom: "Mme Sans", brut: 10000, honoraires: 0, depenses: 0, net: 10000, statut: "CALCULE", sansMandat: true },
      ],
      page: 0, size: 20, totalElements: 3,
    },
  }),
}));

import { ReversementsListe } from "./reversements-liste";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 200 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });
const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient()}><ReversementsListe /></QueryClientProvider>, "fr");

describe("ReversementsListe", () => {
  beforeEach(() => { vi.clearAllMocks(); role = "GESTIONNAIRE"; });

  it("montre le net, l'état et les actions propres à chaque état", () => {
    rendu();
    expect(screen.getByText("Mme Ngono")).toBeInTheDocument();
    expect(screen.getAllByTestId("net-reversement")[0]).toHaveTextContent("320");
    expect(screen.getByRole("button", { name: "Valider le reversement de Mme Ngono" })).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Valider le reversement de M. Fotso" })).not.toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Marquer payé le reversement de M. Fotso" })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "Relevé PDF" })).toHaveAttribute("href", "/api/proxy/api/v1/fichiers/f-2");
    expect(screen.getByText("REV-2026-000001")).toBeInTheDocument();
    expect(screen.getByText("Mandat manquant à la date d'encaissement")).toBeInTheDocument();
  });

  it("calcule le mois choisi", async () => {
    post.mockReturnValueOnce(ok([]));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Calculer le mois" }));
    await waitFor(() => expect(post).toHaveBeenCalledWith("/api/v1/reversements/calculer", { body: { mois: expect.stringMatching(/^\d{4}-\d{2}$/) } }));
  });

  it("demande confirmation avant de valider, puis valide", async () => {
    post.mockReturnValueOnce(ok({ numero: "REV-2026-000002" }));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Valider le reversement de Mme Ngono" }));
    expect(post).not.toHaveBeenCalled();
    const dialogue = await screen.findByRole("alertdialog");
    await userEvent.click(within(dialogue).getByRole("button", { name: "Valider" }));
    await waitFor(() => expect(post).toHaveBeenCalledWith("/api/v1/reversements/{id}/valider", { params: { path: { id: "r-1" } } }));
  });

  it("exige une référence hors espèces avant de marquer payé", async () => {
    post.mockReturnValueOnce(ok({}));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Marquer payé le reversement de M. Fotso" }));
    const confirmer = screen.getByRole("button", { name: "Confirmer le paiement" });
    expect(confirmer).toBeDisabled();
    await userEvent.type(screen.getByLabelText("Référence du paiement"), "VIR-889");
    expect(confirmer).toBeEnabled();
    await userEvent.click(confirmer);
    await waitFor(() => expect(post).toHaveBeenCalledWith("/api/v1/reversements/{id}/payer", { params: { path: { id: "r-2" } }, body: { mode: "VIREMENT", reference: "VIR-889" } }));
  });

  it("affiche l'erreur métier traduite", async () => {
    post.mockReturnValueOnce(ko(409, "REVERSEMENT_MOIS_EN_COURS"));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Valider le reversement de Mme Ngono" }));
    await userEvent.click(within(await screen.findByRole("alertdialog")).getByRole("button", { name: "Valider" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("mois n'est pas terminé");
  });

  it("comptable : valide les reversements mais ne calcule ni ne paie", () => {
    role = "COMPTABLE";
    rendu();
    expect(screen.getAllByRole("button", { name: /^Valider/ }).length).toBeGreaterThan(0);
    expect(screen.queryByRole("button", { name: /Calculer le mois/ })).not.toBeInTheDocument();
    expect(screen.queryByRole("button", { name: /Marquer payé|Payer/ })).not.toBeInTheDocument();
  });

  it("gestionnaire de biens : ni calcul, ni validation, ni paiement des reversements", () => {
    role = "GESTIONNAIRE_BIENS";
    rendu();
    expect(screen.queryByRole("button", { name: /^Valider/ })).not.toBeInTheDocument();
    expect(screen.queryByRole("button", { name: /Calculer le mois/ })).not.toBeInTheDocument();
  });
});
