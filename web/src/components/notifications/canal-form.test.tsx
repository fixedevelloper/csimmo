import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const put = vi.fn();
const post = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { PUT: (...a: unknown[]) => put(...a), POST: (...a: unknown[]) => post(...a) } }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

import { CanalForm } from "./canal-form";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 200 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });

const whatsapp = {
  canal: "WHATSAPP" as const,
  actif: false,
  configure: true,
  champs: [
    { nom: "phoneNumberId", secret: false, requis: true, valeur: "123", renseigne: true },
    { nom: "token", secret: true, requis: true, renseigne: true },
  ],
};
const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient()}><CanalForm canal={whatsapp} /></QueryClientProvider>, "fr");

describe("CanalForm", () => {
  beforeEach(() => vi.clearAllMocks());

  it("n'affiche jamais un secret : champ masqué et vide, mention « renseigné »", () => {
    rendu();
    const token = screen.getByLabelText(/Jeton d'accès/);
    expect(token).toHaveAttribute("type", "password");
    expect(token).toHaveValue("");
    expect(token).toHaveAttribute("placeholder", expect.stringContaining("Renseigné"));
    expect(screen.getByLabelText(/Identifiant du numéro WhatsApp/)).toHaveValue("123");
  });

  it("enregistre avec le secret vide (conservé côté serveur) et l'état actif", async () => {
    put.mockReturnValueOnce(ok({}));
    rendu();
    await userEvent.click(screen.getByLabelText("Activer WhatsApp"));
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
    await waitFor(() => expect(put).toHaveBeenCalledTimes(1));
    expect(put.mock.calls[0][1].body).toEqual({ actif: true, champs: { phoneNumberId: "123", token: "" } });
  });

  it("affiche l'erreur métier traduite", async () => {
    put.mockReturnValueOnce(ko(400, "NOTIF_CONFIG_INCOMPLETE"));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("Renseignez tous les champs obligatoires");
  });

  it("envoie un test et affiche le résultat", async () => {
    post.mockReturnValueOnce(ok({ succes: false, erreur: "HTTP 401" })).mockReturnValueOnce(ok({ succes: true }));
    rendu();
    expect(screen.getByRole("button", { name: "Envoyer un test" })).toBeDisabled();
    await userEvent.type(screen.getByLabelText("Numéro de test"), "+237699112233");
    await userEvent.click(screen.getByRole("button", { name: "Envoyer un test" }));
    expect(await screen.findByRole("status")).toHaveTextContent("Échec du test : HTTP 401");
    expect(post.mock.calls[0][1].body).toEqual({ destinataire: "+237699112233" });
    await userEvent.click(screen.getByRole("button", { name: "Envoyer un test" }));
    await waitFor(() => expect(screen.getByRole("status")).toHaveTextContent("Test envoyé avec succès."));
  });
});
