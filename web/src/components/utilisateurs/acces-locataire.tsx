"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import { Dices, KeyRound } from "lucide-react";
import { useTranslations } from "next-intl";
import Link from "next/link";
import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

import { TextField } from "@/components/text-field";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Recapitulatif } from "@/components/utilisateurs/utilisateurs-liste";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useMe } from "@/hooks/use-me";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { optionnel } from "@/lib/tiers";
import { genererMotDePasse } from "@/lib/utilisateurs";
import type { components } from "@/lib/api/schema";

type Locataire = components["schemas"]["LocataireDto"];

/** Accès à l'application d'un locataire (ADMIN) : crée le compte `LOCATAIRE` avec son téléphone / e-mail puis le rattache à la fiche. */
export function AccesLocataire({ locataire }: { locataire: Locataire }) {
  const t = useTranslations("utilisateurs");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const { data: moi } = useMe();
  const [erreur, setErreur] = useState<string | null>(null);
  const [recap, setRecap] = useState<{ identifiant: string; motDePasse: string } | null>(null);

  const schema = useMemo(() => z.object({ motDePasse: z.string().min(12, t("erreurs.motDePasse")).max(100, t("erreurs.motDePasse")) }), [t]);
  const form = useForm<z.infer<typeof schema>>({ resolver: zodResolver(schema), defaultValues: { motDePasse: "" } });

  if (moi?.role !== "ADMIN") return null;

  const contact = optionnel(locataire.telephone) ?? optionnel(locataire.email);

  async function creer(v: z.infer<typeof schema>) {
    setErreur(null);
    let compteId: string | undefined;
    try {
      const compte = deballer(
        await api.POST("/api/v1/utilisateurs", {
          body: { nom: locataire.nom ?? "", telephone: optionnel(locataire.telephone), email: optionnel(locataire.email), role: "LOCATAIRE", motDePasse: v.motDePasse },
        }),
      );
      compteId = compte.id;
    } catch (e) {
      setErreur(message(e));
      return;
    }
    try {
      deballer(
        await api.PUT("/api/v1/locataires/{id}", {
          params: { path: { id: locataire.id ?? "" } },
          body: {
            nom: locataire.nom ?? "", profession: optionnel(locataire.profession), telephone: optionnel(locataire.telephone), email: optionnel(locataire.email),
            adresse: optionnel(locataire.adresse), dateNaissance: optionnel(locataire.dateNaissance), numeroCni: optionnel(locataire.numeroCni),
            notes: optionnel(locataire.notes), utilisateurId: compteId,
          },
        }),
      );
    } catch (e) {
      // le compte existe déjà : on le dit, pour ne pas en recréer un second (et il reste utilisable depuis Paramètres > Utilisateurs)
      setErreur(`${t("acces.lienEchoue")} ${message(e)}`);
      setRecap({ identifiant: contact ?? "", motDePasse: v.motDePasse });
      return;
    }
    await Promise.all(["locataire", "locataires", "utilisateurs"].map((k) => queryClient.invalidateQueries({ queryKey: [k] })));
    toast.success(t("cree"));
    setRecap({ identifiant: contact ?? "", motDePasse: v.motDePasse });
  }

  return (
    <Card className="mt-6 border-border/60 shadow-sm">
      <CardHeader className="border-b bg-muted/10 pb-3">
        <CardTitle className="flex items-center gap-2 text-base font-semibold">
          <KeyRound className="size-4 text-primary" aria-hidden />
          {t("acces.titre")}
        </CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-4 pt-4">
        {recap ? <Recapitulatif {...recap} onFermer={() => setRecap(null)} /> : null}
        {locataire.utilisateurId ? (
          <p className="text-sm">
            {t("acces.deja")}{" "}
            <Link href="/parametres/utilisateurs" className="underline underline-offset-2">{t("acces.gerer")}</Link>
          </p>
        ) : !contact ? (
          <p className="text-sm text-muted-foreground">{t("acces.contactRequis")}</p>
        ) : (
          <form onSubmit={(e) => void form.handleSubmit(creer)(e)} noValidate className="flex max-w-xl flex-col gap-3">
            <p className="text-sm text-muted-foreground">{t("acces.aide", { contact })}</p>
            <TextField control={form.control} name="motDePasse" label={t("motDePasse")} type="password" autoComplete="new-password" />
            <div className="flex flex-wrap items-center gap-2">
              <Button type="button" variant="outline" size="sm" onClick={() => form.setValue("motDePasse", genererMotDePasse(), { shouldValidate: true })}>
                <Dices className="size-4" aria-hidden />
                {t("generer")}
              </Button>
              <Button type="submit" variant="accent" size="sm" disabled={form.formState.isSubmitting}>{t("acces.creer")}</Button>
            </div>
          </form>
        )}
        {erreur ? <p role="alert" className="text-sm text-destructive">{erreur}</p> : null}
      </CardContent>
    </Card>
  );
}
