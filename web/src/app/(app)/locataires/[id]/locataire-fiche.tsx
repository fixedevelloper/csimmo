"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";

import { ErrorDisplay } from "@/components/error-display";
import { ArchiverBouton } from "@/components/tiers/archiver-bouton";
import { ConformiteLocataire } from "@/components/conformite/conformite-locataire";
import { Garants } from "@/components/tiers/garants";
import { Pieces } from "@/components/tiers/pieces";
import { buttonVariants } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useLocataire } from "@/hooks/use-tiers";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { formaterDate } from "@/lib/licence";

export function LocataireFiche() {
  const t = useTranslations("tiers");
  const locale = useLocale();
  const router = useRouter();
  const queryClient = useQueryClient();
  const { id } = useParams<{ id: string }>();
  const { data: l, error, refetch } = useLocataire(id);

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (!l) return <Skeleton className="h-48 w-full" />;
  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href="/locataires" className="text-sm text-muted-foreground underline underline-offset-2">
          {t("locataires")}
        </Link>
        <div className="flex flex-wrap items-center justify-between gap-3">
          <h1 className="text-2xl font-semibold">{l.nom}</h1>
          {!l.archive ? (
            <div className="flex gap-2">
              <Link href={`/locataires/${id}/compte`} className={buttonVariants({ variant: "outline" })}>
                {t("compte")}
              </Link>
              <Link href={`/locataires/${id}/modifier`} className={buttonVariants({ variant: "outline" })}>
                {t("modifier")}
              </Link>
              <ArchiverBouton
                titre={t("archiverLocataire")}
                action={async () => deballer(await api.DELETE("/api/v1/locataires/{id}", { params: { path: { id } } }))}
                apres={() => {
                  void queryClient.invalidateQueries({ queryKey: ["locataires"] });
                  router.push("/locataires");
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
          <div><span className="text-muted-foreground">{t("telephone")} : </span>{l.telephone ?? "—"}</div>
          <div><span className="text-muted-foreground">{t("email")} : </span>{l.email ?? "—"}</div>
          <div><span className="text-muted-foreground">{t("profession")} : </span>{l.profession ?? "—"}</div>
          <div><span className="text-muted-foreground">{t("dateNaissance")} : </span>{l.dateNaissance ? formaterDate(l.dateNaissance, locale) : "—"}</div>
          <div><span className="text-muted-foreground">{t("adresse")} : </span>{l.adresse ?? "—"}</div>
          <div><span className="text-muted-foreground">{t("numeroCni")} : </span><span data-testid="numero-cni">{l.numeroCni ?? "—"}</span></div>
          {l.notes ? <p className="sm:col-span-2">{l.notes}</p> : null}
        </CardContent>
      </Card>
      <Garants locataireId={id} actif={!l.archive} />
      <Pieces locataireId={id} actif={!l.archive} />
      {!l.archive ? <ConformiteLocataire locataireId={id} /> : null}
    </div>
  );
}
