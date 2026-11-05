"use client";

import { useTranslations } from "next-intl";

import { ErrorDisplay } from "@/components/error-display";
import { CanalForm } from "@/components/notifications/canal-form";
import { JournalEnvois } from "@/components/notifications/journal-envois";
import { Skeleton } from "@/components/ui/skeleton";
import { useCanaux } from "@/hooks/use-notifications";
import { useMe } from "@/hooks/use-me";
import { EnteteParametres } from "@/components/entete-parametres";

export function NotificationsPage() {
  const t = useTranslations("notifications");
  const { data: me } = useMe();
  const admin = me?.role === "ADMIN";
  const { data, error, refetch } = useCanaux();
  return (
    <div className="flex flex-col gap-8">
      <EnteteParametres titre={t("titre")} />
      {admin ? (
        <section className="flex flex-col gap-4" aria-label={t("canauxTitre")}>
          <p className="text-sm text-muted-foreground">{t("intro")}</p>
          {error ? <ErrorDisplay erreur={error} onRetry={() => void refetch()} /> : null}
          {!data && !error ? <Skeleton className="h-48 w-full" /> : null}
          <div className="grid gap-4 lg:grid-cols-2">
            {(data ?? []).map((c) => (
              <CanalForm key={`${c.canal}-${c.actif}-${c.configure}`} canal={c} />
            ))}
          </div>
        </section>
      ) : null}
      <section className="flex flex-col gap-3">
        <h2 className="text-lg font-semibold">{t("journal.titre")}</h2>
        <JournalEnvois />
      </section>
    </div>
  );
}
