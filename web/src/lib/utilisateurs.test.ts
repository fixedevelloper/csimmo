import { describe, expect, it } from "vitest";

import { genererMotDePasse } from "./utilisateurs";

describe("genererMotDePasse", () => {
  it("produit la longueur demandée (12 par défaut, 8 au minimum)", () => {
    expect(genererMotDePasse()).toHaveLength(12);
    expect(genererMotDePasse(20)).toHaveLength(20);
    expect(genererMotDePasse(3)).toHaveLength(8);
  });

  it("contient une minuscule, une majuscule, un chiffre et un caractère spécial, sans caractères ambigus", () => {
    for (let i = 0; i < 200; i++) {
      const mdp = genererMotDePasse();
      expect(mdp).toMatch(/[a-z]/);
      expect(mdp).toMatch(/[A-Z]/);
      expect(mdp).toMatch(/[0-9]/);
      expect(mdp).toMatch(/[!#$%*+\-=?@]/);
      expect(mdp).not.toMatch(/[lIOo01]/);
    }
  });

  it("ne répète pas deux fois la même valeur", () => {
    const valeurs = new Set(Array.from({ length: 50 }, () => genererMotDePasse()));
    expect(valeurs.size).toBe(50);
  });
});
