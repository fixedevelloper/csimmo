"use client";

import { useLocale, useTranslations } from "next-intl";
import { Cell, Pie, PieChart, ResponsiveContainer } from "recharts";

import { formaterTaux } from "@/lib/dashboard";

type Props = { tauxBp?: number | null; occupes: number; libres: number; travaux: number };

/** Anneau d'occupation du parc (Recharts, ADR 0008) : taux au centre, légende chiffrée et tableau masqué pour les lecteurs d'écran. */
export function DonutOccupation({ tauxBp, occupes, libres, travaux }: Props) {
  const t = useTranslations("dashboard.occupation");
  const locale = useLocale();
  const parts = [
    { cle: "occupes", valeur: occupes, couleur: "var(--primary)" },
    { cle: "libres", valeur: libres, couleur: "var(--marque-orange)" },
    { cle: "travaux", valeur: travaux, couleur: "var(--muted-foreground)" },
  ];
  const total = occupes + libres + travaux;
  const donnees = total === 0 ? [{ cle: "vide", valeur: 1, couleur: "var(--muted)" }] : parts.filter((p) => p.valeur > 0);

  return (
    <div className="flex flex-wrap items-center gap-6">
      <div role="img" aria-label={t("titre")} className="relative size-36 shrink-0">
        <ResponsiveContainer width="100%" height="100%" minWidth={0}>
          <PieChart>
            <Pie data={donnees} dataKey="valeur" innerRadius="68%" outerRadius="100%" paddingAngle={total === 0 ? 0 : 2} stroke="none" startAngle={90} endAngle={-270} animationDuration={700}>
              {donnees.map((p) => (
                <Cell key={p.cle} fill={p.couleur} />
              ))}
            </Pie>
          </PieChart>
        </ResponsiveContainer>
        <div className="pointer-events-none absolute inset-0 flex flex-col items-center justify-center">
          <span className="tabular text-xl font-semibold">{formaterTaux(tauxBp, locale)}</span>
          <span className="text-[11px] text-muted-foreground">{t("occupe")}</span>
        </div>
      </div>
      <ul className="flex flex-col gap-2 text-sm">
        {parts.map((p) => (
          <li key={p.cle} className="flex items-center gap-2">
            <span className="inline-block size-2.5 rounded-full" style={{ background: p.couleur }} aria-hidden />
            <span className="text-muted-foreground">{t(p.cle)}</span>
            <span className="tabular ml-auto pl-4 font-medium">{p.valeur}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}
