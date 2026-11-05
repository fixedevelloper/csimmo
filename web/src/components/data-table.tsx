"use client";

import { ChevronLeft, ChevronRight, Inbox } from "lucide-react";
import { useTranslations } from "next-intl";
import type { ReactNode } from "react";

import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

export type Colonne<T> = {
  id: string;
  /** Texte déjà traduit de l'en-tête. */
  entete: string;
  cellule: (ligne: T) => ReactNode;
  className?: string;
};

/** Réponse paginée standard de l'API : { content, page, size, totalElements }. */
export type PageDonnees<T> = {
  content?: T[];
  page?: number;
  size?: number;
  totalElements?: number;
};

type Props<T> = {
  colonnes: Colonne<T>[];
  donnees: PageDonnees<T> | undefined;
  chargement?: boolean;
  onPageChange: (page: number) => void;
  idLigne: (ligne: T) => string;
  messageVide?: string;
};

/** Tableau paginé côté serveur (pages numérotées à partir de 0, comme l'API). */
export function DataTable<T>({ colonnes, donnees, chargement, onPageChange, idLigne, messageVide }: Props<T>) {
  const t = useTranslations("common");
  const lignes = donnees?.content ?? [];
  const page = donnees?.page ?? 0;
  const taille = donnees?.size ?? 20;
  const total = donnees?.totalElements ?? 0;
  const nbPages = Math.max(1, Math.ceil(total / taille));

  return (
    <div className="flex flex-col gap-3">
      <div className="overflow-hidden rounded-xl border bg-card shadow-carte">
        <Table>
          <TableHeader className="bg-muted/50">
            <TableRow className="hover:bg-transparent">
              {colonnes.map((c) => (
                <TableHead key={c.id} className={`h-10 text-xs font-semibold uppercase tracking-wide text-muted-foreground ${c.className ?? ""}`}>
                  {c.entete}
                </TableHead>
              ))}
            </TableRow>
          </TableHeader>
          <TableBody>
            {chargement ? (
              Array.from({ length: 5 }, (_, i) => (
                <TableRow key={`squelette-${i}`}>
                  {colonnes.map((c) => (
                    <TableCell key={c.id}>
                      <Skeleton className="h-4 w-full" />
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : lignes.length === 0 ? (
              <TableRow>
                <TableCell colSpan={colonnes.length} className="h-32 text-center text-muted-foreground">
                  <span className="flex flex-col items-center gap-2">
                    <Inbox className="size-6 opacity-60" aria-hidden />
                    {messageVide ?? t("aucunResultat")}
                  </span>
                </TableCell>
              </TableRow>
            ) : (
              lignes.map((ligne) => (
                <TableRow key={idLigne(ligne)} className="transition-colors hover:bg-muted/40">
                  {colonnes.map((c) => (
                    <TableCell key={c.id} className={c.className}>
                      {c.cellule(ligne)}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>
      <div className="flex items-center justify-between gap-2 text-sm text-muted-foreground">
        <span>{t("total", { count: total })}</span>
        <div className="flex items-center gap-2">
          <span>{t("page", { page: page + 1, total: nbPages })}</span>
          <Button variant="outline" size="sm" disabled={page <= 0 || chargement} onClick={() => onPageChange(page - 1)}>
            <ChevronLeft aria-hidden />
            {t("precedent")}
          </Button>
          <Button
            variant="outline"
            size="sm"
            disabled={page + 1 >= nbPages || chargement}
            onClick={() => onPageChange(page + 1)}
          >
            {t("suivant")}
            <ChevronRight aria-hidden />
          </Button>
        </div>
      </div>
    </div>
  );
}
