"use client";

import { useLocale, useTranslations } from "next-intl";
import Link from "next/link";
import { useState } from "react";

import { BailStatutBadge } from "@/components/baux/bail-statut-badge";
import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { buttonVariants } from "@/components/ui/button";
import { useBaux } from "@/hooks/use-baux";
import { STATUTS_BAIL, formaterFcfa, type BailDto } from "@/lib/baux";

export function BauxListe() {
  const t = useTranslations("baux");
  const locale = useLocale();
  const [statut, setStatut] = useState("");
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = useBaux(statut, page);

  const colonnes: Colonne<BailDto>[] = [
    {
      id: "numero",
      entete: t("numero"),
      cellule: (b) => (
        <Link href={`/baux/${b.id}`} className="font-medium underline-offset-2 hover:underline">
          {b.numero}
        </Link>
      ),
    },
    { id: "lot", entete: t("lot"), cellule: (b) => `${b.immeubleNom ?? ""} — ${b.lotReference ?? ""}` },
    { id: "locataire", entete: t("locataire"), cellule: (b) => b.locataireNom ?? "—" },
    { id: "periode", entete: t("periode"), cellule: (b) => `${b.dateDebut} → ${b.dateFin ?? t("sansFin")}` },
    { id: "loyer", entete: t("loyer"), cellule: (b) => formaterFcfa(b.loyer, locale) },
    { id: "statut", entete: t("statut"), cellule: (b) => <BailStatutBadge statut={b.statut} /> },
  ];

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <label className="flex flex-col gap-1 text-sm">
          {t("filtreStatut")}
          <select
            value={statut}
            onChange={(e) => {
              setPage(0);
              setStatut(e.target.value);
            }}
            className="h-8 w-48 rounded-lg border border-input bg-transparent px-2.5 text-sm"
          >
            <option value="">{t("tous")}</option>
            {STATUTS_BAIL.map((s) => (
              <option key={s} value={s}>
                {t(`statuts.${s}`)}
              </option>
            ))}
          </select>
        </label>
        <Link href="/baux/nouveau" className={buttonVariants({})}>
          {t("nouveau")}
        </Link>
      </div>
      <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(b) => b.id ?? ""} messageVide={t("aucun")} />
    </div>
  );
}
