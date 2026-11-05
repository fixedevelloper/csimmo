"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

/** Utilisateur connecté (GET /api/v1/auth/moi). */
export function useMe() {
  return useQuery({
    queryKey: ["moi"],
    queryFn: async () => deballer(await api.GET("/api/v1/auth/moi")),
    staleTime: 5 * 60_000,
  });
}
