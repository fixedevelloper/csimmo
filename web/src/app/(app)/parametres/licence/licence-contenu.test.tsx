import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import type { ReactElement } from "react";
import { beforeEach, describe, expect, it, vi } from "vitest";

import type { EtatLicenceDto } from "@/lib/licence";
import { renderIntl } from "@/test/render";

const get = vi.fn();
const post = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { GET: (...a: unknown[]) => get(...a), POST: (...a: unknown[]) => post(...a) } }));
const moi = vi.fn();
vi.mock("@/hooks/use-me", () => ({ useMe: () => moi() }));
const succes = vi.fn();
vi.mock("sonner", () => ({ toast: { success: (m: string) => succes(m) } }));

import { LicenceContenu } from "./licence-contenu";

const etat: EtatLicenceDto = {
  etat: "VALIDE",
  lectureSeule: false,
  licenseId: "L-2026-001",
  client: "Agence Étoile",
  palierLots: 50,
  dateDebut: "2026-01-01",
  dateExpiration: "2026-12-31",
  joursRestants: 200,
  fonctionnalites: ["mobile", "relances"],
  empreinteServeur: "ab".repeat(32),
  verificationEnLigneActive: true,
  derniereVerificationEnLigne: "2026-06-10T08:00:00Z",
  joursDeGraceRestants: 24,
};

function ok(data: unknown) {
  return Promise.resolve({ data, response: new Response(null, { status: 200 }) });
}
function ko(status: number, code: string) {
  return Promise.resolve({ error: { code }, response: new Response(null, { status }) });
}
function rendu(ui: ReactElement, locale: "fr" | "en" = "fr") {
  const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return renderIntl(<QueryClientProvider client={client}>{ui}</QueryClientProvider>, locale);
}

