import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

vi.mock("qrcode", () => ({ default: { toDataURL: () => Promise.resolve("data:image/png;base64,iVBORw0KGgo=") } }));

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

  it("enchaîne sur le code de l'application quand la double authentification est requise", async () => {
    fetchMock
      .mockReturnValueOnce(reponse(200, { deuxFacteursRequis: true, defi: "defi-1" }))
      .mockReturnValueOnce(reponse(200, { role: "GESTIONNAIRE", deuxFacteursRequis: false }));
    renderIntl(<LoginForm />);
    await saisir("+237600000001");

    await userEvent.type(await screen.findByLabelText("Code de l'application"), "123456");
    await userEvent.click(screen.getByRole("button", { name: "Vérifier" }));

    await waitFor(() => expect(replace).toHaveBeenCalledWith("/tableau-de-bord"));
    expect(fetchMock.mock.calls[1][0]).toBe("/api/auth/deux-facteurs");
    expect(JSON.parse(fetchMock.mock.calls[1][1].body)).toEqual({ defi: "defi-1", code: "123456" });
  });

  it("refuse un code qui n'a pas 6 chiffres", async () => {
    fetchMock.mockReturnValueOnce(reponse(200, { deuxFacteursRequis: true, defi: "defi-1" }));
    renderIntl(<LoginForm />);
    await saisir();

    await userEvent.type(await screen.findByLabelText("Code de l'application"), "12ab");
    await userEvent.click(screen.getByRole("button", { name: "Vérifier" }));

    expect(await screen.findByText("Le code comporte 6 chiffres")).toBeInTheDocument();
    expect(fetchMock).toHaveBeenCalledTimes(1);
  });

  it("affiche le code refusé et reste à l'étape du code", async () => {
    fetchMock
      .mockReturnValueOnce(reponse(200, { deuxFacteursRequis: true, defi: "defi-1" }))
      .mockReturnValueOnce(reponse(401, { code: "OTP_INVALIDE" }));
    renderIntl(<LoginForm />);
    await saisir();
    await userEvent.type(await screen.findByLabelText("Code de l'application"), "000000");
    await userEvent.click(screen.getByRole("button", { name: "Vérifier" }));

    expect(await screen.findByRole("alert")).toBeInTheDocument();
    expect(replace).not.toHaveBeenCalled();
    expect(screen.getByLabelText("Code de l'application")).toBeInTheDocument();
  });

  it("configure l'application à la première connexion (QR, clé) puis ouvre la session", async () => {
    fetchMock
      .mockReturnValueOnce(reponse(200, { enrolementRequis: true, defi: "defi-2" }))
      .mockReturnValueOnce(reponse(200, { secret: "ABCDEFGHIJKLMNOPABCDEFGHIJKLMNOP", uri: "otpauth://totp/ImmoGest:a?secret=ABCDEFGHIJKLMNOPABCDEFGHIJKLMNOP" }))
      .mockReturnValueOnce(reponse(200, { role: "ENCAISSEUR", deuxFacteursRequis: false }));
    renderIntl(<LoginForm />);
    await saisir("+237600000002");

    expect(await screen.findByText("Configurez votre application d'authentification")).toBeInTheDocument();
    expect(fetchMock.mock.calls[1][0]).toBe("/api/auth/enrolement");
    expect(JSON.parse(fetchMock.mock.calls[1][1].body)).toEqual({ defi: "defi-2" });
    expect(screen.getByTestId("cle-application")).toHaveTextContent("ABCD EFGH IJKL MNOP ABCD EFGH IJKL MNOP");
    expect(await screen.findByAltText(/Code QR/)).toBeInTheDocument();

    await userEvent.type(screen.getByLabelText("Code de l'application"), "654321");
    await userEvent.click(screen.getByRole("button", { name: "Confirmer et se connecter" }));
    await waitFor(() => expect(replace).toHaveBeenCalledWith("/tableau-de-bord"));
    expect(fetchMock.mock.calls[2][0]).toBe("/api/auth/enrolement/confirmer");
    expect(JSON.parse(fetchMock.mock.calls[2][1].body)).toEqual({ defi: "defi-2", code: "654321" });
  });

  it("ne garde aucun défi en revenant à l'écran du mot de passe", async () => {
    fetchMock.mockReturnValueOnce(reponse(200, { deuxFacteursRequis: true, defi: "defi-1" }));
    renderIntl(<LoginForm />);
    await saisir();
    await userEvent.click(await screen.findByRole("button", { name: "Retour" }));
    expect(screen.getByLabelText("Téléphone ou e-mail")).toBeInTheDocument();
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
