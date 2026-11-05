"use client";

import { useMutation } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useState } from "react";

import { ConfirmDialog } from "@/components/confirm-dialog";
import { Button } from "@/components/ui/button";
import { useErrorMessage } from "@/hooks/use-error-message";

/** Bouton « Archiver » avec confirmation ; `action` appelle l'API et lève en cas d'erreur. */
export function ArchiverBouton({ titre, action, apres }: { titre: string; action: () => Promise<unknown>; apres: () => void }) {
  const t = useTranslations("tiers");
  const message = useErrorMessage();
  const [ouvert, setOuvert] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);
  const archiver = useMutation({
    mutationFn: action,
    onSuccess: apres,
    onError: (e) => {
      setOuvert(false);
      setErreur(message(e));
    },
  });
  return (
    <>
      <Button variant="outline" onClick={() => setOuvert(true)}>
        {t("archiver")}
      </Button>
      {erreur ? (
        <p role="alert" className="text-sm text-destructive">
          {erreur}
        </p>
      ) : null}
      <ConfirmDialog open={ouvert} onOpenChange={setOuvert} title={titre} description={t("archiverDescription")} confirmLabel={t("archiver")} onConfirm={() => archiver.mutate()} />
    </>
  );
}
