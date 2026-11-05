export type Licence = {
  id: string;
  clientId: string;
  clientNom: string;
  statut: "ACTIVE" | "REVOQUEE";
  palierLots: number;
  dateDebut: string;
  dateExpiration: string;
  empreinteServeur: string;
  fonctionnalites: string[];
  joursRestants: number;
  transferts: number;
  derniereVerification: string | null;
  versionApplication: string | null;
  motifRevocation: string | null;
};
export type Emission = { licence: Licence; fichier: string };
export type Client = { id: string; nom: string; email?: string | null; telephone?: string | null; pays?: string | null; notes?: string | null };
export type Page<T> = { content: T[]; page: number; size: number; totalElements: number };
export type HistoriqueEmission = { id: string; motif: string; palierLots: number; dateDebut: string; dateExpiration: string; empreinteServeur: string; emisePar: string; creeLe: string };
export type Paiement = { id: string; montant: number; mode: string; reference?: string | null; datePaiement: string; note?: string | null; enregistrePar: string };
export type Version = { id: string; numero: string; notes?: string | null; urlTelechargement: string; sha256: string; publieePar: string; publieeLe: string };

export type Niveau = "expiree" | "critique" | "proche" | "ok" | "revoquee";

/** Seuils d'alerte alignés sur ceux des installations (30, 15, 7 jours). */
export function niveau(l: Pick<Licence, "statut" | "joursRestants">): Niveau {
  if (l.statut === "REVOQUEE") return "revoquee";
  if (l.joursRestants < 0) return "expiree";
  if (l.joursRestants <= 7) return "critique";
  if (l.joursRestants <= 30) return "proche";
  return "ok";
}

export const LIBELLE_NIVEAU: Record<Niveau, string> = {
  expiree: "Expirée",
  critique: "Expire dans 7 jours ou moins",
  proche: "Expire dans 30 jours ou moins",
  ok: "Valide",
  revoquee: "Révoquée",
};

export const formaterFcfa = (n: number) => `${new Intl.NumberFormat("fr-FR").format(n).replace(/[  ]/g, " ")} FCFA`;

/** Empreinte : 64 caractères hexadécimaux (SHA-256), telle qu'affichée par l'installation. */
export const EMPREINTE_VALIDE = /^[0-9a-fA-F]{64}$/;

/** Déclenche le téléchargement d'un fichier .lic dans le navigateur. */
export function telechargerLic(id: string, contenu: string): void {
  const url = URL.createObjectURL(new Blob([contenu], { type: "application/json" }));
  const a = document.createElement("a");
  a.href = url;
  a.download = `licence-${id}.lic`;
  a.click();
  URL.revokeObjectURL(url);
}
