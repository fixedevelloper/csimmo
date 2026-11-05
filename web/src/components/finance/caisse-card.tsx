"use client";

import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";

import { ErrorDisplay } from "@/components/error-display";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useCaisse } from "@/hooks/use-finance";
import { formaterFcfa } from "@/lib/baux";

/** Rapprochement de caisse : espèces saisies par agent un jour donné, nettes des contre-passations. */
export function CaisseCard() {
  const t = useTranslations("finance");
  const locale = useLocale();
  const [jour, setJour] = useState("");
  const { data, error, refetch } = useCaisse(jour);
  const total = (data ?? []).reduce((s, l) => s + (l.total ?? 0), 0);
  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between gap-3">
        <CardTitle>{t("caisse")}</CardTitle>
        <label className="flex items-center gap-2 text-sm">
          {t("jour")}
          <Input type="date" value={jour} onChange={(e) => setJour(e.target.value)} className="w-40" />
        </label>
      </CardHeader>
      <CardContent>
        {error ? <ErrorDisplay erreur={error} onRetry={() => void refetch()} /> : null}
        {data && data.length === 0 ? <p className="text-sm text-muted-foreground">{t("aucuneEspece")}</p> : null}
        <ul className="flex flex-col gap-1 text-sm">
          {(data ?? []).map((l) => (
            <li key={l.agentId} className="flex justify-between">
              <span>{l.agentNom ?? t("agentInconnu")} ({t("operations", { n: l.operations ?? 0 })})</span>
              <span className="font-medium">{formaterFcfa(l.total, locale)}</span>
            </li>
          ))}
        </ul>
        {data && data.length > 1 ? (
          <p className="mt-2 flex justify-between border-t pt-2 text-sm font-semibold">
            <span>{t("total")}</span>
            <span data-testid="total-caisse">{formaterFcfa(total, locale)}</span>
          </p>
        ) : null}
      </CardContent>
    </Card>
  );
}
