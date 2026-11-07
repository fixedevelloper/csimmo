"use client";

import { useTranslations } from "next-intl";

import { Indicateurs } from "@/components/dashboard/indicateurs";
import { ErrorDisplay } from "@/components/error-display";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useMe } from "@/hooks/use-me";
import {Hand, Info} from "lucide-react";

export function DashboardContenu() {
  const t = useTranslations("dashboard");
  const { data: moi, isPending, error, refetch } = useMe();

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;

  const gestion = moi?.role === "ADMIN" || moi?.role === "GESTIONNAIRE";
  return (
    <div className="flex flex-col gap-6">

      <Card className="relative overflow-hidden rounded-2xl border border-border/60 bg-card shadow-sm">
        <div className="pointer-events-none absolute -right-12 -top-16 h-48 w-48 rounded-full bg-primary/[0.06] blur-3xl" />

        <CardHeader className="relative space-y-3 pb-4">
          {isPending ? (
            <>
              <Skeleton className="h-4 w-28" />
              <Skeleton className="h-8 w-64" />
            </>
          ) : (
            <>
              <div className="flex items-center gap-2">
                <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-primary/10">
                  <Hand className="h-5 w-5 text-primary" />
                </div>
                <span className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            Tableau de bord
          </span>
              </div>

              <CardTitle className="text-2xl font-bold tracking-tight">
                {t("bienvenue", { nom: moi?.nom ?? "" })}
              </CardTitle>
            </>
          )}
        </CardHeader>

        {!isPending && !gestion && (
          <CardContent className="relative">
            <div className="flex items-start gap-3 rounded-xl border border-dashed bg-muted/20 p-4">
              <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-muted">
                <Info className="h-5 w-5 text-muted-foreground" />
              </div>
              <p className="text-sm leading-relaxed text-muted-foreground">
                {t("vide")}
              </p>
            </div>
          </CardContent>
        )}
      </Card>

      {gestion ? <Indicateurs /> : null}
    </div>
  );
}
