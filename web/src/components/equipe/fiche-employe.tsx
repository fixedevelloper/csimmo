"use client";

import { useLocale, useTranslations } from "next-intl";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";

import { DataTable, type Colonne } from "@/components/data-table";
import { AffectationsEmploye } from "@/components/equipe/affectations-employe";
import { ErrorDisplay } from "@/components/error-display";
import { PageHeader } from "@/components/page-header";
import { StatutPastille } from "@/components/statut-pastille";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useAffectations, useActivite, useEmploye, useEncaissementsDe } from "@/hooks/use-equipe";
import { useCaisse } from "@/hooks/use-finance";
import { useImmeuble } from "@/hooks/use-patrimoine";
import type { components } from "@/lib/api/schema";
import { formaterFcfa } from "@/lib/baux";
import { estRestreint, type RoleUtilisateur } from "@/lib/utilisateurs";

type Paiement = components["schemas"]["PaiementDto"];
type EntreeAudit = { id?: string; date?: string; action?: string; entite?: string; entiteId?: string };

const MAX_IMMEUBLES_AFFICHES = 12;

function NomImmeuble({ id }: { id: string }) {
  const { data } = useImmeuble(id);
  return (
    <li>
      <Link href={`/patrimoine/${id}`} className="underline-offset-2 hover:underline">
        {data?.nom ?? "…"}
      </Link>
    </li>
  );
}

function ImmeublesAffectes({ id, nom }: { id: string; nom: string }) {
  const t = useTranslations("equipe");
  const { data, isPending } = useAffectations(id);
  const [edition, setEdition] = useState(false);
  if (edition) return <AffectationsEmploye utilisateur={{ id, nom }} onFini={() => setEdition(false)} />;
  return (
    <Card>
      <CardHeader className="flex-row items-center justify-between gap-2">
        <CardTitle>{t("immeublesAffectes")}</CardTitle>
        <Button type="button" size="sm" variant="outline" onClick={() => setEdition(true)}>
          {t("modifierImmeubles")}
        </Button>
      </CardHeader>
      <CardContent>
        {isPending ? (
          <Skeleton className="h-16 w-full" />
        ) : (data ?? []).length === 0 ? (
          <p role="status" className="text-sm text-amber-700 dark:text-amber-400">{t("aucuneAffectation")}</p>
        ) : (
          <>
            <ul className="grid gap-1 text-sm sm:grid-cols-2">
              {(data ?? []).slice(0, MAX_IMMEUBLES_AFFICHES).map((i) => (
                <NomImmeuble key={i} id={i} />
              ))}
            </ul>
            {(data ?? []).length > MAX_IMMEUBLES_AFFICHES ? (
              <p className="mt-2 text-xs text-muted-foreground">{t("fiche.etAutres", { count: (data ?? []).length - MAX_IMMEUBLES_AFFICHES })}</p>
            ) : null}
          </>
        )}
      </CardContent>
    </Card>
  );
}

function CaisseDuJour({ id }: { id: string }) {
  const t = useTranslations("equipe");
  const locale = useLocale();
  const { data, isPending } = useCaisse("");
  const ligne = (data ?? []).find((l) => l.agentId === id);
  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("fiche.caisseDuJour")}</CardTitle>
      </CardHeader>
      <CardContent>
        {isPending ? (
          <Skeleton className="h-10 w-48" />
        ) : (
          <div className="flex flex-col gap-1">
            <strong data-testid="caisse-total" className="text-2xl">{formaterFcfa(ligne?.total ?? 0, locale)}</strong>
            <span className="text-sm text-muted-foreground">{t("fiche.especesOperations", { count: ligne?.operations ?? 0 })}</span>
          </div>
        )}
      </CardContent>
    </Card>
  );
}

function Encaissements({ id }: { id: string }) {
  const t = useTranslations("equipe");
  const tf = useTranslations("finance");
  const locale = useLocale();
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = useEncaissementsDe(id, page);
  const colonnes: Colonne<Paiement>[] = [
    { id: "date", entete: t("fiche.date"), cellule: (p) => (p.datePaiement ? new Date(p.datePaiement).toLocaleDateString(locale) : "—") },
    { id: "locataire", entete: tf("locataire"), cellule: (p) => p.locataireNom ?? "—" },
    { id: "bail", entete: tf("bail"), cellule: (p) => p.bailNumero ?? "—" },
    { id: "mode", entete: tf("mode"), cellule: (p) => (p.mode ? tf(`modes.${p.mode}`) : "—") },
    { id: "montant", entete: tf("montant"), cellule: (p) => formaterFcfa(p.montant, locale) },
    {
      id: "statut",
      entete: "",
      cellule: (p) => (p.contrePasse ? <StatutPastille statut="CONTRE_PASSATION" libelle={tf("contrePasse")} /> : null),
    },
  ];
  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("fiche.encaissementsRecents")}</CardTitle>
      </CardHeader>
      <CardContent>
        <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(p) => p.id ?? ""} messageVide={t("fiche.aucunEncaissement")} />
      </CardContent>
    </Card>
  );
}

function Activite({ id }: { id: string }) {
  const t = useTranslations("equipe");
  const locale = useLocale();
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = useActivite(id, page);
  const colonnes: Colonne<EntreeAudit>[] = [
    { id: "date", entete: t("fiche.date"), cellule: (e) => (e.date ? new Date(e.date).toLocaleString(locale) : "—") },
    { id: "action", entete: t("fiche.action"), cellule: (e) => <code className="text-xs">{e.action}</code> },
    { id: "entite", entete: t("fiche.entite"), cellule: (e) => e.entite ?? "—" },
  ];
  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("fiche.activite")}</CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-2">
        <p className="text-xs text-muted-foreground">{t("fiche.activiteAide")}</p>
        <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(e) => e.id ?? ""} messageVide={t("fiche.aucuneActivite")} />
      </CardContent>
    </Card>
  );
}

/** Fiche d'un employé (ADMIN) : immeubles affectés, caisse du jour, encaissements récents, journal d'activité. */
export function FicheEmploye() {
  const t = useTranslations("equipe");
  const tu = useTranslations("utilisateurs");
  const { id } = useParams<{ id: string }>();
  const { data: u, error, refetch } = useEmploye(id);

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (!u) {
    return (
      <div className="flex flex-col gap-4">
        <Skeleton className="h-16 w-full" />
        <Skeleton className="h-40 w-full" />
      </div>
    );
  }
  return (
    <div className="flex flex-col gap-6">
      <PageHeader
        retour={{ href: "/equipe", libelle: t("titre") }}
        titre={u.nom}
        statut={<StatutPastille statut={u.actif ? "ACTIF" : "ANNULE"} libelle={u.actif ? tu("actif") : tu("inactif")} />}
        sousTitre={[u.role ? t(`roles.${u.role as RoleUtilisateur}`) : null, u.telephone, u.email].filter(Boolean).join(" · ")}
      />
      <div className="grid gap-4 lg:grid-cols-2">
        {estRestreint(u.role) ? <ImmeublesAffectes id={id} nom={u.nom ?? ""} /> : null}
        {u.role === "ENCAISSEUR" || u.role === "GESTIONNAIRE_BIENS" || u.role === "GESTIONNAIRE" ? <CaisseDuJour id={id} /> : null}
      </div>
      {u.role === "ENCAISSEUR" || u.role === "GESTIONNAIRE_BIENS" || u.role === "GESTIONNAIRE" ? <Encaissements id={id} /> : null}
      <Activite id={id} />
    </div>
  );
}
