"use client";

import { useLocale, useTranslations } from "next-intl";
import Link from "next/link";

import {
  AlertTriangle,
  CalendarClock,
  Percent,
  PieChart,
  type LucideIcon,
  CheckCircle2,
  AlertCircle, ExternalLink, House
} from "lucide-react";
import dynamic from "next/dynamic";

import { DonutOccupation } from "@/components/dashboard/donut-occupation";
import { ErrorDisplay } from "@/components/error-display";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useArrieresDashboard, useAttenduEncaisse, useBauxAEcheance, useSynthese } from "@/hooks/use-dashboard";
import { formaterFcfa } from "@/lib/baux";
import { formaterTaux, type LigneArriereDto } from "@/lib/dashboard";

// Recharts n'est chargé que pour cette page, et jamais côté serveur (ADR 0008)
const BarresAttenduEncaisse = dynamic(() => import("@/components/dashboard/barres-attendu-encaisse").then((m) => m.BarresAttenduEncaisse), {
  ssr: false,
  loading: () => <Skeleton className="h-64 w-full" />,
});

const TONS = {
  bleu: "bg-primary/10 text-primary",
  orange: "bg-[color:var(--marque-orange)]/15 text-[color:var(--marque-orange)]",
  rouge: "bg-destructive/10 text-destructive",
  neutre: "bg-muted text-muted-foreground",
} as const;

function Indicateur({ titre, valeur, detail, testid, icone: Icone, ton }: { titre: string; valeur: string; detail?: string; testid: string; icone: LucideIcon; ton: keyof typeof TONS }) {
  return (
    <Card className="shadow-carte transition-shadow hover:shadow-md">
      <CardContent className="flex items-start gap-4">
        <span className={`flex size-10 shrink-0 items-center justify-center rounded-xl ${TONS[ton]}`} aria-hidden>
          <Icone className="size-5" />
        </span>
        <div className="min-w-0">
          <p className="text-sm text-muted-foreground">{titre}</p>
          <p className="tabular mt-0.5 text-2xl font-semibold tracking-tight" data-testid={testid}>
            {valeur}
          </p>
          {detail ? <p className="mt-0.5 text-xs text-muted-foreground">{detail}</p> : null}
        </div>
      </CardContent>
    </Card>
  );
}


function ListeArrieres({
                         titre,
                         lignes,
                         lien,
                         vide,
                       }: {
  titre: string;
  lignes: LigneArriereDto[];
  lien?: (id: string) => string;
  vide: string;
}) {
  const locale = useLocale();
  const t = useTranslations("dashboard");

  const max = Math.max(1, ...lignes.map((l) => l.total ?? 0));
  const totalGlobal = lignes.reduce(
    (acc, l) => acc + (l.total ?? 0),
    0
  );

  return (
    <Card className="overflow-hidden rounded-2xl border border-border/60 bg-card shadow-sm transition-shadow hover:shadow-md">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-4">
        <div className="space-y-1">
          <CardTitle className="text-base font-semibold tracking-tight">
            {titre}
          </CardTitle>
          <p className="text-xs text-muted-foreground">
            {lignes.length} élément{lignes.length > 1 ? "s" : ""} en retard
          </p>
        </div>

        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-destructive/10">
          <AlertCircle className="h-5 w-5 text-destructive" />
        </div>
      </CardHeader>

      <CardContent className="space-y-4">
        {lignes.length === 0 ? (
          <div className="flex flex-col items-center justify-center rounded-xl border border-dashed bg-muted/20 px-4 py-8 text-center">
            <div className="mb-3 flex h-11 w-11 items-center justify-center rounded-full bg-emerald-500/10">
              <CheckCircle2 className="h-5 w-5 text-emerald-600" />
            </div>
            <p className="text-sm font-medium">{vide}</p>
            <p className="mt-1 text-xs text-muted-foreground">
              Aucun arriéré à signaler.
            </p>
          </div>
        ) : (
          <>
            <div className="rounded-xl bg-destructive/[0.06] p-4">
              <p className="text-xs font-medium text-muted-foreground">
                Total des arriérés
              </p>
              <p className="mt-1 text-2xl font-bold tracking-tight text-destructive tabular-nums">
                {formaterFcfa(totalGlobal, locale)}
              </p>
            </div>

            <ul className="flex flex-col gap-4">
              {lignes.map((l) => {
                const montant = l.total ?? 0;
                const progression = (montant / max) * 100;

                return (
                  <li key={l.id} className="group space-y-2">
                    <div className="flex items-center justify-between gap-3">
                      <div className="min-w-0 flex-1">
                        {lien && l.id ? (
                          <Link
                            href={lien(l.id)}
                            className="truncate font-medium underline-offset-4 transition-colors hover:text-primary hover:underline"
                          >
                            {l.libelle}
                          </Link>
                        ) : (
                          <span className="truncate font-medium">
                            {l.libelle}
                          </span>
                        )}

                        <p className="mt-0.5 text-xs text-muted-foreground">
                          {t("echeancesN", {
                            n: l.echeances ?? 0,
                          })}
                        </p>
                      </div>

                      <span className="whitespace-nowrap text-sm font-semibold tabular-nums">
                        {formaterFcfa(montant, locale)}
                      </span>
                    </div>

                    <div className="h-2 overflow-hidden rounded-full bg-muted">
                      <div
                        className="h-full rounded-full bg-gradient-to-r from-destructive/60 to-destructive transition-all duration-500"
                        style={{ width: `${progression}%` }}
                      />
                    </div>
                  </li>
                );
              })}
            </ul>
          </>
        )}
      </CardContent>
    </Card>
  );
}


