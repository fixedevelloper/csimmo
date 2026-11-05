"use client";

import { useTranslations } from "next-intl";

import { TiersListe } from "@/components/tiers/tiers-liste";
import { useProprietaires } from "@/hooks/use-tiers";
import type { ProprietaireDto } from "@/lib/tiers";

export function ProprietairesListe() {
  const t = useTranslations("tiers");
  return (
    <TiersListe<ProprietaireDto>
      utiliser={useProprietaires}
      ficheHref={(p) => `/proprietaires/${p.id}`}
      nouveauHref="/proprietaires/nouveau"
      nouveauLabel={t("nouveauProprietaire")}
      entetNom={t("nom")}
      colonnes={[
        { id: "tel", entete: t("telephone"), cellule: (p) => p.telephone ?? "—" },
        { id: "email", entete: t("email"), cellule: (p) => p.email ?? "—" },
        { id: "mode", entete: t("modeReversement"), cellule: (p) => t(`modes.${p.modeReversement ?? "VIREMENT"}`) },
      ]}
    />
  );
}
