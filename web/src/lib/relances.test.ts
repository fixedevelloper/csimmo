import { describe, expect, it } from "vitest";

import { lireNiveaux } from "./relances";

describe("lireNiveaux", () => {
  it("lit des entiers séparés par virgules, points-virgules ou espaces, négatifs compris", () => {
    expect(lireNiveaux("-5, 3, 10;30")).toEqual([-5, 3, 10, 30]);
    expect(lireNiveaux("7 2")).toEqual([7, 2]);
  });
  it("refuse le vide et tout ce qui n'est pas entier", () => {
    expect(lireNiveaux("")).toBeNull();
    expect(lireNiveaux("3, abc")).toBeNull();
    expect(lireNiveaux("1.5")).toBeNull();
  });
});
