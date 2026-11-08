
"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslations } from "next-intl";
import { useRouter, useSearchParams } from "next/navigation";
import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import {
  ArrowRight,
  ShieldCheck,
  LockKeyhole,
  Fingerprint,
  Sparkles,
  Shield,
} from "lucide-react";

import { ConfigurationApplication } from "@/components/auth/configuration-application";
import { TextField } from "@/components/text-field";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { FieldGroup } from "@/components/ui/field";
import { useErrorMessage } from "@/hooks/use-error-message";
import { enApiError } from "@/lib/api/errors";

function destinationSure(next: string | null): string {
  return next &&
  next.startsWith("/") &&
  !next.startsWith("//") &&
  !next.startsWith("/login")
    ? next
    : "/tableau-de-bord";
}

/** Réponse d'une étape de connexion : session ouverte, ou second facteur / application à configurer (avec le défi à renvoyer). */
type ReponseEtape = { deuxFacteursRequis?: boolean; enrolementRequis?: boolean; defi?: string; secret?: string; uri?: string };

async function appeler(
  url: string,
  corps: unknown,
): Promise<ReponseEtape> {
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

  return donnees as ReponseEtape;
}

export function LoginForm() {
  const t = useTranslations("auth");
  const message = useErrorMessage();
  const router = useRouter();
  const params = useSearchParams();

  const [etape, setEtape] = useState<"identifiants" | "code" | "enrolement">(
    "identifiants",
  );
  const [erreur, setErreur] = useState<string | null>(null);
  const [defi, setDefi] = useState("");
  const [enrolement, setEnrolement] = useState<{ secret: string; uri: string } | null>(null);

  const schemaIdentifiants = useMemo(
    () =>
      z.object({
        identifiant: z.string().trim().min(1, t("identifiantRequis")),
        motDePasse: z.string().min(1, t("motDePasseRequis")),
      }),
    [t],
  );

  const schemaOtp = useMemo(
    () =>
      z.object({
        code: z.string().regex(/^\d{6}$/, t("codeInvalide")),
      }),
    [t],
  );

  const formIdentifiants = useForm({
    resolver: zodResolver(schemaIdentifiants),
    defaultValues: { identifiant: "", motDePasse: "" },
  });

  const formOtp = useForm({
    resolver: zodResolver(schemaOtp),
    defaultValues: { code: "" },
  });

  const raison = params.get("raison");

  const info =
    raison === "inactivite"
      ? t("inactivite")
      : raison === "session"
        ? t("sessionExpiree")
        : raison === "installe"
          ? t("installationTerminee")
          : raison === "mot-de-passe"
            ? t("motDePasseChange")
            : null;

  function termine() {
    router.replace(destinationSure(params.get("next")));
    router.refresh();
  }

  async function soumettreIdentifiants(
    valeurs: z.infer<typeof schemaIdentifiants>,
  ) {
    setErreur(null);

    try {
      const reponse = await appeler("/api/auth/login", valeurs);

      if (reponse.deuxFacteursRequis && reponse.defi) {
        setDefi(reponse.defi);
        setEtape("code");
      } else if (reponse.enrolementRequis && reponse.defi) {
        // première connexion d'un compte qui doit avoir une application d'authentification : on la configure avant d'ouvrir la session
        const e = await appeler("/api/auth/enrolement", { defi: reponse.defi });
        setDefi(reponse.defi);
        setEnrolement({ secret: e.secret ?? "", uri: e.uri ?? "" });
        setEtape("enrolement");
      } else {
        termine();
      }
    } catch (e) {
      setErreur(message(e));
    }
  }

  async function soumettreCode(
    valeurs: z.infer<typeof schemaOtp>,
  ) {
    setErreur(null);

    try {
      await appeler(etape === "enrolement" ? "/api/auth/enrolement/confirmer" : "/api/auth/deux-facteurs", {
        defi,
        code: valeurs.code,
      });

      termine();
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <div className="relative w-full max-w-md">

      {/* Effet lumineux en arrière-plan */}
      <div className="absolute -inset-3 rounded-[2rem] bg-primary/10 blur-2xl" />

      <Card className="relative overflow-hidden rounded-2xl border border-border/60 bg-card shadow-2xl shadow-black/5">

        {/* Barre supérieure */}
        <div className="h-1 w-full bg-gradient-to-r from-primary/40 via-primary to-primary/40" />

        <CardHeader className="space-y-5 px-7 pt-9 sm:px-9">

          {/* Logo */}
          <div className="flex items-center justify-between">
            <div className="flex size-14 items-center justify-center rounded-2xl bg-primary text-primary-foreground shadow-lg shadow-primary/20">
              {etape !== "identifiants" ? (
                <Fingerprint className="size-7" />
              ) : (
                <ShieldCheck className="size-7" />
              )}
            </div>

            <div className="flex items-center gap-2 rounded-full border border-border/60 bg-muted/50 px-3 py-1.5">
              <span className="size-2 rounded-full bg-emerald-500" />
              <span className="text-xs font-medium text-muted-foreground">
                Accès sécurisé
              </span>
            </div>
          </div>

          {/* Titre */}
          <div className="space-y-2">
            <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-[0.18em] text-primary">
              <Sparkles className="size-3.5" />
              {etape !== "identifiants" ? "Double authentification" : "Espace sécurisé"}
            </div>

            <CardTitle className="text-3xl font-bold tracking-tight">
              {etape !== "identifiants"
                ? t("codeOtp")
                : t("titre")}
            </CardTitle>

            <CardDescription className="text-sm leading-relaxed">
              {etape !== "identifiants"
                ? t("deuxFacteursInfo")
                : "Connectez-vous à votre espace personnel pour accéder à votre tableau de bord."}
            </CardDescription>
          </div>

          {/* Message informatif */}
          {info && etape === "identifiants" && (
            <div
              role="status"
              className="rounded-xl border border-primary/20 bg-primary/5 px-4 py-3 text-sm text-foreground"
            >
              {info}
            </div>
          )}

        </CardHeader>

        <CardContent className="px-7 pb-8 sm:px-9">

          <div className="mb-6 mt-2 h-px bg-border/70" />

          {etape === "identifiants" ? (
            <form
              onSubmit={formIdentifiants.handleSubmit(soumettreIdentifiants)}
              noValidate
              className="space-y-5"
            >
              <FieldGroup className="gap-5">

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

              </FieldGroup>

              {erreur && (
                <div
                  role="alert"
                  className="rounded-xl border border-destructive/20 bg-destructive/5 px-4 py-3 text-sm text-destructive"
                >
                  {erreur}
                </div>
              )}

              <Button
                type="submit"
                disabled={formIdentifiants.formState.isSubmitting}
                className="group h-12 w-full rounded-xl text-sm font-semibold shadow-lg shadow-primary/20 transition-all hover:-translate-y-0.5 hover:shadow-xl"
              >
                {formIdentifiants.formState.isSubmitting
                  ? t("connexionEnCours")
                  : t("seConnecter")}

                {!formIdentifiants.formState.isSubmitting && (
                  <ArrowRight className="ml-2 size-4 transition-transform group-hover:translate-x-1" />
                )}
              </Button>

            </form>
          ) : (
            <form
              onSubmit={formOtp.handleSubmit(soumettreCode)}
              noValidate
              className="space-y-5"
            >
              <div className="rounded-xl border border-primary/15 bg-primary/5 p-4">
                <div className="flex gap-3">
                  <div className="flex size-10 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
                    <LockKeyhole className="size-5" />
                  </div>

                  <div className="space-y-1">
                    <p className="text-sm font-semibold">
                      {etape === "enrolement" ? t("enrolementTitre") : t("codeApplicationTitre")}
                    </p>
                    <p className="text-xs leading-relaxed text-muted-foreground">
                      {etape === "enrolement" ? t("enrolementAide") : t("codeApplicationAide")}
                    </p>
                  </div>
                </div>
              </div>

              {etape === "enrolement" && enrolement ? <ConfigurationApplication secret={enrolement.secret} uri={enrolement.uri} /> : null}

              <TextField
                control={formOtp.control}
                name="code"
                label={t("codeOtp")}
                inputMode="numeric"
                autoComplete="one-time-code"
                autoFocus
              />

              {erreur && (
                <div
                  role="alert"
                  className="rounded-xl border border-destructive/20 bg-destructive/5 px-4 py-3 text-sm text-destructive"
                >
                  {erreur}
                </div>
              )}

              <Button
                type="submit"
                disabled={formOtp.formState.isSubmitting}
                className="group h-12 w-full rounded-xl font-semibold shadow-lg shadow-primary/20"
              >
                {formOtp.formState.isSubmitting
                  ? t("verificationEnCours")
                  : etape === "enrolement"
                    ? t("enrolementConfirmer")
                    : t("verifier")}

                <ArrowRight className="ml-2 size-4" />
              </Button>

              <Button
                type="button"
                variant="ghost"
                className="h-11 w-full rounded-xl text-muted-foreground"
                onClick={() => {
                  setErreur(null);
                  formOtp.reset();
                  setDefi("");
                  setEnrolement(null);
                  setEtape("identifiants");
                }}
              >
                {t("retour")}
              </Button>
            </form>
          )}

          {/* Footer */}
          <div className="mt-8 flex items-center justify-center gap-2 border-t border-border/60 pt-5 text-xs text-muted-foreground">
            <Shield className="size-3.5" />
            <span>Connexion protégée et sécurisée</span>
          </div>

        </CardContent>
      </Card>
    </div>
  );
}
