"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import { Check, Copy, Dices } from "lucide-react";
import { useTranslations } from "next-intl";
import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

import { DataTable, type Colonne } from "@/components/data-table";
import { ErrorDisplay } from "@/components/error-display";
import { StatutPastille } from "@/components/statut-pastille";
import { TextField } from "@/components/text-field";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { FieldGroup } from "@/components/ui/field";
import { NativeSelect } from "@/components/ui/native-select";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useActionsUtilisateur } from "@/hooks/use-actions-utilisateur";
import { useUtilisateurs } from "@/hooks/use-utilisateurs";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { genererMotDePasse, ROLES, ROLES_CREATION_GENERIQUE, type RoleUtilisateur } from "@/lib/utilisateurs";
import type { components } from "@/lib/api/schema";

type Utilisateur = components["schemas"]["UtilisateurDto"];

const optionnel = (v: string) => (v.trim() === "" ? undefined : v.trim());

/** Identifiants à communiquer, affichés une seule fois (le mot de passe n'est jamais renvoyé par l'API). */
export function Recapitulatif({ identifiant, motDePasse, onFermer }: { identifiant: string; motDePasse: string; onFermer: () => void }) {
  const t = useTranslations("utilisateurs");
  const [copie, setCopie] = useState(false);
  async function copier() {
    try {
      await navigator.clipboard.writeText(`${identifiant}\n${motDePasse}`);
      setCopie(true);
    } catch {
      toast.error(t("copieImpossible"));
    }
  }
  return (
    <Card role="status" className="border-primary/40">
      <CardHeader>
        <CardTitle>{t("recapTitre")}</CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-3">
        <p className="text-sm text-muted-foreground">{t("recapAide")}</p>
        <dl className="grid grid-cols-[auto_1fr] gap-x-4 gap-y-1 text-sm">
          <dt className="text-muted-foreground">{t("identifiant")}</dt>
          <dd data-testid="recap-identifiant" className="font-mono">{identifiant}</dd>
          <dt className="text-muted-foreground">{t("motDePasse")}</dt>
          <dd data-testid="recap-mot-de-passe" className="font-mono">{motDePasse}</dd>
        </dl>
        <div className="flex gap-2">
          <Button type="button" variant="outline" size="sm" onClick={() => void copier()}>
            {copie ? <Check className="size-4" aria-hidden /> : <Copy className="size-4" aria-hidden />}
            {copie ? t("copie") : t("copier")}
          </Button>
          <Button type="button" variant="ghost" size="sm" onClick={onFermer}>
            {t("fermer")}
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}

function FormulaireCreation({ onCree, onAnnuler }: { onCree: (identifiant: string, motDePasse: string) => void; onAnnuler: () => void }) {
  const t = useTranslations("utilisateurs");
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
          role: z.enum(ROLES_CREATION_GENERIQUE),
          motDePasse: z.string().min(8, t("erreurs.motDePasse")).max(100, t("erreurs.motDePasse")),
        })
        .refine((v) => v.telephone !== "" || v.email !== "", { path: ["telephone"], message: t("erreurs.contact") }),
    [t],
  );
  type Valeurs = z.infer<typeof schema>;
  const form = useForm<Valeurs>({
    resolver: zodResolver(schema),
    defaultValues: { nom: "", telephone: "", email: "", role: "LOCATAIRE", motDePasse: "" },
  });

  async function soumettre(v: Valeurs) {
    setErreur(null);
    try {
      deballer(
        await api.POST("/api/v1/utilisateurs", {
          body: { nom: v.nom, telephone: optionnel(v.telephone), email: optionnel(v.email), role: v.role, motDePasse: v.motDePasse },
        }),
      );
      await queryClient.invalidateQueries({ queryKey: ["utilisateurs"] });
      toast.success(t("cree"));
      onCree(optionnel(v.telephone) ?? optionnel(v.email) ?? v.nom, v.motDePasse);
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("nouveau")}</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={(e) => void form.handleSubmit(soumettre)(e)} noValidate className="flex max-w-xl flex-col gap-4">
          <FieldGroup>
            <TextField control={form.control} name="nom" label={t("nom")} autoComplete="off" autoFocus />
            <TextField control={form.control} name="telephone" label={t("telephone")} inputMode="tel" autoComplete="off" />
            <TextField control={form.control} name="email" label={t("email")} type="email" autoComplete="off" />
            <div className="flex flex-col gap-1">
              <label htmlFor="u-role" className="text-sm font-medium">{t("role")}</label>
              <NativeSelect id="u-role" {...form.register("role")}>
                {ROLES_CREATION_GENERIQUE.map((r) => (
                  <option key={r} value={r}>{t(`roles.${r}`)}</option>
                ))}
              </NativeSelect>
            </div>
            <TextField control={form.control} name="motDePasse" label={t("motDePasse")} type="password" autoComplete="new-password" />
          </FieldGroup>
          <div>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => form.setValue("motDePasse", genererMotDePasse(), { shouldValidate: true, shouldDirty: true })}
            >
              <Dices className="size-4" aria-hidden />
              {t("generer")}
            </Button>
            <p className="mt-1 text-xs text-muted-foreground">{t("genererAide")}</p>
          </div>
          {erreur ? <p role="alert" className="text-sm text-destructive">{erreur}</p> : null}
          <div className="flex gap-2">
            <Button type="submit" variant="accent" disabled={form.formState.isSubmitting}>{t("creer")}</Button>
            <Button type="button" variant="ghost" onClick={onAnnuler}>{t("annuler")}</Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}

