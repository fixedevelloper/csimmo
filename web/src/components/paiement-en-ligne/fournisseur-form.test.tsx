import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const put = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { PUT: (...a: unknown[]) => put(...a) } }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

import { FournisseurForm } from "./fournisseur-form";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 200 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });

const fournisseur = {
  code: "simule",
  actif: false,
  configure: true,
  champs: [
    { nom: "identifiantMarchand", secret: false, requis: true, valeur: "MARCHAND-1", renseigne: true },
    { nom: "secretWebhook", secret: true, requis: true, renseigne: true },
  ],
};
const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient()}><FournisseurForm fournisseur={fournisseur} /></QueryClientProvider>, "fr");

describe("FournisseurForm", () => {
  beforeEach(() => vi.clearAllMocks());

  it("affiche l'adresse du webhook et ne montre jamais un secret", () => {
    rendu();
    expect(screen.getByText(/\/api\/v1\/webhooks\/simule/)).toBeInTheDocument();
    const secret = screen.getByLabelText(/Secret de signature/);
    expect(secret).toHaveAttribute("type", "password");
    expect(secret).toHaveValue("");
    expect(secret).toHaveAttribute("placeholder", expect.stringContaining("Renseigné"));
    expect(screen.getByLabelText(/Identifiant marchand/)).toHaveValue("MARCHAND-1");
  });

  it("enregistre avec le secret vide (conservé côté serveur)", async () => {
    put.mockReturnValueOnce(ok({}));
    rendu();
    await userEvent.click(screen.getByLabelText("Activer Agrégateur simulé (développement)"));
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
    await waitFor(() => expect(put).toHaveBeenCalledWith("/api/v1/paiements-en-ligne/fournisseurs/{code}", {
      params: { path: { code: "simule" } },
      body: { actif: true, champs: { identifiantMarchand: "MARCHAND-1", secretWebhook: "" } },
    }));
  });

  it("affiche l'erreur métier traduite", async () => {
    put.mockReturnValueOnce(ko(400, "PAIEMENT_EN_LIGNE_CONFIG_INCOMPLETE"));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("Renseignez tous les champs obligatoires");
  });
});
