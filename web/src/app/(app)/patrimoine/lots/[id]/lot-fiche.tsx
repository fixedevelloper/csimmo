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
import { ErrorDisplay } from "@/components/error-display";
import { PhotoGallery } from "@/components/patrimoine/photo-gallery";
import { StatutBadge } from "@/components/patrimoine/statut-badge";
import { Badge } from "@/components/ui/badge";
import { Button, buttonVariants } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useLot } from "@/hooks/use-patrimoine";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

export function LotFiche() {
  const t = useTranslations("patrimoine");
  const message = useErrorMessage();
  const router = useRouter();
  const queryClient = useQueryClient();
  const { id } = useParams<{ id: string }>();
  const { data: lot, error, refetch } = useLot(id);
  const [confirmation, setConfirmation] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);

  const rafraichir = async () => {
    await queryClient.invalidateQueries({ queryKey: ["lot", id] });
    await queryClient.invalidateQueries({ queryKey: ["lots"] });
    await queryClient.invalidateQueries({ queryKey: ["immeuble"] });
  };

  const statut = useMutation({
    mutationFn: async (cible: "LIBRE" | "EN_TRAVAUX") =>
      deballer(await api.PUT("/api/v1/lots/{id}/statut", { params: { path: { id } }, body: { statut: cible } })),
    onSuccess: async () => {
      setErreur(null);
      toast.success(t("statutChange"));
      await rafraichir();
    },
    onError: (e) => setErreur(message(e)),
  });

  const archiver = useMutation({
    mutationFn: async () => deballer(await api.DELETE("/api/v1/lots/{id}", { params: { path: { id } } })),
    onSuccess: async () => {
      await rafraichir();
      toast.success(t("lotArchive"));
      router.push(lot?.immeubleId ? `/patrimoine/${lot.immeubleId}` : "/patrimoine");
    },
    onError: (e) => {
      setConfirmation(false);
      setErreur(message(e));
    },
  });

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (!lot) return <Skeleton className="h-48 w-full" />;

  const occupe = lot.statut === "OCCUPE";
  return (
    <div className="flex flex-col gap-6">
      <PageHeader
        retour={{ href: `/patrimoine/${lot.immeubleId}`, libelle: `${t("retourImmeuble")} — ${lot.immeubleNom}` }}
        titre={lot.reference}
        statut={<StatutBadge statut={lot.statut} />}
        sousTitre={occupe ? t("statutBail") : undefined}
        actions={
          !lot.archive ? (
            <>
              <Link href={`/patrimoine/lots/${id}/modifier`} className={buttonVariants({ variant: "outline" })}>
                {t("modifier")}
              </Link>
              {!occupe ? (
                <Button variant="outline" onClick={() => statut.mutate(lot.statut === "EN_TRAVAUX" ? "LIBRE" : "EN_TRAVAUX")} disabled={statut.isPending}>
                  {lot.statut === "EN_TRAVAUX" ? t("marquerLibre") : t("marquerTravaux")}
                </Button>
              ) : null}
              {!occupe ? (
                <Button variant="outline" onClick={() => setConfirmation(true)}>
                  {t("archiver")}
                </Button>
              ) : null}
            </>
          ) : (
            <StatutPastille statut="TERMINE" libelle={t("archive")} />
          )
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
            <span className="text-muted-foreground">{t("type")} : </span>
            {t(`types.${lot.type ?? "APPARTEMENT"}`)}
          </div>
          <div>
            <span className="text-muted-foreground">{t("surface")} : </span>
            {lot.surfaceM2 ?? "—"}
          </div>
          <div>
            <span className="text-muted-foreground">{t("pieces")} : </span>
            {lot.pieces ?? "—"}
          </div>
          <div>
            <span className="text-muted-foreground">{t("loyer")} : </span>
            {lot.loyerIndicatif != null ? lot.loyerIndicatif.toLocaleString("fr-FR") : "—"}
          </div>
          <div className="sm:col-span-2">
            <span className="text-muted-foreground">{t("equipements")} : </span>
            <span className="inline-flex flex-wrap gap-1 align-middle">
              {(lot.equipements ?? []).map((e) => (
                <Badge key={e} variant="secondary">
                  {e}
                </Badge>
              ))}
            </span>
          </div>
          {(lot.compteurs ?? []).length > 0 ? (
            <div className="sm:col-span-2">
              <span className="text-muted-foreground">{t("compteurs")} : </span>
              {(lot.compteurs ?? []).map((c) => `${t(`typesCompteur.${c.type ?? "EAU"}`)} ${c.numero}`).join(" · ")}
            </div>
          ) : null}
        </CardContent>
      </Card>

      <PhotoGallery cible="lots" id={id} />

      <ConfirmDialog
        open={confirmation}
        onOpenChange={setConfirmation}
        title={t("archiverLot")}
        description={t("archiverLotDescription")}
        confirmLabel={t("archiver")}
        onConfirm={() => archiver.mutate()}
      />
    </div>
  );
}
