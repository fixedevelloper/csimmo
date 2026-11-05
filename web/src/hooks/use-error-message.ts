"use client";

import { useTranslations } from "next-intl";

import { cleMessageErreur } from "@/lib/api/errors";

/** Renvoie une fonction qui traduit n'importe quelle erreur (ApiError, réseau…) dans la langue courante. */
export function useErrorMessage() {
  const t = useTranslations();
  return (erreur: unknown): string => t(cleMessageErreur(erreur, (cle) => t.has(cle)));
}
