import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import type { ReactElement } from "react";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

vi.mock("next/navigation", () => ({ useRouter: () => ({ push: vi.fn(), back: vi.fn() }) }));
const post = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { POST: (...a: unknown[]) => post(...a), DELETE: vi.fn() } }));
const mandats = vi.fn();
vi.mock("@/hooks/use-tiers", () => ({ useMandats: () => mandats(), useGarants: () => ({ data: [] }), usePieces: () => ({ data: [] }) }));
vi.mock("sonner", () => ({ toast: { success: vi.fn() } }));

import { champsLocataire } from "./champs";
import { FicheForm } from "./fiche-form";
import { Mandats } from "./mandats";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 200 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });
const rendu = (ui: ReactElement, locale: "fr" | "en" = "fr") =>
  renderIntl(<QueryClientProvider client={new QueryClient()}>{ui}</QueryClientProvider>, locale);

describe("FicheForm (locataire)", () => {
  const t = (cle: string) => cle; // libellés = clés : suffisant pour piloter le formulaire
  const onSubmit = vi.fn();
  beforeEach(() => vi.clearAllMocks());

  function formulaire() {
    return rendu(<FicheForm champs={champsLocataire(t)} contactRequis onSubmit={onSubmit} />);
  }

  it("exige le nom puis un téléphone ou un e-mail", async () => {
    formulaire();
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
    expect(await screen.findByText("Le nom est obligatoire")).toBeInTheDocument();
    expect(screen.getByText("Indiquez un téléphone ou un e-mail")).toBeInTheDocument();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it("refuse un e-mail mal formé", async () => {
    formulaire();
    await userEvent.type(screen.getByLabelText("nom"), "Paul");
    await userEvent.type(screen.getByLabelText("email"), "pas-un-email");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
    expect(await screen.findByText("Adresse e-mail invalide")).toBeInTheDocument();
  });

  it("transmet les valeurs saisies", async () => {
    onSubmit.mockResolvedValue(undefined);
    formulaire();
    await userEvent.type(screen.getByLabelText("nom"), "Paul Mbarga");
    await userEvent.type(screen.getByLabelText("telephone"), "+237699111222");
    await userEvent.type(screen.getByLabelText("numeroCni"), "CNI-1");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));

    await waitFor(() => expect(onSubmit).toHaveBeenCalled());
    expect(onSubmit.mock.calls[0][0]).toMatchObject({ nom: "Paul Mbarga", telephone: "+237699111222", numeroCni: "CNI-1", email: "" });
  });

  it("affiche l'erreur traduite renvoyée par l'API", async () => {
    onSubmit.mockRejectedValue(Object.assign(new Error("x"), { name: "ApiError", code: "TELEPHONE_INVALIDE", status: 400, erreurs: [] }));
    const { ApiError } = await import("@/lib/api/errors");
    onSubmit.mockRejectedValue(new ApiError(400, "TELEPHONE_INVALIDE"));
    formulaire();
    await userEvent.type(screen.getByLabelText("nom"), "Paul");
    await userEvent.type(screen.getByLabelText("telephone"), "abc");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("Numéro de téléphone invalide.");
  });
});

describe("Mandats", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mandats.mockReturnValue({ data: [{ id: "m-1", tauxHonorairesBp: 750, dateDebut: "2026-01-01" }], error: null, refetch: vi.fn() });
  });

  it("affiche le taux en pourcentage et « en cours » pour un mandat sans fin", () => {
    rendu(<Mandats proprietaireId="p-1" actif />);
    expect(screen.getByText("7,5 %")).toBeInTheDocument();
    expect(screen.getByText("en cours")).toBeInTheDocument();
  });

  it("convertit le taux saisi en points de base", async () => {
    post.mockReturnValue(ok({ id: "m-2" }));
    rendu(<Mandats proprietaireId="p-1" actif />);
    await userEvent.type(screen.getByLabelText("Taux d'honoraires (%)"), "10");
    await userEvent.type(screen.getByLabelText("Début"), "2027-01-01");
    await userEvent.click(screen.getByRole("button", { name: "Ajouter un mandat" }));

    await waitFor(() => expect(post).toHaveBeenCalled());
    expect(post.mock.calls[0][1].body).toEqual({ tauxHonorairesBp: 1000, dateDebut: "2027-01-01", dateFin: undefined });
  });

  it("contrôle taux et dates avant d'appeler l'API", async () => {
    rendu(<Mandats proprietaireId="p-1" actif />);
    await userEvent.type(screen.getByLabelText("Taux d'honoraires (%)"), "150");
    await userEvent.click(screen.getByRole("button", { name: "Ajouter un mandat" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("Taux entre 0 et 100 %");

    await userEvent.clear(screen.getByLabelText("Taux d'honoraires (%)"));
    await userEvent.type(screen.getByLabelText("Taux d'honoraires (%)"), "10");
    await userEvent.type(screen.getByLabelText("Début"), "2027-06-01");
    await userEvent.type(screen.getByLabelText("Fin (facultative)"), "2027-05-01");
    await userEvent.click(screen.getByRole("button", { name: "Ajouter un mandat" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("La fin précède le début");
    expect(post).not.toHaveBeenCalled();
  });

  it("traduit un chevauchement de mandats", async () => {
    post.mockReturnValue(ko(409, "MANDAT_CHEVAUCHEMENT"));
    rendu(<Mandats proprietaireId="p-1" actif />);
    await userEvent.type(screen.getByLabelText("Taux d'honoraires (%)"), "10");
    await userEvent.type(screen.getByLabelText("Début"), "2026-03-01");
    await userEvent.click(screen.getByRole("button", { name: "Ajouter un mandat" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("a déjà un mandat sur tout ou partie de cette période");
  });

  it("masque le formulaire d'ajout pour un propriétaire archivé", () => {
    rendu(<Mandats proprietaireId="p-1" actif={false} />);
    expect(screen.queryByRole("button", { name: "Ajouter un mandat" })).not.toBeInTheDocument();
  });
});
