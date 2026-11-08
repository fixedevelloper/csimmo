"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import { Dices } from "lucide-react";
import { useTranslations } from "next-intl";
import { useMemo, useState } from "react";
import { useForm, useWatch } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

import { ChoixImmeubles } from "@/components/equipe/choix-immeubles";
import { TextField } from "@/components/text-field";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { FieldGroup } from "@/components/ui/field";
import { NativeSelect } from "@/components/ui/native-select";
import { useErrorMessage } from "@/hooks/use-error-message";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { optionnel } from "@/lib/tiers";
import { estRestreint, genererMotDePasse, ROLES_EMPLOYES } from "@/lib/utilisateurs";

type Props = {
  /** `avertissement` : le compte existe mais l'étape suivante (affectation des immeubles) a échoué. */
  onCree: (identifiant: string, motDePasse: string, avertissement?: string) => void;
  onAnnuler: () => void;
};

/** Création d'un employé : encaisseur (immeubles affectés) ou gestionnaire à accès total. Double authentification toujours activée. */
export function FormulaireEmploye({ onCree, onAnnuler }: Props) {
  const t = useTranslations("equipe");
  const tu = useTranslations("utilisateurs");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [erreur, setErreur] = useState<string | null>(null);
  const [immeubles, setImmeubles] = useState<string[]>([]);

  const schema = useMemo(
    () =>
      z.object({
        nom: z.string().trim().min(1, tu("erreurs.nom")).max(200),
        telephone: z.string().trim().max(30),
        email: z.string().trim().max(200).refine((v) => v === "" || /^\S+@\S+\.\S+$/.test(v), tu("erreurs.email")),
        role: z.enum(ROLES_EMPLOYES),
        motDePasse: z.string().min(12, tu("erreurs.motDePasse")).max(100, tu("erreurs.motDePasse")),
      })
      // un contact suffit (téléphone ou e-mail) : le second facteur vient d'une application, plus d'un SMS
      .refine((v) => v.telephone !== "" || v.email !== "", { path: ["telephone"], message: tu("erreurs.contact") }),
    [t, tu],
  );
  type Valeurs = z.infer<typeof schema>;
  const form = useForm<Valeurs>({
    resolver: zodResolver(schema),
    defaultValues: { nom: "", telephone: "", email: "", role: "ENCAISSEUR", motDePasse: "" },
  });
  const role = useWatch({ control: form.control, name: "role" });

  async function soumettre(v: Valeurs) {
    setErreur(null);
    let id: string | undefined;
    try {
      const cree = deballer(
        await api.POST("/api/v1/utilisateurs", {
          body: { nom: v.nom, telephone: optionnel(v.telephone), email: optionnel(v.email), role: v.role, motDePasse: v.motDePasse, deuxFacteursActif: false },
        }),
      );
      id = cree.id;
    } catch (e) {
      setErreur(message(e));
      return;
    }
    let avertissement: string | undefined;
    if (estRestreint(v.role) && immeubles.length > 0) {
      try {
        deballer(await api.PUT("/api/v1/utilisateurs/{id}/affectations", { params: { path: { id: id ?? "" } }, body: { immeubleIds: immeubles } }));
      } catch (e) {
        // le compte existe : on le signale pour ne pas le recréer, les immeubles se règlent depuis la liste
        avertissement = `${t("affectationEchouee")} ${message(e)}`;
      }
    }
    await queryClient.invalidateQueries({ queryKey: ["utilisateurs"] });
    await queryClient.invalidateQueries({ queryKey: ["affectations"] });
    toast.success(tu("cree"));
    onCree(optionnel(v.telephone) ?? optionnel(v.email) ?? v.nom, v.motDePasse, avertissement);
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("nouveau")}</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={(e) => void form.handleSubmit(soumettre)(e)} noValidate className="flex max-w-xl flex-col gap-4">
          <FieldGroup>
            <TextField control={form.control} name="nom" label={tu("nom")} autoComplete="off" autoFocus />
            <TextField control={form.control} name="telephone" label={tu("telephone")} inputMode="tel" autoComplete="off" />
            <TextField control={form.control} name="email" label={tu("email")} type="email" autoComplete="off" />
            <div className="flex flex-col gap-1">
              <label htmlFor="e-role" className="text-sm font-medium">{tu("role")}</label>
              <NativeSelect id="e-role" {...form.register("role")}>
                {ROLES_EMPLOYES.map((r) => (
                  <option key={r} value={r}>{t(`roles.${r}`)}</option>
                ))}
              </NativeSelect>
              <p className="text-xs text-muted-foreground">{t(`roleAide.${role}`)}</p>
            </div>
            <TextField control={form.control} name="motDePasse" label={tu("motDePasse")} type="password" autoComplete="new-password" />
          </FieldGroup>
          <div>
            <Button type="button" variant="outline" size="sm" onClick={() => form.setValue("motDePasse", genererMotDePasse(), { shouldValidate: true, shouldDirty: true })}>
              <Dices className="size-4" aria-hidden />
              {tu("generer")}
            </Button>
            <p className="mt-1 text-xs text-muted-foreground">{tu("genererAide")}</p>
          </div>
          {estRestreint(role) ? <ChoixImmeubles valeur={immeubles} onChange={setImmeubles} /> : null}
          <p className="text-xs text-muted-foreground">{t("doubleAuth")}</p>
          {erreur ? <p role="alert" className="text-sm text-destructive">{erreur}</p> : null}
          <div className="flex gap-2">
            <Button type="submit" variant="accent" disabled={form.formState.isSubmitting}>{t("creer")}</Button>
            <Button type="button" variant="ghost" onClick={onAnnuler}>{tu("annuler")}</Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}
