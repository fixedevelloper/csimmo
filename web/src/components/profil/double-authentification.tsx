"use client";

import { useQueryClient } from "@tanstack/react-query";
import { ShieldCheck, ShieldOff } from "lucide-react";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { ConfigurationApplication } from "@/components/auth/configuration-application";
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
type Etape = "repos" | "motDePasse" | "configuration" | "desactiver";

/**
 * Double authentification par application (Google Authenticator, Microsoft Authenticator, Authy…) : on la configure en scannant un code QR puis
 * en saisissant le premier code ; on la désactive avec le mot de passe et un code. Obligatoire pour les employés restreints.
 */
export function DoubleAuthentification({ moi }: { moi: Utilisateur }) {
  const t = useTranslations("profil.deuxFacteurs");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [etape, setEtape] = useState<Etape>("repos");
  const [motDePasse, setMotDePasse] = useState("");
  const [code, setCode] = useState("");
  const [enrolement, setEnrolement] = useState<{ secret: string; uri: string } | null>(null);
  const [erreur, setErreur] = useState<string | null>(null);
  const [envoi, setEnvoi] = useState(false);

  const concerne = ROLES_MOT_DE_PASSE_OBLIGATOIRE.includes((moi.role ?? "") as RoleUtilisateur);
  const obligatoire = ROLES_RESTREINTS.includes(moi.role ?? "");
  const active = moi.deuxFacteursActif === true;
  const configuree = active && moi.deuxFacteursConfigure === true;

  function annuler() {
    setEtape("repos");
    setMotDePasse("");
    setCode("");
    setEnrolement(null);
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

  const demarrer = () =>
    agir(async () => {
      const e = deballer(await api.POST("/api/v1/auth/moi/double-authentification/demarrer", { body: { motDePasse } }));
      setEnrolement({ secret: e.secret ?? "", uri: e.uri ?? "" });
      setMotDePasse("");
      setEtape("configuration");
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
      deballer(await api.POST("/api/v1/auth/moi/double-authentification/desactiver", { body: { motDePasse, code } }));
      await queryClient.invalidateQueries({ queryKey: ["moi"] });
      toast.success(t("desactivee"));
      annuler();
    });

  const statut = !active ? { code: "ANNULE", libelle: t("inactive") } : configuree ? { code: "ACTIF", libelle: t("active") } : { code: "EN_ATTENTE", libelle: t("aConfigurer") };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          {configuree ? <ShieldCheck className="size-4 text-primary" aria-hidden /> : <ShieldOff className="size-4 text-muted-foreground" aria-hidden />}
          {t("titre")}
          {concerne ? <StatutPastille statut={statut.code} libelle={statut.libelle} /> : null}
        </CardTitle>
      </CardHeader>
      <CardContent className="flex max-w-xl flex-col gap-3">
        {!concerne ? (
          <p className="text-sm text-muted-foreground">{t("nonConcerne")}</p>
        ) : (
          <>
            <p className="text-sm text-muted-foreground">{configuree ? t("aideActive") : active ? t("aideAConfigurer") : t("aideInactive")}</p>
            {obligatoire ? <p className="text-sm font-medium">{t("obligatoire")}</p> : null}

            {etape === "repos" ? (
              <div className="flex flex-wrap gap-2">
                {!configuree ? (
                  <Button type="button" variant="accent" onClick={() => setEtape("motDePasse")}>
                    {active ? t("configurerMaintenant") : t("configurer")}
                  </Button>
                ) : null}
                {configuree && !obligatoire ? (
                  <Button type="button" variant="outline" onClick={() => setEtape("desactiver")}>
                    {t("desactiver")}
                  </Button>
                ) : null}
              </div>
            ) : null}

            {etape === "motDePasse" || etape === "desactiver" ? (
              <form
                className="flex flex-col gap-3"
                onSubmit={(e) => {
                  e.preventDefault();
                  void (etape === "motDePasse" ? demarrer() : desactiver());
                }}
              >
                <div className="flex flex-col gap-1">
                  <label htmlFor="df-mdp" className="text-sm font-medium">{t("confirmerMotDePasse")}</label>
                  <Input id="df-mdp" type="password" autoComplete="current-password" value={motDePasse} onChange={(e) => setMotDePasse(e.target.value)} />
                </div>
                {etape === "desactiver" ? (
                  <div className="flex flex-col gap-1">
                    <label htmlFor="df-code-desactiver" className="text-sm font-medium">{t("codeApplication")}</label>
                    <Input id="df-code-desactiver" inputMode="numeric" autoComplete="one-time-code" maxLength={6} value={code} onChange={(e) => setCode(e.target.value.replace(/\D/g, ""))} className="w-40 font-mono tracking-widest" />
                  </div>
                ) : null}
                <div className="flex gap-2">
                  <Button type="submit" variant="accent" disabled={envoi || motDePasse === "" || (etape === "desactiver" && code.length !== 6)}>
                    {etape === "motDePasse" ? t("continuer") : t("desactiver")}
                  </Button>
                  <Button type="button" variant="ghost" onClick={annuler}>{t("annuler")}</Button>
                </div>
              </form>
            ) : null}

            {etape === "configuration" && enrolement ? (
              <form
                className="flex flex-col gap-3"
                onSubmit={(e) => {
                  e.preventDefault();
                  void activer();
                }}
              >
                <ConfigurationApplication secret={enrolement.secret} uri={enrolement.uri} />
                <div className="flex flex-col gap-1">
                  <label htmlFor="df-code" className="text-sm font-medium">{t("codeApplication")}</label>
                  <Input id="df-code" inputMode="numeric" autoComplete="one-time-code" maxLength={6} value={code} onChange={(e) => setCode(e.target.value.replace(/\D/g, ""))} className="w-40 font-mono tracking-widest" />
                </div>
                <div className="flex gap-2">
                  <Button type="submit" variant="accent" disabled={envoi || code.length !== 6}>{t("confirmerActivation")}</Button>
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
