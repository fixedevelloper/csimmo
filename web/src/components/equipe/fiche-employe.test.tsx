import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

let employe: Record<string, unknown> = {};
vi.mock("next/navigation", () => ({ useParams: () => ({ id: "u-1" }) }));
vi.mock("@/hooks/use-equipe", () => ({
  useEmploye: () => ({ data: employe, error: null, refetch: vi.fn() }),
  useAffectations: () => ({ data: ["i-1", "i-2"], isPending: false }),
  useImmeublesChoix: () => ({ data: [{ id: "i-1", nom: "Résidence Palmiers" }], isPending: false }),
  useEncaissementsDe: () => ({
    isPending: false,
    error: null,
    refetch: vi.fn(),
    data: {
      content: [
        { id: "p-1", datePaiement: "2026-11-05", locataireNom: "Marie Tenant", bailNumero: "BAIL-2026-0001", mode: "ESPECES", montant: 50000, contrePasse: false },
        { id: "p-2", datePaiement: "2026-11-04", locataireNom: "Paul Autre", bailNumero: "BAIL-2026-0002", mode: "VIREMENT", montant: 30000, contrePasse: true },
      ],
      page: 0, size: 10, totalElements: 2,
    },
  }),
  useActivite: () => ({
    isPending: false,
    error: null,
    refetch: vi.fn(),
    data: { content: [{ id: "a-1", date: "2026-11-05T09:00:00Z", action: "FINANCE.PAIEMENT_ENREGISTRE", entite: "paiement" }], page: 0, size: 10, totalElements: 1 },
  }),
}));
vi.mock("@/hooks/use-finance", () => ({
  useCaisse: () => ({ isPending: false, data: [{ agentId: "u-1", total: 50000, operations: 1 }, { agentId: "u-9", total: 999, operations: 7 }] }),
}));
vi.mock("@/hooks/use-patrimoine", () => ({ useImmeuble: (id: string) => ({ data: { nom: id === "i-1" ? "Résidence Palmiers" : "Immeuble Bonanjo" } }) }));
vi.mock("@/lib/api/client", () => ({ api: { PUT: vi.fn() } }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

import { FicheEmploye } from "./fiche-employe";

const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient()}><FicheEmploye /></QueryClientProvider>, "fr");

describe("FicheEmploye", () => {
  beforeEach(() => {
    employe = { id: "u-1", nom: "Awa Encaisseuse", telephone: "+237611111111", role: "ENCAISSEUR", actif: true };
  });

  it("montre l'identité, les immeubles, la caisse du jour, les encaissements et l'activité d'un encaisseur", () => {
    rendu();
    expect(screen.getByRole("heading", { name: /Awa Encaisseuse/, level: 1 })).toBeInTheDocument();
    expect(screen.getByText(/Encaisseur · \+237611111111/)).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "Résidence Palmiers" })).toHaveAttribute("href", "/patrimoine/i-1");
    expect(screen.getByRole("link", { name: "Immeuble Bonanjo" })).toBeInTheDocument();
    // seule la ligne de caisse de cet employé compte
    expect(screen.getByTestId("caisse-total").textContent?.replace(/\s/g, " ")).toBe("50 000 FCFA");
    expect(screen.getByText("1 opération aujourd'hui")).toBeInTheDocument();
    const ligne = screen.getByText("Marie Tenant").closest("tr") as HTMLElement;
    expect(within(ligne).getByText("BAIL-2026-0001")).toBeInTheDocument();
    expect(screen.getByText("Paul Autre").closest("tr")).toHaveTextContent("Paiement contre-passé.");
    expect(screen.getByText("FINANCE.PAIEMENT_ENREGISTRE")).toBeInTheDocument();
  });

  it("propose de modifier les immeubles d'un employé restreint", async () => {
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Modifier les immeubles" }));
    expect(await screen.findByText("Immeubles affectés à Awa Encaisseuse")).toBeInTheDocument();
  });

  it("un comptable n'a ni caisse ni encaissements, mais son activité et ses immeubles", () => {
    employe = { id: "u-1", nom: "Carl Comptable", email: "carl@x.cm", role: "COMPTABLE", actif: true };
    rendu();
    expect(screen.getByText("Immeubles affectés")).toBeInTheDocument();
    expect(screen.queryByText("Caisse du jour (espèces)")).not.toBeInTheDocument();
    expect(screen.queryByText("Encaissements récents")).not.toBeInTheDocument();
    expect(screen.getByText("Activité récente")).toBeInTheDocument();
  });

  it("un gestionnaire (accès total) n'a pas d'immeubles affectés", () => {
    employe = { id: "u-1", nom: "Boris Chef", role: "GESTIONNAIRE", actif: true };
    rendu();
    expect(screen.queryByText("Immeubles affectés")).not.toBeInTheDocument();
    expect(screen.getByText("Caisse du jour (espèces)")).toBeInTheDocument();
  });
});
