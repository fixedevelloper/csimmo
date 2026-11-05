"use client";

import { useLocale, useTranslations } from "next-intl";
import Link from "next/link";
import { useParams } from "next/navigation";

import { ErrorDisplay } from "@/components/error-display";
import { DocumentsPaiement } from "@/components/finance/documents-paiement";
import { PaiementsListe } from "@/components/finance/paiements-liste";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useCompte } from "@/hooks/use-finance";
import { formaterFcfa } from "@/lib/baux";

/** Fiche « compte locataire » : arriérés, à venir, crédit, solde, baux et historique des paiements. */
export function CompteLocataire() {
  const t = useTranslations("finance");
  const locale = useLocale();
  const { id } = useParams<{ id: string }>();
  const { data: c, error, refetch } = useCompte(id);
  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (!c) return <Skeleton className="h-48 w-full" />;
  const ligne = (label: string, valeur: number | undefined, testid: string) => (
    <div className="flex flex-col">
      <span className="text-sm text-muted-foreground">{label}</span>
      <span className="text-lg font-semibold" data-testid={testid}>
        {formaterFcfa(valeur, locale)}
      </span>
    </div>
  );
  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href={`/locataires/${id}`} className="text-sm text-muted-foreground underline underline-offset-2">
          {c.locataireNom}
        </Link>
        <h1 className="text-2xl font-semibold">{t("compteTitre")}</h1>
      </div>
      <Card>
        <CardContent className="grid gap-4 pt-4 sm:grid-cols-4">
          {ligne(t("arrieres"), c.arrieres, "arrieres")}
          {ligne(t("aVenir"), c.aVenir, "a-venir")}
          {ligne(t("credit"), c.credit, "credit")}
          {ligne(c.solde !== undefined && c.solde < 0 ? t("soldeCrediteur") : t("soldeDebiteur"), c.solde, "solde")}
        </CardContent>
      </Card>
      <Card>
        <CardHeader>
          <CardTitle>{t("baux")}</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-wrap gap-3 text-sm">
          {(c.baux ?? []).map((b) => (
            <Link key={b.bailId} href={`/baux/${b.bailId}`} className="underline-offset-2 hover:underline">
              {b.numero}
            </Link>
          ))}
        </CardContent>
      </Card>
      <Card>
        <CardHeader>
          <CardTitle>{t("documents.titre")}</CardTitle>
        </CardHeader>
        <CardContent>
          <DocumentsPaiement locataireId={id} />
        </CardContent>
      </Card>
      <Card>
        <CardHeader>
          <CardTitle>{t("historique")}</CardTitle>
        </CardHeader>
        <CardContent>
          <PaiementsListe locataireId={id} />
        </CardContent>
      </Card>
    </div>
  );
}
