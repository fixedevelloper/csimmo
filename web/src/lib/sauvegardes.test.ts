import { describe, expect, it } from "vitest";

import { formaterOctets } from "./sauvegardes";

/** Intl insère des espaces insécables (fines) comme séparateurs : on les normalise pour comparer. */
const norm = (t: string) => t.replace(/[\u202f\u00a0]/g, " ");

describe("formaterOctets", () => {
  it("formate en unités lisibles (fr)", () => {
    expect(formaterOctets(0)).toBe("0 o");
    expect(norm(formaterOctets(1023))).toBe("1 023 o");
    expect(formaterOctets(1536)).toMatch(/^1,5 Ko$/);
    expect(formaterOctets(5 * 1024 * 1024 * 1024)).toMatch(/^5 Go$/);
  });
  it("utilise B/KB/MB en anglais et tolère l'absence de valeur", () => {
    expect(formaterOctets(2048, "en")).toBe("2 KB");
    expect(formaterOctets(undefined)).toBe("—");
  });
});
