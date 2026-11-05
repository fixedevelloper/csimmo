"use client";

import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";

import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { StatutPastille } from "@/components/statut-pastille";
import { useDocumentsPaiement } from "@/hooks/use-finance";
import { formaterFcfa } from "@/lib/baux";
import { urlDocument, type DocumentDto } from "@/lib/finance";

/** Quittances et reçus d'un bail ou d'un locataire, avec téléchargement du PDF. */
export function DocumentsPaiement({ bailId, locataireId }: { bailId?: string; locataireId?: string }) {
  const t = useTranslations("finance.documents");
  const locale = useLocale();
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = useDocumentsPaiement({ bailId, locataireId }, page);

  const colonnes: Colonne<DocumentDto>[] = [
    { id: "numero", entete: t("numero"), cellule: (d) => d.numero },
    { id: "type", entete: t("type"), cellule: (d) => (d.type ? t(`types.${d.type}`) : "") },
    {
      id: "periode",
      entete: t("periode"),
      cellule: (d) => (d.periodeDebut ? `${d.periodeDebut} → ${d.periodeFin}` : "—"),
    },
    { id: "montant", entete: t("montant"), cellule: (d) => formaterFcfa(d.montant, locale) },
    { id: "date", entete: t("emisLe"), cellule: (d) => d.dateEmission },
    {
      id: "statut",
      entete: t("statut"),
      cellule: (d) =>
        d.statut === "ANNULE" ? (
          <StatutPastille statut="ANNULE" libelle={t("annule")} title={d.motifAnnulation} />
        ) : (
          <StatutPastille statut="EMIS" libelle={t("emis")} />
        ),
    },
    {
      id: "pdf",
      entete: "",
      cellule: (d) => (
        <a href={urlDocument(d.fichierId ?? "")} target="_blank" rel="noopener noreferrer" className="underline underline-offset-2" aria-label={t("telechargerNumero", { numero: d.numero ?? "" })}>
          {t("telecharger")}
        </a>
      ),
    },
  ];

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(d) => d.id ?? ""} messageVide={t("aucun")} />;
}
