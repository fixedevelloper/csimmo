"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { CopyButton } from "@/components/copy-button";
import { ErrorDisplay } from "@/components/error-display";
import { LicenceFileField } from "@/components/licence-file-field";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useLicence } from "@/hooks/use-licence";
import { useMe } from "@/hooks/use-me";
import { api } from "@/lib/api/client";
import { ApiError, deballer } from "@/lib/api/errors";
import { formaterDate, ROLES_LICENCE, type EtatLicenceDto } from "@/lib/licence";

function varianteBadge(etat: EtatLicenceDto["etat"]): "default" | "secondary" | "destructive" {
  if (etat === "VALIDE") return "default";
  return etat === "AUCUNE" ? "secondary" : "destructive";
}

function Ligne({ libelle, children }: { libelle: string; children: React.ReactNode }) {
  return (
    <div className="grid gap-1 sm:grid-cols-[14rem_1fr] sm:gap-4">
      <dt className="text-sm text-muted-foreground">{libelle}</dt>
      <dd className="text-sm font-medium">{children}</dd>
    </div>
  );
}

export function LicenceContenu() {
  const t = useTranslations("licence");
  const locale = useLocale();
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const { data: moi } = useMe();
  const autorise = moi?.role !== undefined && (ROLES_LICENCE as readonly string[]).includes(moi.role);
  const { data: etat, error, isPending, refetch } = useLicence({ enabled: autorise });

  const [contenu, setContenu] = useState("");
  const [erreurFormulaire, setErreurFormulaire] = useState<string | null>(null);

  const importer = useMutation({
    mutationFn: async (texte: string) => deballer(await api.POST("/api/v1/licence", { body: { contenu: texte } })),
    onSuccess: (nouvelEtat) => {
      queryClient.setQueryData(["licence"], nouvelEtat);
      toast.success(t("importee"));
      setContenu("");
      setErreurFormulaire(null);
    },
    onError: (e) => setErreurFormulaire(message(e)),
  });

  function soumettre(evenement: React.FormEvent) {
    evenement.preventDefault();
    if (contenu.trim() === "") {
      setErreurFormulaire(t("contenuRequis"));
      return;
    }
    setErreurFormulaire(null);
    importer.mutate(contenu);
  }

  if (moi && !autorise) return <ErrorDisplay erreur={new ApiError(403, "ACCES_REFUSE")} />;
  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (isPending || !etat) return <Skeleton className="h-64 w-full" />;

  const estAdmin = moi?.role === "ADMIN";
  const jours = etat.joursRestants;

  return (
    <div className="flex flex-col gap-6">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-3">
            {t("etat")}
            <Badge data-testid="etat-licence" variant={varianteBadge(etat.etat)}>
              {t(`etats.${etat.etat ?? "AUCUNE"}`)}
            </Badge>
          </CardTitle>
          {etat.lectureSeule ? <CardDescription role="alert">{t("lectureSeule")}</CardDescription> : null}
        </CardHeader>
        <CardContent>
          <dl className="flex flex-col gap-3">
            {etat.detail ? <Ligne libelle={t("detail")}>{etat.detail}</Ligne> : null}
            {etat.client ? <Ligne libelle={t("client")}>{etat.client}</Ligne> : null}
            {etat.licenseId ? <Ligne libelle={t("licenseId")}>{etat.licenseId}</Ligne> : null}
            {etat.palierLots != null ? <Ligne libelle={t("palier")}>{t("lots", { count: etat.palierLots })}</Ligne> : null}
            {etat.dateDebut && etat.dateExpiration ? (
              <Ligne libelle={t("periode")}>
                {t("du", { debut: formaterDate(etat.dateDebut, locale), fin: formaterDate(etat.dateExpiration, locale) })}
              </Ligne>
            ) : null}
            {jours != null ? (
              <Ligne libelle={t("joursRestants")}>
                {jours >= 0 ? t("jours", { count: jours }) : t("expireDepuis", { count: -jours })}
              </Ligne>
            ) : null}
            {etat.fonctionnalites && etat.fonctionnalites.length > 0 ? (
              <Ligne libelle={t("fonctionnalites")}>
                <span className="flex flex-wrap gap-1">
                  {etat.fonctionnalites.map((f) => (
                    <Badge key={f} variant="secondary">
                      {f}
                    </Badge>
                  ))}
                </span>
              </Ligne>
            ) : null}
            <Ligne libelle={t("verificationEnLigne")}>
              {etat.verificationEnLigneActive ? (
                <>
                  {t("verificationActive", { date: formaterDate(etat.derniereVerificationEnLigne, locale) })}
                  {etat.joursDeGraceRestants != null ? (
                    <span className="block font-normal text-muted-foreground">
                      {t("grace", { count: etat.joursDeGraceRestants })}
                    </span>
                  ) : null}
                </>
              ) : (
                t("verificationDesactivee")
              )}
            </Ligne>
          </dl>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>{t("empreinte")}</CardTitle>
          <CardDescription>{t("empreinteAide")}</CardDescription>
        </CardHeader>
        <CardContent className="flex items-center gap-2">
          <code data-testid="empreinte" className="min-w-0 flex-1 break-all rounded bg-muted px-2 py-1 text-xs">
            {etat.empreinteServeur}
          </code>
          {etat.empreinteServeur ? <CopyButton texte={etat.empreinteServeur} namespace="licence" /> : null}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>{t("importTitre")}</CardTitle>
          <CardDescription>{estAdmin ? t("importAide") : t("adminSeulement")}</CardDescription>
        </CardHeader>
        {estAdmin ? (
          <CardContent>
            <form onSubmit={soumettre} noValidate className="flex flex-col gap-4">
              <LicenceFileField
                valeur={contenu}
                onChange={setContenu}
                erreur={erreurFormulaire ?? undefined}
                libelleFichier={t("fichier")}
                libelleContenu={t("contenu")}
                onFichierTropGros={() => {
                  setContenu("");
                  setErreurFormulaire(t("fichierTropGros"));
                }}
              />
              <div>
                <Button type="submit" disabled={importer.isPending}>
                  {importer.isPending ? t("importEnCours") : t("importer")}
                </Button>
              </div>
            </form>
          </CardContent>
        ) : null}
      </Card>
    </div>
  );
}
