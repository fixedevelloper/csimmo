"use client";

import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { StatutPastille } from "@/components/statut-pastille";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useErrorMessage } from "@/hooks/use-error-message";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

const MODES = ["PAPIER", "ELECTRONIQUE", "ORAL"] as const;

/** Consentements d'un locataire (registre en ajout seul) et demande d'effacement (anonymisation). */
export function ConformiteLocataire({ locataireId }: { locataireId: string }) {
  const t = useTranslations("conformite");
  const locale = useLocale();
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [mode, setMode] = useState<(typeof MODES)[number]>("PAPIER");
  const [erreur, setErreur] = useState<string | null>(null);
  const { data } = useQuery({
    queryKey: ["consentements", "LOCATAIRE", locataireId],
    queryFn: async () => deballer(await api.GET("/api/v1/conformite/consentements/{type}/{personneId}", { params: { path: { type: "LOCATAIRE", personneId: locataireId } } })),
  });

  async function decider(finalite: "NOTIFICATIONS" | "PIECES_IDENTITE" | "TRANSMISSION_PROPRIETAIRE" | "APPLICATION_MOBILE", accorde: boolean) {
    setErreur(null);
    try {
      deballer(await api.POST("/api/v1/conformite/consentements", { body: { type: "LOCATAIRE", personneId: locataireId, finalite, accorde, modeRecueil: mode } }));
      toast.success(t("consentementEnregistre"));
      await queryClient.invalidateQueries({ queryKey: ["consentements", "LOCATAIRE", locataireId] });
    } catch (e) {
      setErreur(message(e));
    }
  }

  async function demanderEffacement() {
    setErreur(null);
    try {
      deballer(await api.POST("/api/v1/conformite/demandes", { body: { type: "LOCATAIRE", personneId: locataireId } }));
      toast.success(t("demandeEnregistree"));
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("donneesPersonnelles")}</CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-4">
        <label className="flex w-56 flex-col gap-1 text-sm">
          {t("modeRecueil")}
          <select value={mode} onChange={(e) => setMode(e.target.value as (typeof MODES)[number])} className="h-8 rounded-lg border border-input bg-transparent px-2.5 text-sm">
            {MODES.map((m) => (
              <option key={m} value={m}>
                {t(`modes.${m}`)}
              </option>
            ))}
          </select>
        </label>
        <ul className="flex flex-col gap-2">
          {data?.finalites?.map((f) => (
            <li key={f.finalite} className="flex flex-wrap items-center gap-3 text-sm">
              <span className="min-w-56 font-medium">{t(`finalites.${f.finalite}`)}</span>
              <StatutPastille statut={f.statut} libelle={t(`statuts.${f.statut}`)} />
              {f.derniere?.decideLe ? <span className="text-muted-foreground">{new Date(f.derniere.decideLe).toLocaleDateString(locale)}</span> : null}
              <Button size="sm" variant="outline" onClick={() => void decider(f.finalite!, true)}>
                {t("accord")}
              </Button>
              <Button size="sm" variant="outline" onClick={() => void decider(f.finalite!, false)}>
                {t("refusRetrait")}
              </Button>
            </li>
          ))}
        </ul>
        {erreur ? (
          <p role="alert" className="text-sm text-destructive">
            {erreur}
          </p>
        ) : null}
        <div>
          <Button variant="outline" onClick={() => void demanderEffacement()}>
            {t("demanderEffacement")}
          </Button>
          <p className="mt-1 text-xs text-muted-foreground">{t("effacementAide")}</p>
        </div>
      </CardContent>
    </Card>
  );
}
