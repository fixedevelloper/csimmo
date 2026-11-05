"use client";

import { useTranslations } from "next-intl";

import { useHistoriqueRelances } from "@/hooks/use-relances";

/** Historique des relances d'une échéance : niveau, date, canal choisi, résultat et état d'envoi. */
export function HistoriqueRelances({ echeanceId }: { echeanceId: string }) {
  const t = useTranslations("relances.historique");
  const { data, isPending } = useHistoriqueRelances(echeanceId);
  if (isPending) return <p className="text-sm text-muted-foreground">…</p>;
  const lignes = data?.content ?? [];
  if (lignes.length === 0) return <p className="text-sm text-muted-foreground">{t("aucune")}</p>;
  return (
    <ul className="flex flex-col gap-1 text-sm" aria-label={t("titre")}>
      {lignes.map((r) => (
        <li key={r.id}>
          {r.dateRelance} — {r.type === "MANUELLE" ? t("manuelle") : t("niveau", { jours: r.niveau ?? 0 })} — {t(`resultats.${r.resultat}`)}
          {r.canal ? ` — ${r.canal}` : ""}
          {r.statutEnvoi ? ` (${t(`statuts.${r.statutEnvoi}`)})` : ""}
        </li>
      ))}
    </ul>
  );
}