/** Indicateurs de pilotage : occupation, recouvrement du mois, arriérés, baux à échéance, graphique attendu/encaissé. */
export function Indicateurs() {
  const t = useTranslations("dashboard");
  const locale = useLocale();
  const synthese = useSynthese();
  const serie = useAttenduEncaisse(12);
  const arrieres = useArrieresDashboard();
  const baux = useBauxAEcheance();

  const erreur = synthese.error ?? serie.error ?? arrieres.error ?? baux.error;
  if (erreur) return <ErrorDisplay erreur={erreur} onRetry={() => { void synthese.refetch(); void serie.refetch(); void arrieres.refetch(); void baux.refetch(); }} />;
  const s = synthese.data;
  if (!s) return <Skeleton className="h-40 w-full" />;
  const o = s.occupation;

  return (
    <div className="flex flex-col gap-6">
      <section aria-label={t("indicateurs")} className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <Indicateur
          titre={t("tauxOccupation")}
          valeur={formaterTaux(o?.tauxBp, locale)}
          detail={t("detailOccupation", { occupes: o?.occupes ?? 0, libres: o?.libres ?? 0, travaux: o?.enTravaux ?? 0 })}
          testid="taux-occupation"
          icone={PieChart}
          ton="bleu"
        />
        <Indicateur
          titre={t("recouvrementMois")}
          valeur={formaterTaux(s.tauxRecouvrementMoisBp, locale)}
          detail={t("detailRecouvrement", { encaisse: formaterFcfa(s.encaisseMois, locale), attendu: formaterFcfa(s.attenduMois, locale) })}
          testid="taux-recouvrement"
          icone={Percent}
          ton="orange"
        />
        <Indicateur
          titre={t("arrieres")}
          valeur={formaterFcfa(s.arrieresTotal, locale)}
          detail={t("detailArrieres", { n: s.echeancesEnRetard ?? 0 })}
          testid="arrieres-total"
          icone={AlertTriangle}
          ton="rouge"
        />
        <Indicateur titre={t("bauxAEcheance")} valeur={String(s.bauxAEcheance ?? 0)} detail={t("detailBaux")} testid="baux-a-echeance" icone={CalendarClock} ton="neutre" />
      </section>

      <div className="grid gap-4 xl:grid-cols-3">
        <Card className="shadow-carte xl:col-span-2">
          <CardHeader>
            <CardTitle className="text-base">{t("serie.titre")}</CardTitle>
          </CardHeader>
          <CardContent>{serie.data ? <BarresAttenduEncaisse mois={serie.data} /> : <Skeleton className="h-64 w-full" />}</CardContent>
        </Card>
        <Card className="shadow-carte">
          <CardHeader>
            <CardTitle className="text-base">{t("occupation.titre")}</CardTitle>
          </CardHeader>
          <CardContent>
            <DonutOccupation tauxBp={o?.tauxBp} occupes={o?.occupes ?? 0} libres={o?.libres ?? 0} travaux={o?.enTravaux ?? 0} />
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <ListeArrieres titre={t("arrieresParBien")} lignes={arrieres.data?.parBien ?? []} lien={(id) => `/patrimoine/${id}`} vide={t("aucunArriere")} />
        <ListeArrieres titre={t("arrieresParLocataire")} lignes={arrieres.data?.parLocataire ?? []} lien={(id) => `/locataires/${id}/compte`} vide={t("aucunArriere")} />
      </div>


      <Card className="overflow-hidden rounded-2xl border border-border/60 bg-card shadow-sm transition-shadow hover:shadow-md">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-4">
          <div className="space-y-1">
            <CardTitle className="text-base font-semibold tracking-tight">
              {t("bauxAEcheanceTitre")}
            </CardTitle>
            <p className="text-xs text-muted-foreground">
              {(baux.data ?? []).length} bail(s) à échéance
            </p>
          </div>

          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-amber-500/10">
            <CalendarClock className="h-5 w-5 text-amber-600" />
          </div>
        </CardHeader>

        <CardContent>
          {(baux.data ?? []).length === 0 ? (
            <div className="flex flex-col items-center justify-center rounded-xl border border-dashed bg-muted/20 px-4 py-8 text-center">
              <div className="mb-3 flex h-11 w-11 items-center justify-center rounded-full bg-emerald-500/10">
                <CheckCircle2 className="h-5 w-5 text-emerald-600" />
              </div>
              <p className="text-sm font-medium">{t("aucunBail")}</p>
              <p className="mt-1 text-xs text-muted-foreground">
                Aucun bail ne nécessite de suivi particulier.
              </p>
            </div>
          ) : (
            <ul className="flex flex-col gap-3">
              {(baux.data ?? []).map((b) => (
                <li
                  key={b.bailId}
                  className="group rounded-xl border border-border/60 bg-muted/10 p-3 transition-colors hover:bg-muted/30"
                >
                  <div className="flex flex-wrap items-start justify-between gap-3">
                    <div className="min-w-0 flex-1 space-y-1.5">
                      <Link
                        href={`/baux/${b.bailId}`}
                        className="inline-flex items-center gap-1 font-semibold underline-offset-4 transition-colors hover:text-primary hover:underline"
                      >
                        {b.numero}
                        <ExternalLink className="h-3 w-3 opacity-0 transition-opacity group-hover:opacity-100" />
                      </Link>

                      <p className="truncate text-sm text-muted-foreground">
                        {b.locataireNom}
                      </p>

                      <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                        <House className="h-3.5 w-3.5 shrink-0" />
                        <span className="truncate">{b.logement}</span>
                      </div>
                    </div>

                    <div className="flex shrink-0 flex-col items-end gap-2">
                <span className="whitespace-nowrap text-sm font-semibold tabular-nums">
                  {b.dateFin}
                </span>

                      <span
                        className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-medium ${
                          b.statut === "RESILIE"
                            ? "bg-destructive/10 text-destructive"
                            : (b.joursRestants ?? 0) <= 7
                              ? "bg-destructive/10 text-destructive"
                              : (b.joursRestants ?? 0) <= 30
                                ? "bg-amber-500/10 text-amber-600"
                                : "bg-emerald-500/10 text-emerald-600"
                        }`}
                      >
                  {t("dansJours", {
                    n: b.joursRestants ?? 0,
                  })}
                </span>
                    </div>
                  </div>

                  {b.statut === "RESILIE" && (
                    <div className="mt-3 flex items-center gap-2 border-t border-border/50 pt-2 text-xs text-destructive">
                      <AlertCircle className="h-3.5 w-3.5" />
                      {t("sortieAnnoncee")}
                    </div>
                  )}
                </li>
              ))}
            </ul>
          )}
        </CardContent>
      </Card>

    </div>
  );
}
