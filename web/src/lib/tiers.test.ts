import { describe, expect, it } from "vitest";

import { optionnel, pointsDeBaseEnPourcentage, pourcentageEnPointsDeBase } from "./tiers";

describe("taux d'honoraires", () => {
  it("convertit un pourcentage saisi en points de base", () => {
    expect(pourcentageEnPointsDeBase("10")).toBe(1000);
    expect(pourcentageEnPointsDeBase("7,5")).toBe(750);
    expect(pourcentageEnPointsDeBase(" 7.25 ")).toBe(725);
    expect(pourcentageEnPointsDeBase("0")).toBe(0);
    expect(pourcentageEnPointsDeBase("100")).toBe(10000);
  });

  it("refuse les saisies invalides ou hors de 0–100 %", () => {
    for (const saisie of ["", "  ", "abc", "-1", "100,01", "1e3x"]) {
      expect(pourcentageEnPointsDeBase(saisie), saisie).toBeNaN();
    }
  });

  it("affiche des points de base en pourcentage lisible", () => {
    expect(pointsDeBaseEnPourcentage(750, "fr")).toBe("7,5");
    expect(pointsDeBaseEnPourcentage(1000, "fr")).toBe("10");
    expect(pointsDeBaseEnPourcentage(750, "en")).toBe("7.5");
  });

  it("optionnel : vide devient undefined", () => {
    expect(optionnel("  ")).toBeUndefined();
    expect(optionnel(undefined)).toBeUndefined();
    expect(optionnel(" a ")).toBe("a");
  });
});
