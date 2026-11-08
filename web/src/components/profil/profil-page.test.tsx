import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const put = vi.fn();
const post = vi.fn();
const replace = vi.fn();
let moi: Record<string, unknown> = {};
vi.mock("@/lib/api/client", () => ({ api: { PUT: (...a: unknown[]) => put(...a), POST: (...a: unknown[]) => post(...a) } }));
vi.mock("@/hooks/use-me", () => ({ useMe: () => ({ data: moi, error: null, refetch: vi.fn() }) }));
vi.mock("next/navigation", () => ({ useRouter: () => ({ replace, refresh: vi.fn(), push: vi.fn() }) }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));
vi.mock("qrcode", () => ({ default: { toDataURL: () => Promise.resolve("data:image/png;base64,iVBORw0KGgo=") } }));

import { ProfilPage } from "./profil-page";

const ok = (data: unknown = {}) => Promise.resolve({ data, response: new Response(null, { status: 200 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });
const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient()}><ProfilPage /></QueryClientProvider>, "fr");
const gestionnaire = { id: "u-1", nom: "Boris Chef", telephone: "+237690000001", email: "boris@x.cm", role: "GESTIONNAIRE", actif: true, deuxFacteursActif: false };

describe("ProfilPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    moi = { ...gestionnaire };
    vi.stubGlobal("fetch", vi.fn(() => Promise.resolve(new Response(null, { status: 204 }))));
  });

  describe("informations personnelles", () => {
    it("préremplit le profil et exige le mot de passe actuel", async () => {
      rendu();
      expect(screen.getByLabelText("Nom")).toHaveValue("Boris Chef");
      expect(screen.getByLabelText("Téléphone")).toHaveValue("+237690000001");
      await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
      expect(await screen.findByText("Saisissez votre mot de passe actuel.")).toBeInTheDocument();
      expect(put).not.toHaveBeenCalled();
    });

    it("refuse un profil sans contact", async () => {
      rendu();
      await userEvent.clear(screen.getByLabelText("Téléphone"));
      await userEvent.clear(screen.getByLabelText("E-mail"));
      await userEvent.type(screen.getAllByLabelText("Mot de passe actuel")[0], "Motdepasse123");
      await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
      expect(await screen.findByText("Un téléphone ou un e-mail est obligatoire.")).toBeInTheDocument();
      expect(put).not.toHaveBeenCalled();
    });

    it("envoie les informations avec le mot de passe actuel", async () => {
      put.mockReturnValueOnce(ok(gestionnaire));
      rendu();
      await userEvent.clear(screen.getByLabelText("Nom"));
      await userEvent.type(screen.getByLabelText("Nom"), "Boris Directeur");
      await userEvent.clear(screen.getByLabelText("E-mail"));
      await userEvent.type(screen.getAllByLabelText("Mot de passe actuel")[0], "Motdepasse123");
      await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
      await waitFor(() => expect(put).toHaveBeenCalledTimes(1));
      expect(put.mock.calls[0][1].body).toEqual({ nom: "Boris Directeur", telephone: "+237690000001", email: undefined, motDePasse: "Motdepasse123" });
    });

    it("affiche l'erreur du serveur (mot de passe faux)", async () => {
      put.mockReturnValueOnce(ko(422, "MOT_DE_PASSE_ACTUEL_INCORRECT"));
      rendu();
      await userEvent.type(screen.getAllByLabelText("Mot de passe actuel")[0], "faux");
      await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
      expect(await screen.findByRole("alert")).toHaveTextContent(/incorrect/i);
    });
  });

  describe("mot de passe", () => {
    it("exige 12 caractères, une confirmation identique et un mot de passe différent", async () => {
      rendu();
      await userEvent.type(screen.getByLabelText("Nouveau mot de passe"), "court");
      await userEvent.type(screen.getByLabelText("Confirmer le nouveau mot de passe"), "autre");
      await userEvent.click(screen.getByRole("button", { name: "Changer le mot de passe" }));
      expect(await screen.findByText("12 caractères minimum")).toBeInTheDocument();
      expect(screen.getByText("La confirmation ne correspond pas.")).toBeInTheDocument();
      expect(post).not.toHaveBeenCalled();
    });

    it("change le mot de passe, ferme la session et revient à la connexion", async () => {
      post.mockReturnValueOnce(ok());
      rendu();
      const [, actuel] = screen.getAllByLabelText("Mot de passe actuel");
      await userEvent.type(actuel, "Ancien-motdepasse1");
      await userEvent.type(screen.getByLabelText("Nouveau mot de passe"), "Nouveau-motdepasse2");
      await userEvent.type(screen.getByLabelText("Confirmer le nouveau mot de passe"), "Nouveau-motdepasse2");
      await userEvent.click(screen.getByRole("button", { name: "Changer le mot de passe" }));
      await waitFor(() => expect(replace).toHaveBeenCalledWith("/login?raison=mot-de-passe"));
      expect(post.mock.calls[0][0]).toBe("/api/v1/auth/mot-de-passe");
      expect(post.mock.calls[0][1].body).toEqual({ ancienMotDePasse: "Ancien-motdepasse1", nouveauMotDePasse: "Nouveau-motdepasse2" });
      expect(fetch).toHaveBeenCalledWith("/api/auth/logout", { method: "POST" });
    });
  });

  describe("double authentification (application)", () => {
    const cle = "ABCDEFGHIJKLMNOPABCDEFGHIJKLMNOP";

    it("configure en deux étapes : mot de passe, puis QR + premier code de l'application", async () => {
      post.mockReturnValueOnce(ok({ secret: cle, uri: `otpauth://totp/ImmoGest:x?secret=${cle}` })).mockReturnValueOnce(ok({ ...gestionnaire, deuxFacteursActif: true, deuxFacteursConfigure: true }));
      rendu();
      expect(screen.getByText("Désactivée")).toBeInTheDocument();
      await userEvent.click(screen.getByRole("button", { name: "Configurer" }));
      await userEvent.type(screen.getByLabelText("Mot de passe actuel", { selector: "#df-mdp" }), "Motdepasse123");
      await userEvent.click(screen.getByRole("button", { name: "Continuer" }));
      expect(await screen.findByTestId("cle-application")).toHaveTextContent("ABCD EFGH IJKL MNOP ABCD EFGH IJKL MNOP");
      expect(post.mock.calls[0][0]).toBe("/api/v1/auth/moi/double-authentification/demarrer");
      expect(post.mock.calls[0][1].body).toEqual({ motDePasse: "Motdepasse123" });

      const confirmer = screen.getByRole("button", { name: "Confirmer et activer" });
      expect(confirmer).toBeDisabled();
      await userEvent.type(screen.getByLabelText("Code de l'application"), "12a3456"); // seuls les chiffres sont gardés
      expect(screen.getByLabelText("Code de l'application")).toHaveValue("123456");
      await userEvent.click(confirmer);
      await waitFor(() => expect(post).toHaveBeenCalledTimes(2));
      expect(post.mock.calls[1][0]).toBe("/api/v1/auth/moi/double-authentification/activer");
      expect(post.mock.calls[1][1].body).toEqual({ code: "123456" });
    });

    it("affiche un code refusé sans activer", async () => {
      post.mockReturnValueOnce(ok({ secret: cle, uri: "otpauth://totp/x" })).mockReturnValueOnce(ko(401, "OTP_INVALIDE"));
      rendu();
      await userEvent.click(screen.getByRole("button", { name: "Configurer" }));
      await userEvent.type(screen.getByLabelText("Mot de passe actuel", { selector: "#df-mdp" }), "Motdepasse123");
      await userEvent.click(screen.getByRole("button", { name: "Continuer" }));
      await userEvent.type(await screen.findByLabelText("Code de l'application"), "000000");
      await userEvent.click(screen.getByRole("button", { name: "Confirmer et activer" }));
      expect(await screen.findByRole("alert")).toBeInTheDocument();
      expect(screen.getByRole("button", { name: "Confirmer et activer" })).toBeInTheDocument();
    });

    it("désactive avec le mot de passe ET le code de l'application", async () => {
      moi = { ...gestionnaire, deuxFacteursActif: true, deuxFacteursConfigure: true };
      post.mockReturnValueOnce(ok({ ...gestionnaire, deuxFacteursActif: false }));
      rendu();
      expect(screen.getByText("Activée")).toBeInTheDocument();
      await userEvent.click(screen.getByRole("button", { name: "Désactiver" }));
      const valider = screen.getAllByRole("button", { name: "Désactiver" })[0];
      expect(valider).toBeDisabled();
      await userEvent.type(screen.getByLabelText("Mot de passe actuel", { selector: "#df-mdp" }), "Motdepasse123");
      await userEvent.type(screen.getByLabelText("Code de l'application"), "123456");
      await userEvent.click(screen.getAllByRole("button", { name: "Désactiver" })[0]);
      await waitFor(() => expect(post).toHaveBeenCalledTimes(1));
      expect(post.mock.calls[0][0]).toBe("/api/v1/auth/moi/double-authentification/desactiver");
      expect(post.mock.calls[0][1].body).toEqual({ motDePasse: "Motdepasse123", code: "123456" });
    });

    it("ne propose pas la désactivation à un employé restreint", () => {
      moi = { ...gestionnaire, role: "ENCAISSEUR", deuxFacteursActif: true, deuxFacteursConfigure: true };
      rendu();
      expect(screen.getByText("Obligatoire pour votre rôle : elle ne peut pas être désactivée.")).toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Désactiver" })).not.toBeInTheDocument();
    });

    it("invite à configurer maintenant quand elle est exigée mais pas encore configurée", () => {
      moi = { ...gestionnaire, role: "COMPTABLE", deuxFacteursActif: true, deuxFacteursConfigure: false };
      rendu();
      expect(screen.getByText("À configurer")).toBeInTheDocument();
      expect(screen.getByRole("button", { name: "Configurer maintenant" })).toBeInTheDocument();
    });

    it("n'est pas proposée à un locataire", () => {
      moi = { ...gestionnaire, role: "LOCATAIRE" };
      rendu();
      expect(screen.getByText("La double authentification n'est proposée qu'aux comptes d'employés.")).toBeInTheDocument();
      expect(screen.queryByRole("button", { name: "Configurer" })).not.toBeInTheDocument();
    });
  });
});
