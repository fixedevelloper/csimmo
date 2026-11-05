"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import type { ModePaiement } from "@/lib/finance";

export type FiltresPaiements = { mode?: ModePaiement | ""; du?: string; au?: string; locataireId?: string };

export function usePaiements(filtres: FiltresPaiements, page: number) {
  return useQuery({
    queryKey: ["paiements", filtres, page],
    queryFn: async () =>
      deballer(
        await api.GET("/api/v1/paiements", {
          params: {
            query: {
              mode: filtres.mode || undefined,
              du: filtres.du || undefined,
              au: filtres.au || undefined,
              locataireId: filtres.locataireId || undefined,
              page,
              size: 20,
              sort: "createdAt,desc",
            },
          },
        }),
      ),
  });
}

export function useCaisse(jour: string) {
  return useQuery({
    queryKey: ["caisse", jour],
    queryFn: async () => deballer(await api.GET("/api/v1/paiements/caisse", { params: { query: { jour: jour || undefined } } })),
  });
}

export function useCompte(locataireId: string) {
  return useQuery({
    queryKey: ["compte", locataireId],
    queryFn: async () => deballer(await api.GET("/api/v1/locataires/{id}/compte", { params: { path: { id: locataireId } } })),
  });
}

/** Baux signés (hors brouillon) d'un locataire : un paiement se saisit par locataire, ce qui reste utilisable avec des milliers de baux. */
export function useBauxDuLocataire(locataireId: string) {
  return useQuery({
    queryKey: ["baux-locataire", locataireId],
    enabled: locataireId !== "",
    queryFn: async () => {
      const page = deballer(await api.GET("/api/v1/baux", { params: { query: { locataireId, size: 50, sort: "dateDebut,desc" } } }));
      return (page.content ?? []).filter((b) => b.statut !== "BROUILLON");
    },
  });
}

export function useDocumentsPaiement(filtre: { bailId?: string; locataireId?: string }, page: number) {
  return useQuery({
    queryKey: ["documents-paiement", filtre, page],
    queryFn: async () =>
      deballer(await api.GET("/api/v1/documents-paiement", { params: { query: { ...filtre, page, size: 20, sort: "createdAt,desc" } } })),
  });
}
