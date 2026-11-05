import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import type { ReactElement } from "react";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const push = vi.fn();
vi.mock("next/navigation", () => ({ useRouter: () => ({ push, back: vi.fn() }) }));
const post = vi.fn();
const put = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { POST: (...a: unknown[]) => post(...a), PUT: (...a: unknown[]) => put(...a) } }));
vi.mock("@/hooks/use-tiers", () => ({ useProprietaires: () => ({ data: { content: [{ id: "p-1", nom: "Mme Ngono" }] } }) }));
const succes = vi.fn();
vi.mock("sonner", () => ({ toast: { success: (m: string) => succes(m) } }));

import { ImmeubleForm } from "./immeuble-form";
import { LotForm } from "./lot-form";

const ok = (data: unknown) => Promise.resolve({ data, response: new Response(null, { status: 200 }) });
const ko = (status: number, code: string) => Promise.resolve({ error: { code }, response: new Response(null, { status }) });
function rendu(ui: ReactElement, locale: "fr" | "en" = "fr") {
  return renderIntl(<QueryClientProvider client={new QueryClient()}>{ui}</QueryClientProvider>, locale);
}

describe("ImmeubleForm", () => {
  beforeEach(() => vi.clearAllMocks());

  it("signale les champs obligatoires sans appeler l'API", async () => {
    rendu(<ImmeubleForm />);
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));

    expect(await screen.findByText("Le nom est obligatoire")).toBeInTheDocument();
    expect(screen.getByText("L'adresse est obligatoire")).toBeInTheDocument();
    expect(screen.getByText("La ville est obligatoire")).toBeInTheDocument();
    expect(post).not.toHaveBeenCalled();
  });

  it("refuse une latitude sans longitude et des coordonnées hors limites", async () => {
    rendu(<ImmeubleForm />);
    await userEvent.type(screen.getByLabelText("Nom"), "A");
    await userEvent.type(screen.getByLabelText("Adresse"), "B");
    await userEvent.type(screen.getByLabelText("Ville"), "Douala");
    await userEvent.type(screen.getByLabelText("Latitude"), "4.05");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
    expect(await screen.findByText("Renseignez la latitude et la longitude, ou aucune")).toBeInTheDocument();

    await userEvent.type(screen.getByLabelText("Longitude"), "200");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));
    expect(await screen.findByText("Renseignez la latitude et la longitude, ou aucune")).toBeInTheDocument();
    expect(post).not.toHaveBeenCalled();
  });

  it("crée l'immeuble puis ouvre sa fiche", async () => {
    post.mockReturnValue(ok({ id: "i-1" }));
    rendu(<ImmeubleForm />);
    await userEvent.type(screen.getByLabelText("Nom"), "Résidence Soleil");
    await userEvent.type(screen.getByLabelText("Adresse"), "12 rue des Palmiers");
    await userEvent.type(screen.getByLabelText("Ville"), "Douala");
    await userEvent.type(screen.getByLabelText("Latitude"), "4.0511");
    await userEvent.type(screen.getByLabelText("Longitude"), "9.7679");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));

    await waitFor(() => expect(push).toHaveBeenCalledWith("/patrimoine/i-1"));
    expect(post).toHaveBeenCalledWith("/api/v1/immeubles", {
      body: { nom: "Résidence Soleil", adresse: "12 rue des Palmiers", quartier: undefined, ville: "Douala", latitude: 4.0511, longitude: 9.7679, description: undefined, proprietaireTiersId: undefined },
    });
    expect(succes).toHaveBeenCalledWith("Immeuble créé");
  });

  it("rattache l'immeuble à un propriétaire choisi dans la liste", async () => {
    post.mockReturnValue(ok({ id: "i-2" }));
    rendu(<ImmeubleForm />);
    await userEvent.type(screen.getByLabelText("Nom"), "A");
    await userEvent.type(screen.getByLabelText("Adresse"), "B");
    await userEvent.type(screen.getByLabelText("Ville"), "C");
    await userEvent.selectOptions(screen.getByLabelText("Propriétaire (facultatif)"), "p-1");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));

    await waitFor(() => expect(post).toHaveBeenCalled());
    expect(post.mock.calls[0][1].body.proprietaireTiersId).toBe("p-1");
  });

  it("modifie un immeuble existant (PUT)", async () => {
    put.mockReturnValue(ok({ id: "i-9" }));
    rendu(<ImmeubleForm immeuble={{ id: "i-9", nom: "Ancien", adresse: "Rue 1", ville: "Douala" }} />);
    await userEvent.clear(screen.getByLabelText("Nom"));
    await userEvent.type(screen.getByLabelText("Nom"), "Nouveau");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));

    await waitFor(() => expect(put).toHaveBeenCalled());
    expect(put.mock.calls[0][0]).toBe("/api/v1/immeubles/{id}");
    expect(put.mock.calls[0][1].body.nom).toBe("Nouveau");
    expect(push).toHaveBeenCalledWith("/patrimoine/i-9");
  });

  it("traduit l'erreur du serveur", async () => {
    post.mockReturnValue(ko(409, "IMMEUBLE_ARCHIVE"));
    rendu(<ImmeubleForm />);
    await userEvent.type(screen.getByLabelText("Nom"), "A");
    await userEvent.type(screen.getByLabelText("Adresse"), "B");
    await userEvent.type(screen.getByLabelText("Ville"), "C");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("Cet immeuble est archivé.");
    expect(push).not.toHaveBeenCalled();
  });
});

