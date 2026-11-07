"use client";

import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";

import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { StatutPastille } from "@/components/statut-pastille";
import { useTransactionsEnLigne } from "@/hooks/use-paiement-en-ligne";
import { formaterFcfa } from "@/lib/baux";
import type { components } from "@/lib/api/schema";
import { NativeSelect } from "@/components/ui/native-select";

type TransactionDto = components["schemas"]["TransactionDto"];
const STATUTS = ["EN_ATTENTE", "CONFIRMEE", "ECHOUEE", "EXPIREE", "ANOMALIE"] as const;

/** Paiements Mobile Money des locataires : une transaction n'est CONFIRMEE (et son paiement créé) qu'après confirmation de l'opérateur. */
export function TransactionsListe() {
  const t = useTranslations("paiementEnLigne.transactions");
  const locale = useLocale();
  const [statut, setStatut] = useState("");
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = useTransactionsEnLigne(statut, page);

  const colonnes: Colonne<TransactionDto>[] = [
    { id: "date", entete: t("date"), cellule: (x) => (x.creeLe ? new Date(x.creeLe).toLocaleString() : "") },
    { id: "locataire", entete: t("locataire"), cellule: (x) => x.locataireNom ?? "—" },
    { id: "bail", entete: t("bail"), cellule: (x) => x.bailNumero ?? "—" },
    { id: "operateur", entete: t("operateur"), cellule: (x) => (x.operateur ? t(`operateurs.${x.operateur}`) : "") },
    { id: "montant", entete: t("montant"), cellule: (x) => formaterFcfa(x.montant, locale) },
    {
      id: "statut",
      entete: t("statut"),
      cellule: (x) => (
        <StatutPastille statut={x.statut} libelle={x.statut ? t(`statuts.${x.statut}`) : ""} testid="statut-transaction" />
      ),
    },
  ];

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <div className="flex flex-col gap-3">
      <label className="flex w-52 flex-col gap-1 text-sm">
        {t("filtreStatut")}
        <NativeSelect value={statut} onChange={(e) => { setPage(0); setStatut(e.target.value); }}>
          <option value="">{t("tous")}</option>
          {STATUTS.map((s) => (
            <option key={s} value={s}>
              {t(`statuts.${s}`)}
            </option>
          ))}
        </NativeSelect>
      </label>
      <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(x) => x.id ?? ""} messageVide={t("aucune")} />
    </div>
  );
}
