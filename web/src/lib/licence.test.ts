import { describe, expect, it } from "vitest";

import { bandeauLicence, formaterDate, type EtatLicenceDto } from "./licence";

const valide: EtatLicenceDto = {
  etat: "VALIDE",
  lectureSeule: false,
  joursRestants: 200,
  dateExpiration: "2026-12-31",
  verificationEnLigneActive: false,
};

describe("bandeauLicence", () => {
  it("n'affiche rien tant que l'état n'est pas connu", () => {
    expect(bandeauLicence(undefined)).toBeNull();
    expect(bandeauLicence({})).toBeNull();
  });

  it("n'affiche rien pour une licence valide loin de l'expiration", () => {
    expect(bandeauLicence(valide)).toBeNull();
  });

  it.each(["AUCUNE", "EXPIREE", "INVALIDE", "REVOQUEE", "VERIFICATION_REQUISE"] as const)(
    "bandeau rouge (lecture seule) pour l'état %s",
    (etat) => {
      expect(bandeauLicence({ ...valide, etat, lectureSeule: true })).toEqual({ niveau: "erreur", cle: etat, valeurs: {} });
    },
  );

  it.each([30, 15, 7])("bandeau orange au seuil de %i jours", (seuil) => {
    const b = bandeauLicence({ ...valide, alerteJours: seuil, joursRestants: seuil - 1 });
    expect(b).toEqual({
      niveau: "alerte",
      cle: "expireBientot",
      valeurs: { jours: seuil - 1, date: "2026-12-31" },
    });
  });

  it("l'alerte d'expiration prime sur celle de la grâce", () => {
    const b = bandeauLicence({
      ...valide,
      alerteJours: 7,
      joursRestants: 3,
      verificationEnLigneActive: true,
      joursDeGraceRestants: 2,
    });
    expect(b?.cle).toBe("expireBientot");
  });

  it("prévient quand le délai de grâce hors ligne touche à sa fin", () => {
    const b = bandeauLicence({ ...valide, verificationEnLigneActive: true, joursDeGraceRestants: 5 });
    expect(b).toEqual({ niveau: "alerte", cle: "graceBientotFinie", valeurs: { jours: 5 } });
  });

  it("ne parle pas de grâce si la vérification en ligne est désactivée ou si il reste du temps", () => {
    expect(bandeauLicence({ ...valide, verificationEnLigneActive: false, joursDeGraceRestants: 2 })).toBeNull();
    expect(bandeauLicence({ ...valide, verificationEnLigneActive: true, joursDeGraceRestants: 20 })).toBeNull();
  });
});

describe("formaterDate", () => {
  it("affiche la date sans décalage de fuseau", () => {
    expect(formaterDate("2026-12-31", "fr")).toBe("31 décembre 2026");
    expect(formaterDate("2026-01-01", "fr")).toBe("1 janvier 2026");
    expect(formaterDate("2026-12-31", "en")).toBe("December 31, 2026");
  });

  it("accepte un horodatage complet et une valeur absente", () => {
    expect(formaterDate("2026-06-15T23:30:00Z", "fr")).toBe("15 juin 2026");
    expect(formaterDate(undefined, "fr")).toBe("");
  });
});
