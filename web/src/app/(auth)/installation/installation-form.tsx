"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { useMemo, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { z } from "zod";

import { CopyButton } from "@/components/copy-button";
import { LicenceFileField } from "@/components/licence-file-field";
import { TextField } from "@/components/text-field";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { FieldDescription, FieldGroup } from "@/components/ui/field";
import { useErrorMessage } from "@/hooks/use-error-message";
import { ApiError, enApiError } from "@/lib/api/errors";

export function InstallationForm({ empreinte }: { empreinte: string | null }) {
  const t = useTranslations("installation");
  const message = useErrorMessage();
  const router = useRouter();
  const [erreur, setErreur] = useState<string | null>(null);

  const schema = useMemo(
    () =>
      z
        .object({
          licence: z.string().trim().min(1, t("licenceRequise")),
          nom: z.string().trim().min(1, t("nomRequis")),
          telephone: z.string().trim(),
          email: z.string().trim(),
          motDePasse: z.string().min(12, t("motDePasseCourt")),
          confirmation: z.string(),
        })
        .refine((v) => v.telephone !== "" || v.email !== "", { path: ["telephone"], message: t("contactRequis") })
        .refine((v) => v.motDePasse === v.confirmation, { path: ["confirmation"], message: t("confirmationDifferente") }),
    [t],
  );
  type Valeurs = z.infer<typeof schema>;

  const form = useForm<Valeurs>({
    resolver: zodResolver(schema),
    defaultValues: { licence: "", nom: "", telephone: "", email: "", motDePasse: "", confirmation: "" },
  });

  async function installer(v: Valeurs) {
    setErreur(null);
    try {
      let reponse: Response;
      try {
        reponse = await fetch("/api/installation", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            licence: v.licence,
            admin: {
              nom: v.nom,
              telephone: v.telephone || undefined,
              email: v.email || undefined,
              motDePasse: v.motDePasse,
            },
          }),
        });
      } catch {
        throw enApiError(null, 0);
      }
      if (!reponse.ok) throw enApiError(await reponse.json().catch(() => null), reponse.status);
      router.replace("/login?raison=installe");
      router.refresh();
    } catch (e) {
      if (e instanceof ApiError && e.code === "INSTALLATION_DEJA_FAITE") {
        router.replace("/login");
        return;
      }
      setErreur(message(e));
    }
  }

  return (
    <form onSubmit={form.handleSubmit(installer)} noValidate className="flex flex-col gap-6">
      <Card>
        <CardHeader>
          <CardTitle>{t("licenceTitre")}</CardTitle>
          <CardDescription>{t("empreinteAide")}</CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col gap-6">
          <div className="flex flex-col gap-2">
            <span className="text-sm font-medium">{t("empreinteTitre")}</span>
            {empreinte ? (
              <div className="flex items-center gap-2">
                <code data-testid="empreinte" className="min-w-0 flex-1 break-all rounded bg-muted px-2 py-1 text-xs">
                  {empreinte}
                </code>
                <CopyButton texte={empreinte} namespace="installation" />
              </div>
            ) : (
              <p className="text-sm text-destructive">{t("empreinteIndisponible")}</p>
            )}
          </div>
          <Controller
            control={form.control}
            name="licence"
            render={({ field, fieldState }) => (
              <LicenceFileField
                valeur={field.value}
                onChange={(contenu) => form.setValue("licence", contenu, { shouldValidate: true })}
                erreur={fieldState.error?.message}
                libelleFichier={t("fichierLicence")}
                libelleContenu={t("contenuLicence")}
                aide={t("contenuLicenceAide")}
                onFichierTropGros={() => {
                  form.setValue("licence", "");
                  form.setError("licence", { message: t("fichierTropGros") });
                }}
              />
            )}
          />
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>{t("adminTitre")}</CardTitle>
          <CardDescription>{t("contactAide")}</CardDescription>
        </CardHeader>
        <CardContent>
          <FieldGroup>
            <TextField control={form.control} name="nom" label={t("nom")} autoComplete="name" />
            <TextField control={form.control} name="telephone" label={t("telephone")} autoComplete="tel" inputMode="tel" />
            <TextField control={form.control} name="email" label={t("email")} type="email" autoComplete="email" />
            <TextField
              control={form.control}
              name="motDePasse"
              label={t("motDePasse")}
              type="password"
              autoComplete="new-password"
            />
            <TextField
              control={form.control}
              name="confirmation"
              label={t("confirmation")}
              type="password"
              autoComplete="new-password"
            />
            <FieldDescription>{t("motDePasseCourt")}</FieldDescription>
            {erreur ? (
              <p role="alert" className="text-sm text-destructive">
                {erreur}
              </p>
            ) : null}
            <Button type="submit" disabled={form.formState.isSubmitting}>
              {form.formState.isSubmitting ? t("installationEnCours") : t("installer")}
            </Button>
          </FieldGroup>
        </CardContent>
      </Card>
    </form>
  );
}
