"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { toast } from "sonner";

import { useErrorMessage } from "@/hooks/use-error-message";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import type { components } from "@/lib/api/schema";

type Utilisateur = components["schemas"]["UtilisateurDto"];

/** Actions d'un compte (activer / désactiver, déverrouiller) partagées par Paramètres > Utilisateurs et la page Équipe. */
export function useActionsUtilisateur() {
  const t = useTranslations("utilisateurs");
  const message = useErrorMessage();
  const queryClient = useQueryClient();

  async function agir(action: () => Promise<unknown>, succes: string) {
    try {
      await action();
      await queryClient.invalidateQueries({ queryKey: ["utilisateurs"] });
      toast.success(succes);
    } catch (e) {
      toast.error(message(e));
    }
  }

  const basculerActif = (u: Utilisateur) =>
    agir(
      async () =>
        deballer(
          await api.PUT("/api/v1/utilisateurs/{id}", {
            params: { path: { id: u.id ?? "" } },
            body: {
              nom: u.nom ?? "", telephone: u.telephone, email: u.email, role: u.role ?? "LOCATAIRE",
              actif: !u.actif, deuxFacteursActif: u.deuxFacteursActif,
            },
          }),
        ),
      t(u.actif ? "desactive" : "active"),
    );

  const deverrouiller = (u: Utilisateur) =>
    agir(async () => deballer(await api.POST("/api/v1/utilisateurs/{id}/deverrouiller", { params: { path: { id: u.id ?? "" } } })), t("deverrouille"));

  /** Téléphone perdu : retire l'application d'authentification du compte et ferme ses sessions (ADMIN). */
  const reinitialiserDeuxFacteurs = (u: Utilisateur) =>
    agir(
      async () => deballer(await api.POST("/api/v1/utilisateurs/{id}/reinitialiser-deux-facteurs", { params: { path: { id: u.id ?? "" } } })),
      t("deuxFacteursReinitialises"),
    );

  return { basculerActif, deverrouiller, reinitialiserDeuxFacteurs };
}
