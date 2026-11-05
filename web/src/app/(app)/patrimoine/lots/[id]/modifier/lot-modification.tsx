"use client";

import { useTranslations } from "next-intl";
import { useParams } from "next/navigation";

import { ErrorDisplay } from "@/components/error-display";
import { LotForm } from "@/components/patrimoine/lot-form";
import { Skeleton } from "@/components/ui/skeleton";
import { useLot } from "@/hooks/use-patrimoine";

export function LotModification() {
  const t = useTranslations("patrimoine");
  const { id } = useParams<{ id: string }>();
  const { data, error, refetch } = useLot(id);

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (!data) return <Skeleton className="h-64 w-full max-w-2xl" />;
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">
        {t("modifier")} — {data.reference}
      </h1>
      <LotForm lot={data} />
    </div>
  );
}
