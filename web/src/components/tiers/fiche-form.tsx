"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { useMemo, useState } from "react";
import { Controller, useForm, type Resolver } from "react-hook-form";
import { z } from "zod";

import { SelectField } from "@/components/patrimoine/select-field";
import { TextField } from "@/components/text-field";
import { Button } from "@/components/ui/button";
import { Field, FieldDescription, FieldGroup, FieldLabel } from "@/components/ui/field";
import { Textarea } from "@/components/ui/textarea";
import { useErrorMessage } from "@/hooks/use-error-message";

export type ChampConfig = {
  nom: string;
  /** Libellé déjà traduit. */
  libelle: string;
  type?: "text" | "email" | "tel" | "date" | "textarea" | "select";
  requis?: boolean;
  options?: { valeur: string; libelle: string }[];
  aide?: string;
};

type Props = {
  champs: ChampConfig[];
  valeurs?: Record<string, string>;
  /** Exige un téléphone ou un e-mail (propriétaires, locataires, prestataires). */
  contactRequis?: boolean;
  /** Crée ou modifie ; lève l'erreur de l'API en cas d'échec. */
  onSubmit: (valeurs: Record<string, string>) => Promise<void>;
  onAnnuler?: () => void;
};

/** Formulaire de fiche piloté par une configuration de champs (propriétaires, locataires, prestataires, garants). */
export function FicheForm({ champs, valeurs = {}, contactRequis = false, onSubmit, onAnnuler }: Props) {
  const t = useTranslations("tiers");
  const message = useErrorMessage();
  const router = useRouter();
  const [erreur, setErreur] = useState<string | null>(null);

  const schema = useMemo(() => {
    const forme: Record<string, z.ZodType<string>> = {};
    for (const c of champs) {
      let regle = z.string().trim();
      if (c.requis) regle = regle.min(1, c.nom === "telephone" ? t("telephoneRequis") : t("nomRequis"));
      forme[c.nom] = regle;
    }
    return z.object(forme).superRefine((v, ctx) => {
      const email = (v.email ?? "").trim();
      if (email !== "" && !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email)) {
        ctx.addIssue({ code: "custom", path: ["email"], message: t("emailInvalide") });
      }
      if (contactRequis && (v.telephone ?? "").trim() === "" && email === "") {
        ctx.addIssue({ code: "custom", path: ["telephone"], message: t("contactRequis") });
      }
    });
  }, [champs, contactRequis, t]);

  const form = useForm<Record<string, string>>({
    // Schéma construit dynamiquement depuis la configuration des champs : les valeurs sont toutes des chaînes
    resolver: zodResolver(schema) as unknown as Resolver<Record<string, string>>,
    defaultValues: Object.fromEntries(champs.map((c) => [c.nom, valeurs[c.nom] ?? c.options?.[0]?.valeur ?? ""])),
  });

  async function soumettre(v: Record<string, string>) {
    setErreur(null);
    try {
      await onSubmit(v);
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <form onSubmit={form.handleSubmit(soumettre)} noValidate className="max-w-3xl rounded-xl border bg-card p-6 shadow-carte">
      <FieldGroup>
        <div className="grid gap-4 sm:grid-cols-2">
        {champs.map((c, index) => {
          if (c.type === "select") {
            return <SelectField key={c.nom} control={form.control} name={c.nom} label={c.libelle} options={c.options ?? []} />;
          }
          if (c.type === "textarea") {
            return (
              <Controller
                key={c.nom}
                control={form.control}
                name={c.nom}
                render={({ field }) => (
                  <Field className="sm:col-span-2">
                    <FieldLabel htmlFor={c.nom}>{c.libelle}</FieldLabel>
                    <Textarea {...field} id={c.nom} rows={3} />
                    {c.aide ? <FieldDescription>{c.aide}</FieldDescription> : null}
                  </Field>
                )}
              />
            );
          }
          return (
            <div key={c.nom} className={c.nom === "nom" || c.nom === "adresse" ? "sm:col-span-2" : undefined}>
              <TextField control={form.control} name={c.nom} label={c.libelle} type={c.type ?? "text"} autoFocus={index === 0} />
              {c.aide ? <FieldDescription className="mt-1">{c.aide}</FieldDescription> : null}
            </div>
          );
        })}
        </div>
        {erreur ? (
          <p role="alert" className="text-sm text-destructive">
            {erreur}
          </p>
        ) : null}
        <div className="flex gap-2 border-t pt-4">
          <Button type="submit" disabled={form.formState.isSubmitting}>
            {form.formState.isSubmitting ? t("enregistrement") : t("enregistrer")}
          </Button>
          <Button type="button" variant="outline" onClick={onAnnuler ?? (() => router.back())}>
            {t("annuler")}
          </Button>
        </div>
      </FieldGroup>
    </form>
  );
}
