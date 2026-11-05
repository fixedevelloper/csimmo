import { describe, expect, it } from "vitest";

import { moisCourant, moisPrecedent } from "./reversements";

describe("reversements", () => {
  it("formate le mois courant AAAA-MM", () => {
    expect(moisCourant(new Date(2026, 10, 4))).toBe("2026-11");
    expect(moisCourant(new Date(2026, 0, 31))).toBe("2026-01");
  });
  it("calcule le mois précédent, y compris à cheval sur deux années", () => {
    expect(moisPrecedent("2026-11")).toBe("2026-10");
    expect(moisPrecedent("2026-01")).toBe("2025-12");
  });
});
