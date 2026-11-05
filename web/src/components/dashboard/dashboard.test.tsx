import { screen, within } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import { renderIntl } from "@/test/render";

import { BarresAttenduEncaisse } from "./barres-attendu-encaisse";

const mois = [
  { mois: "2026-09-01", attendu: 0, encaisse: 0, tauxBp: undefined },
  { mois: "2026-10-01", attendu: 400000, encaisse: 300000, tauxBp: 7500 },
  { mois: "2026-11-01", attendu: 200000, encaisse: 200000, tauxBp: 10000 },
];

describe("BarresAttenduEncaisse", () => {
  it("dessine deux barres par mois, proportionnelles au plus grand montant", () => {
    renderIntl(<BarresAttenduEncaisse mois={mois} />, "fr");
    const attendu = screen.getAllByTestId("barre-attendu");
    const encaisse = screen.getAllByTestId("barre-encaisse");
    expect(attendu).toHaveLength(3);
    expect(encaisse).toHaveLength(3);
    expect(Number(attendu[1].getAttribute("height"))).toBeCloseTo(180); // 400 000 = le maximum
    expect(Number(encaisse[1].getAttribute("height"))).toBeCloseTo(135); // 300 000
    expect(Number(attendu[0].getAttribute("height"))).toBe(0);
  });

  it("donne les mêmes chiffres dans un tableau pour les lecteurs d'écran", () => {
    renderIntl(<BarresAttenduEncaisse mois={mois} />, "fr");
    const tableau = screen.getByRole("table", { hidden: true });
    const lignes = within(tableau).getAllByRole("row", { hidden: true });
    expect(lignes).toHaveLength(4); // en-tête + 3 mois
    expect(lignes[2]).toHaveTextContent("75 %");
    expect(lignes[1]).toHaveTextContent("—");
    expect(screen.getByRole("img", { name: /Loyers attendus et encaissés/ })).toBeInTheDocument();
  });

  it("supporte l'anglais", () => {
    renderIntl(<BarresAttenduEncaisse mois={mois} />, "en");
    expect(screen.getByRole("img", { name: /Rent expected and collected/ })).toBeInTheDocument();
  });
});
