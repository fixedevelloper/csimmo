/**
 * Actions de l'interface qui dépendent du rôle (le serveur reste seul juge : il refuse de toute façon ; ceci évite d'afficher un bouton
 * qui répondrait 403). Voir docs/adr/0009-equipe-et-perimetres.md.
 */
export type Action =
  | "encaisser" // saisir un paiement, voir la caisse
  | "contrePasser"
  | "calculerReversements"
  | "validerReversement"
  | "payerReversement"
  | "saisirDepense";

const COMPLET = ["ADMIN", "GESTIONNAIRE"];

const DROITS: Record<Action, readonly string[]> = {
  encaisser: [...COMPLET, "ENCAISSEUR", "GESTIONNAIRE_BIENS"],
  contrePasser: COMPLET,
  calculerReversements: COMPLET,
  validerReversement: [...COMPLET, "COMPTABLE"],
  payerReversement: COMPLET,
  saisirDepense: COMPLET,
};

export function peut(role: string | undefined, action: Action): boolean {
  return role !== undefined && DROITS[action].includes(role);
}
