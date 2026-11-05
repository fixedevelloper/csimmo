import type { components } from "@/lib/api/schema";

export type BailDto = components["schemas"]["BailDto"];
export type DocumentBailDto = components["schemas"]["DocumentBailDto"];
export type ModeleBailDto = components["schemas"]["ModeleBailDto"];

export const PERIODICITES = ["MENSUELLE", "TRIMESTRIELLE", "ANNUELLE"] as const;
export type Periodicite = (typeof PERIODICITES)[number];
export const STATUTS_BAIL = ["BROUILLON", "ACTIF", "RESILIE", "TERMINE"] as const;

/** Montant en FCFA entiers, sans décimales (« 150 000 FCFA »). */
export function formaterFcfa(montant: number | undefined, locale: string): string {
  if (montant === undefined) return "—";
  return `${new Intl.NumberFormat(locale, { maximumFractionDigits: 0 }).format(montant)} FCFA`;
}

/** Entier positif ou nul saisi (« 150000 », « 150 000 ») ; `NaN` si invalide. */
export function entierSaisi(saisie: string): number {
  const t = saisie.replace(/[\s  ]/g, "");
  if (t === "" || !/^\d+$/.test(t)) return Number.NaN;
  return Number(t);
}

export const urlApercuBail = (id: string) => `/api/proxy/api/v1/baux/${id}/apercu`;
