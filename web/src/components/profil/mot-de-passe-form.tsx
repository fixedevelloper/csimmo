"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
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

/** Changement du mot de passe : le serveur ferme ensuite toutes les sessions, on revient donc à la page de connexion. */
export function MotDePasseForm() {
  const t = useTranslations("profil.motDePasse");
  const message = useErrorMessage();
  const router = useRouter();
  const [erreur, setErreur] = useState<string | null>(null);

  const schema = useMemo(
    () =>
      z
        .object({
          ancien: z.string().min(1, t("erreurs.actuel")),
          nouveau: z.string().min(12, t("erreurs.court")).max(100, t("erreurs.court")),
          confirmation: z.string(),
        })
        .refine((v) => v.nouveau === v.confirmation, { path: ["confirmation"], message: t("erreurs.different") })
        .refine((v) => v.nouveau !== v.ancien, { path: ["nouveau"], message: t("erreurs.identique") }),
    [t],
  );
  type Valeurs = z.infer<typeof schema>;
  const form = useForm<Valeurs>({ resolver: zodResolver(schema), defaultValues: { ancien: "", nouveau: "", confirmation: "" } });

  async function soumettre(v: Valeurs) {
    setErreur(null);
    try {
      deballer(await api.POST("/api/v1/auth/mot-de-passe", { body: { ancienMotDePasse: v.ancien, nouveauMotDePasse: v.nouveau } }));
      toast.success(t("change"));
      await fetch("/api/auth/logout", { method: "POST" }).catch(() => undefined);
      router.replace("/login?raison=mot-de-passe");
      router.refresh();
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
            <TextField control={form.control} name="ancien" label={t("actuel")} type="password" autoComplete="current-password" />
            <TextField control={form.control} name="nouveau" label={t("nouveau")} type="password" autoComplete="new-password" />
            <TextField control={form.control} name="confirmation" label={t("confirmation")} type="password" autoComplete="new-password" />
          </FieldGroup>
          {erreur ? <p role="alert" className="text-sm text-destructive">{erreur}</p> : null}
          <div>
            <Button type="submit" variant="accent" disabled={form.formState.isSubmitting}>{t("changer")}</Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}