describe("LicenceContenu", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    moi.mockReturnValue({ data: { role: "ADMIN" } });
    get.mockReturnValue(ok(etat));
  });

  it("affiche l'état, le client, le palier, la période, les fonctionnalités et l'empreinte", async () => {
    rendu(<LicenceContenu />);

    expect(await screen.findByTestId("etat-licence")).toHaveTextContent("Valide");
    expect(screen.getByText("Agence Étoile")).toBeInTheDocument();
    expect(screen.getByText("L-2026-001")).toBeInTheDocument();
    expect(screen.getByText("50 lots")).toBeInTheDocument();
    expect(screen.getByText("Du 1 janvier 2026 au 31 décembre 2026")).toBeInTheDocument();
    expect(screen.getByText("200 jours")).toBeInTheDocument();
    expect(screen.getByText("relances")).toBeInTheDocument();
    expect(screen.getByTestId("empreinte")).toHaveTextContent("ab".repeat(32));
    expect(screen.getByText(/Active : dernière confirmation le 10 juin 2026/)).toBeInTheDocument();
    expect(screen.getByText(/Il reste 24 jours avant d'exiger une vérification en ligne/)).toBeInTheDocument();
  });

  it("explique la lecture seule d'une licence expirée et compte les jours depuis l'expiration", async () => {
    get.mockReturnValue(ok({ ...etat, etat: "EXPIREE", lectureSeule: true, joursRestants: -12 }));
    rendu(<LicenceContenu />);

    expect(await screen.findByTestId("etat-licence")).toHaveTextContent("Expirée");
    expect(screen.getByRole("alert")).toHaveTextContent("L'application est en lecture seule");
    expect(screen.getByText("Expirée depuis 12 jours")).toBeInTheDocument();
  });

  it("n'affiche jamais de contenu pour une licence invalide, mais explique pourquoi", async () => {
    get.mockReturnValue(
      ok({ etat: "INVALIDE", lectureSeule: true, detail: "Signature invalide : le fichier a été modifié",
        verificationEnLigneActive: false, empreinteServeur: "ab".repeat(32) }),
    );
    rendu(<LicenceContenu />);

    expect(await screen.findByTestId("etat-licence")).toHaveTextContent("Invalide");
    expect(screen.getByText("Signature invalide : le fichier a été modifié")).toBeInTheDocument();
    expect(screen.queryByText("Agence Étoile")).not.toBeInTheDocument();
    expect(screen.getByText("Désactivée (aucun serveur de licences configuré)")).toBeInTheDocument();
  });

  it("l'administrateur peut importer une licence : le nouvel état remplace l'ancien", async () => {
    post.mockReturnValue(ok({ ...etat, licenseId: "L-2027-002", palierLots: 200 }));
    rendu(<LicenceContenu />);
    await screen.findByTestId("etat-licence");

    await userEvent.click(screen.getByLabelText("Contenu de la licence"));
    await userEvent.paste('{"format":1}');
    await userEvent.click(screen.getByRole("button", { name: "Importer la licence" }));

    await waitFor(() => expect(succes).toHaveBeenCalledWith("Licence importée"));
    expect(post).toHaveBeenCalledWith("/api/v1/licence", { body: { contenu: '{"format":1}' } });
    expect(await screen.findByText("L-2027-002")).toBeInTheDocument();
    expect(screen.getByText("200 lots")).toBeInTheDocument();
    expect(screen.getByLabelText("Contenu de la licence")).toHaveValue("");
  });

  it("traduit le refus d'un import (licence d'un autre serveur)", async () => {
    post.mockReturnValue(ko(422, "LICENCE_MAUVAISE_EMPREINTE"));
    rendu(<LicenceContenu />);
    await screen.findByTestId("etat-licence");

    await userEvent.click(screen.getByLabelText("Contenu de la licence"));
    await userEvent.paste("x");
    await userEvent.click(screen.getByRole("button", { name: "Importer la licence" }));

    expect(await screen.findByText(/émise pour un autre serveur/)).toBeInTheDocument();
    expect(succes).not.toHaveBeenCalled();
  });

  it("n'appelle pas l'API pour un import vide", async () => {
    rendu(<LicenceContenu />);
    await screen.findByTestId("etat-licence");

    await userEvent.click(screen.getByRole("button", { name: "Importer la licence" }));

    expect(await screen.findByText("Choisissez un fichier ou collez le contenu de la licence")).toBeInTheDocument();
    expect(post).not.toHaveBeenCalled();
  });

  it("un gestionnaire voit l'état mais ne peut pas importer", async () => {
    moi.mockReturnValue({ data: { role: "GESTIONNAIRE" } });
    rendu(<LicenceContenu />);

    expect(await screen.findByTestId("etat-licence")).toHaveTextContent("Valide");
    expect(screen.getByText("Seul un administrateur peut importer une licence.")).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Importer la licence" })).not.toBeInTheDocument();
  });

  it("un locataire n'a pas accès et l'API n'est pas appelée", () => {
    moi.mockReturnValue({ data: { role: "LOCATAIRE" } });
    rendu(<LicenceContenu />);

    expect(screen.getByRole("alert")).toHaveTextContent("Vous n'avez pas les droits nécessaires");
    expect(get).not.toHaveBeenCalled();
  });

  it("propose de réessayer quand l'état ne peut pas être chargé", async () => {
    get.mockReturnValueOnce(ko(500, "ERREUR_INTERNE")).mockReturnValue(ok(etat));
    rendu(<LicenceContenu />);

    await userEvent.click(await screen.findByRole("button", { name: "Réessayer" }));

    expect(await screen.findByTestId("etat-licence")).toHaveTextContent("Valide");
  });

  it("s'affiche en anglais", async () => {
    rendu(<LicenceContenu />, "en");

    expect(await screen.findByTestId("etat-licence")).toHaveTextContent("Valid");
    expect(screen.getByText("From January 1, 2026 to December 31, 2026")).toBeInTheDocument();
    expect(screen.getByText("50 units")).toBeInTheDocument();
  });
});
