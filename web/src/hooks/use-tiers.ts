"use client";

import { keepPreviousData, useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

const requete = (q: string, page: number) => ({ q: q || undefined, page, size: 20, sort: "nom" });

export function useProprietaires(q: string, page: number, taille = 20) {
  return useQuery({
    queryKey: ["proprietaires", q, page, taille],
    queryFn: async () =>
      deballer(await api.GET("/api/v1/proprietaires-tiers", { params: { query: { ...requete(q, page), size: taille } } })),
  });
}

export function useProprietaire(id: string) {
  return useQuery({
    queryKey: ["proprietaire", id],
    queryFn: async () => deballer(await api.GET("/api/v1/proprietaires-tiers/{id}", { params: { path: { id } } })),
  });
}

export function useMandats(proprietaireId: string) {
  return useQuery({
    queryKey: ["mandats", proprietaireId],
    queryFn: async () =>
      deballer(await api.GET("/api/v1/proprietaires-tiers/{id}/mandats", { params: { path: { id: proprietaireId } } })),
  });
}

export function useLocataires(q: string, page: number) {
  return useQuery({
    queryKey: ["locataires", q, page],
    queryFn: async () => deballer(await api.GET("/api/v1/locataires", { params: { query: requete(q, page) } })),
    placeholderData: keepPreviousData,
  });
}

export function useLocataire(id: string) {
  return useQuery({
    queryKey: ["locataire", id],
    queryFn: async () => deballer(await api.GET("/api/v1/locataires/{id}", { params: { path: { id } } })),
  });
}

export function useGarants(locataireId: string) {
  return useQuery({
    queryKey: ["garants", locataireId],
    queryFn: async () => deballer(await api.GET("/api/v1/locataires/{id}/garants", { params: { path: { id: locataireId } } })),
  });
}

export function usePieces(cible: "locataires" | "garants", id: string) {
  return useQuery({
    queryKey: ["pieces", cible, id],
    queryFn: async () =>
      cible === "locataires"
        ? deballer(await api.GET("/api/v1/locataires/{id}/pieces", { params: { path: { id } } }))
        : deballer(await api.GET("/api/v1/garants/{id}/pieces", { params: { path: { id } } })),
  });
}

export function usePrestataires(q: string, page: number) {
  return useQuery({
    queryKey: ["prestataires", q, page],
    queryFn: async () => deballer(await api.GET("/api/v1/prestataires", { params: { query: requete(q, page) } })),
  });
}

export function usePrestataire(id: string) {
  return useQuery({
    queryKey: ["prestataire", id],
    queryFn: async () => deballer(await api.GET("/api/v1/prestataires/{id}", { params: { path: { id } } })),
  });
}
