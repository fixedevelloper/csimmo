import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const post = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { POST: (...a: unknown[]) => post(...a) } }));
vi.mock("@/hooks/use-finance", () => ({
  useBauxDuLocataire: (id: string) => ({ data: id ? [{ id: "b-1", numero: "BAIL-2026-0001", locataireNom: "Paul", immeubleNom: "Palmiers", lotReference: "A1" }] : undefined }),
}));
vi.mock("@/hooks/use-tiers", () => ({ useLocataires: () => ({ data: { content: [{ id: "t-1", nom: "Paul" }] } }) }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

import { PaiementForm } from "./paiement-form";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 201 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });
const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient()}><PaiementForm /></QueryClientProvider>, "fr");
const cleEnvoyee = (appel: number) => post.mock.calls[appel][1].params.header["Idempotency-Key"] as string;

describe("PaiementForm", () => {
  beforeEach(() => vi.clearAllMocks());

  it("exige bail, montant entier et référence hors espèces, sans appeler l'API", async () => {
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer l'encaissement" }));
    expect(screen.getByText("Choisissez un bail.")).toBeInTheDocument();
    expect(screen.getByText("Montant entier en FCFA.")).toBeInTheDocument();

    await userEvent.selectOptions(screen.getByLabelText("Locataire"), "t-1");
    await userEvent.type(screen.getByLabelText("Montant (FCFA)"), "50000");
    await userEvent.selectOptions(screen.getByLabelText("Mode"), "MTN_MOMO");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer l'encaissement" }));
    expect(screen.getByText("La référence est obligatoire pour ce mode.")).toBeInTheDocument();
    expect(post).not.toHaveBeenCalled();
  });

  it("envoie l'encaissement avec une Idempotency-Key, renouvelée après un succès", async () => {
    post.mockReturnValueOnce(ok({ id: "p-1" })).mockReturnValueOnce(ok({ id: "p-2" }));
    rendu();
    await userEvent.selectOptions(screen.getByLabelText("Locataire"), "t-1");
    await userEvent.type(screen.getByLabelText("Montant (FCFA)"), "50 000");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer l'encaissement" }));
    await waitFor(() => expect(post).toHaveBeenCalledTimes(1));
    expect(post.mock.calls[0][1].body).toMatchObject({ bailId: "b-1", montant: 50000, mode: "ESPECES" });
    await waitFor(() => expect(screen.getByLabelText("Montant (FCFA)")).toHaveValue(""));

    await userEvent.type(screen.getByLabelText("Montant (FCFA)"), "1000");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer l'encaissement" }));
    await waitFor(() => expect(post).toHaveBeenCalledTimes(2));
    expect(cleEnvoyee(1)).not.toBe(cleEnvoyee(0));
  });

  it("garde la même clé après une panne réseau (le renvoi ne peut pas doubler) et affiche l'erreur métier", async () => {
    post.mockRejectedValueOnce(new TypeError("fetch failed")).mockReturnValueOnce(ko(400, "PAIEMENT_REFERENCE_REQUISE"));
    rendu();
    await userEvent.selectOptions(screen.getByLabelText("Locataire"), "t-1");
    await userEvent.type(screen.getByLabelText("Montant (FCFA)"), "1000");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer l'encaissement" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("Impossible de joindre le serveur");

    await userEvent.click(screen.getByRole("button", { name: "Enregistrer l'encaissement" }));
    await waitFor(() => expect(post).toHaveBeenCalledTimes(2));
    expect(cleEnvoyee(1)).toBe(cleEnvoyee(0));
    expect(await screen.findByRole("alert")).toHaveTextContent("référence");
  });
});
