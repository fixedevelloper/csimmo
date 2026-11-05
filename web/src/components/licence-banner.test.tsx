import { screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";

import type { EtatLicenceDto } from "@/lib/licence";
import { renderIntl } from "@/test/render";

const moi = vi.fn();
const licence = vi.fn();
vi.mock("@/hooks/use-me", () => ({ useMe: () => moi() }));
vi.mock("@/hooks/use-licence", () => ({ useLicence: (o: unknown) => licence(o) }));

import { LicenceBanner } from "./licence-banner";

const valide: EtatLicenceDto = { etat: "VALIDE", lectureSeule: false, joursRestants: 200, dateExpiration: "2026-12-31" };

describe("LicenceBanner", () => {
  beforeEach(() => {
    moi.mockReturnValue({ data: { role: "ADMIN" } });
    licence.mockReturnValue({ data: valide });
  });

  it("n'affiche rien pour une licence valide", () => {
    renderIntl(<LicenceBanner />);
    expect(screen.queryByTestId("bandeau-licence")).not.toBeInTheDocument();
  });

  it("bandeau d'erreur en lecture seule, avec un lien vers l'écran Licence", () => {
    licence.mockReturnValue({ data: { ...valide, etat: "EXPIREE", lectureSeule: true } });
    renderIntl(<LicenceBanner />);

    expect(screen.getByRole("alert")).toHaveTextContent("Votre licence a expiré : l'application est en lecture seule");
    expect(screen.getByRole("link", { name: "Voir la licence" })).toHaveAttribute("href", "/parametres/licence");
  });

  it("alerte d'expiration avec le nombre de jours et la date", () => {
    licence.mockReturnValue({ data: { ...valide, alerteJours: 7, joursRestants: 5 } });
    renderIntl(<LicenceBanner />);

    expect(screen.getByRole("status")).toHaveTextContent("Votre licence expire dans 5 jours (le 31 décembre 2026)");
  });

  it("alerte d'expiration le dernier jour et au singulier", () => {
    licence.mockReturnValue({ data: { ...valide, alerteJours: 7, joursRestants: 0 } });
    const { unmount } = renderIntl(<LicenceBanner />);
    expect(screen.getByRole("status")).toHaveTextContent("expire aujourd'hui");
    unmount();

    licence.mockReturnValue({ data: { ...valide, alerteJours: 7, joursRestants: 1 } });
    renderIntl(<LicenceBanner />);
    expect(screen.getByRole("status")).toHaveTextContent("expire dans 1 jour (");
  });

  it("existe aussi en anglais", () => {
    licence.mockReturnValue({ data: { ...valide, etat: "REVOQUEE", lectureSeule: true } });
    renderIntl(<LicenceBanner />, "en");

    expect(screen.getByRole("alert")).toHaveTextContent("Your licence has been revoked");
  });

  it("n'interroge pas l'API et n'affiche rien pour un rôle sans accès (locataire, prestataire…)", () => {
    moi.mockReturnValue({ data: { role: "LOCATAIRE" } });
    licence.mockReturnValue({ data: { ...valide, etat: "EXPIREE" } });
    renderIntl(<LicenceBanner />);

    expect(licence).toHaveBeenCalledWith({ enabled: false });
    expect(screen.queryByTestId("bandeau-licence")).not.toBeInTheDocument();
  });

  it("n'affiche rien tant que l'utilisateur n'est pas connu", () => {
    moi.mockReturnValue({ data: undefined });
    renderIntl(<LicenceBanner />);
    expect(screen.queryByTestId("bandeau-licence")).not.toBeInTheDocument();
  });
});
