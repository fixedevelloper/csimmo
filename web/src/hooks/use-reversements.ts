"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

export function useReversements(mois: string, proprietaireId: string | null, page: number) {
  return useQuery({
    queryKey: ["reversements", mois, proprietaireId, page],
    queryFn: async () =>
      deballer(
        await api.GET("/api/v1/reversements", {
          params: { query: { mois: mois || undefined, proprietaireId: proprietaireId || undefined, page, size: 20, sort: "periode,desc" } },
        }),
      ),
  });
}

export function useDepenses(mois: string, page: number) {
  return useQuery({
    queryKey: ["depenses", mois, page],
    queryFn: async () =>
      deballer(await api.GET("/api/v1/depenses", { params: { query: { mois: mois || undefined, page, size: 20, sort: "dateDepense,desc" } } })),
  });
}
