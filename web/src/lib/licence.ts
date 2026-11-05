import type { components } from "@/lib/api/schema";

export type EtatLicenceDto = components["schemas"]["EtatLicenceDto"];

export type Bandeau = {
  niveau: "erreur" | "alerte";
  /** Clé de traduction sous `bandeau.*`. */
  cle: string;
  valeurs: { jours?: number; date?: string };
};

/** Seuil (en jours) sous lequel on prévient que la vérification en ligne sera bientôt exigée. */
const SEUIL_GRACE_JOURS = 7;

/**
 * Bandeau d'alerte global : rouge si l'application est en lecture seule, orange à 30 / 15 / 7 jours de l'expiration
 * ou quand le délai de grâce hors ligne touche à sa fin. `null` : rien à signaler.
 */
export function bandeauLicence(etat: EtatLicenceDto | undefined): Bandeau | null {
  if (!etat?.etat) return null;
  if (etat.etat !== "VALIDE") {
    return { niveau: "erreur", cle: etat.etat, valeurs: {} };
  }
  if (etat.alerteJours != null && etat.joursRestants != null) {
    return {
      niveau: "alerte",
      cle: "expireBientot",
      valeurs: { jours: etat.joursRestants, date: etat.dateExpiration },
    };
  }
  if (
    etat.verificationEnLigneActive &&
    etat.joursDeGraceRestants != null &&
    etat.joursDeGraceRestants <= SEUIL_GRACE_JOURS
  ) {
    return { niveau: "alerte", cle: "graceBientotFinie", valeurs: { jours: etat.joursDeGraceRestants } };
  }
  return null;
}

/** Date seule (AAAA-MM-JJ) affichée sans décalage de fuseau. */
export function formaterDate(iso: string | undefined, locale: string): string {
  if (!iso) return "";
  const date = new Date(`${iso.slice(0, 10)}T00:00:00Z`);
  return new Intl.DateTimeFormat(locale, { dateStyle: "long", timeZone: "UTC" }).format(date);
}

/** Rôles autorisés à consulter la licence (le backend répond 403 aux autres). */
export const ROLES_LICENCE = ["ADMIN", "GESTIONNAIRE"] as const;
