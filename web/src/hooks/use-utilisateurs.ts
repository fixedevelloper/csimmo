"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

/** Comptes de l'installation (ADMIN seul). */
export function useUtilisateurs(page: number) {
  return useQuery({
    queryKey: ["utilisateurs", page],
    queryFn: async () => deballer(await api.GET("/api/v1/utilisateurs", { params: { query: { page, size: 20, sort: "nom" } } })),
  });
}
