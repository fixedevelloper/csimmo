import { describe, expect, it } from "vitest";

import { EMPREINTE_VALIDE, formaterFcfa, niveau } from "./licences";
import { messageErreur } from "./messages";

describe("niveau d'alerte", () => {
  it.each([
    [{ statut: "ACTIVE", joursRestants: -1 }, "expiree"],
    [{ statut: "ACTIVE", joursRestants: 0 }, "critique"],
    [{ statut: "ACTIVE", joursRestants: 7 }, "critique"],
    [{ statut: "ACTIVE", joursRestants: 8 }, "proche"],
    [{ statut: "ACTIVE", joursRestants: 30 }, "proche"],
    [{ statut: "ACTIVE", joursRestants: 31 }, "ok"],
    [{ statut: "REVOQUEE", joursRestants: 200 }, "revoquee"],
    [{ statut: "REVOQUEE", joursRestants: -5 }, "revoquee"],
  ] as const)("%j → %s", (l, attendu) => expect(niveau(l)).toBe(attendu));
});

describe("divers", () => {
  it("valide une empreinte SHA-256 hexadécimale", () => {
    expect(EMPREINTE_VALIDE.test("a".repeat(64))).toBe(true);
    expect(EMPREINTE_VALIDE.test("A".repeat(64))).toBe(true);
    expect(EMPREINTE_VALIDE.test("a".repeat(63))).toBe(false);
    expect(EMPREINTE_VALIDE.test("g".repeat(64))).toBe(false);
  });
  it("formate les FCFA sans décimales", () => expect(formaterFcfa(1500000)).toBe("1 500 000 FCFA"));
  it("traduit les codes d'erreur, avec un message par défaut", () => {
    expect(messageErreur("TRANSFERT_LIMITE_ATTEINTE")).toContain("transferts");
    expect(messageErreur("CODE_INCONNU")).toBe("Une erreur est survenue.");
    expect(messageErreur(undefined)).toBe("Une erreur est survenue.");
  });
});
