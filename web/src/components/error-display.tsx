"use client";

import { useTranslations } from "next-intl";

import { Button } from "@/components/ui/button";
import { useErrorMessage } from "@/hooks/use-error-message";

/** Page/encart d'erreur : traduit le code métier de l'API, sans jamais afficher de détail technique. */
export function ErrorDisplay({ erreur, onRetry }: { erreur: unknown; onRetry?: () => void }) {
  const t = useTranslations();
  const message = useErrorMessage();
  return (
    <div role="alert" className="flex flex-col items-center gap-4 py-16 text-center">
      <h1 className="text-xl font-semibold">{t("erreur.titre")}</h1>
      <p className="max-w-md text-muted-foreground">{message(erreur)}</p>
      {onRetry ? <Button onClick={onRetry}>{t("common.reessayer")}</Button> : null}
    </div>
  );
}
