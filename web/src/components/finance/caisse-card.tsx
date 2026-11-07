"use client";

import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";
import {
  Wallet,
  Calendar,
  User,
  Banknote,
  AlertCircle
} from "lucide-react";

import { ErrorDisplay } from "@/components/error-display";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
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
    <Card className="shadow-sm border-border/60">
      <CardHeader className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b bg-muted/10 pb-4">
        <div className="flex items-center gap-2.5">
          <div className="flex size-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
            <Wallet className="size-5" />
          </div>
          <div>
            <CardTitle className="text-base font-semibold">{t("caisse")}</CardTitle>
            <CardDescription className="text-xs">
              Rapprochement des espèces par agent et par jour
            </CardDescription>
          </div>
        </div>

        {/* Sélection de la date */}
        <div className="flex items-center gap-2">
          <Label htmlFor="caisse-jour" className="text-xs font-medium text-muted-foreground flex items-center gap-1.5 shrink-0">
            <Calendar className="size-3.5" />
            {t("jour")} :
          </Label>
          <Input
            id="caisse-jour"
            type="date"
            value={jour}
            onChange={(e) => setJour(e.target.value)}
            className="w-40 h-9 text-xs bg-background"
          />
        </div>
      </CardHeader>

      <CardContent className="flex flex-col gap-4 pt-6">
        {/* Gestion des erreurs */}
        {error ? (
          <div className="py-2">
            <ErrorDisplay erreur={error} onRetry={() => void refetch()} />
          </div>
        ) : null}

        {/* État vide */}
        {data && data.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-8 text-center text-muted-foreground">
            <Banknote className="size-8 text-muted-foreground/40 mb-2" />
            <p className="text-sm italic">{t("aucuneEspece")}</p>
          </div>
        ) : null}

        {/* Liste des encaissements par agent */}
        {data && data.length > 0 ? (
          <div className="flex flex-col gap-2">
            {(data ?? []).map((l) => (
              <div
                key={l.agentId}
                className="flex items-center justify-between gap-3 p-3.5 rounded-xl border border-border/50 bg-card hover:bg-muted/20 transition-all text-sm"
              >
                <div className="flex items-center gap-3">
                  <div className="p-2 rounded-lg bg-muted/40 text-muted-foreground shrink-0">
                    <User className="size-4" />
                  </div>
                  <div>
                    <span className="font-semibold text-foreground">{l.agentNom ?? t("agentInconnu")}</span>
                    <p className="text-xs text-muted-foreground mt-0.5">
                      {t("operations", { n: l.operations ?? 0 })}
                    </p>
                  </div>
                </div>
                <span className="font-bold text-foreground text-base tracking-tight">
                  {formaterFcfa(l.total, locale)}
                </span>
              </div>
            ))}
          </div>
        ) : null}

        {/* Total général (si plusieurs entrées) */}
        {data && data.length > 1 ? (
          <div className="mt-2 pt-4 border-t border-border/60 flex items-center justify-between px-2">
            <span className="text-sm font-semibold text-muted-foreground uppercase tracking-wider">{t("total")}</span>
            <span data-testid="total-caisse" className="text-lg font-bold text-primary tracking-tight">
              {formaterFcfa(total, locale)}
            </span>
          </div>
        ) : null}
      </CardContent>
    </Card>
  );
}
