import type { components } from "@/lib/api/schema";

export type ArriereDto = components["schemas"]["ArriereDto"];
export type RelanceDto = components["schemas"]["RelanceDto"];
export type ParametresRelanceDto = components["schemas"]["ParametresRelanceDto"];
export type MiseEnDemeureDto = components["schemas"]["MiseEnDemeureDto"];

/** « -5, 3, 10 » → [-5, 3, 10] ; `null` si une valeur n'est pas un entier. */
export function lireNiveaux(saisie: string): number[] | null {
  const morceaux = saisie.split(/[,;\s]+/).filter((m) => m !== "");
  const valeurs = morceaux.map((m) => (/^-?\d+$/.test(m) ? Number(m) : Number.NaN));
  return valeurs.length === 0 || valeurs.some(Number.isNaN) ? null : valeurs;
}
