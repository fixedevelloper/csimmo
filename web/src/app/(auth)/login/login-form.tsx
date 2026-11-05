"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslations } from "next-intl";
import { useRouter, useSearchParams } from "next/navigation";
import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";

import { TextField } from "@/components/text-field";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { FieldGroup } from "@/components/ui/field";
import { useErrorMessage } from "@/hooks/use-error-message";
import { enApiError } from "@/lib/api/errors";

/** N'autorise qu'un chemin interne comme destination après connexion (pas de redirection ouverte). */
function destinationSure(next: string | null): string {
  return next && next.startsWith("/") && !next.startsWith("//") && !next.startsWith("/login") ? next : "/tableau-de-bord";
}

async function appeler(url: string, corps: unknown): Promise<{ deuxFacteursRequis: boolean }> {
  let reponse: Response;
  try {
    reponse = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(corps),
    });
  } catch {
    throw enApiError(null, 0);
  }
  const donnees: unknown = await reponse.json().catch(() => null);
  if (!reponse.ok) throw enApiError(donnees, reponse.status);
  return donnees as { deuxFacteursRequis: boolean };
}

export function LoginForm() {
  const t = useTranslations("auth");
  const message = useErrorMessage();
  const router = useRouter();
  const params = useSearchParams();
  const [etape, setEtape] = useState<"identifiants" | "otp">("identifiants");
  const [erreur, setErreur] = useState<string | null>(null);

  const schemaIdentifiants = useMemo(
    () =>
      z.object({
        identifiant: z.string().trim().min(1, t("identifiantRequis")),
        motDePasse: z.string().min(1, t("motDePasseRequis")),
      }),
    [t],
  );
  const schemaOtp = useMemo(() => z.object({ code: z.string().regex(/^\d{6}$/, t("codeInvalide")) }), [t]);

  const formIdentifiants = useForm({
    resolver: zodResolver(schemaIdentifiants),
    defaultValues: { identifiant: "", motDePasse: "" },
  });
  const formOtp = useForm({ resolver: zodResolver(schemaOtp), defaultValues: { code: "" } });

  const raison = params.get("raison");
  const info =
    raison === "inactivite"
      ? t("inactivite")
      : raison === "session"
        ? t("sessionExpiree")
        : raison === "installe"
          ? t("installationTerminee")
          : null;

  function termine() {
    router.replace(destinationSure(params.get("next")));
    router.refresh();
  }

  async function soumettreIdentifiants(valeurs: z.infer<typeof schemaIdentifiants>) {
    setErreur(null);
    try {
      const reponse = await appeler("/api/auth/login", valeurs);
      if (reponse.deuxFacteursRequis) setEtape("otp");
      else termine();
    } catch (e) {
      setErreur(message(e));
    }
  }

  async function soumettreOtp(valeurs: z.infer<typeof schemaOtp>) {
    setErreur(null);
    try {
      await appeler("/api/auth/otp", { identifiant: formIdentifiants.getValues("identifiant"), code: valeurs.code });
      termine();
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <Card className="w-full max-w-sm">
      <CardHeader>
        <CardTitle role="heading" aria-level={1}>
          {t("titre")}
        </CardTitle>
        {etape === "otp" ? <CardDescription>{t("deuxFacteursInfo")}</CardDescription> : null}
        {info && etape === "identifiants" ? <CardDescription role="status">{info}</CardDescription> : null}
      </CardHeader>
      <CardContent>
        {etape === "identifiants" ? (
          <form onSubmit={formIdentifiants.handleSubmit(soumettreIdentifiants)} noValidate>
            <FieldGroup>
              <TextField
                control={formIdentifiants.control}
                name="identifiant"
                label={t("identifiant")}
                autoComplete="username"
                autoFocus
              />
              <TextField
                control={formIdentifiants.control}
                name="motDePasse"
                label={t("motDePasse")}
                type="password"
                autoComplete="current-password"
              />
              {erreur ? (
                <p role="alert" className="text-sm text-destructive">
                  {erreur}
                </p>
              ) : null}
              <Button type="submit" disabled={formIdentifiants.formState.isSubmitting}>
                {formIdentifiants.formState.isSubmitting ? t("connexionEnCours") : t("seConnecter")}
              </Button>
            </FieldGroup>
          </form>
        ) : (
          <form onSubmit={formOtp.handleSubmit(soumettreOtp)} noValidate>
            <FieldGroup>
              <TextField
                control={formOtp.control}
                name="code"
                label={t("codeOtp")}
                inputMode="numeric"
                autoComplete="one-time-code"
                autoFocus
              />
              {erreur ? (
                <p role="alert" className="text-sm text-destructive">
                  {erreur}
                </p>
              ) : null}
              <Button type="submit" disabled={formOtp.formState.isSubmitting}>
                {formOtp.formState.isSubmitting ? t("verificationEnCours") : t("verifier")}
              </Button>
              <Button
                type="button"
                variant="ghost"
                onClick={() => {
                  setErreur(null);
                  formOtp.reset();
                  setEtape("identifiants");
                }}
              >
                {t("retour")}
              </Button>
            </FieldGroup>
          </form>
        )}
      </CardContent>
    </Card>
  );
}
