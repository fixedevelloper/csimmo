import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const post = vi.fn();
const put = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { POST: (...a: unknown[]) => post(...a), PUT: (...a: unknown[]) => put(...a) } }));
vi.mock("@/hooks/use-equipe", () => ({
  useEmployes: () => ({
    isPending: false,
    error: null,
    refetch: vi.fn(),
    data: {
      content: [
        { id: "u-1", nom: "Awa Encaisseuse", telephone: "+237611111111", role: "ENCAISSEUR", actif: true, verrouille: false },
        { id: "u-2", nom: "Boris Chef", telephone: "+237622222222", role: "GESTIONNAIRE", actif: true, verrouille: false },
        { id: "u-3", nom: "Carine Biens", telephone: "+237633000000", role: "GESTIONNAIRE_BIENS", actif: true, verrouille: false },
      ],
      page: 0, size: 20, totalElements: 3,
    },
  }),
  useAffectations: (id: string) => ({ data: id === "u-1" ? ["i-1", "i-2"] : id === "u-3" ? ["i-3"] : [], isPending: false }),
  useImmeublesChoix: () => ({
    isPending: false,
    data: [
      { id: "i-1", nom: "Résidence Palmiers", adresse: "1 rue", ville: "Douala" },
      { id: "i-2", nom: "Immeuble Bonanjo", adresse: "2 rue", ville: "Douala" },
      { id: "i-3", nom: "Villa Akwa", adresse: "3 rue", ville: "Douala" },
    ],
  }),
}));
vi.mock("@/hooks/use-actions-utilisateur", () => ({ useActionsUtilisateur: () => ({ basculerActif: vi.fn(), deverrouiller: vi.fn() }) }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

import { EquipeListe } from "./equipe-liste";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 200 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });
const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient()}><EquipeListe /></QueryClientProvider>, "fr");

