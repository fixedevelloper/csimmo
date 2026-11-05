import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const replace = vi.fn();
const refresh = vi.fn();
vi.mock("next/navigation", () => ({ useRouter: () => ({ replace, refresh }) }));

import { InstallationForm } from "./installation-form";

const EMPREINTE = "ab".repeat(32);
const LICENCE = '{"format":1,"licence":{},"signature":"x"}';

function reponse(status: number, corps: unknown) {
  return Promise.resolve(new Response(JSON.stringify(corps), { status, headers: { "Content-Type": "application/json" } }));
}

type Saisie = { licence?: string; nom?: string; telephone?: string; email?: string; mdp?: string; confirmation?: string };

async function remplir(saisie: Saisie = {}) {
  const { licence = LICENCE, nom = "Directeur", telephone = "+237699111222", email = "", mdp = "Motdepasse123" } = saisie;
  const confirmation = saisie.confirmation ?? mdp;
  if (licence) {
    await userEvent.click(screen.getByLabelText("Contenu de la licence"));
    await userEvent.paste(licence);
  }
  if (nom) await userEvent.type(screen.getByLabelText("Nom complet"), nom);
  if (telephone) await userEvent.type(screen.getByLabelText("Téléphone (facultatif)"), telephone);
  if (email) await userEvent.type(screen.getByLabelText("E-mail (facultatif)"), email);
  await userEvent.type(screen.getByLabelText("Mot de passe"), mdp);
  await userEvent.type(screen.getByLabelText("Confirmer le mot de passe"), confirmation);
  await userEvent.click(screen.getByRole("button", { name: "Terminer l'installation" }));
}

describe("InstallationForm", () => {
  const fetchMock = vi.fn();
  beforeEach(() => vi.stubGlobal("fetch", fetchMock));
  afterEach(() => {
    vi.unstubAllGlobals();
    vi.clearAllMocks();
  });

  it("affiche l'empreinte du serveur à communiquer à l'éditeur", () => {
    renderIntl(<InstallationForm empreinte={EMPREINTE} />);
    expect(screen.getByTestId("empreinte")).toHaveTextContent(EMPREINTE);
    expect(screen.getByRole("button", { name: "Copier" })).toBeInTheDocument();
  });

  it("explique quoi faire quand l'empreinte est indisponible", () => {
    renderIntl(<InstallationForm empreinte={null} />);
    expect(screen.getByText(/IMMOGEST_MACHINE_ID/)).toBeInTheDocument();
    expect(screen.queryByTestId("empreinte")).not.toBeInTheDocument();
  });

  it("signale les champs obligatoires sans appeler le serveur", async () => {
    renderIntl(<InstallationForm empreinte={EMPREINTE} />);
    await userEvent.click(screen.getByRole("button", { name: "Terminer l'installation" }));

    expect(await screen.findByText("La licence est obligatoire")).toBeInTheDocument();
    expect(screen.getByText("Le nom est obligatoire")).toBeInTheDocument();
    expect(screen.getAllByText("8 caractères minimum").length).toBeGreaterThan(0);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("exige un téléphone ou un e-mail et deux mots de passe identiques", async () => {
    renderIntl(<InstallationForm empreinte={EMPREINTE} />);
    await remplir({ telephone: "", email: "", mdp: "Motdepasse123", confirmation: "Autre-mot-de-passe" });

    expect(await screen.findByText("Indiquez un téléphone ou un e-mail")).toBeInTheDocument();
    expect(screen.getByText("Les mots de passe ne correspondent pas")).toBeInTheDocument();
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("envoie la licence et le compte (sans champ vide) puis passe à la connexion", async () => {
    fetchMock.mockReturnValue(reponse(201, { installee: true }));
    renderIntl(<InstallationForm empreinte={EMPREINTE} />);
    await remplir({ email: "" });

    await waitFor(() => expect(replace).toHaveBeenCalledWith("/login?raison=installe"));
    expect(fetchMock).toHaveBeenCalledWith("/api/installation", expect.objectContaining({ method: "POST" }));
    expect(JSON.parse(fetchMock.mock.calls[0][1].body)).toEqual({
      licence: LICENCE,
      admin: { nom: "Directeur", telephone: "+237699111222", motDePasse: "Motdepasse123" },
    });
  });

  it("traduit le refus de la licence par le serveur", async () => {
    fetchMock.mockReturnValue(reponse(422, { code: "LICENCE_MAUVAISE_EMPREINTE" }));
    renderIntl(<InstallationForm empreinte={EMPREINTE} />);
    await remplir();

    expect(await screen.findByRole("alert")).toHaveTextContent("Cette licence a été émise pour un autre serveur");
    expect(replace).not.toHaveBeenCalled();
  });

  it("traduit aussi en anglais", async () => {
    fetchMock.mockReturnValue(reponse(422, { code: "LICENCE_SIGNATURE_INVALIDE" }));
    renderIntl(<InstallationForm empreinte={EMPREINTE} />, "en");
    await userEvent.click(screen.getByLabelText("Licence content"));
    await userEvent.paste(LICENCE);
    await userEvent.type(screen.getByLabelText("Full name"), "Boss");
    await userEvent.type(screen.getByLabelText("E-mail (optional)"), "boss@agence.cm");
    await userEvent.type(screen.getByLabelText("Password"), "Motdepasse123");
    await userEvent.type(screen.getByLabelText("Confirm password"), "Motdepasse123");
    await userEvent.click(screen.getByRole("button", { name: "Finish setup" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("The licence signature is invalid");
  });

  it("renvoie vers la connexion si l'installation a déjà été faite", async () => {
    fetchMock.mockReturnValue(reponse(409, { code: "INSTALLATION_DEJA_FAITE" }));
    renderIntl(<InstallationForm empreinte={EMPREINTE} />);
    await remplir();

    await waitFor(() => expect(replace).toHaveBeenCalledWith("/login"));
  });

  it("signale un serveur injoignable", async () => {
    fetchMock.mockRejectedValue(new TypeError("Failed to fetch"));
    renderIntl(<InstallationForm empreinte={EMPREINTE} />);
    await remplir();

    expect(await screen.findByRole("alert")).toHaveTextContent("Impossible de joindre le serveur");
  });

  it("lit le fichier .lic choisi dans le champ de contenu", async () => {
    renderIntl(<InstallationForm empreinte={EMPREINTE} />);
    const fichier = new File([LICENCE], "client.lic", { type: "application/json" });

    await userEvent.upload(screen.getByLabelText("Fichier de licence (.lic)"), fichier);

    await waitFor(() => expect(screen.getByLabelText("Contenu de la licence")).toHaveValue(LICENCE));
  });

  it("refuse un fichier trop volumineux pour une licence", async () => {
    renderIntl(<InstallationForm empreinte={EMPREINTE} />);
    const gros = new File([new Uint8Array(70 * 1024)], "gros.lic");

    await userEvent.upload(screen.getByLabelText("Fichier de licence (.lic)"), gros);

    expect(await screen.findByText("Ce fichier est trop volumineux pour une licence")).toBeInTheDocument();
    expect(screen.getByLabelText("Contenu de la licence")).toHaveValue("");
  });
});
