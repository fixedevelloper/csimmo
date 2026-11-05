"use client";

import { useLocale, useTranslations } from "next-intl";

import { abreger, formaterTaux, type MoisDto } from "@/lib/dashboard";
import { formaterFcfa } from "@/lib/baux";

const H = 180;
const LARGEUR_GROUPE = 56;

/**
 * Barres groupées « attendu / encaissé » par mois, en SVG (aucune bibliothèque de graphiques). Le tableau masqué en dessous donne
 * les mêmes chiffres aux lecteurs d'écran.
 */
export function BarresAttenduEncaisse({ mois }: { mois: MoisDto[] }) {
  const t = useTranslations("dashboard.serie");
  const locale = useLocale();
  const max = Math.max(1, ...mois.map((m) => Math.max(m.attendu ?? 0, m.encaisse ?? 0)));
  const largeur = mois.length * LARGEUR_GROUPE + 48;
  const echelle = (v: number) => (v / max) * H;
  const etiquette = (iso?: string) => (iso ? new Date(`${iso}T00:00:00`).toLocaleDateString(locale, { month: "short" }) : "");

  return (
    <figure className="flex flex-col gap-2">
      <svg role="img" aria-label={t("titre")} viewBox={`0 0 ${largeur} ${H + 34}`} className="h-64 w-full">
        <title>{t("titre")}</title>
        {[0, 0.5, 1].map((f) => (
          <g key={f}>
            <line x1={44} x2={largeur} y1={H - f * H} y2={H - f * H} stroke="currentColor" strokeOpacity={0.12} />
            <text x={40} y={H - f * H + 4} textAnchor="end" fontSize={10} fill="currentColor" fillOpacity={0.6}>
              {abreger(max * f, locale)}
            </text>
          </g>
        ))}
        {mois.map((m, i) => {
          const x = 48 + i * LARGEUR_GROUPE;
          return (
            <g key={m.mois}>
              <rect x={x} y={H - echelle(m.attendu ?? 0)} width={20} height={echelle(m.attendu ?? 0)} className="fill-muted-foreground/40" data-testid="barre-attendu">
                <title>{`${etiquette(m.mois)} — ${t("attendu")} : ${formaterFcfa(m.attendu, locale)}`}</title>
              </rect>
              <rect x={x + 22} y={H - echelle(m.encaisse ?? 0)} width={20} height={echelle(m.encaisse ?? 0)} className="fill-primary" data-testid="barre-encaisse">
                <title>{`${etiquette(m.mois)} — ${t("encaisse")} : ${formaterFcfa(m.encaisse, locale)}`}</title>
              </rect>
              <text x={x + 21} y={H + 16} textAnchor="middle" fontSize={11} fill="currentColor">
                {etiquette(m.mois)}
              </text>
            </g>
          );
        })}
      </svg>
      <figcaption className="flex flex-wrap items-center gap-4 text-xs text-muted-foreground">
        <span className="flex items-center gap-1">
          <span className="inline-block size-3 rounded-sm bg-muted-foreground/40" aria-hidden /> {t("attendu")}
        </span>
        <span className="flex items-center gap-1">
          <span className="inline-block size-3 rounded-sm bg-primary" aria-hidden /> {t("encaisse")}
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
