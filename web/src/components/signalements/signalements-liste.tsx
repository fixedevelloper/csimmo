"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { StatutPastille } from "@/components/statut-pastille";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useSignalements } from "@/hooks/use-signalements";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { urlDocument } from "@/lib/finance";
import type { components } from "@/lib/api/schema";

type SignalementDto = components["schemas"]["SignalementDto"];
const STATUTS = ["NOUVEAU", "EN_COURS", "RESOLU", "REJETE"] as const;

/** Pannes signalées par les locataires depuis l'application mobile : prise en charge, résolution ou rejet (avec note). */
export function SignalementsListe() {
  const t = useTranslations("signalements");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [statut, setStatut] = useState("NOUVEAU");
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = useSignalements(statut, page);
  const [cible, setCible] = useState<{ id: string; vers: "EN_COURS" | "RESOLU" | "REJETE" } | null>(null);
  const [note, setNote] = useState("");
  const [erreur, setErreur] = useState<string | null>(null);

  async function changer() {
    if (!cible) return;
    setErreur(null);
    try {
      deballer(await api.PUT("/api/v1/signalements/{id}/statut", { params: { path: { id: cible.id } }, body: { statut: cible.vers, note: note.trim() || undefined } }));
      setCible(null);
      setNote("");
      await queryClient.invalidateQueries({ queryKey: ["signalements"] });
      toast.success(t("mis-a-jour"));
    } catch (e) {
      setErreur(message(e));
    }
  }

  const colonnes: Colonne<SignalementDto>[] = [
    { id: "date", entete: t("date"), cellule: (s) => (s.creeLe ? new Date(s.creeLe).toLocaleString() : "") },
    { id: "locataire", entete: t("locataire"), cellule: (s) => s.locataireNom ?? "—" },
    { id: "logement", entete: t("logement"), cellule: (s) => s.logement ?? "—" },
    { id: "description", entete: t("description"), cellule: (s) => <span className="whitespace-pre-wrap">{s.description}</span> },
    {
      id: "photo",
      entete: t("photo"),
      cellule: (s) =>
        s.photoFichierId ? (
          <a href={urlDocument(s.photoFichierId)} target="_blank" rel="noopener noreferrer" className="underline underline-offset-2">
            {t("voirPhoto")}
          </a>
        ) : (
          "—"
        ),
    },
    {
      id: "statut",
      entete: t("statut"),
      cellule: (s) => (
        <span className="flex flex-col gap-1">
          <StatutPastille statut={s.statut} libelle={s.statut ? t(`statuts.${s.statut}`) : ""} testid="statut-signalement" />
          {s.noteGestion ? <span className="text-xs text-muted-foreground">{s.noteGestion}</span> : null}
        </span>
      ),
    },
    {
      id: "actions",
      entete: "",
      cellule: (s) =>
        s.statut === "NOUVEAU" || s.statut === "EN_COURS" ? (
          <div className="flex flex-wrap gap-2">
            {s.statut === "NOUVEAU" ? (
              <Button size="sm" variant="outline" onClick={() => { setCible({ id: s.id ?? "", vers: "EN_COURS" }); setErreur(null); }}>
                {t("prendreEnCharge")}
              </Button>
            ) : null}
            <Button size="sm" variant="outline" onClick={() => { setCible({ id: s.id ?? "", vers: "RESOLU" }); setErreur(null); }}>
              {t("resoudre")}
            </Button>
            <Button size="sm" variant="outline" onClick={() => { setCible({ id: s.id ?? "", vers: "REJETE" }); setErreur(null); }}>
              {t("rejeter")}
            </Button>
          </div>
        ) : null,
    },
  ];

  return (
    <div className="flex flex-col gap-4">
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
      {cible ? (
        <div className="flex max-w-xl flex-col gap-2 rounded-lg border p-3" role="group" aria-label={t(`action.${cible.vers}`)}>
          <label className="flex flex-col gap-1 text-sm">
            {t("note")}
            <Input value={note} onChange={(e) => setNote(e.target.value)} />
          </label>
          {erreur ? (
            <p role="alert" className="text-sm text-destructive">
              {erreur}
            </p>
          ) : null}
          <div className="flex gap-2">
            <Button size="sm" onClick={() => void changer()}>
              {t(`action.${cible.vers}`)}
            </Button>
            <Button size="sm" variant="outline" onClick={() => setCible(null)}>
              {t("annuler")}
            </Button>
          </div>
        </div>
      ) : null}
      {error ? (
        <ErrorDisplay erreur={error} onRetry={() => void refetch()} />
      ) : (
        <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(s) => s.id ?? ""} messageVide={t("aucun")} />
      )}
    </div>
  );
}
