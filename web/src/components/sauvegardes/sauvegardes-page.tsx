"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { ErrorDisplay } from "@/components/error-display";
import { StatutPastille } from "@/components/statut-pastille";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useDiagnostic, useEtatSauvegardes } from "@/hooks/use-sauvegardes";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import type { components } from "@/lib/api/schema";
import { ALERTES_GRAVES, formaterOctets } from "@/lib/sauvegardes";

type SauvegardeDto = components["schemas"]["SauvegardeDto"];

export function SauvegardesPage() {
  const t = useTranslations("sauvegardes");
  const locale = useLocale();
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const etat = useEtatSauvegardes();
  const diagnostic = useDiagnostic();
  const [aRestaurer, setARestaurer] = useState<SauvegardeDto | null>(null);
  const [confirmation, setConfirmation] = useState("");
  const [erreur, setErreur] = useState<string | null>(null);

  const operation = etat.data?.operation;
  const enCours = operation?.statut === "EN_COURS";
  const date = (d?: string) => (d ? new Date(d).toLocaleString(locale) : "—");

  async function rafraichir() {
    await queryClient.invalidateQueries({ queryKey: ["sauvegardes"] });
    await queryClient.invalidateQueries({ queryKey: ["sauvegardes-diagnostic"] });
  }

  async function sauvegarder() {
    setErreur(null);
    try {
      deballer(await api.POST("/api/v1/sauvegardes"));
      toast.success(t("demandee"));
      await rafraichir();
    } catch (e) {
      setErreur(message(e));
    }
  }

  async function restaurer() {
    if (!aRestaurer?.id) return;
    setErreur(null);
    try {
      deballer(await api.POST("/api/v1/sauvegardes/{id}/restauration", { params: { path: { id: aRestaurer.id } }, body: { confirmation } }));
      toast.success(t("restaurationDemandee"));
      setARestaurer(null);
      setConfirmation("");
      await rafraichir();
    } catch (e) {
      setErreur(message(e));
    }
  }

  if (etat.error) return <ErrorDisplay erreur={etat.error} onRetry={() => void etat.refetch()} />;
  const d = diagnostic.data;

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-semibold">{t("titre")}</h1>
        <Button onClick={() => void sauvegarder()} disabled={enCours}>
          {t("sauvegarderMaintenant")}
        </Button>
      </div>
      <p className="text-sm text-muted-foreground">{t("intro", { heure: etat.data?.heureQuotidienne ?? "02:30" })}</p>

      {erreur ? (
        <p role="alert" className="text-sm text-destructive">
          {erreur}
        </p>
      ) : null}

      {enCours ? (
        <p role="status" className="rounded-lg border bg-muted/50 p-3 text-sm" data-testid="operation-en-cours">
          {t(`operation.${operation?.operation === "restauration" ? "restauration" : "sauvegarde"}EnCours`)} {operation?.message ? `(${operation.message})` : ""}
        </p>
      ) : null}
      {operation?.statut === "OK" && operation.operation === "restauration" ? (
        <p role="status" className="rounded-lg border bg-muted/50 p-3 text-sm">
          {t("restaurationTerminee")}
        </p>
      ) : null}

      {d && d.alertes && d.alertes.length > 0 ? (
        <ul className="flex flex-col gap-2" aria-label={t("alertes")}>
          {d.alertes.map((a) => (
            <li key={a} className={`rounded-lg border p-3 text-sm ${ALERTES_GRAVES.has(a) ? "border-destructive/50 text-destructive" : "border-yellow-500/50"}`}>
              {t(`alerte.${a}`)}
            </li>
          ))}
        </ul>
      ) : null}

      <section aria-labelledby="liste" className="flex flex-col gap-3">
        <h2 id="liste" className="text-lg font-semibold">
          {t("liste")}
        </h2>
        {etat.data && etat.data.sauvegardes?.length === 0 ? <p className="text-sm">{t("aucune")}</p> : null}
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b">
                <th className="py-2">{t("date")}</th>
                <th>{t("type")}</th>
                <th>{t("version")}</th>
                <th>{t("taille")}</th>
                <th>{t("copieExterne")}</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {etat.data?.sauvegardes?.map((s) => (
                <tr key={s.id} className="border-b">
                  <td className="py-2">{date(s.date)}</td>
                  <td>{s.etiquette ? t(`etiquettes.${s.etiquette}`, { defaut: s.etiquette }) : "—"}</td>
                  <td>{s.version}</td>
                  <td>{formaterOctets(s.tailleOctets, locale)}</td>
                  <td>
                    <StatutPastille statut={s.externe} libelle={t(`externe.${s.externe}`)} />
                  </td>
                  <td className="text-right">
                    <Button
                      variant="outline"
                      size="sm"
                      disabled={!s.restaurable || enCours}
                      title={s.restaurable ? undefined : t("autreVersion", { version: s.version ?? "?" })}
                      onClick={() => {
                        setErreur(null);
                        setConfirmation("");
                        setARestaurer(s);
                      }}
                    >
                      {t("restaurer")}
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      {aRestaurer ? (
        <Card role="alertdialog" aria-labelledby="titre-restauration" className="border-destructive/50">
          <CardHeader>
            <CardTitle id="titre-restauration">{t("restaurationTitre", { date: date(aRestaurer.date) })}</CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col gap-3">
            <p className="text-sm">{t("restaurationAvertissement")}</p>
            <label htmlFor="confirmation" className="text-sm font-medium">
              {t("tapezRestaurer")}
            </label>
            <Input id="confirmation" value={confirmation} onChange={(e) => setConfirmation(e.target.value)} autoComplete="off" className="max-w-xs" />
            <div className="flex gap-2">
              <Button variant="destructive" disabled={confirmation !== "RESTAURER"} onClick={() => void restaurer()}>
                {t("restaurerConfirmer")}
              </Button>
              <Button variant="outline" onClick={() => setARestaurer(null)}>
                {t("annuler")}
              </Button>
            </div>
          </CardContent>
        </Card>
      ) : null}

      <section aria-labelledby="diag" className="flex flex-col gap-3">
        <h2 id="diag" className="text-lg font-semibold">
          {t("diagnostic")}
        </h2>
        {d ? (
          <dl className="grid gap-x-6 gap-y-2 text-sm sm:grid-cols-2">
            <dt className="font-medium">{t("diag.version")}</dt>
            <dd>{d.versionApplication}</dd>
            <dt className="font-medium">{t("diag.java")}</dt>
            <dd>{d.versionJava}</dd>
            <dt className="font-medium">{t("diag.base")}</dt>
            <dd>
              PostgreSQL {d.versionBase} — {formaterOctets(d.tailleBaseOctets, locale)}
            </dd>
            <dt className="font-medium">{t("diag.disque")}</dt>
            <dd>{t("diag.disqueValeur", { libre: formaterOctets(d.disqueLibreOctets, locale), total: formaterOctets(d.disqueTotalOctets, locale) })}</dd>
            <dt className="font-medium">{t("diag.licence")}</dt>
            <dd>
              {d.etatLicence}
              {d.joursLicenceRestants !== undefined && d.joursLicenceRestants !== null ? ` — ${t("diag.joursRestants", { n: d.joursLicenceRestants })}` : ""}
            </dd>
            <dt className="font-medium">{t("diag.derniere")}</dt>
            <dd>{d.derniereSauvegarde ? `${date(d.derniereSauvegarde)} (${t("diag.age", { n: d.ageDerniereSauvegardeHeures ?? 0 })})` : t("diag.jamais")}</dd>
          </dl>
        ) : null}
      </section>
    </div>
  );
}
