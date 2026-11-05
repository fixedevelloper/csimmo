"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import Link from "next/link";
import { useState } from "react";
import { toast } from "sonner";

import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useErrorMessage } from "@/hooks/use-error-message";
import { usePaiements, type FiltresPaiements } from "@/hooks/use-finance";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { formaterFcfa } from "@/lib/baux";
import { MODES_PAIEMENT, type ModePaiement, type PaiementDto } from "@/lib/finance";

/** Liste filtrable des paiements ; `locataireId` fixe la liste au compte d'un locataire. Contre-passation avec motif obligatoire. */
export function PaiementsListe({ locataireId }: { locataireId?: string }) {
  const t = useTranslations("finance");
  const locale = useLocale();
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [filtres, setFiltres] = useState<FiltresPaiements>({ locataireId });
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = usePaiements(filtres, page);
  const [cible, setCible] = useState<string | null>(null);
  const [motif, setMotif] = useState("");
  const [erreur, setErreur] = useState<string | null>(null);

  async function contrePasser(id: string) {
    setErreur(null);
    try {
      deballer(await api.POST("/api/v1/paiements/{id}/contre-passation", { params: { path: { id } }, body: { motif } }));
      setCible(null);
      setMotif("");
      await Promise.all(["paiements", "caisse", "compte", "echeances"].map((k) => queryClient.invalidateQueries({ queryKey: [k] })));
      toast.success(t("contrePasse"));
    } catch (e) {
      setErreur(message(e));
    }
  }

  const colonnes: Colonne<PaiementDto>[] = [
    { id: "date", entete: t("datePaiement"), cellule: (p) => p.datePaiement },
    {
      id: "locataire",
      entete: t("locataire"),
      cellule: (p) =>
        locataireId || !p.locataireId ? (p.locataireNom ?? "—") : (
          <Link href={`/locataires/${p.locataireId}/compte`} className="underline-offset-2 hover:underline">
            {p.locataireNom}
          </Link>
        ),
    },
    { id: "bail", entete: t("bail"), cellule: (p) => p.bailNumero ?? "—" },
    { id: "mode", entete: t("mode"), cellule: (p) => (p.mode ? t(`modes.${p.mode}`) : t("avance")) },
    { id: "reference", entete: t("reference"), cellule: (p) => p.reference ?? "—" },
    {
      id: "montant",
      entete: t("montant"),
      cellule: (p) => (p.type === "CONTRE_PASSATION" ? `− ${formaterFcfa(p.montant, locale)}` : formaterFcfa(p.montant, locale)),
    },
    {
      id: "etat",
      entete: t("etat"),
      cellule: (p) =>
        p.type === "CONTRE_PASSATION" ? (
          <span className="text-sm" title={p.motif}>
            <Badge variant="outline">{t("contrePassation")}</Badge> {p.motif}
          </span>
        ) : p.contrePasse ? (
          <Badge variant="outline">{t("annule")}</Badge>
        ) : (
          <Badge variant="secondary">{t("valide")}</Badge>
        ),
    },
    {
      id: "actions",
      entete: "",
      cellule: (p) =>
        p.type === "ENCAISSEMENT" && !p.contrePasse ? (
          <Button size="sm" variant="outline" onClick={() => { setCible(p.id ?? null); setErreur(null); }}>
            {t("contrePasser")}
          </Button>
        ) : null,
    },
  ];

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-wrap items-end gap-3">
        <label className="flex flex-col gap-1 text-sm">
          {t("mode")}
          <select
            value={filtres.mode ?? ""}
            onChange={(e) => { setPage(0); setFiltres({ ...filtres, mode: e.target.value as ModePaiement | "" }); }}
            className="h-8 w-44 rounded-lg border border-input bg-transparent px-2.5 text-sm"
          >
            <option value="">{t("tous")}</option>
            {MODES_PAIEMENT.map((m) => (
              <option key={m} value={m}>
                {t(`modes.${m}`)}
              </option>
            ))}
          </select>
        </label>
        <label className="flex flex-col gap-1 text-sm">
          {t("du")}
          <Input type="date" value={filtres.du ?? ""} onChange={(e) => { setPage(0); setFiltres({ ...filtres, du: e.target.value }); }} className="w-40" />
        </label>
        <label className="flex flex-col gap-1 text-sm">
          {t("au")}
          <Input type="date" value={filtres.au ?? ""} onChange={(e) => { setPage(0); setFiltres({ ...filtres, au: e.target.value }); }} className="w-40" />
        </label>
      </div>

      {cible ? (
        <div className="flex max-w-xl flex-col gap-2 rounded-lg border p-3" role="group" aria-label={t("contrePasser")}>
          <label className="flex flex-col gap-1 text-sm">
            {t("motif")}
            <Input value={motif} onChange={(e) => setMotif(e.target.value)} />
          </label>
          {erreur ? (
            <p role="alert" className="text-sm text-destructive">
              {erreur}
            </p>
          ) : null}
          <div className="flex gap-2">
            <Button size="sm" disabled={motif.trim() === ""} onClick={() => void contrePasser(cible)}>
              {t("confirmerContrePassation")}
            </Button>
            <Button size="sm" variant="outline" onClick={() => setCible(null)}>
              {t("annuler")}
            </Button>
          </div>
        </div>
      ) : null}

      <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(p) => p.id ?? ""} messageVide={t("aucunPaiement")} />
    </div>
  );
}
