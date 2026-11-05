"use client";

import { useTranslations } from "next-intl";
import { useParams } from "next/navigation";

import { ErrorDisplay } from "@/components/error-display";
import { ImmeubleForm } from "@/components/patrimoine/immeuble-form";
import { Skeleton } from "@/components/ui/skeleton";
import { useImmeuble } from "@/hooks/use-patrimoine";

export function ImmeubleModification() {
  const t = useTranslations("patrimoine");
  const { id } = useParams<{ id: string }>();
  const { data, error, refetch } = useImmeuble(id);

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (!data) return <Skeleton className="h-64 w-full max-w-2xl" />;
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">
        {t("modifier")} — {data.nom}
      </h1>
      <ImmeubleForm immeuble={data} />
    </div>
  );
}
