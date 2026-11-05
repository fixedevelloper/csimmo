"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

export function useBaux(statut: string, page: number) {
  return useQuery({
    queryKey: ["baux", statut, page],
    queryFn: async () =>
      deballer(
        await api.GET("/api/v1/baux", {
          params: {
            query: {
              statut: statut ? (statut as "BROUILLON" | "ACTIF" | "RESILIE" | "TERMINE") : undefined,
              page,
              size: 20,
              sort: "createdAt,desc",
            },
          },
        }),
      ),
  });
}

export function useBail(id: string) {
  return useQuery({
    queryKey: ["bail", id],
    queryFn: async () => deballer(await api.GET("/api/v1/baux/{id}", { params: { path: { id } } })),
  });
}

export function useDocumentsBail(id: string) {
  return useQuery({
    queryKey: ["bail-documents", id],
    queryFn: async () => deballer(await api.GET("/api/v1/baux/{id}/documents", { params: { path: { id } } })),
  });
}

export function useEcheances(bailId: string, page: number) {
  return useQuery({
    queryKey: ["echeances", bailId, page],
    queryFn: async () =>
      deballer(await api.GET("/api/v1/echeances", { params: { query: { bailId, page, size: 24, sort: "periodeDebut" } } })),
  });
}

export function useModeleBail() {
  return useQuery({
    queryKey: ["bail-modele"],
    queryFn: async () => deballer(await api.GET("/api/v1/baux/modele")),
  });
}
