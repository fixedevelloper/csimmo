import { describe, expect, it } from "vitest";

import { entierSaisi, formaterFcfa } from "./baux";

describe("baux", () => {
  it("lit un entier avec espaces, refuse décimales et négatifs", () => {
    expect(entierSaisi("150 000")).toBe(150000);
    expect(entierSaisi("150 000")).toBe(150000);
    expect(entierSaisi("0")).toBe(0);
    expect(Number.isNaN(entierSaisi(""))).toBe(true);
    expect(Number.isNaN(entierSaisi("1,5"))).toBe(true);
    expect(Number.isNaN(entierSaisi("-3"))).toBe(true);
  });

  it("formate les FCFA sans décimales", () => {
    expect(formaterFcfa(150000, "fr").replace(/\s/g, " ")).toBe("150 000 FCFA");
    expect(formaterFcfa(undefined, "fr")).toBe("—");
  });
});
