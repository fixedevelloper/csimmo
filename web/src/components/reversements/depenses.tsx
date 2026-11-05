"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useImmeubles } from "@/hooks/use-patrimoine";
import { useDepenses } from "@/hooks/use-reversements";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { entierSaisi, formaterFcfa } from "@/lib/baux";
import { moisCourant, type DepenseDto } from "@/lib/reversements";

/** Saisie simple des dépenses imputées au propriétaire d'un immeuble ; annulation avec motif (jamais de suppression). */
export function Depenses() {
  const t = useTranslations("reversements.depenses");
  const locale = useLocale();
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [rechercheImmeuble, setRechercheImmeuble] = useState("");
  const { data: immeubles } = useImmeubles({ q: rechercheImmeuble }, 0);
  const [mois, setMois] = useState(moisCourant());
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = useDepenses(mois, page);
  const [immeubleId, setImmeubleId] = useState("");
  const [date, setDate] = useState("");
  const [nature, setNature] = useState("");
  const [montant, setMontant] = useState("");
  const [reference, setReference] = useState("");
  const [erreurs, setErreurs] = useState<Record<string, string>>({});
  const [erreurApi, setErreurApi] = useState<string | null>(null);
  const [cible, setCible] = useState<string | null>(null);
  const [motif, setMotif] = useState("");

  async function rafraichir() {
    await Promise.all(["depenses", "reversements"].map((k) => queryClient.invalidateQueries({ queryKey: [k] })));
  }

  async function enregistrer(e: React.FormEvent) {
    e.preventDefault();
    const err: Record<string, string> = {};
    if (!immeubleId) err.immeuble = t("erreurs.immeuble");
    if (!date) err.date = t("erreurs.date");
    if (nature.trim() === "") err.nature = t("erreurs.nature");
    if (!(entierSaisi(montant) > 0)) err.montant = t("erreurs.montant");
    setErreurs(err);
    if (Object.keys(err).length > 0) return;
    setErreurApi(null);
    try {
      deballer(await api.POST("/api/v1/depenses", { body: { immeubleId, dateDepense: date, nature: nature.trim(), montant: entierSaisi(montant), reference: reference.trim() || undefined } }));
      setNature("");
      setMontant("");
      setReference("");
      await rafraichir();
      toast.success(t("enregistree"));
    } catch (ex) {
      setErreurApi(message(ex));
    }
  }

  async function annuler(id: string) {
    setErreurApi(null);
    try {
      deballer(await api.POST("/api/v1/depenses/{id}/annuler", { params: { path: { id } }, body: { motif } }));
      setCible(null);
      setMotif("");
      await rafraichir();
      toast.success(t("annulee"));
    } catch (ex) {
      setErreurApi(message(ex));
    }
  }

  const colonnes: Colonne<DepenseDto>[] = [
    { id: "date", entete: t("date"), cellule: (d) => d.dateDepense },
    { id: "immeuble", entete: t("immeuble"), cellule: (d) => d.immeubleNom ?? "—" },
    { id: "nature", entete: t("nature"), cellule: (d) => d.nature },
    { id: "montant", entete: t("montant"), cellule: (d) => formaterFcfa(d.montant, locale) },
    {
      id: "statut",
      entete: t("statut"),
      cellule: (d) => (d.statut === "ANNULEE" ? <Badge variant="outline" title={d.motifAnnulation}>{t("annuleeBadge")}</Badge> : <Badge variant="secondary">{t("imputee")}</Badge>),
    },
    {
      id: "actions",
      entete: "",
      cellule: (d) =>
        d.statut === "ENREGISTREE" ? (
          <Button size="sm" variant="outline" onClick={() => { setCible(d.id ?? null); setErreurApi(null); }} aria-label={t("annulerNature", { nature: d.nature ?? "" })}>
            {t("annuler")}
          </Button>
        ) : null,
    },
  ];

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("titre")}</CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-4">
        <form onSubmit={(e) => void enregistrer(e)} noValidate className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
          <div className="flex flex-col gap-1">
            <label htmlFor="d-recherche" className="text-sm font-medium">{t("rechercheImmeuble")}</label>
            <Input id="d-recherche" value={rechercheImmeuble} onChange={(e) => setRechercheImmeuble(e.target.value)} />
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="d-immeuble" className="text-sm font-medium">{t("immeuble")}</label>
            <select id="d-immeuble" value={immeubleId} onChange={(e) => setImmeubleId(e.target.value)} className="h-8 rounded-lg border border-input bg-transparent px-2.5 text-sm">
              <option value="">{t("choisirImmeuble")}</option>
              {(immeubles?.content ?? []).map((i) => (
                <option key={i.id} value={i.id}>{i.nom}</option>
              ))}
            </select>
            {erreurs.immeuble ? <p className="text-sm text-destructive">{erreurs.immeuble}</p> : null}
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="d-date" className="text-sm font-medium">{t("date")}</label>
            <Input id="d-date" type="date" value={date} onChange={(e) => setDate(e.target.value)} />
            {erreurs.date ? <p className="text-sm text-destructive">{erreurs.date}</p> : null}
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="d-nature" className="text-sm font-medium">{t("nature")}</label>
            <Input id="d-nature" value={nature} onChange={(e) => setNature(e.target.value)} />
            {erreurs.nature ? <p className="text-sm text-destructive">{erreurs.nature}</p> : null}
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="d-montant" className="text-sm font-medium">{t("montantFcfa")}</label>
            <Input id="d-montant" inputMode="numeric" value={montant} onChange={(e) => setMontant(e.target.value)} />
            {erreurs.montant ? <p className="text-sm text-destructive">{erreurs.montant}</p> : null}
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="d-ref" className="text-sm font-medium">{t("reference")}</label>
            <Input id="d-ref" value={reference} onChange={(e) => setReference(e.target.value)} />
          </div>
          <div className="flex items-end">
            <Button type="submit">{t("enregistrer")}</Button>
          </div>
        </form>
        {erreurApi ? <p role="alert" className="text-sm text-destructive">{erreurApi}</p> : null}
        {cible ? (
          <div className="flex max-w-xl flex-col gap-2 rounded-lg border p-3" role="group" aria-label={t("annuler")}>
            <label className="flex flex-col gap-1 text-sm">
              {t("motif")}
              <Input value={motif} onChange={(e) => setMotif(e.target.value)} />
            </label>
            <div className="flex gap-2">
              <Button size="sm" disabled={motif.trim() === ""} onClick={() => void annuler(cible)}>{t("confirmerAnnulation")}</Button>
              <Button size="sm" variant="outline" onClick={() => setCible(null)}>{t("renoncer")}</Button>
            </div>
          </div>
        ) : null}
        <label className="flex flex-col gap-1 text-sm">
          {t("filtreMois")}
          <Input type="month" value={mois} onChange={(e) => { setPage(0); setMois(e.target.value); }} className="w-44" />
        </label>
        {error ? (
          <ErrorDisplay erreur={error} onRetry={() => void refetch()} />
        ) : (
          <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(d) => d.id ?? ""} messageVide={t("aucune")} />
        )}
      </CardContent>
    </Card>
  );
}
