import { screen, within } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import { renderIntl } from "@/test/render";

import { BarresAttenduEncaisse } from "./barres-attendu-encaisse";
import { DonutOccupation } from "./donut-occupation";

const mois = [
  { mois: "2026-09-01", attendu: 0, encaisse: 0, tauxBp: undefined },
  { mois: "2026-10-01", attendu: 400000, encaisse: 300000, tauxBp: 7500 },
  { mois: "2026-11-01", attendu: 200000, encaisse: 200000, tauxBp: 10000 },
];

describe("BarresAttenduEncaisse", () => {
  it("expose le graphique avec son libellé et la légende", () => {
    renderIntl(<BarresAttenduEncaisse mois={mois} />, "fr");
    expect(screen.getByRole("img", { name: /Loyers attendus et encaissés/ })).toBeInTheDocument();
    expect(screen.getAllByText("Attendu").length).toBeGreaterThan(0);
    expect(screen.getAllByText("Encaissé").length).toBeGreaterThan(0);
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

describe("DonutOccupation", () => {
  it("affiche le taux au centre et le détail par statut", () => {
    renderIntl(<DonutOccupation tauxBp={8030} occupes={57} libres={14} travaux={0} />, "fr");
    expect(screen.getByText("80,3 %")).toBeInTheDocument();
    expect(screen.getByText("Occupés").nextElementSibling).toHaveTextContent("57");
    expect(screen.getByText("Libres").nextElementSibling).toHaveTextContent("14");
    expect(screen.getByRole("img", { name: "Occupation du parc" })).toBeInTheDocument();
  });

  it("supporte un parc vide", () => {
    renderIntl(<DonutOccupation tauxBp={null} occupes={0} libres={0} travaux={0} />, "en");
    expect(screen.getByText("—")).toBeInTheDocument();
  });
});
