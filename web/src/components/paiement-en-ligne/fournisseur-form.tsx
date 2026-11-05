"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useErrorMessage } from "@/hooks/use-error-message";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import type { components } from "@/lib/api/schema";

type FournisseurDto = components["schemas"]["FournisseurDto"];

/** Identifiants marchands du client chez l'agrégateur. Un secret n'est jamais réaffiché : « renseigné » ; le laisser vide le conserve. */
export function FournisseurForm({ fournisseur }: { fournisseur: FournisseurDto }) {
  const t = useTranslations("paiementEnLigne");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const code = fournisseur.code ?? "";
  const [valeurs, setValeurs] = useState<Record<string, string>>(
    Object.fromEntries((fournisseur.champs ?? []).map((c) => [c.nom ?? "", c.secret ? "" : (c.valeur ?? "")])),
  );
  const [actif, setActif] = useState(fournisseur.actif ?? false);
  const [erreur, setErreur] = useState<string | null>(null);

  async function enregistrer() {
    setErreur(null);
    try {
      deballer(await api.PUT("/api/v1/paiements-en-ligne/fournisseurs/{code}", { params: { path: { code } }, body: { actif, champs: valeurs } }));
      await queryClient.invalidateQueries({ queryKey: ["fournisseurs-paiement"] });
      setValeurs((v) => Object.fromEntries(Object.entries(v).map(([k, val]) => [k, (fournisseur.champs ?? []).find((c) => c.nom === k)?.secret ? "" : val])));
      toast.success(t("enregistre"));
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center justify-between gap-3">
          {t(`fournisseurs.${code}`)}
          <label className="flex items-center gap-2 text-sm font-normal">
            <input type="checkbox" checked={actif} onChange={(e) => setActif(e.target.checked)} aria-label={t("activer", { nom: t(`fournisseurs.${code}`) })} />
            {t("actif")}
          </label>
        </CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-3">
        <p className="text-sm text-muted-foreground">{t("urlWebhook", { url: `/api/v1/webhooks/${code}` })}</p>
        {(fournisseur.champs ?? []).map((c) => {
          const nom = c.nom ?? "";
          return (
            <div key={nom} className="flex flex-col gap-1">
              <label htmlFor={`f-${code}-${nom}`} className="text-sm font-medium">
                {t(`champs.${nom}`)} {c.requis ? "*" : ""}
              </label>
              <Input
                id={`f-${code}-${nom}`}
                type={c.secret ? "password" : "text"}
                autoComplete="off"
                value={valeurs[nom] ?? ""}
                placeholder={c.secret && c.renseigne ? t("secretRenseigne") : ""}
                onChange={(e) => setValeurs({ ...valeurs, [nom]: e.target.value })}
              />
            </div>
          );
        })}
        {erreur ? (
          <p role="alert" className="text-sm text-destructive">
            {erreur}
          </p>
        ) : null}
        <div>
          <Button onClick={() => void enregistrer()}>{t("enregistrer")}</Button>
        </div>
      </CardContent>
    </Card>
  );
}
