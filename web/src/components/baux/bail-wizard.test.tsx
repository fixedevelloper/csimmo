import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const push = vi.fn();
vi.mock("next/navigation", () => ({ useRouter: () => ({ push, back: vi.fn() }) }));
const post = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { POST: (...a: unknown[]) => post(...a) } }));
vi.mock("@/hooks/use-patrimoine", () => ({
  useLots: () => ({ data: { content: [{ id: "l-1", reference: "A1", immeubleNom: "Palmiers", loyerIndicatif: 150000 }] } }),
}));
vi.mock("@/hooks/use-tiers", () => ({
  useLocataires: () => ({ data: { content: [{ id: "t-1", nom: "Paul Mbarga", telephone: "+237690000001" }] } }),
  useGarants: () => ({ data: [{ id: "g-1", nom: "Marie", lien: "Mère" }] }),
}));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

import { BailWizard } from "./bail-wizard";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 200 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });
const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient()}><BailWizard /></QueryClientProvider>, "fr");

async function jusquAuxConditions() {
  await userEvent.click(screen.getByRole("button", { name: /Palmiers — A1/ }));
  await userEvent.click(screen.getByRole("button", { name: "Suivant" }));
  await userEvent.click(screen.getByRole("button", { name: /Paul Mbarga/ }));
  await userEvent.click(screen.getByText("Marie"));
  await userEvent.click(screen.getByRole("button", { name: "Suivant" }));
}

describe("BailWizard", () => {
  beforeEach(() => vi.clearAllMocks());

  it("n'avance pas sans lot ni locataire", async () => {
    rendu();
    expect(screen.getByRole("button", { name: "Suivant" })).toBeDisabled();
    await userEvent.click(screen.getByRole("button", { name: /Palmiers — A1/ }));
    await userEvent.click(screen.getByRole("button", { name: "Suivant" }));
    expect(screen.getByRole("button", { name: "Suivant" })).toBeDisabled();
  });

  it("valide les conditions puis crée le bail avec le loyer indicatif du lot", async () => {
    post.mockReturnValueOnce(ok({ id: "b-1" }));
    rendu();
    await jusquAuxConditions();

    expect(screen.getByLabelText("Loyer mensuel (FCFA)")).toHaveValue("150000");
    await userEvent.click(screen.getByRole("button", { name: "Suivant" }));
    expect(await screen.findByText("La date de début est obligatoire.")).toBeInTheDocument();

    await userEvent.type(screen.getByLabelText("Date de début"), "2026-01-01");
    await userEvent.clear(screen.getByLabelText("Mois d'avance"));
    await userEvent.type(screen.getByLabelText("Mois d'avance"), "2");
    await userEvent.click(screen.getByRole("button", { name: "Suivant" }));
    expect(screen.getByText("Palmiers — A1")).toBeInTheDocument();
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer le brouillon" }));

    await waitFor(() => expect(push).toHaveBeenCalledWith("/baux/b-1"));
    expect(post).toHaveBeenCalledTimes(1);
    expect(post.mock.calls[0][1].body).toMatchObject({
      lotId: "l-1", locataireId: "t-1", garantIds: ["g-1"], dateDebut: "2026-01-01", loyer: 150000, charges: 0,
      periodicite: "MENSUELLE", jourEcheance: 5, moisAvance: 2, preavisMois: 3,
    });
  });

  it("affiche l'erreur métier traduite quand le lot est déjà occupé", async () => {
    post.mockReturnValueOnce(ko(409, "BAIL_LOT_DEJA_OCCUPE"));
    rendu();
    await jusquAuxConditions();
    await userEvent.type(screen.getByLabelText("Date de début"), "2026-01-01");
    await userEvent.click(screen.getByRole("button", { name: "Suivant" }));
    await userEvent.click(screen.getByRole("button", { name: "Créer et activer" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("déjà un bail en vigueur");
    expect(push).not.toHaveBeenCalled();
  });
});
