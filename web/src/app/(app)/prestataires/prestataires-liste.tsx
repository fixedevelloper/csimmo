"use client";

import { useTranslations } from "next-intl";

import { TiersListe } from "@/components/tiers/tiers-liste";
import { usePrestataires } from "@/hooks/use-tiers";
import type { PrestataireDto } from "@/lib/tiers";

export function PrestatairesListe() {
  const t = useTranslations("tiers");
  return (
    <TiersListe<PrestataireDto>
      utiliser={usePrestataires}
      ficheHref={(p) => `/prestataires/${p.id}`}
      nouveauHref="/prestataires/nouveau"
      nouveauLabel={t("nouveauPrestataire")}
      entetNom={t("nom")}
      colonnes={[
        { id: "metier", entete: t("metier"), cellule: (p) => p.metier ?? "—" },
        { id: "tel", entete: t("telephone"), cellule: (p) => p.telephone ?? "—" },
      ]}
    />
  );
}
