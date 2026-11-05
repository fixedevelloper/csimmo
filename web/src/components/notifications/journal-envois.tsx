"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { StatutPastille } from "@/components/statut-pastille";
import { Button } from "@/components/ui/button";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useJournal } from "@/hooks/use-notifications";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { STATUTS_MESSAGE, type MessageDto } from "@/lib/notifications";

/** Journal des envois : statut, essais, dernière erreur ; relance manuelle d'un message en échec. */
export function JournalEnvois() {
  const t = useTranslations("notifications.journal");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [statut, setStatut] = useState("");
  const [page, setPage] = useState(0);
  const [erreur, setErreur] = useState<string | null>(null);
  const { data, isPending, error, refetch } = useJournal(statut, page);

  async function relancer(id: string) {
    setErreur(null);
    try {
      deballer(await api.POST("/api/v1/notifications/journal/{id}/reessayer", { params: { path: { id } } }));
      await queryClient.invalidateQueries({ queryKey: ["notif-journal"] });
      toast.success(t("relance"));
    } catch (e) {
      setErreur(message(e));
    }
  }

  const colonnes: Colonne<MessageDto>[] = [
    { id: "date", entete: t("date"), cellule: (m) => (m.creeLe ? new Date(m.creeLe).toLocaleString() : "") },
    { id: "canal", entete: t("canal"), cellule: (m) => m.canal },
    { id: "type", entete: t("type"), cellule: (m) => (m.typeMessage ? t(`types.${m.typeMessage}`) : "") },
    { id: "dest", entete: t("destinataire"), cellule: (m) => m.destinataire },
    {
      id: "statut",
      entete: t("statut"),
      cellule: (m) => (
        <StatutPastille statut={m.statut} libelle={m.statut ? t(`statuts.${m.statut}`) : ""} testid="statut-message" />
      ),
    },
    { id: "essais", entete: t("essais"), cellule: (m) => m.tentatives },
    { id: "erreur", entete: t("erreur"), cellule: (m) => m.derniereErreur ?? "—" },
    {
      id: "actions",
      entete: "",
      cellule: (m) =>
        m.statut === "ECHEC" ? (
          <Button size="sm" variant="outline" onClick={() => void relancer(m.id ?? "")}>
            {t("reessayer")}
          </Button>
        ) : null,
    },
  ];

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <div className="flex flex-col gap-3">
      <label className="flex w-52 flex-col gap-1 text-sm">
        {t("filtreStatut")}
        <select value={statut} onChange={(e) => { setPage(0); setStatut(e.target.value); }} className="h-8 rounded-lg border border-input bg-transparent px-2.5 text-sm">
          <option value="">{t("tous")}</option>
          {STATUTS_MESSAGE.map((s) => (
            <option key={s} value={s}>
              {t(`statuts.${s}`)}
            </option>
          ))}
        </select>
      </label>
      {erreur ? (
        <p role="alert" className="text-sm text-destructive">
          {erreur}
        </p>
      ) : null}
      <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(m) => m.id ?? ""} messageVide={t("aucun")} />
    </div>
  );
}
