"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

import { TextField } from "@/components/text-field";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { FieldGroup } from "@/components/ui/field";
import { useErrorMessage } from "@/hooks/use-error-message";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import type { components } from "@/lib/api/schema";
import { optionnel } from "@/lib/tiers";

type Utilisateur = components["schemas"]["UtilisateurDto"];

/** Nom, téléphone et e-mail de l'utilisateur connecté ; le mot de passe actuel est exigé (le téléphone reçoit les codes de connexion). */
export function InfosPersonnelles({ moi }: { moi: Utilisateur }) {
  const t = useTranslations("profil.infos");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [erreur, setErreur] = useState<string | null>(null);

  const schema = useMemo(
    () =>
      z
        .object({
          nom: z.string().trim().min(1, t("erreurs.nom")).max(200),
          telephone: z.string().trim().max(30),
          email: z.string().trim().max(200).refine((v) => v === "" || /^\S+@\S+\.\S+$/.test(v), t("erreurs.email")),
          motDePasse: z.string().min(1, t("erreurs.motDePasse")),
        })
        .refine((v) => v.telephone !== "" || v.email !== "", { path: ["telephone"], message: t("erreurs.contact") }),
    [t],
  );
  type Valeurs = z.infer<typeof schema>;
  const form = useForm<Valeurs>({
    resolver: zodResolver(schema),
    defaultValues: { nom: moi.nom ?? "", telephone: moi.telephone ?? "", email: moi.email ?? "", motDePasse: "" },
  });

  async function soumettre(v: Valeurs) {
    setErreur(null);
    try {
      deballer(
        await api.PUT("/api/v1/auth/moi", {
          body: { nom: v.nom, telephone: optionnel(v.telephone), email: optionnel(v.email), motDePasse: v.motDePasse },
        }),
      );
      await queryClient.invalidateQueries({ queryKey: ["moi"] });
      form.setValue("motDePasse", "");
      toast.success(t("enregistre"));
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("titre")}</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={(e) => void form.handleSubmit(soumettre)(e)} noValidate className="flex max-w-xl flex-col gap-4">
          <p className="text-sm text-muted-foreground">{t("aide")}</p>
          <FieldGroup>
            <TextField control={form.control} name="nom" label={t("nom")} autoComplete="name" />
            <TextField control={form.control} name="telephone" label={t("telephone")} inputMode="tel" autoComplete="tel" />
            <TextField control={form.control} name="email" label={t("email")} type="email" autoComplete="email" />
            <TextField control={form.control} name="motDePasse" label={t("motDePasseActuel")} type="password" autoComplete="current-password" />
          </FieldGroup>
          {erreur ? <p role="alert" className="text-sm text-destructive">{erreur}</p> : null}
          <div>
            <Button type="submit" variant="accent" disabled={form.formState.isSubmitting}>{t("enregistrer")}</Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}
