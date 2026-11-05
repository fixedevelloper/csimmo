"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";

import { ErrorDisplay } from "@/components/error-display";
import { ArchiverBouton } from "@/components/tiers/archiver-bouton";
import { Mandats } from "@/components/tiers/mandats";
import { buttonVariants } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useProprietaire } from "@/hooks/use-tiers";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

export function ProprietaireFiche() {
  const t = useTranslations("tiers");
  const router = useRouter();
  const queryClient = useQueryClient();
  const { id } = useParams<{ id: string }>();
  const { data: p, error, refetch } = useProprietaire(id);

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (!p) return <Skeleton className="h-48 w-full" />;
  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href="/proprietaires" className="text-sm text-muted-foreground underline underline-offset-2">
          {t("proprietaires")}
        </Link>
        <div className="flex flex-wrap items-center justify-between gap-3">
          <h1 className="text-2xl font-semibold">{p.nom}</h1>
          {!p.archive ? (
            <div className="flex gap-2">
              <Link href={`/proprietaires/${id}/modifier`} className={buttonVariants({ variant: "outline" })}>
                {t("modifier")}
              </Link>
              <ArchiverBouton
                titre={t("archiverProprietaire")}
                action={async () => deballer(await api.DELETE("/api/v1/proprietaires-tiers/{id}", { params: { path: { id } } }))}
                apres={() => {
                  void queryClient.invalidateQueries({ queryKey: ["proprietaires"] });
                  router.push("/proprietaires");
                }}
              />
            </div>
          ) : (
            <span className="text-sm text-muted-foreground">{t("archivee")}</span>
          )}
        </div>
      </div>
      <Card>
        <CardContent className="grid gap-2 pt-4 text-sm sm:grid-cols-2">
          <div><span className="text-muted-foreground">{t("telephone")} : </span>{p.telephone ?? "—"}</div>
          <div><span className="text-muted-foreground">{t("email")} : </span>{p.email ?? "—"}</div>
          <div><span className="text-muted-foreground">{t("adresse")} : </span>{p.adresse ?? "—"}</div>
          <div><span className="text-muted-foreground">{t("modeReversement")} : </span>{t(`modes.${p.modeReversement ?? "VIREMENT"}`)}</div>
          {p.coordonneesReversement ? <div className="sm:col-span-2"><span className="text-muted-foreground">{t("coordonneesReversement")} : </span>{p.coordonneesReversement}</div> : null}
          {p.notes ? <p className="sm:col-span-2">{p.notes}</p> : null}
        </CardContent>
      </Card>
      <Mandats proprietaireId={id} actif={!p.archive} />
    </div>
  );
}
