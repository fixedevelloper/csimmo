"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { useMemo, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

import { SelectField } from "@/components/patrimoine/select-field";
import { TextField } from "@/components/text-field";
import { Button } from "@/components/ui/button";
import { Field, FieldDescription, FieldGroup, FieldLabel } from "@/components/ui/field";
import { Textarea } from "@/components/ui/textarea";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useProprietaires } from "@/hooks/use-tiers";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { nombreOptionnel, type ImmeubleDto } from "@/lib/patrimoine";

/** Création (sans `immeuble`) ou modification d'un immeuble. */
export function ImmeubleForm({ immeuble }: { immeuble?: ImmeubleDto }) {
  const t = useTranslations("patrimoine");
  const message = useErrorMessage();
  const router = useRouter();
  const queryClient = useQueryClient();
  const [erreur, setErreur] = useState<string | null>(null);
  const tiers = useTranslations("tiers");
  const proprietaires = useProprietaires("", 0, 100);

  const schema = useMemo(
    () =>
      z
        .object({
          nom: z.string().trim().min(1, t("nomRequis")),
          adresse: z.string().trim().min(1, t("adresseRequise")),
          quartier: z.string().trim(),
          ville: z.string().trim().min(1, t("villeRequise")),
          latitude: z.string().trim(),
          longitude: z.string().trim(),
          description: z.string().trim(),
          proprietaireTiersId: z.string(),
        })
        .superRefine((v, ctx) => {
          const lat = nombreOptionnel(v.latitude);
          const lon = nombreOptionnel(v.longitude);
          const ok =
            (lat === undefined) === (lon === undefined) &&
            (lat === undefined || (!Number.isNaN(lat) && Math.abs(lat) <= 90)) &&
            (lon === undefined || (!Number.isNaN(lon) && Math.abs(lon) <= 180));
          if (!ok) ctx.addIssue({ code: "custom", path: ["latitude"], message: t("gpsInvalide") });
        }),
    [t],
  );
  type Valeurs = z.infer<typeof schema>;

  const form = useForm<Valeurs>({
    resolver: zodResolver(schema),
    defaultValues: {
      nom: immeuble?.nom ?? "",
      adresse: immeuble?.adresse ?? "",
      quartier: immeuble?.quartier ?? "",
      ville: immeuble?.ville ?? "",
      latitude: immeuble?.latitude?.toString() ?? "",
      longitude: immeuble?.longitude?.toString() ?? "",
      description: immeuble?.description ?? "",
      proprietaireTiersId: immeuble?.proprietaireTiersId ?? "",
    },
  });

  async function enregistrer(v: Valeurs) {
    setErreur(null);
    const corps = {
      nom: v.nom,
      adresse: v.adresse,
      quartier: v.quartier || undefined,
      ville: v.ville,
      latitude: nombreOptionnel(v.latitude),
      longitude: nombreOptionnel(v.longitude),
      description: v.description || undefined,
      proprietaireTiersId: v.proprietaireTiersId || undefined,
    };
    try {
      const resultat = immeuble?.id
        ? deballer(await api.PUT("/api/v1/immeubles/{id}", { params: { path: { id: immeuble.id } }, body: corps }))
        : deballer(await api.POST("/api/v1/immeubles", { body: corps }));
      await queryClient.invalidateQueries({ queryKey: ["immeubles"] });
      await queryClient.invalidateQueries({ queryKey: ["immeuble"] });
      toast.success(immeuble ? t("immeubleModifie") : t("immeubleCree"));
      router.push(`/patrimoine/${resultat.id}`);
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <form onSubmit={form.handleSubmit(enregistrer)} noValidate className="max-w-2xl">
      <FieldGroup>
        <TextField control={form.control} name="nom" label={t("nom")} autoFocus />
        <TextField control={form.control} name="adresse" label={t("adresse")} autoComplete="street-address" />
        <div className="grid gap-4 sm:grid-cols-2">
          <TextField control={form.control} name="ville" label={t("ville")} />
          <TextField control={form.control} name="quartier" label={t("quartier")} />
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <TextField control={form.control} name="latitude" label={t("latitude")} inputMode="decimal" />
          <TextField control={form.control} name="longitude" label={t("longitude")} inputMode="decimal" />
        </div>
        <FieldDescription>{t("gpsAide")}</FieldDescription>
        <SelectField
          control={form.control}
          name="proprietaireTiersId"
          label={tiers("proprietaireImmeuble")}
          options={[
            { valeur: "", libelle: tiers("aucunProprietaire") },
            ...(proprietaires.data?.content ?? []).map((p) => ({ valeur: p.id ?? "", libelle: p.nom ?? "" })),
          ]}
        />
        <Controller
          control={form.control}
          name="description"
          render={({ field }) => (
            <Field>
              <FieldLabel htmlFor="description">{t("description")}</FieldLabel>
              <Textarea {...field} id="description" rows={4} />
            </Field>
          )}
        />
        {erreur ? (
          <p role="alert" className="text-sm text-destructive">
            {erreur}
          </p>
        ) : null}
        <div className="flex gap-2">
          <Button type="submit" disabled={form.formState.isSubmitting}>
            {form.formState.isSubmitting ? t("enregistrement") : t("enregistrer")}
          </Button>
          <Button type="button" variant="outline" onClick={() => router.back()}>
            {t("annuler")}
          </Button>
        </div>
      </FieldGroup>
    </form>
  );
}