describe("LotForm", () => {
  beforeEach(() => vi.clearAllMocks());

  it("exige une référence et refuse une surface nulle", async () => {
    rendu(<LotForm immeubleId="i-1" />);
    await userEvent.type(screen.getByLabelText("Surface (m²)"), "0");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));

    expect(await screen.findByText("La référence est obligatoire")).toBeInTheDocument();
    expect(screen.getByText("Valeur invalide")).toBeInTheDocument();
    expect(post).not.toHaveBeenCalled();
  });

  it("envoie les équipements (un par ligne) et les compteurs", async () => {
    post.mockReturnValue(ok({ id: "l-1" }));
    rendu(<LotForm immeubleId="i-1" />);
    await userEvent.type(screen.getByLabelText("Référence"), "A101");
    await userEvent.selectOptions(screen.getByLabelText("Type"), "STUDIO");
    await userEvent.type(screen.getByLabelText("Surface (m²)"), "35,5");
    await userEvent.type(screen.getByLabelText("Loyer indicatif (FCFA)"), "90000");
    await userEvent.type(screen.getByLabelText("Équipements"), "Parking{enter}Balcon{enter}Parking");
    await userEvent.click(screen.getByRole("button", { name: "Ajouter un compteur" }));
    await userEvent.type(screen.getByLabelText("Numéro"), "E-123");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));

    await waitFor(() => expect(push).toHaveBeenCalledWith("/patrimoine/lots/l-1"));
    expect(post).toHaveBeenCalledWith("/api/v1/lots", {
      body: {
        immeubleId: "i-1", reference: "A101", type: "STUDIO", surfaceM2: 35.5, pieces: undefined, loyerIndicatif: 90000,
        equipements: ["Parking", "Balcon"], compteurs: [{ type: "EAU", numero: "E-123" }],
      },
    });
  });

  it("traduit un doublon de référence", async () => {
    post.mockReturnValue(ko(409, "LOT_REFERENCE_EXISTANTE"));
    rendu(<LotForm immeubleId="i-1" />);
    await userEvent.type(screen.getByLabelText("Référence"), "A101");
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("porte déjà cette référence");
  });

  it("traduit le palier de licence atteint (en anglais)", async () => {
    post.mockReturnValue(ko(409, "LICENCE_PALIER_ATTEINT"));
    rendu(<LotForm immeubleId="i-1" />, "en");
    await userEvent.type(screen.getByLabelText("Reference"), "A101");
    await userEvent.click(screen.getByRole("button", { name: "Save" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("number of units allowed by your licence");
  });

  it("ne renvoie pas l'immeuble à la modification d'un lot (PUT)", async () => {
    put.mockReturnValue(ok({ id: "l-7" }));
    rendu(<LotForm lot={{ id: "l-7", immeubleId: "i-1", reference: "Z9", type: "BUREAU", equipements: ["Fibre"], compteurs: [] }} />);
    await userEvent.click(screen.getByRole("button", { name: "Enregistrer" }));

    await waitFor(() => expect(put).toHaveBeenCalled());
    expect(put.mock.calls[0][1].body).toMatchObject({ immeubleId: undefined, reference: "Z9", type: "BUREAU", equipements: ["Fibre"] });
  });
});
