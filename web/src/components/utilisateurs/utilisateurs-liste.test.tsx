import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const post = vi.fn();
const put = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { POST: (...a: unknown[]) => post(...a), PUT: (...a: unknown[]) => put(...a) } }));
vi.mock("@/hooks/use-utilisateurs", () => ({
  useUtilisateurs: () => ({
    isPending: false,
    error: null,
    refetch: vi.fn(),
    data: {
      content: [
        { id: "u-1", nom: "Marie Tenant", telephone: "+237612345678", role: "LOCATAIRE", actif: true, verrouille: true },
        { id: "u-2", nom: "Paul Off", email: "paul@x.cm", role: "GESTIONNAIRE", actif: false, deuxFacteursActif: false },
      ],
      page: 0, size: 20, totalElements: 2,
    },
  }),
}));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

import { UtilisateursListe } from "./utilisateurs-liste";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 201 }) });
const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient()}><UtilisateursListe /></QueryClientProvider>, "fr");

describe("UtilisateursListe", () => {
  beforeEach(() => vi.clearAllMocks());

  it("affiche les comptes avec leur rôle et leur statut", () => {
    rendu();
    expect(screen.getByText("Marie Tenant")).toBeInTheDocument();
    expect(screen.getByText("Locataire")).toBeInTheDocument();
    expect(screen.getByText("Verrouillé")).toBeInTheDocument();
    expect(screen.getByText("Désactivé")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Déverrouiller" })).toBeInTheDocument();
  });

  it("refuse un compte sans nom, sans contact ou avec un mot de passe trop court, sans appeler l'API", async () => {
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Nouvel utilisateur" }));
    await userEvent.click(screen.getByRole("button", { name: "Créer le compte" }));
    expect(await screen.findByText("Le nom est obligatoire.")).toBeInTheDocument();
    expect(screen.getByText("Le mot de passe contient entre 8 et 100 caractères.")).toBeInTheDocument();

    await userEvent.type(screen.getByLabelText("Nom"), "Paul");
    await userEvent.type(screen.getByLabelText("Mot de passe", { exact: true }), "Abcdef12!");
    await userEvent.click(screen.getByRole("button", { name: "Créer le compte" }));
    expect(await screen.findByText("Un téléphone ou un e-mail est obligatoire.")).toBeInTheDocument();
    expect(post).not.toHaveBeenCalled();
  });

  it("crée un compte locataire avec le mot de passe généré puis l'affiche une seule fois", async () => {
    post.mockReturnValueOnce(ok({ id: "u-9" }));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Nouvel utilisateur" }));
    await userEvent.type(screen.getByLabelText("Nom"), "Jean Locataire");
    await userEvent.type(screen.getByLabelText("Téléphone"), "+237677000111");
    await userEvent.click(screen.getByRole("button", { name: "Générer un mot de passe" }));
    await userEvent.click(screen.getByRole("button", { name: "Créer le compte" }));

    await waitFor(() => expect(post).toHaveBeenCalledTimes(1));
    const corps = post.mock.calls[0][1].body;
    expect(corps).toMatchObject({ nom: "Jean Locataire", telephone: "+237677000111", role: "LOCATAIRE" });
    expect(corps.email).toBeUndefined();
    expect(corps.motDePasse).toHaveLength(12);

    expect(await screen.findByTestId("recap-mot-de-passe")).toHaveTextContent(corps.motDePasse);
    expect(screen.getByTestId("recap-identifiant")).toHaveTextContent("+237677000111");
    await userEvent.click(screen.getByRole("button", { name: "Fermer" }));
    expect(screen.queryByTestId("recap-mot-de-passe")).not.toBeInTheDocument();
  });

  it("active un compte désactivé en renvoyant tous ses champs", async () => {
    put.mockReturnValueOnce(ok({}));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Activer" }));
    await waitFor(() => expect(put).toHaveBeenCalledTimes(1));
    expect(put.mock.calls[0][1].body).toMatchObject({ nom: "Paul Off", email: "paul@x.cm", role: "GESTIONNAIRE", actif: true });
  });
});
