"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import { useSearchParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";

import { ConfirmDialog } from "@/components/confirm-dialog";
import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { StatutPastille } from "@/components/statut-pastille";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useReversements } from "@/hooks/use-reversements";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { formaterFcfa } from "@/lib/baux";
import { urlDocument } from "@/lib/finance";
import { MODES_REVERSEMENT, moisCourant, moisPrecedent, urlApercuReleve, type ReversementDto } from "@/lib/reversements";

/** Reversements d'un mois : calcul, aperçu du relevé, validation (fige et numérote), paiement. */
export function ReversementsListe() {
  const t = useTranslations("reversements");
  const locale = useLocale();
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [mois, setMois] = useState(moisPrecedent(moisCourant()));
  const [page, setPage] = useState(0);
  const proprietaire = useSearchParams().get("proprietaire"); // lien direct depuis la fiche d'un propriétaire
  const { data, isPending, error, refetch } = useReversements(mois, proprietaire, page);
  const [aValider, setAValider] = useState<ReversementDto | null>(null);
  const [aPayer, setAPayer] = useState<string | null>(null);
  const [mode, setMode] = useState<string>("VIREMENT");
  const [reference, setReference] = useState("");
  const [erreur, setErreur] = useState<string | null>(null);

  async function rafraichir() {
    await queryClient.invalidateQueries({ queryKey: ["reversements"] });
  }

  async function agir(action: () => Promise<void>) {
    setErreur(null);
    try {
      await action();
    } catch (e) {
      setErreur(message(e));
    }
  }

  const colonnes: Colonne<ReversementDto>[] = [
    { id: "proprietaire", entete: t("proprietaire"), cellule: (r) => r.proprietaireNom ?? "—" },
    { id: "brut", entete: t("encaisse"), cellule: (r) => formaterFcfa(r.brut, locale) },
    { id: "honoraires", entete: t("honoraires"), cellule: (r) => formaterFcfa(r.honoraires, locale) },
    { id: "depenses", entete: t("colDepenses"), cellule: (r) => formaterFcfa(r.depenses, locale) },
    { id: "net", entete: t("net"), cellule: (r) => <strong data-testid="net-reversement">{formaterFcfa(r.net, locale)}</strong> },
    {
      id: "statut",
      entete: t("statut"),
      cellule: (r) => (
        <span className="flex flex-col gap-1">
          <StatutPastille statut={r.statut} libelle={r.statut ? t(`statuts.${r.statut}`) : ""} testid="statut-reversement" />
          {r.sansMandat ? <span className="text-xs text-destructive">{t("sansMandat")}</span> : null}
          {r.numero ? <span className="text-xs text-muted-foreground">{r.numero}</span> : null}
        </span>
      ),
    },
    {
      id: "actions",
      entete: "",
      cellule: (r) => (
        <div className="flex flex-wrap gap-2">
          {r.statut === "CALCULE" ? (
            <>
              <a href={urlApercuReleve(r.id ?? "")} target="_blank" rel="noopener noreferrer" className="inline-flex h-7 items-center rounded-lg border px-2.5 text-sm">
                {t("apercu")}
              </a>
              <Button size="sm" onClick={() => setAValider(r)} aria-label={t("validerNom", { nom: r.proprietaireNom ?? "" })}>
                {t("valider")}
              </Button>
            </>
          ) : (
            <a href={urlDocument(r.fichierId ?? "")} target="_blank" rel="noopener noreferrer" className="inline-flex h-7 items-center rounded-lg border px-2.5 text-sm">
              {t("releve")}
            </a>
          )}
          {r.statut === "VALIDE" ? (
            <Button size="sm" variant="outline" onClick={() => { setAPayer(r.id ?? null); setErreur(null); }} aria-label={t("payerNom", { nom: r.proprietaireNom ?? "" })}>
              {t("marquerPaye")}
            </Button>
          ) : null}
        </div>
      ),
    },
  ];

  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-wrap items-end gap-3">
        <label className="flex flex-col gap-1 text-sm">
          {t("mois")}
          <Input type="month" value={mois} onChange={(e) => { setPage(0); setMois(e.target.value); }} className="w-44" />
        </label>
        <Button
          variant="outline"
          disabled={!mois}
          onClick={() =>
            void agir(async () => {
              deballer(await api.POST("/api/v1/reversements/calculer", { body: { mois } }));
              await rafraichir();
              toast.success(t("calcule"));
            })
          }
        >
          {t("calculer")}
        </Button>
      </div>
      {erreur ? (
        <p role="alert" className="text-sm text-destructive">
          {erreur}
        </p>
      ) : null}
      {aPayer ? (
        <div className="flex max-w-xl flex-col gap-2 rounded-lg border p-3" role="group" aria-label={t("marquerPaye")}>
          <label className="flex flex-col gap-1 text-sm">
            {t("modePaiement")}
            <select value={mode} onChange={(e) => setMode(e.target.value)} className="h-8 rounded-lg border border-input bg-transparent px-2.5 text-sm">
              {MODES_REVERSEMENT.map((m) => (
                <option key={m} value={m}>
                  {t(`modes.${m}`)}
                </option>
              ))}
            </select>
          </label>
          <label className="flex flex-col gap-1 text-sm">
            {mode === "ESPECES" ? t("referenceFacultative") : t("reference")}
            <Input value={reference} onChange={(e) => setReference(e.target.value)} />
          </label>
          <div className="flex gap-2">
            <Button
              size="sm"
              disabled={mode !== "ESPECES" && reference.trim() === ""}
              onClick={() =>
                void agir(async () => {
                  deballer(await api.POST("/api/v1/reversements/{id}/payer", { params: { path: { id: aPayer } }, body: { mode, reference: reference.trim() || undefined } }));
                  setAPayer(null);
                  setReference("");
                  await rafraichir();
                  toast.success(t("paye"));
                })
              }
            >
              {t("confirmerPaiement")}
            </Button>
            <Button size="sm" variant="outline" onClick={() => setAPayer(null)}>
              {t("annuler")}
            </Button>
          </div>
        </div>
      ) : null}
      {error ? (
        <ErrorDisplay erreur={error} onRetry={() => void refetch()} />
      ) : (
        <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(r) => r.id ?? ""} messageVide={t("aucun")} />
      )}
      <ConfirmDialog
        open={aValider !== null}
        onOpenChange={(o) => !o && setAValider(null)}
        title={t("confirmerValidationTitre")}
        description={t("confirmerValidationDescription", { nom: aValider?.proprietaireNom ?? "", net: formaterFcfa(aValider?.net, locale) })}
        confirmLabel={t("valider")}
        onConfirm={() => {
          const r = aValider;
          setAValider(null);
          return r
            ? agir(async () => {
                const v = deballer(await api.POST("/api/v1/reversements/{id}/valider", { params: { path: { id: r.id ?? "" } } }));
                await rafraichir();
                toast.success(t("valide", { numero: v.numero ?? "" }));
              })
            : undefined;
        }}
      />
    </div>
  );
}
