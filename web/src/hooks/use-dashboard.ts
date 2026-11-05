"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

export const useSynthese = () =>
  useQuery({ queryKey: ["dashboard-synthese"], queryFn: async () => deballer(await api.GET("/api/v1/tableau-de-bord/synthese")) });

export const useAttenduEncaisse = (mois = 12) =>
  useQuery({
    queryKey: ["dashboard-serie", mois],
    queryFn: async () => deballer(await api.GET("/api/v1/tableau-de-bord/attendu-encaisse", { params: { query: { mois } } })),
  });

export const useArrieresDashboard = () =>
  useQuery({ queryKey: ["dashboard-arrieres"], queryFn: async () => deballer(await api.GET("/api/v1/tableau-de-bord/arrieres")) });

export const useBauxAEcheance = () =>
  useQuery({ queryKey: ["dashboard-baux"], queryFn: async () => deballer(await api.GET("/api/v1/tableau-de-bord/baux-a-echeance")) });
