"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { useMemo, useState } from "react";
import { Controller, useFieldArray, useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

import { SelectField } from "@/components/patrimoine/select-field";
import { TextField } from "@/components/text-field";
import { Button } from "@/components/ui/button";
import { Field, FieldDescription, FieldGroup, FieldLabel } from "@/components/ui/field";
import { Textarea } from "@/components/ui/textarea";
import { useErrorMessage } from "@/hooks/use-error-message";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { nombreOptionnel, parseEquipements, TYPES_COMPTEUR, TYPES_LOT, type LotDto, type TypeCompteur, type TypeLot } from "@/lib/patrimoine";

/** Création (avec `immeubleId`) ou modification (avec `lot`) d'un lot. */
export function LotForm({ immeubleId, lot }: { immeubleId?: string; lot?: LotDto }) {
  const t = useTranslations("patrimoine");
  const message = useErrorMessage();
  const router = useRouter();
  const queryClient = useQueryClient();
  const [erreur, setErreur] = useState<string | null>(null);

  const schema = useMemo(() => {
    const positif = (valeur: string) => {
      const n = nombreOptionnel(valeur);
      return n === undefined || (!Number.isNaN(n) && n >= 0);
    };
    return z.object({
      reference: z.string().trim().min(1, t("referenceRequise")),
      type: z.string().min(1, t("typeRequis")),
      surfaceM2: z.string().trim().refine((v) => positif(v) && nombreOptionnel(v) !== 0, t("nombrePositif")),
      pieces: z.string().trim().refine((v) => positif(v) && Number.isInteger(nombreOptionnel(v) ?? 0), t("nombrePositif")),
      loyerIndicatif: z.string().trim().refine((v) => positif(v) && Number.isInteger(nombreOptionnel(v) ?? 0), t("nombrePositif")),
      equipements: z.string(),
      compteurs: z.array(z.object({ type: z.string(), numero: z.string().trim().min(1, t("nombrePositif")) })),
    });
  }, [t]);
  type Valeurs = z.infer<typeof schema>;

  const form = useForm<Valeurs>({
    resolver: zodResolver(schema),
    defaultValues: {
      reference: lot?.reference ?? "",
      type: lot?.type ?? "APPARTEMENT",
      surfaceM2: lot?.surfaceM2?.toString() ?? "",
      pieces: lot?.pieces?.toString() ?? "",
      loyerIndicatif: lot?.loyerIndicatif?.toString() ?? "",
      equipements: (lot?.equipements ?? []).join("\n"),
      compteurs: (lot?.compteurs ?? []).map((c) => ({ type: c.type ?? "EAU", numero: c.numero ?? "" })),
    },
  });
  const compteurs = useFieldArray({ control: form.control, name: "compteurs" });

  async function enregistrer(v: Valeurs) {
    setErreur(null);
    const corps = {
      immeubleId: lot ? undefined : immeubleId,
      reference: v.reference,
      type: v.type as TypeLot,
      surfaceM2: nombreOptionnel(v.surfaceM2),
      pieces: nombreOptionnel(v.pieces),
      loyerIndicatif: nombreOptionnel(v.loyerIndicatif),
      equipements: parseEquipements(v.equipements),
      compteurs: v.compteurs.map((c) => ({ type: c.type as TypeCompteur, numero: c.numero })),
    };
    try {
      const resultat = lot?.id
        ? deballer(await api.PUT("/api/v1/lots/{id}", { params: { path: { id: lot.id } }, body: corps }))
        : deballer(await api.POST("/api/v1/lots", { body: corps }));
      await queryClient.invalidateQueries({ queryKey: ["lots"] });
      await queryClient.invalidateQueries({ queryKey: ["lot"] });
      await queryClient.invalidateQueries({ queryKey: ["immeuble"] });
      toast.success(lot ? t("lotModifie") : t("lotCree"));
      router.push(`/patrimoine/lots/${resultat.id}`);
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <form onSubmit={form.handleSubmit(enregistrer)} noValidate className="max-w-2xl">
      <FieldGroup>
        <div className="grid gap-4 sm:grid-cols-2">
          <TextField control={form.control} name="reference" label={t("reference")} autoFocus />
          <SelectField
            control={form.control}
            name="type"
            label={t("type")}
            options={TYPES_LOT.map((v) => ({ valeur: v, libelle: t(`types.${v}`) }))}
          />
        </div>
        <div className="grid gap-4 sm:grid-cols-3">
          <TextField control={form.control} name="surfaceM2" label={t("surface")} inputMode="decimal" />
          <TextField control={form.control} name="pieces" label={t("pieces")} inputMode="numeric" />
          <TextField control={form.control} name="loyerIndicatif" label={t("loyer")} inputMode="numeric" />
        </div>
        <Controller
          control={form.control}
          name="equipements"
          render={({ field }) => (
            <Field>
              <FieldLabel htmlFor="equipements">{t("equipements")}</FieldLabel>
              <Textarea {...field} id="equipements" rows={4} />
              <FieldDescription>{t("equipementsAide")}</FieldDescription>
            </Field>
          )}
        />
        <fieldset className="flex flex-col gap-3">
          <legend className="text-sm font-medium">{t("compteurs")}</legend>
          {compteurs.fields.map((champ, index) => (
            <div key={champ.id} className="grid items-end gap-3 sm:grid-cols-[10rem_1fr_auto]">
              <SelectField
                control={form.control}
                name={`compteurs.${index}.type`}
                label={t("type")}
                options={TYPES_COMPTEUR.map((v) => ({ valeur: v, libelle: t(`typesCompteur.${v}`) }))}
              />
              <TextField control={form.control} name={`compteurs.${index}.numero`} label={t("numeroCompteur")} />
              <Button type="button" variant="outline" size="sm" onClick={() => compteurs.remove(index)}>
                {t("retirer")}
              </Button>
            </div>
          ))}
          <div>
            <Button type="button" variant="outline" size="sm" onClick={() => compteurs.append({ type: "EAU", numero: "" })}>
              {t("ajouterCompteur")}
            </Button>
          </div>
        </fieldset>
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
