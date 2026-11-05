"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

/** Interroge souvent (3 s) tant qu'une opération est en cours, sinon toutes les 30 s. */
export const useEtatSauvegardes = () =>
  useQuery({
    queryKey: ["sauvegardes"],
    queryFn: async () => deballer(await api.GET("/api/v1/sauvegardes")),
    refetchInterval: (q) => (q.state.data?.operation?.statut === "EN_COURS" ? 3000 : 30000),
  });

export const useDiagnostic = () =>
  useQuery({ queryKey: ["sauvegardes-diagnostic"], queryFn: async () => deballer(await api.GET("/api/v1/sauvegardes/diagnostic")), refetchInterval: 60000 });
