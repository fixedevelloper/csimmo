"use client";

import { useTranslations } from "next-intl";
import { useState } from "react";

import { DataTable, type Colonne } from "@/components/data-table";
import { AffectationsEmploye } from "@/components/equipe/affectations-employe";
import { FormulaireEmploye } from "@/components/equipe/formulaire-employe";
import { ErrorDisplay } from "@/components/error-display";
import { StatutPastille } from "@/components/statut-pastille";
import { Button } from "@/components/ui/button";
import { FormulaireReinitialisation, Recapitulatif } from "@/components/utilisateurs/utilisateurs-liste";
import { useActionsUtilisateur } from "@/hooks/use-actions-utilisateur";
import { useAffectations, useEmployes } from "@/hooks/use-equipe";
import type { components } from "@/lib/api/schema";

type Utilisateur = components["schemas"]["UtilisateurDto"];

function NombreImmeubles({ utilisateur }: { utilisateur: Utilisateur }) {
  const t = useTranslations("equipe");
  const restreint = utilisateur.role === "ENCAISSEUR";
  const { data } = useAffectations(utilisateur.id ?? "", restreint);
  if (!restreint) return <span className="text-muted-foreground">{t("tous")}</span>;
  if (!data) return <span className="text-muted-foreground">…</span>;
  return data.length === 0 ? (
    <span className="text-amber-700 dark:text-amber-400">{t("aucun")}</span>
  ) : (
    <span>{t("nombreImmeubles", { count: data.length })}</span>
  );
}

type Panneau = { type: "creation" } | { type: "immeubles" | "motDePasse"; utilisateur: Utilisateur } | null;

/** Page Équipe (ADMIN) : employés, immeubles affectés, mot de passe, activation. */
export function EquipeListe() {
  const t = useTranslations("equipe");
  const tu = useTranslations("utilisateurs");
  const [page, setPage] = useState(0);
  const { data, isPending, error, refetch } = useEmployes(page);
  const [panneau, setPanneau] = useState<Panneau>(null);
  const [recap, setRecap] = useState<{ identifiant: string; motDePasse: string } | null>(null);
  const [avertissement, setAvertissement] = useState<string | null>(null);
  const { basculerActif, deverrouiller } = useActionsUtilisateur();

  const colonnes: Colonne<Utilisateur>[] = [
    {
      id: "nom",
      entete: tu("nom"),
      cellule: (u) => (
        <div className="flex flex-col">
          <span className="font-medium">{u.nom}</span>
          <span className="text-xs text-muted-foreground">{[u.telephone, u.email].filter(Boolean).join(" · ")}</span>
        </div>
      ),
    },
    { id: "role", entete: tu("role"), cellule: (u) => (u.role === "ENCAISSEUR" || u.role === "GESTIONNAIRE" ? t(`roles.${u.role}`) : (u.role ?? "—")) },
    { id: "immeubles", entete: t("immeubles"), cellule: (u) => <NombreImmeubles utilisateur={u} /> },
    {
      id: "statut",
      entete: tu("statut"),
      cellule: (u) => (
        <div className="flex flex-wrap gap-1.5">
          <StatutPastille statut={u.actif ? "ACTIF" : "ANNULE"} libelle={u.actif ? tu("actif") : tu("inactif")} />
          {u.verrouille ? <StatutPastille statut="ECHEC" libelle={tu("verrouille")} /> : null}
        </div>
      ),
    },
    {
      id: "actions",
      entete: tu("actions"),
      cellule: (u) => (
        <div className="flex flex-wrap gap-1.5">
          {u.role === "ENCAISSEUR" ? (
            <Button type="button" size="sm" variant="outline" onClick={() => { setPanneau({ type: "immeubles", utilisateur: u }); setRecap(null); }}>
              {t("immeubles")}
            </Button>
          ) : null}
          <Button type="button" size="sm" variant="outline" onClick={() => { setPanneau({ type: "motDePasse", utilisateur: u }); setRecap(null); }}>
            {tu("reinitialiser")}
          </Button>
          {u.verrouille ? <Button type="button" size="sm" variant="outline" onClick={() => void deverrouiller(u)}>{tu("deverrouiller")}</Button> : null}
          <Button type="button" size="sm" variant="ghost" onClick={() => void basculerActif(u)}>
            {u.actif ? tu("desactiver") : tu("activer")}
          </Button>
        </div>
      ),
    },
  ];

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <div className="flex flex-col gap-4">
      <div className="flex justify-end">
        <Button type="button" variant="accent" onClick={() => { setPanneau({ type: "creation" }); setRecap(null); setAvertissement(null); }}>
          {t("nouveau")}
        </Button>
      </div>
      {avertissement ? <p role="alert" className="text-sm text-destructive">{avertissement}</p> : null}
      {recap ? <Recapitulatif {...recap} onFermer={() => setRecap(null)} /> : null}
      {panneau?.type === "creation" ? (
        <FormulaireEmploye onAnnuler={() => setPanneau(null)} onCree={(identifiant, motDePasse, avert) => { setPanneau(null); setRecap({ identifiant, motDePasse }); setAvertissement(avert ?? null); }} />
      ) : null}
      {panneau?.type === "immeubles" ? <AffectationsEmploye utilisateur={panneau.utilisateur} onFini={() => setPanneau(null)} /> : null}
      {panneau?.type === "motDePasse" ? (
        <FormulaireReinitialisation
          utilisateur={panneau.utilisateur}
          onAnnuler={() => setPanneau(null)}
          onFait={(motDePasse) => {
            const u = panneau.utilisateur;
            setPanneau(null);
            setRecap({ identifiant: u.telephone ?? u.email ?? u.nom ?? "", motDePasse });
          }}
        />
      ) : null}
      <DataTable colonnes={colonnes} donnees={data} chargement={isPending} onPageChange={setPage} idLigne={(u) => u.id ?? ""} messageVide={t("aucunEmploye")} />
    </div>
  );
}
