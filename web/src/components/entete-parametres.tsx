"use client";

import { useTranslations } from "next-intl";
import type { ReactNode } from "react";

import { PageHeader } from "@/components/page-header";

/** En-tête des sous-pages de Paramètres : retour vers la grille des réglages. */
export function EnteteParametres({ titre, sousTitre, actions }: { titre: ReactNode; sousTitre?: ReactNode; actions?: ReactNode }) {
  const t = useTranslations("nav");
  return <PageHeader retour={{ href: "/parametres", libelle: t("parametres") }} titre={titre} sousTitre={sousTitre} actions={actions} />;
}
