import type { components } from "@/lib/api/schema";

export type PaiementDto = components["schemas"]["PaiementDto"];
export type CaisseLigne = components["schemas"]["CaisseLigne"];
export type CompteLocataireDto = components["schemas"]["CompteLocataireDto"];

export const MODES_PAIEMENT = ["ESPECES", "VIREMENT", "MTN_MOMO", "ORANGE_MONEY"] as const;
export type ModePaiement = (typeof MODES_PAIEMENT)[number];

/** Ces modes exigent la référence de l'opération (le serveur refuse sinon). */
export const referenceObligatoire = (mode: string): boolean => mode !== "ESPECES";

/** Clé d'idempotence d'une saisie : conservée pour les nouvelles tentatives, renouvelée après un succès. */
export const nouvelleCle = (): string => globalThis.crypto.randomUUID();
export type DocumentDto = components["schemas"]["DocumentDto"];

/** Le PDF d'une quittance ou d'un reçu se télécharge par l'API des fichiers (contrôle d'accès côté serveur). */
export const urlDocument = (fichierId: string): string => `/api/proxy/api/v1/fichiers/${fichierId}`;
