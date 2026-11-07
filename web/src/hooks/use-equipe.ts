"use client";

import { useQuery } from "@tanstack/react-query";

import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { ROLES_EMPLOYES } from "@/lib/utilisateurs";

/** Employés (encaisseurs et gestionnaires) de l'installation. */
export function useEmployes(page: number) {
  return useQuery({
    queryKey: ["utilisateurs", "employes", page],
    queryFn: async () =>
      deballer(await api.GET("/api/v1/utilisateurs", { params: { query: { page, size: 20, sort: "nom", roles: [...ROLES_EMPLOYES] } } })),
  });
}

/** Identifiants des immeubles affectés à un employé restreint. */
export function useAffectations(utilisateurId: string, actif = true) {
  return useQuery({
    queryKey: ["affectations", utilisateurId],
    enabled: actif && utilisateurId !== "",
    queryFn: async () =>
      deballer(await api.GET("/api/v1/utilisateurs/{id}/affectations", { params: { path: { id: utilisateurId } } })).immeubleIds ?? [],
  });
}

/** Immeubles proposés au choix (recherche par nom ou adresse, 100 au plus). */
export function useImmeublesChoix(q: string) {
  return useQuery({
    queryKey: ["immeubles", "choix", q],
    queryFn: async () =>
      deballer(await api.GET("/api/v1/immeubles", { params: { query: { q: q || undefined, page: 0, size: 100, sort: "nom" } } })).content ?? [],
  });
}
