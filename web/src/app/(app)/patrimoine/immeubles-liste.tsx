"use client";

import { useTranslations } from "next-intl";
import Link from "next/link";
import { useState } from "react";

import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { Button, buttonVariants } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useImmeubles, type FiltresImmeubles } from "@/hooks/use-patrimoine";
import type { ImmeubleDto } from "@/lib/patrimoine";

export function ImmeublesListe() {
  const t = useTranslations("patrimoine");
  const [saisie, setSaisie] = useState<FiltresImmeubles>({ q: "", ville: "", quartier: "" });
  const [filtres, setFiltres] = useState<FiltresImmeubles>({});
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = useImmeubles(filtres, page);

  const colonnes: Colonne<ImmeubleDto>[] = [
    {
      id: "nom",
      entete: t("nom"),
      cellule: (i) => (
        <Link href={`/patrimoine/${i.id}`} className="font-medium underline-offset-2 hover:underline">
          {i.nom}
        </Link>
      ),
    },
    { id: "adresse", entete: t("adresse"), cellule: (i) => i.adresse },
    { id: "ville", entete: t("ville"), cellule: (i) => [i.quartier, i.ville].filter(Boolean).join(", ") },
    {
      id: "lots",
      entete: t("colLots"),
      cellule: (i) => (
        <span title={t("lotsResume", { libres: i.lotsLibres ?? 0, occupes: i.lotsOccupes ?? 0, travaux: i.lotsEnTravaux ?? 0 })}>
          {i.nombreLots ?? 0}
        </span>
      ),
    },
  ];

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;

  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <form
          role="search"
          className="flex flex-wrap items-end gap-2"
          onSubmit={(e) => {
            e.preventDefault();
            setPage(0);
            setFiltres({ ...saisie });
          }}
        >
          <Input aria-label={t("recherche")} placeholder={t("recherche")} value={saisie.q} onChange={(e) => setSaisie({ ...saisie, q: e.target.value })} className="w-56" />
          <Input aria-label={t("ville")} placeholder={t("ville")} value={saisie.ville} onChange={(e) => setSaisie({ ...saisie, ville: e.target.value })} className="w-36" />
          <Input aria-label={t("quartier")} placeholder={t("quartier")} value={saisie.quartier} onChange={(e) => setSaisie({ ...saisie, quartier: e.target.value })} className="w-36" />
          <Button type="submit" variant="outline">
            {t("rechercher")}
          </Button>
        </form>
        <Link href="/patrimoine/nouveau" className={buttonVariants({ variant: "accent" })}>
          {t("nouvelImmeuble")}
        </Link>
      </div>
      <DataTable
        colonnes={colonnes}
        donnees={data}
        chargement={isPending}
        onPageChange={setPage}
        idLigne={(i) => i.id ?? ""}
        messageVide={t("aucunImmeuble")}
      />
    </div>
  );
}
