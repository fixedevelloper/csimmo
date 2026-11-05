import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import { BadgeLicence } from "./ui";

describe("BadgeLicence", () => {
  it("dit l'état en toutes lettres (pas seulement par la couleur)", () => {
    render(<BadgeLicence licence={{ statut: "ACTIVE", joursRestants: 5 }} />);
    expect(screen.getByText("Expire dans 7 jours ou moins")).toBeInTheDocument();
  });
  it("signale une licence expirée ou révoquée", () => {
    render(
      <>
        <BadgeLicence licence={{ statut: "ACTIVE", joursRestants: -3 }} />
        <BadgeLicence licence={{ statut: "REVOQUEE", joursRestants: 100 }} />
      </>,
    );
    expect(screen.getByText("Expirée")).toBeInTheDocument();
    expect(screen.getByText("Révoquée")).toBeInTheDocument();
  });
});
