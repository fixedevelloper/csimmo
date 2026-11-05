"use client";

import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";

import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { Badge } from "@/components/ui/badge";
import { useTransactionsEnLigne } from "@/hooks/use-paiement-en-ligne";
import { formaterFcfa } from "@/lib/baux";
import type { components } from "@/lib/api/schema";

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
        <Badge variant={x.statut === "CONFIRMEE" ? "default" : x.statut === "ANOMALIE" ? "destructive" : "secondary"} data-testid="statut-transaction">
          {x.statut ? t(`statuts.${x.statut}`) : ""}
        </Badge>
      ),
    },
  ];

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <div className="flex flex-col gap-3">
      <label className="flex w-52 flex-col gap-1 text-sm">
        {t("filtreStatut")}
        <select value={statut} onChange={(e) => { setPage(0); setStatut(e.target.value); }} className="h-8 rounded-lg border border-input bg-transparent px-2.5 text-sm">
          <option value="">{t("tous")}</option>
          {STATUTS.map((s) => (
            <option key={s} value={s}>
              {t(`statuts.${s}`)}
            </option>
          ))}
        </select>
      </label>
      <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(x) => x.id ?? ""} messageVide={t("aucune")} />
    </div>
  );
}
