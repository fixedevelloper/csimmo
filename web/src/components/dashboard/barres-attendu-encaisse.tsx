"use client";

import { useLocale, useTranslations } from "next-intl";
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";

import { abreger, formaterTaux, type MoisDto } from "@/lib/dashboard";
import { formaterFcfa } from "@/lib/baux";

/**
 * Barres groupées « attendu / encaissé » par mois (Recharts, ADR 0008). Le tableau masqué en dessous donne les mêmes chiffres
 * aux lecteurs d'écran ; le conteneur porte le rôle « img » et son libellé.
 */
export function BarresAttenduEncaisse({ mois }: { mois: MoisDto[] }) {
  const t = useTranslations("dashboard.serie");
  const locale = useLocale();
  const etiquette = (iso?: string) => (iso ? new Date(`${iso}T00:00:00`).toLocaleDateString(locale, { month: "short" }) : "");
  const donnees = mois.map((m) => ({ ...m, libelle: etiquette(m.mois), attendu: m.attendu ?? 0, encaisse: m.encaisse ?? 0 }));

  return (
    <figure className="flex flex-col gap-2">
      <div role="img" aria-label={t("titre")} className="h-64 w-full">
        <ResponsiveContainer width="100%" height="100%" minWidth={0}>
          <BarChart data={donnees} margin={{ top: 8, right: 8, bottom: 0, left: 0 }} barGap={3} barCategoryGap="22%">
            <CartesianGrid vertical={false} stroke="currentColor" strokeOpacity={0.1} />
            <XAxis dataKey="libelle" tickLine={false} axisLine={false} tick={{ fontSize: 12, fill: "currentColor", fillOpacity: 0.7 }} />
            <YAxis
              tickLine={false}
              axisLine={false}
              width={48}
              tickFormatter={(v: number) => abreger(v, locale)}
              tick={{ fontSize: 11, fill: "currentColor", fillOpacity: 0.6 }}
            />
            <Tooltip
              cursor={{ fill: "currentColor", fillOpacity: 0.06 }}
              content={({ active, payload }) => {
                const m = active ? (payload?.[0]?.payload as (typeof donnees)[number] | undefined) : undefined;
                if (!m) return null;
                return (
                  <div className="rounded-lg border bg-popover px-3 py-2 text-xs text-popover-foreground shadow-carte">
                    <p className="mb-1 font-semibold capitalize">{new Date(`${m.mois}T00:00:00`).toLocaleDateString(locale, { month: "long", year: "numeric" })}</p>
                    <p className="tabular">
                      {t("attendu")} : <strong>{formaterFcfa(m.attendu, locale)}</strong>
                    </p>
                    <p className="tabular">
                      {t("encaisse")} : <strong>{formaterFcfa(m.encaisse, locale)}</strong>
                    </p>
                    <p className="tabular text-muted-foreground">
                      {t("taux")} : {formaterTaux(m.tauxBp, locale)}
                    </p>
                  </div>
                );
              }}
            />
            <Bar dataKey="attendu" name={t("attendu")} fill="var(--muted-foreground)" fillOpacity={0.35} radius={[4, 4, 0, 0]} isAnimationActive animationDuration={600} />
            <Bar dataKey="encaisse" name={t("encaisse")} fill="var(--primary)" radius={[4, 4, 0, 0]} isAnimationActive animationDuration={600} />
          </BarChart>
        </ResponsiveContainer>
      </div>
      <figcaption className="flex flex-wrap items-center gap-4 text-xs text-muted-foreground">
        <span className="flex items-center gap-1.5">
          <span className="inline-block size-2.5 rounded-sm bg-muted-foreground/40" aria-hidden /> {t("attendu")}
        </span>
        <span className="flex items-center gap-1.5">
          <span className="inline-block size-2.5 rounded-sm bg-primary" aria-hidden /> {t("encaisse")}
        </span>
      </figcaption>
      <table className="sr-only">
        <caption>{t("titre")}</caption>
        <thead>
          <tr>
            <th>{t("mois")}</th>
            <th>{t("attendu")}</th>
            <th>{t("encaisse")}</th>
            <th>{t("taux")}</th>
          </tr>
        </thead>
        <tbody>
          {mois.map((m) => (
            <tr key={m.mois}>
              <td>{m.mois}</td>
              <td>{formaterFcfa(m.attendu, locale)}</td>
              <td>{formaterFcfa(m.encaisse, locale)}</td>
              <td>{formaterTaux(m.tauxBp, locale)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </figure>
  );
}
