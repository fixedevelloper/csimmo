"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

/** État de la licence (GET /api/v1/licence), réservé aux administrateurs et gestionnaires. */
export function useLicence(options: { enabled?: boolean } = {}) {
  return useQuery({
    queryKey: ["licence"],
    queryFn: async () => deballer(await api.GET("/api/v1/licence")),
    staleTime: 5 * 60_000,
    enabled: options.enabled ?? true,
  });
}
