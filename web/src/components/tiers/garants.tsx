"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { champsGarant } from "@/components/tiers/champs";
import { FicheForm } from "@/components/tiers/fiche-form";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useGarants } from "@/hooks/use-tiers";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { optionnel } from "@/lib/tiers";

export function Garants({ locataireId, actif }: { locataireId: string; actif: boolean }) {
  const t = useTranslations("tiers");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const { data: garants } = useGarants(locataireId);
  const [ajout, setAjout] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);
  const rafraichir = () => queryClient.invalidateQueries({ queryKey: ["garants", locataireId] });

  async function retirer(id: string) {
    try {
      deballer(await api.DELETE("/api/v1/garants/{id}", { params: { path: { id } } }));
      await rafraichir();
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <Card>
      <CardHeader className="flex-row items-center justify-between">
        <CardTitle>{t("garants")}</CardTitle>
        {actif && !ajout ? (
          <Button size="sm" onClick={() => setAjout(true)}>
            {t("ajouterGarant")}
          </Button>
        ) : null}
      </CardHeader>
      <CardContent className="flex flex-col gap-4">
        {garants && garants.length === 0 && !ajout ? <p className="text-sm text-muted-foreground">{t("aucunGarant")}</p> : null}
        <ul className="flex flex-col gap-2">
          {(garants ?? []).map((g) => (
            <li key={g.id} className="flex items-center justify-between gap-2 text-sm">
              <span>
                <span className="font-medium">{g.nom}</span>
                {g.lien ? ` (${g.lien})` : ""} — {g.telephone}
              </span>
              {actif ? (
                <Button type="button" variant="outline" size="sm" onClick={() => void retirer(g.id ?? "")}>
                  {t("retirerGarant")}
                </Button>
              ) : null}
            </li>
          ))}
        </ul>
        {ajout ? (
          <FicheForm
            champs={champsGarant(t)}
            onAnnuler={() => setAjout(false)}
            onSubmit={async (v) => {
              deballer(
                await api.POST("/api/v1/locataires/{id}/garants", {
                  params: { path: { id: locataireId } },
                  body: { nom: v.nom, lien: optionnel(v.lien), profession: optionnel(v.profession), telephone: v.telephone, adresse: optionnel(v.adresse), numeroCni: optionnel(v.numeroCni) },
                }),
              );
              await rafraichir();
              toast.success(t("garantAjoute"));
              setAjout(false);
            }}
          />
        ) : null}
        {erreur ? (
          <p role="alert" className="text-sm text-destructive">
            {erreur}
          </p>
        ) : null}
      </CardContent>
    </Card>
  );
}
