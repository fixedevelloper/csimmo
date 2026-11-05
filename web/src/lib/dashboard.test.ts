import { describe, expect, it } from "vitest";

import { abreger, formaterTaux } from "./dashboard";

describe("dashboard", () => {
  it("formate les points de base en pourcentage, tiret si indéfini", () => {
    expect(formaterTaux(8750, "fr").replace(/\s/g, " ")).toBe("87,5 %");
    expect(formaterTaux(10000, "en")).toBe("100 %");
    expect(formaterTaux(0, "fr")).toBe("0 %");
    expect(formaterTaux(null, "fr")).toBe("—");
    expect(formaterTaux(undefined, "fr")).toBe("—");
  });
  it("abrège les montants des axes", () => {
    expect(abreger(1_200_000, "en")).toBe("1.2M");
    expect(abreger(350_000, "en")).toBe("350K");
  });
});
