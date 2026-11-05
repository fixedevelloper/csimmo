"use client";

import { useTranslations } from "next-intl";

import { Indicateurs } from "@/components/dashboard/indicateurs";
import { ErrorDisplay } from "@/components/error-display";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useMe } from "@/hooks/use-me";

export function DashboardContenu() {
  const t = useTranslations("dashboard");
  const { data: moi, isPending, error, refetch } = useMe();

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;

  const gestion = moi?.role === "ADMIN" || moi?.role === "GESTIONNAIRE";
  return (
    <div className="flex flex-col gap-6">
      <Card>
        <CardHeader>
          <CardTitle>{isPending ? <Skeleton className="h-6 w-56" /> : t("bienvenue", { nom: moi?.nom ?? "" })}</CardTitle>
        </CardHeader>
        {!isPending && !gestion ? <CardContent className="text-muted-foreground">{t("vide")}</CardContent> : null}
      </Card>
      {gestion ? <Indicateurs /> : null}
    </div>
  );
}
