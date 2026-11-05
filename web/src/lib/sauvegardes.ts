/** Taille lisible (Ko, Mo, Go) en base 1024. */
export function formaterOctets(octets: number | undefined, locale = "fr"): string {
  if (octets === undefined || octets === null || Number.isNaN(octets)) return "—";
  const unites = locale === "fr" ? ["o", "Ko", "Mo", "Go", "To"] : ["B", "KB", "MB", "GB", "TB"];
  let v = octets;
  let i = 0;
  while (v >= 1024 && i < unites.length - 1) {
    v /= 1024;
    i++;
  }
  const n = new Intl.NumberFormat(locale, { maximumFractionDigits: i === 0 ? 0 : 1 }).format(v);
  return `${n} ${unites[i]}`;
}

/** Alertes bloquantes (rouge) ; les autres sont des avertissements. */
export const ALERTES_GRAVES = new Set(["SAUVEGARDE_ABSENTE", "SAUVEGARDE_ANCIENNE", "COPIE_EXTERNE_ECHEC", "DERNIERE_OPERATION_ECHEC", "DISQUE_FAIBLE", "LICENCE_INVALIDE"]);
