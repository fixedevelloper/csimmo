"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { useErrorMessage } from "@/hooks/use-error-message";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { CHAMPS_LONGS, type CanalParametresDto } from "@/lib/notifications";

/** Identifiants d'un canal. Un secret n'est jamais affiché : « renseigné » ; le laisser vide le conserve. */
export function CanalForm({ canal }: { canal: CanalParametresDto }) {
  const t = useTranslations("notifications");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const type = canal.canal ?? "SMS";
  const initial = Object.fromEntries((canal.champs ?? []).map((c) => [c.nom ?? "", c.secret ? "" : (c.valeur ?? "")]));
  const [valeurs, setValeurs] = useState<Record<string, string>>(initial);
  const [actif, setActif] = useState(canal.actif ?? false);
  const [erreur, setErreur] = useState<string | null>(null);
  const [destinataire, setDestinataire] = useState("");
  const [resultat, setResultat] = useState<{ succes?: boolean; erreur?: string } | null>(null);

  async function enregistrer() {
    setErreur(null);
    try {
      deballer(await api.PUT("/api/v1/notifications/canaux/{canal}", { params: { path: { canal: type } }, body: { actif, champs: valeurs } }));
      await queryClient.invalidateQueries({ queryKey: ["notif-canaux"] });
      setValeurs((v) => Object.fromEntries(Object.entries(v).map(([k, val]) => [k, (canal.champs ?? []).find((c) => c.nom === k)?.secret ? "" : val])));
      toast.success(t("enregistre"));
    } catch (e) {
      setErreur(message(e));
    }
  }

  async function tester() {
    setResultat(null);
    setErreur(null);
    try {
      setResultat(deballer(await api.POST("/api/v1/notifications/canaux/{canal}/test", { params: { path: { canal: type } }, body: { destinataire } })));
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center justify-between gap-3">
          {t(`canaux.${type}`)}
          <label className="flex items-center gap-2 text-sm font-normal">
            <input type="checkbox" checked={actif} onChange={(e) => setActif(e.target.checked)} aria-label={t("activerCanal", { canal: t(`canaux.${type}`) })} />
            {t("actif")}
          </label>
        </CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-3">
        <p className="text-sm text-muted-foreground">{t(`aide.${type}`)}</p>
        {(canal.champs ?? []).map((c) => {
          const id = `${type}-${c.nom}`;
          const nom = c.nom ?? "";
          return (
            <div key={id} className="flex flex-col gap-1">
              <label htmlFor={id} className="text-sm font-medium">
                {t(`champs.${nom}`)} {c.requis ? "*" : ""}
              </label>
              {CHAMPS_LONGS.has(nom) ? (
                <Textarea id={id} rows={4} value={valeurs[nom] ?? ""} placeholder={c.secret && c.renseigne ? t("secretRenseigne") : ""} onChange={(e) => setValeurs({ ...valeurs, [nom]: e.target.value })} />
              ) : (
                <Input
                  id={id}
                  type={c.secret ? "password" : "text"}
                  autoComplete="off"
                  value={valeurs[nom] ?? ""}
                  placeholder={c.secret && c.renseigne ? t("secretRenseigne") : ""}
                  onChange={(e) => setValeurs({ ...valeurs, [nom]: e.target.value })}
                />
              )}
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
        <div className="flex flex-wrap items-end gap-2 border-t pt-3">
          <div className="flex flex-col gap-1">
            <label htmlFor={`${type}-test`} className="text-sm font-medium">
              {t(`destinataireTest.${type}`)}
            </label>
            <Input id={`${type}-test`} value={destinataire} onChange={(e) => setDestinataire(e.target.value)} className="w-64" />
          </div>
          <Button variant="outline" disabled={destinataire.trim() === ""} onClick={() => void tester()}>
            {t("tester")}
          </Button>
        </div>
        {resultat ? (
          <p role="status" className={resultat.succes ? "text-sm text-green-700" : "text-sm text-destructive"}>
            {resultat.succes ? t("testReussi") : t("testEchoue", { erreur: resultat.erreur ?? "" })}
          </p>
        ) : null}
      </CardContent>
    </Card>
  );
}
