import { describe, expect, it } from "vitest";

import { nombreOptionnel, parseEquipements, urlPhoto } from "./patrimoine";

describe("patrimoine", () => {
  it("parseEquipements : un par ligne, sans vide ni doublon", () => {
    expect(parseEquipements("Parking\n\n  Balcon \r\nParking\n")).toEqual(["Parking", "Balcon"]);
    expect(parseEquipements("")).toEqual([]);
  });

  it("nombreOptionnel : vide, virgule décimale, invalide", () => {
    expect(nombreOptionnel("  ")).toBeUndefined();
    expect(nombreOptionnel("65,5")).toBe(65.5);
    expect(nombreOptionnel("abc")).toBeNaN();
  });

  it("urlPhoto passe par le proxy de l'API (jamais par S3)", () => {
    expect(urlPhoto("abc")).toBe("/api/proxy/api/v1/fichiers/abc");
  });
});
