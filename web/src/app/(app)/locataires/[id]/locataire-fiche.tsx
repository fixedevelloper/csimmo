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
import { PageHeader } from "@/components/page-header";
import { InfoList, Section } from "@/components/section";
import { StatutPastille } from "@/components/statut-pastille";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
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
      <PageHeader
        retour={{ href: "/locataires", libelle: t("locataires") }}
        titre={l.nom}
        statut={l.archive ? <StatutPastille statut="TERMINE" libelle={t("archivee")} /> : undefined}
        sousTitre={[l.telephone, l.email].filter(Boolean).join(" · ") || undefined}
        actions={
          !l.archive ? (
            <>
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
            </>
          ) : undefined
        }
      />
      <Tabs defaultValue="infos">
        <TabsList variant="line" className="h-10 border-b">
          <TabsTrigger value="infos">{t("onglets.infos")}</TabsTrigger>
          <TabsTrigger value="garants">{t("onglets.garantsPieces")}</TabsTrigger>
          {!l.archive ? <TabsTrigger value="conformite">{t("onglets.conformite")}</TabsTrigger> : null}
        </TabsList>
        <TabsContent value="infos" className="pt-4">
          <Section>
            <InfoList
              items={[
                { libelle: t("telephone"), valeur: l.telephone },
                { libelle: t("email"), valeur: l.email },
                { libelle: t("profession"), valeur: l.profession },
                { libelle: t("dateNaissance"), valeur: l.dateNaissance ? formaterDate(l.dateNaissance, locale) : undefined },
                { libelle: t("adresse"), valeur: l.adresse },
                { libelle: t("numeroCni"), valeur: l.numeroCni, testid: "numero-cni" },
              ]}
            />
            {l.notes ? <p className="mt-4 border-t pt-4 text-sm">{l.notes}</p> : null}
          </Section>
        </TabsContent>
        <TabsContent value="garants" className="flex flex-col gap-6 pt-4">
          <Garants locataireId={id} actif={!l.archive} />
          <Pieces locataireId={id} actif={!l.archive} />
        </TabsContent>
        {!l.archive ? (
          <TabsContent value="conformite" className="pt-4">
            <ConformiteLocataire locataireId={id} />
          </TabsContent>
        ) : null}
      </Tabs>
    </div>
  );
}
