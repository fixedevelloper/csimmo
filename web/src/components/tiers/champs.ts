import type { ChampConfig } from "@/components/tiers/fiche-form";
import { MODES_REVERSEMENT } from "@/lib/tiers";

type T = (cle: string) => string;

export const champsProprietaire = (t: T): ChampConfig[] => [
  { nom: "nom", libelle: t("nom"), requis: true },
  { nom: "telephone", libelle: t("telephone"), type: "tel" },
  { nom: "email", libelle: t("email"), type: "email" },
  { nom: "adresse", libelle: t("adresse") },
  { nom: "modeReversement", libelle: t("modeReversement"), type: "select", options: MODES_REVERSEMENT.map((v) => ({ valeur: v, libelle: t(`modes.${v}`) })) },
  { nom: "coordonneesReversement", libelle: t("coordonneesReversement") },
  { nom: "notes", libelle: t("notes"), type: "textarea" },
];

export const champsLocataire = (t: T): ChampConfig[] => [
  { nom: "nom", libelle: t("nom"), requis: true },
  { nom: "profession", libelle: t("profession") },
  { nom: "telephone", libelle: t("telephone"), type: "tel" },
  { nom: "email", libelle: t("email"), type: "email" },
  { nom: "adresse", libelle: t("adresse") },
  { nom: "dateNaissance", libelle: t("dateNaissance"), type: "date" },
  { nom: "numeroCni", libelle: t("numeroCni"), aide: t("cniAide") },
  { nom: "notes", libelle: t("notes"), type: "textarea" },
];

export const champsPrestataire = (t: T): ChampConfig[] => [
  { nom: "nom", libelle: t("nom"), requis: true },
  { nom: "metier", libelle: t("metier") },
  { nom: "telephone", libelle: t("telephone"), type: "tel" },
  { nom: "email", libelle: t("email"), type: "email" },
  { nom: "adresse", libelle: t("adresse") },
  { nom: "notes", libelle: t("notes"), type: "textarea" },
];

export const champsGarant = (t: T): ChampConfig[] => [
  { nom: "nom", libelle: t("nom"), requis: true },
  { nom: "lien", libelle: t("lien") },
  { nom: "profession", libelle: t("profession") },
  { nom: "telephone", libelle: t("telephone"), type: "tel", requis: true },
  { nom: "adresse", libelle: t("adresse") },
  { nom: "numeroCni", libelle: t("numeroCni"), aide: t("cniAide") },
];
