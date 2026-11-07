"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import Link from "next/link";

import { PageHeader } from "@/components/page-header";
import { StatutPastille } from "@/components/statut-pastille";
import { useParams, useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";

import { ConfirmDialog } from "@/components/confirm-dialog";
import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { PhotoGallery } from "@/components/patrimoine/photo-gallery";
import { StatutBadge } from "@/components/patrimoine/statut-badge";
import { Button, buttonVariants } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useImmeuble, useLots } from "@/hooks/use-patrimoine";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import type { LotDto } from "@/lib/patrimoine";

export function ImmeubleFiche() {
  const t = useTranslations("patrimoine");
  const message = useErrorMessage();
  const router = useRouter();
  const queryClient = useQueryClient();
  const { id } = useParams<{ id: string }>();
  const { data: immeuble, error, refetch } = useImmeuble(id);
  const [pageLots, setPageLots] = useState(0);
  const lots = useLots({ immeubleId: id }, pageLots);
  const [confirmation, setConfirmation] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);

  const archiver = useMutation({
    mutationFn: async () => deballer(await api.DELETE("/api/v1/immeubles/{id}", { params: { path: { id } } })),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["immeubles"] });
      toast.success(t("immeubleArchive"));
      router.push("/patrimoine");
    },
    onError: (e) => {
      setConfirmation(false);
      setErreur(message(e));
    },
  });

  const colonnes: Colonne<LotDto>[] = [
    {
      id: "reference",
      entete: t("reference"),
      cellule: (l) => (
        <Link href={`/patrimoine/lots/${l.id}`} className="font-medium underline-offset-2 hover:underline">
          {l.reference}
        </Link>
      ),
    },
    { id: "type", entete: t("type"), cellule: (l) => t(`types.${l.type ?? "APPARTEMENT"}`) },
    { id: "surface", entete: t("surface"), cellule: (l) => l.surfaceM2 ?? "—" },
    { id: "loyer", entete: t("loyer"), cellule: (l) => (l.loyerIndicatif != null ? l.loyerIndicatif.toLocaleString("fr-FR") : "—") },
    { id: "statut", entete: t("statut"), cellule: (l) => <StatutBadge statut={l.statut} /> },
  ];

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (!immeuble) return <Skeleton className="h-48 w-full" />;

  return (
    <div className="flex flex-col gap-6">
      <PageHeader
        retour={{ href: "/patrimoine", libelle: t("retourListe") }}
        titre={immeuble.nom}
        statut={immeuble.archive ? <StatutPastille statut="TERMINE" libelle={t("archive")} /> : undefined}
        actions={
          !immeuble.archive ? (
            <>
              <Link href={`/patrimoine/${id}/modifier`} className={buttonVariants({ variant: "outline" })}>
                {t("modifier")}
              </Link>
              <Button variant="outline" onClick={() => setConfirmation(true)}>
                {t("archiver")}
              </Button>
            </>
          ) : undefined
        }
      />
      {erreur ? (
        <p role="alert" className="text-sm text-destructive">
          {erreur}
        </p>
      ) : null}

      <Card>
        <CardContent className="grid gap-2 pt-4 text-sm sm:grid-cols-2">
          <div>
            <span className="text-muted-foreground">{t("adresse")} : </span>
            {immeuble.adresse}
          </div>
          <div>
            <span className="text-muted-foreground">{t("ville")} : </span>
            {[immeuble.quartier, immeuble.ville].filter(Boolean).join(", ")}
          </div>
          {immeuble.latitude != null && immeuble.longitude != null ? (
            <div>
              <span className="text-muted-foreground">GPS : </span>
              {immeuble.latitude}, {immeuble.longitude}
            </div>
          ) : null}
          <div data-testid="resume-lots">{t("lotsResume", { libres: immeuble.lotsLibres ?? 0, occupes: immeuble.lotsOccupes ?? 0, travaux: immeuble.lotsEnTravaux ?? 0 })}</div>
          {immeuble.description ? <p className="sm:col-span-2">{immeuble.description}</p> : null}
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="flex-row items-center justify-between">
          <CardTitle>{t("lots")}</CardTitle>
          {!immeuble.archive ? (
            <Link href={`/patrimoine/lots/nouveau?immeubleId=${id}`} className={buttonVariants({ variant: "accent", size: "sm" })}>
              {t("nouveauLot")}
            </Link>
          ) : null}
        </CardHeader>
        <CardContent>
          <DataTable
            colonnes={colonnes}
            donnees={lots.data}
            chargement={lots.isPending}
            onPageChange={setPageLots}
            idLigne={(l) => l.id ?? ""}
            messageVide={t("aucunLot")}
          />
        </CardContent>
      </Card>

      <PhotoGallery cible="immeubles" id={id} />

      <ConfirmDialog
        open={confirmation}
        onOpenChange={setConfirmation}
        title={t("archiverImmeuble")}
        description={t("archiverImmeubleDescription")}
        confirmLabel={t("archiver")}
        onConfirm={() => archiver.mutate()}
      />
    </div>
  );
}
