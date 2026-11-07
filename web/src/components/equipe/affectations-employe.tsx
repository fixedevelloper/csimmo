"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { ChoixImmeubles } from "@/components/equipe/choix-immeubles";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useAffectations } from "@/hooks/use-equipe";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import type { components } from "@/lib/api/schema";

type Utilisateur = components["schemas"]["UtilisateurDto"];

function Edition({ utilisateur, initial, onFini }: { utilisateur: Utilisateur; initial: string[]; onFini: () => void }) {
  const t = useTranslations("equipe");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [ids, setIds] = useState<string[]>(initial);
  const [erreur, setErreur] = useState<string | null>(null);
  const [envoi, setEnvoi] = useState(false);

  async function enregistrer() {
    setErreur(null);
    setEnvoi(true);
    try {
      deballer(await api.PUT("/api/v1/utilisateurs/{id}/affectations", { params: { path: { id: utilisateur.id ?? "" } }, body: { immeubleIds: ids } }));
      await queryClient.invalidateQueries({ queryKey: ["affectations"] });
      toast.success(t("affectationsEnregistrees"));
      onFini();
    } catch (e) {
      setErreur(message(e));
    } finally {
      setEnvoi(false);
    }
  }

  return (
    <div className="flex max-w-xl flex-col gap-4">
      <ChoixImmeubles valeur={ids} onChange={setIds} />
      {ids.length === 0 ? <p role="status" className="text-sm text-amber-700 dark:text-amber-400">{t("aucuneAffectation")}</p> : null}
      {erreur ? <p role="alert" className="text-sm text-destructive">{erreur}</p> : null}
      <div className="flex gap-2">
        <Button type="button" variant="accent" disabled={envoi} onClick={() => void enregistrer()}>{t("enregistrer")}</Button>
        <Button type="button" variant="ghost" onClick={onFini}>{t("annuler")}</Button>
      </div>
    </div>
  );
}

/** Immeubles affectés à un encaisseur : un retrait agit immédiatement côté serveur. */
export function AffectationsEmploye({ utilisateur, onFini }: { utilisateur: Utilisateur; onFini: () => void }) {
  const t = useTranslations("equipe");
  const { data, isPending } = useAffectations(utilisateur.id ?? "");
  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("affectationsDe", { nom: utilisateur.nom ?? "" })}</CardTitle>
      </CardHeader>
      <CardContent>
        {isPending || !data ? <Skeleton className="h-40 w-full max-w-xl" /> : <Edition utilisateur={utilisateur} initial={data} onFini={onFini} />}
      </CardContent>
    </Card>
  );
}
