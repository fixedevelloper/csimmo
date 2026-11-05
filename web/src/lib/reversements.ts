import type { components } from "@/lib/api/schema";

export type ReversementDto = components["schemas"]["ReversementDto"];
export type DepenseDto = components["schemas"]["DepenseDto"];

export const MODES_REVERSEMENT = ["VIREMENT", "MOBILE_MONEY", "ESPECES", "CHEQUE"] as const;

/** Mois au format AAAA-MM (valeur d'un champ `type="month"`), mois courant par défaut. */
export function moisCourant(maintenant: Date = new Date()): string {
  return `${maintenant.getFullYear()}-${String(maintenant.getMonth() + 1).padStart(2, "0")}`;
}

/** Mois précédent d'un mois AAAA-MM. */
export function moisPrecedent(mois: string): string {
  const [a, m] = mois.split("-").map(Number);
  const d = new Date(a, m - 2, 1);
  return moisCourant(d);
}

export const urlApercuReleve = (id: string) => `/api/proxy/api/v1/reversements/${id}/apercu`;
