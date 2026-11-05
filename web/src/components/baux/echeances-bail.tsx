"use client";

import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";

import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { StatutPastille } from "@/components/statut-pastille";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useEcheances } from "@/hooks/use-baux";
import { formaterFcfa } from "@/lib/baux";
import type { components } from "@/lib/api/schema";

type EcheanceDto = components["schemas"]["EcheanceDto"];

/** Échéances d'un bail : période, date limite, montant, payé, reste, statut. */
export function EcheancesBail({ bailId }: { bailId: string }) {
  const t = useTranslations("baux.echeances");
  const locale = useLocale();
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = useEcheances(bailId, page);

  const colonnes: Colonne<EcheanceDto>[] = [
    { id: "periode", entete: t("periode"), cellule: (e) => `${e.periodeDebut} → ${e.periodeFin}` },
    { id: "date", entete: t("dateEcheance"), cellule: (e) => e.dateEcheance },
    { id: "montant", entete: t("montant"), cellule: (e) => formaterFcfa(e.montant, locale) },
    { id: "paye", entete: t("paye"), cellule: (e) => formaterFcfa(e.montantPaye, locale) },
    { id: "reste", entete: t("reste"), cellule: (e) => formaterFcfa(e.reste, locale) },
    {
      id: "statut",
      entete: t("statut"),
      cellule: (e) => (
        <StatutPastille statut={e.statut} libelle={e.statut ? t(`statuts.${e.statut}`) : ""} testid="statut-echeance" />
      ),
    },
  ];

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("titre")}</CardTitle>
      </CardHeader>
      <CardContent>
        {error ? (
          <ErrorDisplay erreur={error} onRetry={() => void refetch()} />
        ) : (
          <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(e) => e.id ?? ""} messageVide={t("aucune")} />
        )}
      </CardContent>
    </Card>
  );
}