describe("EquipeListe", () => {
  beforeEach(() => vi.clearAllMocks());

  it("affiche les employés, leur rôle et le nombre d'immeubles affectés", () => {
    rendu();
    const encaisseuse = screen.getByText("Awa Encaisseuse").closest("tr") as HTMLElement;
    expect(within(encaisseuse).getByText("Encaisseur")).toBeInTheDocument();
    expect(within(encaisseuse).getByText("2 immeubles")).toBeInTheDocument();
    const chef = screen.getByText("Boris Chef").closest("tr") as HTMLElement;
    expect(within(chef).getByText("Gestionnaire (accès total)")).toBeInTheDocument();
    expect(within(chef).getByText("Tous")).toBeInTheDocument();
    expect(within(chef).queryByRole("button", { name: "Immeubles" })).not.toBeInTheDocument();
  });

  it("affiche un gestionnaire de biens avec ses immeubles et le bouton pour les modifier", () => {
    rendu();
    const ligne = screen.getByText("Carine Biens").closest("tr") as HTMLElement;
    expect(within(ligne).getByText("Gestionnaire de biens")).toBeInTheDocument();
    expect(within(ligne).getByText("1 immeuble")).toBeInTheDocument();
    expect(within(ligne).getByRole("button", { name: "Immeubles" })).toBeInTheDocument();
  });

  it("crée un gestionnaire de biens avec ses immeubles affectés", async () => {
    post.mockReturnValueOnce(ok({ id: "u-11" }));
    put.mockReturnValueOnce(ok({ immeubleIds: ["i-2"] }));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Nouvel employé" }));
    await userEvent.selectOptions(screen.getByLabelText("Rôle"), "GESTIONNAIRE_BIENS");
    expect(screen.getByText("Immeubles affectés")).toBeInTheDocument();
    await userEvent.type(screen.getByLabelText("Nom"), "Hugo Biens");
    await userEvent.type(screen.getByLabelText("Téléphone"), "+237677000222");
    await userEvent.click(screen.getByRole("button", { name: "Générer un mot de passe" }));
    await userEvent.click(screen.getByText("Immeuble Bonanjo"));
    await userEvent.click(screen.getByRole("button", { name: "Créer l'employé" }));
    await waitFor(() => expect(put).toHaveBeenCalledTimes(1));
    expect(post.mock.calls[0][1].body).toMatchObject({ role: "GESTIONNAIRE_BIENS", deuxFacteursActif: true });
    expect(put.mock.calls[0][1].body.immeubleIds).toEqual(["i-2"]);
  });

  it("crée un comptable avec ses immeubles affectés", async () => {
    post.mockReturnValueOnce(ok({ id: "u-12" }));
    put.mockReturnValueOnce(ok({ immeubleIds: ["i-1"] }));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Nouvel employé" }));
    await userEvent.selectOptions(screen.getByLabelText("Rôle"), "COMPTABLE");
    expect(screen.getByText("Immeubles affectés")).toBeInTheDocument();
    await userEvent.type(screen.getByLabelText("Nom"), "Carl Comptable");
    await userEvent.type(screen.getByLabelText("Téléphone"), "+237688000333");
    await userEvent.click(screen.getByRole("button", { name: "Générer un mot de passe" }));
    await userEvent.click(screen.getByText("Résidence Palmiers"));
    await userEvent.click(screen.getByRole("button", { name: "Créer l'employé" }));
    await waitFor(() => expect(put).toHaveBeenCalledTimes(1));
    expect(post.mock.calls[0][1].body).toMatchObject({ role: "COMPTABLE", deuxFacteursActif: true });
    expect(put.mock.calls[0][1].body.immeubleIds).toEqual(["i-1"]);
  });

  it("exige un téléphone et un mot de passe valides avant d'appeler l'API", async () => {
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Nouvel employé" }));
    await userEvent.type(screen.getByLabelText("Nom"), "Nina");
    await userEvent.click(screen.getByRole("button", { name: "Créer l'employé" }));
    expect(await screen.findByText(/Le téléphone est obligatoire/)).toBeInTheDocument();
    expect(screen.getByText("Le mot de passe contient entre 8 et 100 caractères.")).toBeInTheDocument();
    expect(post).not.toHaveBeenCalled();
  });

  it("crée un encaisseur avec double authentification, puis affecte les immeubles cochés", async () => {
    post.mockReturnValueOnce(ok({ id: "u-9" }));
    put.mockReturnValueOnce(ok({ immeubleIds: ["i-1", "i-3"] }));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Nouvel employé" }));
    await userEvent.type(screen.getByLabelText("Nom"), "Nina Nouvelle");
    await userEvent.type(screen.getByLabelText("Téléphone"), "+237633333333");
    await userEvent.click(screen.getByRole("button", { name: "Générer un mot de passe" }));
    await userEvent.click(screen.getByText("Résidence Palmiers"));
    await userEvent.click(screen.getByText("Villa Akwa"));
    expect(screen.getByText("2 immeubles sélectionnés")).toBeInTheDocument();
    await userEvent.click(screen.getByRole("button", { name: "Créer l'employé" }));

    await waitFor(() => expect(put).toHaveBeenCalledTimes(1));
    expect(post.mock.calls[0][1].body).toMatchObject({ nom: "Nina Nouvelle", telephone: "+237633333333", role: "ENCAISSEUR", deuxFacteursActif: true });
    expect(put.mock.calls[0][1].params.path.id).toBe("u-9");
    expect(put.mock.calls[0][1].body.immeubleIds.sort()).toEqual(["i-1", "i-3"]);
    expect(await screen.findByTestId("recap-mot-de-passe")).toHaveTextContent(post.mock.calls[0][1].body.motDePasse);
  });

  it("un gestionnaire n'a pas de choix d'immeubles et aucune affectation n'est envoyée", async () => {
    post.mockReturnValueOnce(ok({ id: "u-10" }));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Nouvel employé" }));
    await userEvent.selectOptions(screen.getByLabelText("Rôle"), "GESTIONNAIRE");
    expect(screen.queryByText("Immeubles affectés")).not.toBeInTheDocument();
    await userEvent.type(screen.getByLabelText("Nom"), "Gaston");
    await userEvent.type(screen.getByLabelText("Téléphone"), "+237644444444");
    await userEvent.click(screen.getByRole("button", { name: "Générer un mot de passe" }));
    await userEvent.click(screen.getByRole("button", { name: "Créer l'employé" }));
    await waitFor(() => expect(post).toHaveBeenCalledTimes(1));
    expect(post.mock.calls[0][1].body).toMatchObject({ role: "GESTIONNAIRE", deuxFacteursActif: true });
    expect(put).not.toHaveBeenCalled();
  });

  it("signale un compte créé dont l'affectation a échoué, sans le recréer", async () => {
    post.mockReturnValueOnce(ok({ id: "u-9" }));
    put.mockReturnValueOnce(ko(500, "ERREUR_INTERNE"));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Nouvel employé" }));
    await userEvent.type(screen.getByLabelText("Nom"), "Nina");
    await userEvent.type(screen.getByLabelText("Téléphone"), "+237655555555");
    await userEvent.click(screen.getByRole("button", { name: "Générer un mot de passe" }));
    await userEvent.click(screen.getByText("Villa Akwa"));
    await userEvent.click(screen.getByRole("button", { name: "Créer l'employé" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("n'ont pas pu être affectés");
    expect(post).toHaveBeenCalledTimes(1);
  });

  it("modifie les immeubles d'un encaisseur existant", async () => {
    put.mockReturnValueOnce(ok({ immeubleIds: ["i-1"] }));
    rendu();
    const ligne = screen.getByText("Awa Encaisseuse").closest("tr") as HTMLElement;
    await userEvent.click(within(ligne).getByRole("button", { name: "Immeubles" }));
    expect(await screen.findByText("Immeubles affectés à Awa Encaisseuse")).toBeInTheDocument();
    expect(screen.getByText("2 immeubles sélectionnés")).toBeInTheDocument();
    await userEvent.click(screen.getByText("Immeuble Bonanjo")); // décoche i-2
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
    await waitFor(() => expect(put).toHaveBeenCalledTimes(1));
    expect(put.mock.calls[0][1].params.path.id).toBe("u-1");
    expect(put.mock.calls[0][1].body.immeubleIds).toEqual(["i-1"]);
  });
});
