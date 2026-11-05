import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";

import { FormulaireLicence, validerLicence } from "./formulaire-licence";

const base = { clientId: "c1", palierLots: 50, dateDebut: "2026-01-01", dateExpiration: "2027-01-01", empreinteServeur: "a".repeat(64), fonctionnalites: [] };

describe("validerLicence", () => {
  it("accepte une saisie correcte", () => expect(validerLicence(base)).toBeNull());
  it("refuse client, palier, dates et empreinte invalides", () => {
    expect(validerLicence({ ...base, clientId: "" })).toMatch(/client/i);
    expect(validerLicence({ ...base, palierLots: 0 })).toMatch(/palier/i);
    expect(validerLicence({ ...base, dateExpiration: "2025-12-31" })).toMatch(/précéder/);
    expect(validerLicence({ ...base, empreinteServeur: "abc" })).toMatch(/empreinte/i);
  });
});

describe("FormulaireLicence", () => {
  const clients = [{ id: "c1", nom: "Agence Akwa" }];

  it("n'appelle pas le serveur si l'empreinte est invalide", async () => {
    const soumettre = vi.fn();
    render(<FormulaireLicence clients={clients} clientId="c1" onSoumettre={soumettre} />);
    fireEvent.change(screen.getByLabelText(/Expiration/), { target: { value: "2030-01-01" } });
    fireEvent.change(screen.getByLabelText(/Empreinte/), { target: { value: "xyz" } });
    fireEvent.click(screen.getByRole("button", { name: "Émettre la licence" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("64 caractères");
    expect(soumettre).not.toHaveBeenCalled();
  });

  it("envoie la saisie normalisée (empreinte en minuscules, fonctionnalités découpées)", async () => {
    const soumettre = vi.fn().mockResolvedValue(undefined);
    render(<FormulaireLicence clients={clients} clientId="c1" onSoumettre={soumettre} />);
    fireEvent.change(screen.getByLabelText(/Expiration/), { target: { value: "2030-01-01" } });
    fireEvent.change(screen.getByLabelText(/Empreinte/), { target: { value: ` ${"AB".repeat(32)} ` } });
    fireEvent.change(screen.getByLabelText(/Fonctionnalités/), { target: { value: "BASE, PAIEMENT_EN_LIGNE ," } });
    fireEvent.click(screen.getByRole("button", { name: "Émettre la licence" }));
    await waitFor(() => expect(soumettre).toHaveBeenCalledTimes(1));
    expect(soumettre.mock.calls[0][0]).toMatchObject({ clientId: "c1", palierLots: 50, empreinteServeur: "ab".repeat(32), fonctionnalites: ["BASE", "PAIEMENT_EN_LIGNE"] });
  });

  it("affiche l'erreur renvoyée par le serveur", async () => {
    render(<FormulaireLicence clients={clients} clientId="c1" onSoumettre={() => Promise.reject(new Error("Les données de la licence sont invalides."))} />);
    fireEvent.change(screen.getByLabelText(/Expiration/), { target: { value: "2030-01-01" } });
    fireEvent.change(screen.getByLabelText(/Empreinte/), { target: { value: "a".repeat(64) } });
    fireEvent.click(screen.getByRole("button", { name: "Émettre la licence" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("invalides");
  });
});
