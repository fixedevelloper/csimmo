import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const post = vi.fn();
const put = vi.fn();
let role = "ADMIN";
vi.mock("@/lib/api/client", () => ({ api: { POST: (...a: unknown[]) => post(...a), PUT: (...a: unknown[]) => put(...a) } }));
vi.mock("@/hooks/use-me", () => ({ useMe: () => ({ data: { role } }) }));
vi.mock("sonner", () => ({ toast: { success: vi.fn(), error: vi.fn() } }));

import { AccesLocataire } from "./acces-locataire";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 201 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });
const locataire = { id: "t-1", nom: "Marie Tenant", telephone: "+237612345678", email: "marie@x.cm", profession: "Prof", notes: "n" };
const rendu = (l = locataire) => renderIntl(<QueryClientProvider client={new QueryClient()}><AccesLocataire locataire={l} /></QueryClientProvider>, "fr");

describe("AccesLocataire", () => {
  beforeEach(() => { vi.clearAllMocks(); role = "ADMIN"; });

  it("n'apparaît pas pour un non-administrateur", () => {
    role = "GESTIONNAIRE";
    rendu();
    expect(screen.queryByText("Accès à l'application")).not.toBeInTheDocument();
  });

  it("explique qu'un contact est nécessaire quand la fiche n'en a pas", () => {
    rendu({ id: "t-2", nom: "Sans Contact" } as typeof locataire);
    expect(screen.getByText(/Renseignez un téléphone ou un e-mail/)).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Créer l'accès" })).not.toBeInTheDocument();
  });

  it("indique qu'un compte est déjà rattaché", () => {
    rendu({ ...locataire, utilisateurId: "u-1" } as typeof locataire);
    expect(screen.getByText("Un compte est rattaché à ce locataire.")).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Créer l'accès" })).not.toBeInTheDocument();
  });

  it("crée le compte LOCATAIRE puis le rattache à la fiche, et affiche le mot de passe une fois", async () => {
    post.mockReturnValueOnce(ok({ id: "u-9" }));
    put.mockReturnValueOnce(ok({}));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Générer un mot de passe" }));
    await userEvent.click(screen.getByRole("button", { name: "Créer l'accès" }));

    await waitFor(() => expect(put).toHaveBeenCalledTimes(1));
    const compte = post.mock.calls[0][1].body;
    expect(compte).toMatchObject({ nom: "Marie Tenant", telephone: "+237612345678", email: "marie@x.cm", role: "LOCATAIRE" });
    expect(put.mock.calls[0][1].params.path.id).toBe("t-1");
    expect(put.mock.calls[0][1].body).toMatchObject({ nom: "Marie Tenant", profession: "Prof", utilisateurId: "u-9" });
    expect(await screen.findByTestId("recap-mot-de-passe")).toHaveTextContent(compte.motDePasse);
  });

  it("accepte les champs `null` renvoyés par l'API (e-mail, adresse… vides)", async () => {
    post.mockReturnValueOnce(ok({ id: "u-9" }));
    put.mockReturnValueOnce(ok({}));
    rendu({ id: "t-1", nom: "Marie", telephone: "+237612345678", email: null, adresse: null, notes: null } as unknown as typeof locataire);
    await userEvent.click(screen.getByRole("button", { name: "Générer un mot de passe" }));
    await userEvent.click(screen.getByRole("button", { name: "Créer l'accès" }));
    await waitFor(() => expect(put).toHaveBeenCalledTimes(1));
    expect(post.mock.calls[0][1].body.email).toBeUndefined();
    expect(put.mock.calls[0][1].body).toMatchObject({ utilisateurId: "u-9" });
  });

  it("n'appelle pas le rattachement si la création du compte échoue", async () => {
    post.mockReturnValueOnce(ko(409, "UTILISATEUR_DEJA_EXISTANT"));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Générer un mot de passe" }));
    await userEvent.click(screen.getByRole("button", { name: "Créer l'accès" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("existe déjà");
    expect(put).not.toHaveBeenCalled();
  });

  it("signale un compte créé mais non rattaché, sans le recréer", async () => {
    post.mockReturnValueOnce(ok({ id: "u-9" }));
    put.mockReturnValueOnce(ko(500, "ERREUR_INTERNE"));
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Générer un mot de passe" }));
    await userEvent.click(screen.getByRole("button", { name: "Créer l'accès" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("n'a pas pu être rattaché");
    expect(screen.getByTestId("recap-mot-de-passe")).toBeInTheDocument();
    expect(post).toHaveBeenCalledTimes(1);
  });
});