export function FormulaireReinitialisation({ utilisateur, onFait, onAnnuler }: { utilisateur: Utilisateur; onFait: (motDePasse: string) => void; onAnnuler: () => void }) {
  const t = useTranslations("utilisateurs");
  const message = useErrorMessage();
  const [erreur, setErreur] = useState<string | null>(null);
  const schema = useMemo(() => z.object({ motDePasse: z.string().min(8, t("erreurs.motDePasse")).max(100, t("erreurs.motDePasse")) }), [t]);
  const form = useForm<z.infer<typeof schema>>({ resolver: zodResolver(schema), defaultValues: { motDePasse: "" } });

  async function soumettre(v: z.infer<typeof schema>) {
    setErreur(null);
    try {
      deballer(await api.POST("/api/v1/utilisateurs/{id}/mot-de-passe", { params: { path: { id: utilisateur.id ?? "" } }, body: { motDePasse: v.motDePasse } }));
      toast.success(t("reinitialise"));
      onFait(v.motDePasse);
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("reinitialiserPour", { nom: utilisateur.nom ?? "" })}</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={(e) => void form.handleSubmit(soumettre)(e)} noValidate className="flex max-w-xl flex-col gap-4">
          <TextField control={form.control} name="motDePasse" label={t("nouveauMotDePasse")} type="password" autoComplete="new-password" autoFocus />
          <div>
            <Button type="button" variant="outline" size="sm" onClick={() => form.setValue("motDePasse", genererMotDePasse(), { shouldValidate: true })}>
              <Dices className="size-4" aria-hidden />
              {t("generer")}
            </Button>
          </div>
          {erreur ? <p role="alert" className="text-sm text-destructive">{erreur}</p> : null}
          <div className="flex gap-2">
            <Button type="submit" variant="accent" disabled={form.formState.isSubmitting}>{t("enregistrer")}</Button>
            <Button type="button" variant="ghost" onClick={onAnnuler}>{t("annuler")}</Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}

/** Gestion des comptes (ADMIN) : création, mot de passe, activation, déverrouillage. */
export function UtilisateursListe() {
  const t = useTranslations("utilisateurs");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = useUtilisateurs(page);
  const [creation, setCreation] = useState(false);
  const [aReinitialiser, setAReinitialiser] = useState<Utilisateur | null>(null);
  const [recap, setRecap] = useState<{ identifiant: string; motDePasse: string } | null>(null);

  const { basculerActif, deverrouiller } = useActionsUtilisateur();

  const colonnes: Colonne<Utilisateur>[] = [
    {
      id: "nom",
      entete: t("nom"),
      cellule: (u) => (
        <div className="flex flex-col">
          <span className="font-medium">{u.nom}</span>
          <span className="text-xs text-muted-foreground">{[u.telephone, u.email].filter(Boolean).join(" · ")}</span>
        </div>
      ),
    },
    { id: "role", entete: t("role"), cellule: (u) => (u.role ? t(`roles.${u.role as RoleUtilisateur}`) : "—") },
    {
      id: "statut",
      entete: t("statut"),
      cellule: (u) => (
        <div className="flex flex-wrap gap-1.5">
          <StatutPastille statut={u.actif ? "ACTIF" : "ANNULE"} libelle={u.actif ? t("actif") : t("inactif")} />
          {u.verrouille ? <StatutPastille statut="ECHEC" libelle={t("verrouille")} /> : null}
        </div>
      ),
    },
    {
      id: "actions",
      entete: t("actions"),
      cellule: (u) => (
        <div className="flex flex-wrap gap-1.5">
          <Button type="button" size="sm" variant="outline" onClick={() => { setAReinitialiser(u); setCreation(false); }}>
            {t("reinitialiser")}
          </Button>
          {u.verrouille ? (
            <Button type="button" size="sm" variant="outline" onClick={() => void deverrouiller(u)}>{t("deverrouiller")}</Button>
          ) : null}
          <Button type="button" size="sm" variant="ghost" onClick={() => void basculerActif(u)}>
            {u.actif ? t("desactiver") : t("activer")}
          </Button>
        </div>
      ),
    },
  ];

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <div className="flex flex-col gap-4">
      <div className="flex justify-end">
        <Button type="button" variant="accent" onClick={() => { setCreation(true); setAReinitialiser(null); setRecap(null); }}>
          {t("nouveau")}
        </Button>
      </div>
      {recap ? <Recapitulatif {...recap} onFermer={() => setRecap(null)} /> : null}
      {creation ? (
        <FormulaireCreation
          onAnnuler={() => setCreation(false)}
          onCree={(identifiant, motDePasse) => { setCreation(false); setRecap({ identifiant, motDePasse }); }}
        />
      ) : null}
      {aReinitialiser ? (
        <FormulaireReinitialisation
          utilisateur={aReinitialiser}
          onAnnuler={() => setAReinitialiser(null)}
          onFait={(motDePasse) => { setRecap({ identifiant: aReinitialiser.telephone ?? aReinitialiser.email ?? aReinitialiser.nom ?? "", motDePasse }); setAReinitialiser(null); }}
        />
      ) : null}
      <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(u) => u.id ?? ""} messageVide={t("aucun")} />
    </div>
  );
}
