"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

export function useCanaux() {
  return useQuery({
    queryKey: ["notif-canaux"],
    queryFn: async () => deballer(await api.GET("/api/v1/notifications/canaux")),
  });
}

export function useJournal(statut: string, page: number) {
  return useQuery({
    queryKey: ["notif-journal", statut, page],
    queryFn: async () =>
      deballer(
        await api.GET("/api/v1/notifications/journal", {
          params: { query: { statut: statut ? (statut as "EN_ATTENTE" | "ENVOYE" | "ECHEC") : undefined, page, size: 20, sort: "createdAt,desc" } },
        }),
      ),
  });
}
