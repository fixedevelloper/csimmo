import type { components } from "@/lib/api/schema";

export type ProprietaireDto = components["schemas"]["ProprietaireDto"];
export type MandatDto = components["schemas"]["MandatDto"];
export type LocataireDto = components["schemas"]["LocataireDto"];
export type GarantDto = components["schemas"]["GarantDto"];
export type PrestataireDto = components["schemas"]["PrestataireDto"];
export type PieceDto = components["schemas"]["PieceDto"];

export const MODES_REVERSEMENT = ["VIREMENT", "MOBILE_MONEY", "ESPECES", "CHEQUE"] as const;
export const TYPES_PIECE = ["CNI_RECTO", "CNI_VERSO", "PASSEPORT", "AUTRE"] as const;

/** Taux saisi en pourcentage (« 7,5 ») → points de base (750). `NaN` si invalide ou hors de 0–100 %. */
export function pourcentageEnPointsDeBase(saisie: string): number {
  const n = Number(saisie.trim().replace(",", "."));
  if (saisie.trim() === "" || Number.isNaN(n) || n < 0 || n > 100) return Number.NaN;
  return Math.round(n * 100);
}

/** Points de base → pourcentage lisible (750 → « 7,5 »). */
export function pointsDeBaseEnPourcentage(bp: number, locale: string): string {
  return new Intl.NumberFormat(locale, { maximumFractionDigits: 2 }).format(bp / 100);
}

/** Chaîne vide → undefined (les champs facultatifs ne sont pas envoyés). */
export const optionnel = (v: string | undefined): string | undefined => (v === undefined || v.trim() === "" ? undefined : v.trim());
