export const LOCALES = ["fr", "en"] as const;
export type Locale = (typeof LOCALES)[number];
export const DEFAULT_LOCALE: Locale = "fr";
export const LOCALE_COOKIE = "NEXT_LOCALE";

export function estLocale(valeur: string | undefined): valeur is Locale {
  return LOCALES.some((l) => l === valeur);
}
