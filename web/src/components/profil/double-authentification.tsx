"use client";

import { useQueryClient } from "@tanstack/react-query";
import { ShieldCheck, ShieldOff } from "lucide-react";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { StatutPastille } from "@/components/statut-pastille";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useErrorMessage } from "@/hooks/use-error-message";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import type { components } from "@/lib/api/schema";
import { ROLES_MOT_DE_PASSE_OBLIGATOIRE, ROLES_RESTREINTS, type RoleUtilisateur } from "@/lib/utilisateurs";

type Utilisateur = components["schemas"]["UtilisateurDto"];
type Etape = "repos" | "motDePasse" | "code" | "desactiver";

/** Active (mot de passe puis code SMS) ou désactive (mot de passe) la double authentification ; obligatoire pour les employés restreints. */
export function DoubleAuthentification({ moi }: { moi: Utilisateur }) {
  const t = useTranslations("profil.deuxFacteurs");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [etape, setEtape] = useState<Etape>("repos");
  const [motDePasse, setMotDePasse] = useState("");
  const [code, setCode] = useState("");
  const [erreur, setErreur] = useState<string | null>(null);
  const [envoi, setEnvoi] = useState(false);

  const concerne = ROLES_MOT_DE_PASSE_OBLIGATOIRE.includes((moi.role ?? "") as RoleUtilisateur);
  const obligatoire = ROLES_RESTREINTS.includes(moi.role ?? "");
  const active = moi.deuxFacteursActif === true;

  function annuler() {
    setEtape("repos");
    setMotDePasse("");
    setCode("");
    setErreur(null);
  }

  async function agir(action: () => Promise<void>) {
    setErreur(null);
    setEnvoi(true);
    try {
      await action();
    } catch (e) {
      setErreur(message(e));
    } finally {
      setEnvoi(false);
    }
  }

  const demanderCode = () =>
    agir(async () => {
      deballer(await api.POST("/api/v1/auth/moi/double-authentification/demander", { body: { motDePasse } }));
      setMotDePasse("");
      setEtape("code");
    });

  const activer = () =>
    agir(async () => {
      deballer(await api.POST("/api/v1/auth/moi/double-authentification/activer", { body: { code } }));
      await queryClient.invalidateQueries({ queryKey: ["moi"] });
      toast.success(t("activee"));
      annuler();
    });

  const desactiver = () =>
    agir(async () => {
      deballer(await api.POST("/api/v1/auth/moi/double-authentification/desactiver", { body: { motDePasse } }));
      await queryClient.invalidateQueries({ queryKey: ["moi"] });
      toast.success(t("desactivee"));
      annuler();
    });

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          {active ? <ShieldCheck className="size-4 text-primary" aria-hidden /> : <ShieldOff className="size-4 text-muted-foreground" aria-hidden />}
          {t("titre")}
          {concerne ? <StatutPastille statut={active ? "ACTIF" : "ANNULE"} libelle={active ? t("active") : t("inactive")} /> : null}
        </CardTitle>
      </CardHeader>
      <CardContent className="flex max-w-xl flex-col gap-3">
        {!concerne ? (
          <p className="text-sm text-muted-foreground">{t("nonConcerne")}</p>
        ) : (
          <>
            <p className="text-sm text-muted-foreground">{active ? t("aideActive") : t("aideInactive")}</p>
            {obligatoire ? <p className="text-sm font-medium">{t("obligatoire")}</p> : null}

            {etape === "repos" && !obligatoire ? (
              !active && !moi.telephone ? (
                <p role="status" className="text-sm text-amber-700 dark:text-amber-400">{t("sansTelephone")}</p>
              ) : (
                <div>
                  <Button type="button" variant={active ? "outline" : "accent"} onClick={() => setEtape(active ? "desactiver" : "motDePasse")}>
                    {active ? t("desactiver") : t("activer")}
                  </Button>
                </div>
              )
            ) : null}

            {etape === "motDePasse" || etape === "desactiver" ? (
              <form
                className="flex flex-col gap-3"
                onSubmit={(e) => {
                  e.preventDefault();
                  void (etape === "motDePasse" ? demanderCode() : desactiver());
                }}
              >
                <div className="flex flex-col gap-1">
                  <label htmlFor="df-mdp" className="text-sm font-medium">{t("confirmerMotDePasse")}</label>
                  <Input id="df-mdp" type="password" autoComplete="current-password" value={motDePasse} onChange={(e) => setMotDePasse(e.target.value)} />
                </div>
                <div className="flex gap-2">
                  <Button type="submit" variant="accent" disabled={envoi || motDePasse === ""}>
                    {etape === "motDePasse" ? t("envoyerCode") : t("desactiver")}
                  </Button>
                  <Button type="button" variant="ghost" onClick={annuler}>{t("annuler")}</Button>
                </div>
              </form>
            ) : null}

            {etape === "code" ? (
              <form
                className="flex flex-col gap-3"
                onSubmit={(e) => {
                  e.preventDefault();
                  void activer();
                }}
              >
                <p role="status" className="text-sm">{t("codeEnvoye", { telephone: moi.telephone ?? "" })}</p>
                <div className="flex flex-col gap-1">
                  <label htmlFor="df-code" className="text-sm font-medium">{t("code")}</label>
                  <Input id="df-code" inputMode="numeric" autoComplete="one-time-code" maxLength={6} value={code} onChange={(e) => setCode(e.target.value.replace(/\D/g, ""))} className="w-40 font-mono tracking-widest" />
                </div>
                <div className="flex gap-2">
                  <Button type="submit" variant="accent" disabled={envoi || code.length !== 6}>{t("confirmer")}</Button>
                  <Button type="button" variant="ghost" onClick={annuler}>{t("annuler")}</Button>
                </div>
              </form>
            ) : null}
          </>
        )}
        {erreur ? <p role="alert" className="text-sm text-destructive">{erreur}</p> : null}
      </CardContent>
    </Card>
  );
}
