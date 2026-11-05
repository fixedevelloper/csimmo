import type { components } from "@/lib/api/schema";

export type SyntheseDto = components["schemas"]["Synthese"];
export type MoisDto = components["schemas"]["Mois"];
export type ArrieresDto = components["schemas"]["Arrieres"];
export type LigneArriereDto = components["schemas"]["LigneArriere"];
export type BailAEcheanceDto = components["schemas"]["BailAEcheance"];

/** Points de base → « 87,5 % » ; « — » quand le taux est indéfini (dénominateur nul). */
export function formaterTaux(bp: number | null | undefined, locale: string): string {
  if (bp === null || bp === undefined) return "—";
  return `${new Intl.NumberFormat(locale, { maximumFractionDigits: 1 }).format(bp / 100)} %`;
}

/** Montant abrégé pour les axes (1,2 M ; 350 k). */
export function abreger(montant: number, locale: string): string {
  return new Intl.NumberFormat(locale, { notation: "compact", maximumFractionDigits: 1 }).format(montant);
}
