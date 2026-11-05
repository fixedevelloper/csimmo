"use client";

import { useTranslations } from "next-intl";
import Link from "next/link";
import { useState, type ReactNode } from "react";

import { DataTable, type Colonne, type PageDonnees } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { buttonVariants } from "@/components/ui/button";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

type Requete<T> = { data: PageDonnees<T> | undefined; isPending: boolean; error: Error | null; refetch: () => unknown };

type Props<T extends { id?: string; nom?: string }> = {
  utiliser: (q: string, page: number) => Requete<T>;
  /** Lien vers la fiche d'une ligne. */
  ficheHref: (ligne: T) => string;
  nouveauHref: string;
  nouveauLabel: string;
  /** Colonnes supplémentaires après le nom. */
  colonnes: Colonne<T>[];
  entetNom: string;
};

/** Liste paginée avec recherche par nom ou téléphone (propriétaires, locataires, prestataires). */
export function TiersListe<T extends { id?: string; nom?: string }>({ utiliser, ficheHref, nouveauHref, nouveauLabel, colonnes, entetNom }: Props<T>) {
  const t = useTranslations("tiers");
  const [saisie, setSaisie] = useState("");
  const [q, setQ] = useState("");
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = utiliser(q, page);

  const toutes: Colonne<T>[] = [
    {
      id: "nom",
      entete: entetNom,
      cellule: (l): ReactNode => (
        <Link href={ficheHref(l)} className="font-medium underline-offset-2 hover:underline">
          {l.nom}
        </Link>
      ),
    },
    ...colonnes,
  ];

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <form
          role="search"
          className="flex gap-2"
          onSubmit={(e) => {
            e.preventDefault();
            setPage(0);
            setQ(saisie);
          }}
        >
          <Input aria-label={t("recherche")} placeholder={t("recherche")} value={saisie} onChange={(e) => setSaisie(e.target.value)} className="w-64" />
          <Button type="submit" variant="outline">
            {t("rechercher")}
          </Button>
        </form>
        <Link href={nouveauHref} className={buttonVariants({})}>
          {nouveauLabel}
        </Link>
      </div>
      <DataTable colonnes={toutes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(l) => l.id ?? ""} messageVide={t("aucunResultat")} />
    </div>
  );
}
