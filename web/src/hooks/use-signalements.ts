"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

export function useSignalements(statut: string, page: number) {
  return useQuery({
    queryKey: ["signalements", statut, page],
    queryFn: async () =>
      deballer(
        await api.GET("/api/v1/signalements", {
          params: { query: { statut: statut ? (statut as "NOUVEAU" | "EN_COURS" | "RESOLU" | "REJETE") : undefined, page, size: 20, sort: "createdAt,desc" } },
        }),
      ),
  });
}
