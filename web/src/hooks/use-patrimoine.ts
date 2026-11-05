"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import type { StatutLot, TypeLot } from "@/lib/patrimoine";

export type FiltresImmeubles = { ville?: string; quartier?: string; q?: string };
export type FiltresLots = { immeubleId?: string; statut?: StatutLot; type?: TypeLot; q?: string };

const sansVide = <T extends Record<string, string | undefined>>(o: T) =>
  Object.fromEntries(Object.entries(o).filter(([, v]) => v !== undefined && v !== "")) as Partial<T>;

export function useImmeubles(filtres: FiltresImmeubles, page: number) {
  return useQuery({
    queryKey: ["immeubles", filtres, page],
    queryFn: async () =>
      deballer(await api.GET("/api/v1/immeubles", { params: { query: { ...sansVide(filtres), page, size: 20, sort: "nom" } } })),
  });
}

export function useImmeuble(id: string) {
  return useQuery({
    queryKey: ["immeuble", id],
    queryFn: async () => deballer(await api.GET("/api/v1/immeubles/{id}", { params: { path: { id } } })),
  });
}

export function useLots(filtres: FiltresLots, page: number) {
  return useQuery({
    queryKey: ["lots", filtres, page],
    queryFn: async () =>
      deballer(await api.GET("/api/v1/lots", { params: { query: { ...sansVide(filtres), page, size: 20, sort: "reference" } } })),
  });
}

export function useLot(id: string) {
  return useQuery({
    queryKey: ["lot", id],
    queryFn: async () => deballer(await api.GET("/api/v1/lots/{id}", { params: { path: { id } } })),
  });
}

export function usePhotos(cible: "immeubles" | "lots", id: string) {
  return useQuery({
    queryKey: ["photos", cible, id],
    queryFn: async () =>
      cible === "immeubles"
        ? deballer(await api.GET("/api/v1/immeubles/{id}/photos", { params: { path: { id } } }))
        : deballer(await api.GET("/api/v1/lots/{id}/photos", { params: { path: { id } } })),
  });
}
