
"use client";

import {
  BellRing,
  CalendarDays,
  CheckCircle2,
  Clock,
  Mail,
  MessageSquare,
  Phone,
  Send,
  XCircle,
} from "lucide-react";

import { useTranslations } from "next-intl";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

import { useHistoriqueRelances } from "@/hooks/use-relances";

/** Historique des relances d'une échéance. */
export function HistoriqueRelances({
                                     echeanceId,
                                   }: {
  echeanceId: string;
}) {
  const t = useTranslations("relances.historique");

  const { data, isPending } = useHistoriqueRelances(echeanceId);

  const lignes = data?.content ?? [];

  function iconeCanal(canal?: string) {
    switch (canal?.toUpperCase()) {
      case "EMAIL":
        return Mail;
      case "SMS":
        return MessageSquare;
      case "TELEPHONE":
        return Phone;
      default:
        return Send;
    }
  }

  function couleurResultat(resultat?: string) {
    switch (resultat) {
      case "EN_FILE":
        return "bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-400";

      case "AUCUN_CANAL":
        return "bg-red-50 text-red-700 dark:bg-red-500/10 dark:text-red-400";

      default:
        return "bg-amber-50 text-amber-700 dark:bg-amber-500/10 dark:text-amber-400";
    }
  }

  function iconeResultat(resultat?: string) {
    switch (resultat) {
      case "EN_FILE":
        return CheckCircle2;

      case "AUCUN_CANAL":
        return XCircle;

      default:
        return Clock;
    }
  }

  if (isPending) {
    return (
      <div className="space-y-3 animate-pulse">
        {[1, 2].map((i) => (
          <div key={i} className="flex gap-3">
            <div className="size-9 rounded-full bg-muted" />
            <div className="flex-1 space-y-2">
              <div className="h-3 w-32 rounded bg-muted" />
              <div className="h-3 w-48 rounded bg-muted" />
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (lignes.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center gap-2 rounded-xl border border-dashed bg-muted/20 px-4 py-8 text-center">
        <div className="flex size-11 items-center justify-center rounded-full bg-muted">
          <BellRing className="size-5 text-muted-foreground" />
        </div>

        <p className="text-sm font-medium">
          {t("aucune")}
        </p>

        <p className="text-xs text-muted-foreground">
          Aucun historique de relance disponible pour cette échéance.
        </p>
      </div>
    );
  }

  return (
    <Card className="shadow-sm">
      <CardHeader className="border-b border-border/50">
        <CardTitle className="flex items-center gap-2 text-base">
          <BellRing className="size-4 text-primary" />
          {t("titre")}

          <Badge variant="secondary" className="ml-auto">
            {lignes.length}
          </Badge>
        </CardTitle>
      </CardHeader>

      <CardContent className="pt-4">
        <div className="relative space-y-5">

          {lignes.map((r, index) => {
            const IconeCanal = iconeCanal(r.canal);
            const IconeResultat = iconeResultat(r.resultat);

            return (
              <div
                key={r.id}
                className="relative flex gap-3"
              >
                {/* LIGNE TIMELINE */}
                {index !== lignes.length - 1 && (
                  <div className="absolute left-[17px] top-9 h-[calc(100%+12px)] w-px bg-border" />
                )}

                {/* ICÔNE */}
                <div className="relative z-10 flex size-9 shrink-0 items-center justify-center rounded-full border bg-background shadow-sm">
                  <IconeCanal className="size-4 text-primary" />
                </div>

                {/* CONTENU */}
                <div className="min-w-0 flex-1 space-y-2 pb-1">

                  <div className="flex flex-wrap items-center justify-between gap-2">
                    <p className="text-sm font-semibold">
                      {r.type === "MANUELLE"
                        ? t("manuelle")
                        : t("niveau", {
                          jours: r.niveau ?? 0,
                        })}
                    </p>

                    <span className="flex items-center gap-1 text-xs text-muted-foreground">
                      <CalendarDays className="size-3.5" />
                      {r.dateRelance}
                    </span>
                  </div>

                  <div className="flex flex-wrap items-center gap-2">

                    {/* RÉSULTAT */}
                    <Badge
                      variant="outline"
                      className={cnResultat(couleurResultat(r.resultat))}
                    >
                      <IconeResultat className="mr-1 size-3" />
                      {r.resultat ? t(`resultats.${r.resultat}`) : null}
                    </Badge>

                    {/* CANAL */}
                    {r.canal && (
                      <Badge variant="secondary">
                        <IconeCanal className="mr-1 size-3" />
                        {r.canal}
                      </Badge>
                    )}

                    {/* STATUT */}
                    {r.statutEnvoi && (
                      <Badge variant="outline" className="text-muted-foreground">
                        {t(`statuts.${r.statutEnvoi}`)}
                      </Badge>
                    )}
                  </div>

                </div>
              </div>
            );
          })}

        </div>
      </CardContent>
    </Card>
  );
}

function cnResultat(couleur: string) {
  return `border-transparent ${couleur}`;
}
