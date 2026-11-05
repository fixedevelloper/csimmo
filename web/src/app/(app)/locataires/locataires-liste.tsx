"use client";

import { useTranslations } from "next-intl";

import { TiersListe } from "@/components/tiers/tiers-liste";
import { useLocataires } from "@/hooks/use-tiers";
import type { LocataireDto } from "@/lib/tiers";

export function LocatairesListe() {
  const t = useTranslations("tiers");
  return (
    <TiersListe<LocataireDto>
      utiliser={useLocataires}
      ficheHref={(l) => `/locataires/${l.id}`}
      nouveauHref="/locataires/nouveau"
      nouveauLabel={t("nouveauLocataire")}
      entetNom={t("nom")}
      colonnes={[
        { id: "tel", entete: t("telephone"), cellule: (l) => l.telephone ?? "—" },
        { id: "profession", entete: t("profession"), cellule: (l) => l.profession ?? "—" },
      ]}
    />
  );
}
