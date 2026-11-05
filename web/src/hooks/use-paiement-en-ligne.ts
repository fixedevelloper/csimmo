"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

export const useFournisseurs = () =>
  useQuery({ queryKey: ["fournisseurs-paiement"], queryFn: async () => deballer(await api.GET("/api/v1/paiements-en-ligne/fournisseurs")) });

export function useTransactionsEnLigne(statut: string, page: number) {
  return useQuery({
    queryKey: ["transactions-en-ligne", statut, page],
    queryFn: async () =>
      deballer(
        await api.GET("/api/v1/paiements-en-ligne", {
          params: {
            query: { statut: statut ? (statut as "EN_ATTENTE" | "CONFIRMEE" | "ECHOUEE" | "EXPIREE" | "ANOMALIE") : undefined, page, size: 20, sort: "createdAt,desc" },
          },
        }),
      ),
  });
}
