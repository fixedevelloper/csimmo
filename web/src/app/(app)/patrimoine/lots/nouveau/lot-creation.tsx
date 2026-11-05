"use client";

import { useTranslations } from "next-intl";
import Link from "next/link";
import { useSearchParams } from "next/navigation";

import { ErrorDisplay } from "@/components/error-display";
import { LotForm } from "@/components/patrimoine/lot-form";
import { Skeleton } from "@/components/ui/skeleton";
import { useImmeuble } from "@/hooks/use-patrimoine";
import { ApiError } from "@/lib/api/errors";

export function LotCreation() {
  const t = useTranslations("patrimoine");
  const immeubleId = useSearchParams().get("immeubleId") ?? "";
  const { data, error, refetch } = useImmeuble(immeubleId);

  if (!immeubleId) return <ErrorDisplay erreur={new ApiError(404, "IMMEUBLE_INTROUVABLE")} />;
  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (!data) return <Skeleton className="h-64 w-full max-w-2xl" />;
  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href={`/patrimoine/${immeubleId}`} className="text-sm text-muted-foreground underline underline-offset-2">
          {t("retourImmeuble")} — {data.nom}
        </Link>
        <h1 className="text-2xl font-semibold">{t("nouveauLot")}</h1>
      </div>
      <LotForm immeubleId={immeubleId} />
    </div>
  );
}
