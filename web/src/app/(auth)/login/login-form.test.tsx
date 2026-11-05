import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const replace = vi.fn();
const refresh = vi.fn();
let parametres = new URLSearchParams();
vi.mock("next/navigation", () => ({
  useRouter: () => ({ replace, refresh }),
  useSearchParams: () => parametres,
}));

import { LoginForm } from "./login-form";

function reponse(status: number, corps: unknown) {
  return Promise.resolve(new Response(JSON.stringify(corps), { status, headers: { "Content-Type": "application/json" } }));
}

async function saisir(identifiant = "admin@immogest.local", motDePasse = "secret") {
  await userEvent.type(screen.getByLabelText("Téléphone ou e-mail"), identifiant);
  await userEvent.type(screen.getByLabelText("Mot de passe"), motDePasse);
  await userEvent.click(screen.getByRole("button", { name: "Se connecter" }));
}

describe("LoginForm", () => {
  const fetchMock = vi.fn();
  beforeEach(() => {
    parametres = new URLSearchParams();
    vi.stubGlobal("fetch", fetchMock);
  });
  afterEach(() => {
    vi.unstubAllGlobals();
    vi.clearAllMocks();
  });

  it("exige l'identifiant et le mot de passe", async () => {
    renderIntl(<LoginForm />);
    await userEvent.click(screen.getByRole("button", { name: "Se connecter" }));
    expect(await screen.findByText("L'identifiant est obligatoire")).toBeInTheDocument();
    expect(screen.getByText("Le mot de passe est obligatoire")).toBeInTheDocument();
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("connecte et redirige vers le tableau de bord", async () => {
    fetchMock.mockReturnValue(reponse(200, { role: "ADMIN", deuxFacteursRequis: false }));
    renderIntl(<LoginForm />);
    await saisir();

    await waitFor(() => expect(replace).toHaveBeenCalledWith("/tableau-de-bord"));
    expect(fetchMock).toHaveBeenCalledWith("/api/auth/login", expect.objectContaining({ method: "POST" }));
    expect(JSON.parse(fetchMock.mock.calls[0][1].body)).toEqual({ identifiant: "admin@immogest.local", motDePasse: "secret" });
  });

  it("traduit le code d'erreur du serveur (fr)", async () => {
    fetchMock.mockReturnValue(reponse(401, { code: "IDENTIFIANTS_INVALIDES" }));
    renderIntl(<LoginForm />);
    await saisir();

    expect(await screen.findByRole("alert")).toHaveTextContent("Identifiant ou mot de passe incorrect.");
    expect(replace).not.toHaveBeenCalled();
  });

  it("traduit le code d'erreur du serveur (en)", async () => {
    fetchMock.mockReturnValue(reponse(423, { code: "COMPTE_VERROUILLE" }));
    renderIntl(<LoginForm />, "en");
    await userEvent.type(screen.getByLabelText("Phone or e-mail"), "a@b.c");
    await userEvent.type(screen.getByLabelText("Password"), "x");
    await userEvent.click(screen.getByRole("button", { name: "Sign in" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("temporarily locked");
  });

  it("signale un serveur injoignable", async () => {
    fetchMock.mockRejectedValue(new TypeError("Failed to fetch"));
    renderIntl(<LoginForm />);
    await saisir();

    expect(await screen.findByRole("alert")).toHaveTextContent("Impossible de joindre le serveur");
  });

  it("enchaîne sur le code SMS quand la double authentification est requise", async () => {
    fetchMock
      .mockReturnValueOnce(reponse(200, { deuxFacteursRequis: true }))
      .mockReturnValueOnce(reponse(200, { role: "GESTIONNAIRE", deuxFacteursRequis: false }));
    renderIntl(<LoginForm />);
    await saisir("+237600000001");

    await userEvent.type(await screen.findByLabelText("Code reçu par SMS"), "123456");
    await userEvent.click(screen.getByRole("button", { name: "Vérifier" }));

    await waitFor(() => expect(replace).toHaveBeenCalledWith("/tableau-de-bord"));
    expect(fetchMock.mock.calls[1][0]).toBe("/api/auth/otp");
    expect(JSON.parse(fetchMock.mock.calls[1][1].body)).toEqual({ identifiant: "+237600000001", code: "123456" });
  });

  it("refuse un code SMS qui n'a pas 6 chiffres", async () => {
    fetchMock.mockReturnValueOnce(reponse(200, { deuxFacteursRequis: true }));
    renderIntl(<LoginForm />);
    await saisir();

    await userEvent.type(await screen.findByLabelText("Code reçu par SMS"), "12ab");
    await userEvent.click(screen.getByRole("button", { name: "Vérifier" }));

    expect(await screen.findByText("Le code comporte 6 chiffres")).toBeInTheDocument();
    expect(fetchMock).toHaveBeenCalledTimes(1);
  });

  it("revient à la page demandée avant la connexion", async () => {
    parametres = new URLSearchParams("next=/baux");
    fetchMock.mockReturnValue(reponse(200, { role: "ADMIN", deuxFacteursRequis: false }));
    renderIntl(<LoginForm />);
    await saisir();

    await waitFor(() => expect(replace).toHaveBeenCalledWith("/baux"));
  });

  it.each(["//evil.example", "https://evil.example", "/login", "evil"])(
    "ignore la destination non sûre %s",
    async (next) => {
      parametres = new URLSearchParams({ next });
      fetchMock.mockReturnValue(reponse(200, { role: "ADMIN", deuxFacteursRequis: false }));
      renderIntl(<LoginForm />);
      await saisir();

      await waitFor(() => expect(replace).toHaveBeenCalledWith("/tableau-de-bord"));
    },
  );

  it("explique la déconnexion pour inactivité", () => {
    parametres = new URLSearchParams("raison=inactivite");
    renderIntl(<LoginForm />);
    expect(screen.getByRole("status")).toHaveTextContent("30 minutes d'inactivité");
  });
});
