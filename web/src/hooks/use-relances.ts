"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

export function useArrieres(q: string, page: number) {
  return useQuery({
    queryKey: ["arrieres", q, page],
    queryFn: async () =>
      deballer(await api.GET("/api/v1/arrieres", { params: { query: { q: q || undefined, page, size: 20, sort: "dateEcheance" } } })),
  });
}

export function useHistoriqueRelances(echeanceId: string) {
  return useQuery({
    queryKey: ["relances", echeanceId],
    queryFn: async () => deballer(await api.GET("/api/v1/relances", { params: { query: { echeanceId, size: 50 } } })),
  });
}

export function useParametresRelance() {
  return useQuery({
    queryKey: ["relances-parametres"],
    queryFn: async () => deballer(await api.GET("/api/v1/relances/parametres")),
  });
}
