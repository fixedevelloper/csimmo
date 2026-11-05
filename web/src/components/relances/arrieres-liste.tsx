"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { ConfirmDialog } from "@/components/confirm-dialog";
import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { HistoriqueRelances } from "@/components/relances/historique-relances";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useArrieres } from "@/hooks/use-relances";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { formaterFcfa } from "@/lib/baux";
import { urlDocument } from "@/lib/finance";
import type { ArriereDto } from "@/lib/relances";

/** Liste des arriérés : une ligne par échéance impayée, avec dernière relance, relance manuelle, historique et mise en demeure. */
export function ArrieresListe() {
  const t = useTranslations("relances");
  const locale = useLocale();
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [saisie, setSaisie] = useState("");
  const [q, setQ] = useState("");
  const { data, isPending, error, refetch } = useArrieres(q, page);
  const [ouvert, setOuvert] = useState<string | null>(null);
  const [mise, setMise] = useState<ArriereDto | null>(null);
  const [erreur, setErreur] = useState<string | null>(null);
  const [fichiers, setFichiers] = useState<Record<string, string>>({});

  async function rafraichir() {
    await Promise.all(["arrieres", "relances"].map((k) => queryClient.invalidateQueries({ queryKey: [k] })));
  }

  async function relancer(a: ArriereDto) {
    setErreur(null);
    try {
      deballer(await api.POST("/api/v1/relances", { body: { echeanceId: a.echeanceId ?? "" } }));
      await rafraichir();
      toast.success(t("relanceEnvoyee"));
    } catch (e) {
      setErreur(message(e));
    }
  }

  async function emettre(a: ArriereDto) {
    setErreur(null);
    try {
      const med = deballer(await api.POST("/api/v1/mises-en-demeure", { body: { echeanceId: a.echeanceId ?? "" } }));
      setFichiers((f) => ({ ...f, [a.echeanceId ?? ""]: med.fichierId ?? "" }));
      await rafraichir();
      toast.success(t("miseEnDemeureEmise", { numero: med.numero ?? "" }));
    } catch (e) {
      setErreur(message(e));
    }
  }

  const colonnes: Colonne<ArriereDto>[] = [
    { id: "locataire", entete: t("locataire"), cellule: (a) => a.locataireNom ?? "—" },
    { id: "logement", entete: t("logement"), cellule: (a) => `${a.logement ?? ""} (${a.bailNumero ?? ""})` },
    { id: "echeance", entete: t("echeance"), cellule: (a) => a.dateEcheance },
    { id: "reste", entete: t("reste"), cellule: (a) => formaterFcfa(a.reste, locale) },
    { id: "retard", entete: t("retard"), cellule: (a) => t("jours", { n: a.joursRetard ?? 0 }) },
    {
      id: "derniere",
      entete: t("derniereRelance"),
      cellule: (a) =>
        a.derniereRelance
          ? `${a.derniereRelance.dateRelance} — ${a.derniereRelance.canal ?? t("aucunCanal")}`
          : t("aucuneRelance"),
    },
    {
      id: "actions",
      entete: "",
      cellule: (a) => (
        <div className="flex flex-wrap gap-2">
          <Button size="sm" variant="outline" onClick={() => void relancer(a)} aria-label={t("relancerNom", { nom: a.locataireNom ?? "" })}>
            {t("relancer")}
          </Button>
          <Button size="sm" variant="outline" onClick={() => setOuvert(ouvert === a.echeanceId ? null : (a.echeanceId ?? null))}>
            {t("historiqueBouton")}
          </Button>
          {a.miseEnDemeureId ? (
            fichiers[a.echeanceId ?? ""] ? (
              <a href={urlDocument(fichiers[a.echeanceId ?? ""])} target="_blank" rel="noopener noreferrer" className="self-center text-sm underline underline-offset-2">
                {t("telechargerMed")}
              </a>
            ) : (
              <span className="self-center text-sm text-muted-foreground">{t("medEmise")}</span>
            )
          ) : a.peutMiseEnDemeure ? (
            <Button size="sm" onClick={() => setMise(a)} aria-label={t("miseEnDemeureNom", { nom: a.locataireNom ?? "" })}>
              {t("miseEnDemeure")}
            </Button>
          ) : null}
        </div>
      ),
    },
  ];

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <div className="flex flex-col gap-4">
      <form
        role="search"
        className="flex gap-2"
        onSubmit={(e) => {
          e.preventDefault();
          setPage(0);
          setQ(saisie.trim());
        }}
      >
        <Input aria-label={t("recherche")} placeholder={t("recherche")} value={saisie} onChange={(e) => setSaisie(e.target.value)} className="w-64" />
        <Button type="submit" variant="outline">
          {t("rechercher")}
        </Button>
      </form>
      {erreur ? (
        <p role="alert" className="text-sm text-destructive">
          {erreur}
        </p>
      ) : null}
      <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(a) => a.echeanceId ?? ""} messageVide={t("aucunArriere")} />
      {ouvert ? (
        <div className="rounded-lg border p-3">
          <h2 className="mb-2 text-sm font-semibold">{t("historique.titre")}</h2>
          <HistoriqueRelances echeanceId={ouvert} />
        </div>
      ) : null}
      <ConfirmDialog
        open={mise !== null}
        onOpenChange={(o) => !o && setMise(null)}
        title={t("confirmerMedTitre")}
        description={t("confirmerMedDescription", { nom: mise?.locataireNom ?? "" })}
        confirmLabel={t("miseEnDemeure")}
        onConfirm={() => {
          const a = mise;
          setMise(null);
          return a ? emettre(a) : undefined;
        }}
      />
    </div>
  );
}
