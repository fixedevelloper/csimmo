
"use client";

import {
  ChevronLeft,
  ChevronRight,
  Inbox,
  Database,
} from "lucide-react";
import { useTranslations } from "next-intl";
import type { ReactNode } from "react";

import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { cn } from "cn";

export type Colonne<T> = {
  id: string;
  /** Texte déjà traduit de l'en-tête. */
  entete: string;
  cellule: (ligne: T) => ReactNode;
  className?: string;
};

/** Réponse paginée standard de l'API. */
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

/** Tableau paginé côté serveur. */
export function DataTable<T>({
                               colonnes,
                               donnees,
                               chargement,
                               onPageChange,
                               idLigne,
                               messageVide,
                             }: Props<T>) {
  const t = useTranslations("common");

  const lignes = donnees?.content ?? [];
  const page = donnees?.page ?? 0;
  const taille = donnees?.size ?? 20;
  const total = donnees?.totalElements ?? 0;

  const nbPages = Math.max(1, Math.ceil(total / taille));

  const debut = total === 0 ? 0 : page * taille + 1;
  const fin = Math.min((page + 1) * taille, total);

  return (
    <div className="flex min-w-0 flex-col gap-4">

      {/* Tableau */}
      <div
        className={cn(
          "overflow-hidden rounded-2xl",
          "border border-border/70 bg-card",
          "shadow-sm shadow-black/[0.03]",
        )}
      >
        <div className="overflow-x-auto">
          <Table>

            {/* En-tête */}
            <TableHeader>
              <TableRow className="border-b border-border/70 bg-muted/40 hover:bg-muted/40">
                {colonnes.map((c) => (
                  <TableHead
                    key={c.id}
                    className={cn(
                      "h-12 whitespace-nowrap px-5",
                      "text-[11px] font-bold uppercase tracking-[0.08em]",
                      "text-muted-foreground",
                      c.className,
                    )}
                  >
                    {c.entete}
                  </TableHead>
                ))}
              </TableRow>
            </TableHeader>

            <TableBody>

              {/* Chargement */}
              {chargement ? (
                Array.from({ length: 6 }, (_, i) => (
                  <TableRow
                    key={`squelette-${i}`}
                    className="border-b border-border/40"
                  >
                    {colonnes.map((c) => (
                      <TableCell
                        key={c.id}
                        className="px-5 py-4"
                      >
                        <Skeleton
                          className={cn(
                            "h-4 rounded-md",
                            i % 2 === 0 ? "w-3/4" : "w-1/2",
                          )}
                        />
                      </TableCell>
                    ))}
                  </TableRow>
                ))
              ) : lignes.length === 0 ? (

                /* État vide */
                <TableRow>
                  <TableCell
                    colSpan={colonnes.length}
                    className="h-64 p-6"
                  >
                    <div className="flex flex-col items-center justify-center gap-3 text-center">

                      <div className="flex size-14 items-center justify-center rounded-2xl border border-border/70 bg-muted/50">
                        <Inbox
                          className="size-6 text-muted-foreground"
                          aria-hidden
                        />
                      </div>

                      <div className="space-y-1">
                        <p className="text-sm font-semibold text-foreground">
                          {messageVide ?? t("aucunResultat")}
                        </p>

                        <p className="text-xs text-muted-foreground">
                          <Database className="mr-1 inline size-3.5" />
                          {t("aucunResultat")}
                        </p>
                      </div>

                    </div>
                  </TableCell>
                </TableRow>

              ) : (

                /* Données */
                lignes.map((ligne, index) => (
                  <TableRow
                    key={idLigne(ligne)}
                    className={cn(
                      "group border-b border-border/40",
                      "transition-colors duration-150",
                      "hover:bg-primary/[0.035]",
                      index === lignes.length - 1 && "border-b-0",
                    )}
                  >
                    {colonnes.map((c) => (
                      <TableCell
                        key={c.id}
                        className={cn(
                          "px-5 py-4 align-middle",
                          "text-sm",
                          c.className,
                        )}
                      >
                        {c.cellule(ligne)}
                      </TableCell>
                    ))}
                  </TableRow>
                ))
              )}

            </TableBody>
          </Table>
        </div>
      </div>

      {/* Pagination */}
      <div
        className={cn(
          "flex flex-col gap-3",
          "rounded-xl border border-border/60",
          "bg-card/70 px-4 py-3",
          "sm:flex-row sm:items-center sm:justify-between",
        )}
      >

        {/* Informations */}
        <div className="flex items-center gap-2 text-xs text-muted-foreground">
          <span className="font-medium">
            {t("total", { count: total })}
          </span>

          <span className="text-border">|</span>

          <span className="tabular-nums">
            {debut} – {fin}
          </span>
        </div>

        {/* Navigation */}
        <div className="flex items-center justify-between gap-3 sm:justify-end">

          <div className="flex items-center gap-1.5 text-xs font-medium text-muted-foreground">
            <span>{t("page", {
              page: page + 1,
              total: nbPages,
            })}</span>
          </div>

          <div className="flex items-center gap-1.5">

            <Button
              variant="outline"
              size="icon"
              className="size-8 rounded-lg"
              disabled={page <= 0 || chargement}
              onClick={() => onPageChange(page - 1)}
              aria-label={t("precedent")}
            >
              <ChevronLeft className="size-4" />
            </Button>

            <Button
              variant="outline"
              size="icon"
              className="size-8 rounded-lg"
              disabled={page + 1 >= nbPages || chargement}
              onClick={() => onPageChange(page + 1)}
              aria-label={t("suivant")}
            >
              <ChevronRight className="size-4" />
            </Button>

          </div>
        </div>
      </div>
    </div>
  );
}
