import type { components } from "@/lib/api/schema";

export type ImmeubleDto = components["schemas"]["ImmeubleDto"];
export type LotDto = components["schemas"]["LotDto"];
export type PhotoDto = components["schemas"]["PhotoDto"];

export const TYPES_LOT = ["APPARTEMENT", "STUDIO", "BOUTIQUE", "BUREAU", "TERRAIN"] as const;
export const STATUTS_LOT = ["LIBRE", "OCCUPE", "EN_TRAVAUX"] as const;
export const TYPES_COMPTEUR = ["EAU", "ELECTRICITE", "GAZ"] as const;
export type TypeLot = (typeof TYPES_LOT)[number];
export type StatutLot = (typeof STATUTS_LOT)[number];
export type TypeCompteur = (typeof TYPES_COMPTEUR)[number];

/** Un équipement par ligne : lignes vides ignorées, doublons exacts retirés. */
export function parseEquipements(texte: string): string[] {
  return [...new Set(texte.split(/\r?\n/).map((l) => l.trim()).filter((l) => l !== ""))];
}

/** Nombre optionnel saisi dans un champ texte : vide → undefined, invalide → NaN. */
export function nombreOptionnel(texte: string): number | undefined {
  const t = texte.trim().replace(",", ".");
  return t === "" ? undefined : Number(t);
}

export function urlPhoto(fichierId: string): string {
  return `/api/proxy/api/v1/fichiers/${fichierId}`;
}
