import { Badge } from "@/components/ui/badge";

export type Ton = "success" | "warning" | "danger" | "info" | "neutral";

/**
 * Ton de couleur par code de statut (cohérent dans toute l'application) : vert = réglé / actif, ambre = à surveiller,
 * rouge = problème, bleu = en cours de traitement, gris = clos ou inactif. Un code inconnu reste neutre.
 */
const TONS: Record<string, Ton> = {
  // réglé, actif, réussi
  PAYEE: "success", PAYE: "success", ACTIF: "success", OCCUPE: "success", CONFIRMEE: "success", ENVOYE: "success",
  RESOLU: "success", ACCORDE: "success", EXECUTEE: "success", OK: "success", EMIS: "success", IMPUTEE: "success",
  // à surveiller
  PARTIELLE: "warning", DUE: "warning", EN_ATTENTE: "warning", LIBRE: "warning", EXPIREE: "warning", EXPIRE: "warning", RECUE: "warning",
  // problème
  EN_RETARD: "danger", ECHEC: "danger", ECHOUEE: "danger", ANOMALIE: "danger", NOUVEAU: "danger", REFUSE: "danger",
  REFUSEE: "danger", REJETE: "danger",
  // en traitement
  EN_COURS: "info", CALCULE: "info", VALIDE: "info", EN_TRAVAUX: "info",
  // clos, inactif
  A_VENIR: "neutral", BROUILLON: "neutral", TERMINE: "neutral", RESILIE: "neutral", ANNULEE: "neutral", ANNULE: "neutral",
  CONTRE_PASSATION: "neutral",
};

export function tonDuStatut(statut?: string): Ton {
  return (statut && TONS[statut]) || "neutral";
}

/** Pastille de statut : point coloré + libellé déjà traduit. `ton` force la couleur quand le code ne suffit pas. */
export function StatutPastille({ statut, libelle, ton, title, testid }: { statut?: string; libelle: string; ton?: Ton; title?: string; testid?: string }) {
  return (
    <Badge variant={ton ?? tonDuStatut(statut)} title={title} data-testid={testid}>
      <span className="size-1.5 rounded-full bg-current" aria-hidden />
      {libelle}
    </Badge>
  );
}
