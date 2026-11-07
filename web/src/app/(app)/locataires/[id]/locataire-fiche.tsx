"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import {
  User,
  Mail,
  Phone,
  Briefcase,
  Calendar,
  MapPin,
  CreditCard,
  FileText,
  ShieldCheck,
  Edit3,
  Wallet,
  Building,
  StickyNote
} from "lucide-react";

import { ErrorDisplay } from "@/components/error-display";
import { ArchiverBouton } from "@/components/tiers/archiver-bouton";
import { ConformiteLocataire } from "@/components/conformite/conformite-locataire";
import { Garants } from "@/components/tiers/garants";
import { Pieces } from "@/components/tiers/pieces";
import { buttonVariants } from "@/components/ui/button";
import { PageHeader } from "@/components/page-header";
import { StatutPastille } from "@/components/statut-pastille";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Skeleton } from "@/components/ui/skeleton";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
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

  if (!l) {
    return (
      <div className="flex flex-col gap-6 max-w-7xl mx-auto w-full">
        <Skeleton className="h-20 w-full rounded-xl" />
        <Skeleton className="h-12 w-96 rounded-xl" />
        <Skeleton className="h-96 w-full rounded-xl" />
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-6 max-w-7xl mx-auto w-full">
      {/* En-tête de page moderne */}
      <PageHeader
        retour={{ href: "/locataires", libelle: t("locataires") }}
        titre={l.nom}
        statut={l.archive ? <StatutPastille statut="TERMINE" libelle={t("archivee")} /> : undefined}
        sousTitre={[l.telephone, l.email].filter(Boolean).join(" · ") || undefined}
        actions={
          !l.archive ? (
            <div className="flex flex-wrap items-center gap-2">
              <Link
                href={`/locataires/${id}/compte`}
                className={buttonVariants({ variant: "outline", className: "gap-1.5 h-9" })}
              >
                <Wallet className="size-4 text-muted-foreground" />
                {t("compte")}
              </Link>
              <Link
                href={`/locataires/${id}/modifier`}
                className={buttonVariants({ variant: "outline", className: "gap-1.5 h-9" })}
              >
                <Edit3 className="size-4 text-muted-foreground" />
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
          ) : undefined
        }
      />

      {/* Onglets de navigation modernisés */}
      <Tabs defaultValue="infos" className="w-full">
        <TabsList variant="line" className="h-11 border-b w-full justify-start rounded-none bg-transparent p-0 gap-6">
          <TabsTrigger value="infos" className="data-[state=active]:border-primary py-2.5 text-sm font-medium gap-2">
            <User className="size-4" />
            {t("onglets.infos")}
          </TabsTrigger>
          <TabsTrigger value="garants" className="data-[state=active]:border-primary py-2.5 text-sm font-medium gap-2">
            <FileText className="size-4" />
            {t("onglets.garantsPieces")}
          </TabsTrigger>
          {!l.archive && (
            <TabsTrigger value="conformite" className="data-[state=active]:border-primary py-2.5 text-sm font-medium gap-2">
              <ShieldCheck className="size-4" />
              {t("onglets.conformite")}
            </TabsTrigger>
          )}
        </TabsList>

        {/* Tab 1 : Informations personnelles */}
        <TabsContent value="infos" className="pt-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Carte Coordonnées & Contact */}
            <Card className="shadow-sm border-border/60">
              <CardHeader className="border-b bg-muted/10 pb-3">
                <CardTitle className="text-base font-semibold flex items-center gap-2">
                  <Phone className="size-4 text-primary" />
                  Coordonnées
                </CardTitle>
              </CardHeader>
              <CardContent className="divide-y text-sm pt-3">
                <div className="flex items-center justify-between py-3">
                  <span className="text-muted-foreground flex items-center gap-2">
                    <Phone className="size-3.5" />
                    {t("telephone")}
                  </span>
                  <span className="font-medium text-foreground">{l.telephone || "—"}</span>
                </div>
                <div className="flex items-center justify-between py-3">
                  <span className="text-muted-foreground flex items-center gap-2">
                    <Mail className="size-3.5" />
                    {t("email")}
                  </span>
                  <span className="font-medium text-foreground">{l.email || "—"}</span>
                </div>
                <div className="flex items-center justify-between py-3">
                  <span className="text-muted-foreground flex items-center gap-2">
                    <MapPin className="size-3.5" />
                    {t("adresse")}
                  </span>
                  <span className="font-medium text-foreground text-right">{l.adresse || "—"}</span>
                </div>
              </CardContent>
            </Card>

            {/* Carte Informations Personnelles & Professionnelles */}
            <Card className="shadow-sm border-border/60">
              <CardHeader className="border-b bg-muted/10 pb-3">
                <CardTitle className="text-base font-semibold flex items-center gap-2">
                  <Briefcase className="size-4 text-primary" />
                  Profil & Identité
                </CardTitle>
              </CardHeader>
              <CardContent className="divide-y text-sm pt-3">
                <div className="flex items-center justify-between py-3">
                  <span className="text-muted-foreground flex items-center gap-2">
                    <Briefcase className="size-3.5" />
                    {t("profession")}
                  </span>
                  <span className="font-medium text-foreground">{l.profession || "—"}</span>
                </div>
                <div className="flex items-center justify-between py-3">
                  <span className="text-muted-foreground flex items-center gap-2">
                    <Calendar className="size-3.5" />
                    {t("dateNaissance")}
                  </span>
                  <span className="font-medium text-foreground">
                    {l.dateNaissance ? formaterDate(l.dateNaissance, locale) : "—"}
                  </span>
                </div>
                <div className="flex items-center justify-between py-3">
                  <span className="text-muted-foreground flex items-center gap-2">
                    <CreditCard className="size-3.5" />
                    {t("numeroCni")}
                  </span>
                  <span className="font-medium font-mono text-foreground" data-testid="numero-cni">{l.numeroCni || "—"}</span>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Notes éventuelles */}
          {l.notes && (
            <Card className="shadow-sm border-border/60 mt-6">
              <CardHeader className="border-b bg-muted/10 pb-3">
                <CardTitle className="text-base font-semibold flex items-center gap-2">
                  <StickyNote className="size-4 text-primary" />
                  Notes & Remarques
                </CardTitle>
              </CardHeader>
              <CardContent className="pt-4">
                <p className="text-sm text-foreground/90 whitespace-pre-wrap leading-relaxed">{l.notes}</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        {/* Tab 2 : Garants & Pièces */}
        <TabsContent value="garants" className="flex flex-col gap-6 pt-6">
          <Garants locataireId={id} actif={!l.archive} />
          <Pieces locataireId={id} actif={!l.archive} />
        </TabsContent>

        {/* Tab 3 : Conformité & Consentements RGPD */}
        {!l.archive && (
          <TabsContent value="conformite" className="pt-6">
            <ConformiteLocataire locataireId={id} />
          </TabsContent>
        )}
      </Tabs>
    </div>
  );
}
