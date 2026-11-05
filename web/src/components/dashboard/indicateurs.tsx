"use client";

import { useLocale, useTranslations } from "next-intl";
import Link from "next/link";

import { AlertTriangle, CalendarClock, Percent, PieChart, type LucideIcon } from "lucide-react";
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

function ListeArrieres({ titre, lignes, lien, vide }: { titre: string; lignes: LigneArriereDto[]; lien?: (id: string) => string; vide: string }) {
  const locale = useLocale();
  const t = useTranslations("dashboard");
  const max = Math.max(1, ...lignes.map((l) => l.total ?? 0));
  return (
    <Card className="shadow-carte">
      <CardHeader>
        <CardTitle className="text-base">{titre}</CardTitle>
      </CardHeader>
      <CardContent>
        {lignes.length === 0 ? <p className="text-sm text-muted-foreground">{vide}</p> : null}
        <ul className="flex flex-col gap-2 text-sm">
          {lignes.map((l) => (
            <li key={l.id} className="flex flex-col gap-1">
              <div className="flex items-baseline justify-between gap-3">
                {lien && l.id ? (
                  <Link href={lien(l.id)} className="truncate underline-offset-2 hover:underline">
                    {l.libelle}
                  </Link>
                ) : (
                  <span className="truncate">{l.libelle}</span>
                )}
                <span className="tabular whitespace-nowrap font-medium">
                  {formaterFcfa(l.total, locale)} <span className="text-xs font-normal text-muted-foreground">({t("echeancesN", { n: l.echeances ?? 0 })})</span>
                </span>
              </div>
              <div className="h-1.5 overflow-hidden rounded-full bg-muted" aria-hidden>
                <div className="h-full rounded-full bg-destructive/70 transition-[width] duration-500" style={{ width: `${((l.total ?? 0) / max) * 100}%` }} />
              </div>
            </li>
          ))}
        </ul>
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

      <Card className="shadow-carte">
        <CardHeader>
          <CardTitle className="text-base">{t("bauxAEcheanceTitre")}</CardTitle>
        </CardHeader>
        <CardContent>
          {(baux.data ?? []).length === 0 ? <p className="text-sm text-muted-foreground">{t("aucunBail")}</p> : null}
          <ul className="flex flex-col gap-2 text-sm">
            {(baux.data ?? []).map((b) => (
              <li key={b.bailId} className="flex flex-wrap items-baseline justify-between gap-3">
                <span>
                  <Link href={`/baux/${b.bailId}`} className="font-medium underline-offset-2 hover:underline">
                    {b.numero}
                  </Link>{" "}
                  — {b.locataireNom} — {b.logement}
                </span>
                <span className="whitespace-nowrap">
                  {b.dateFin} ({t("dansJours", { n: b.joursRestants ?? 0 })}){b.statut === "RESILIE" ? ` — ${t("sortieAnnoncee")}` : ""}
                </span>
              </li>
            ))}
          </ul>
        </CardContent>
      </Card>
    </div>
  );
}
