import type { components } from "@/lib/api/schema";

export type CanalParametresDto = components["schemas"]["CanalParametresDto"];
export type ChampDto = components["schemas"]["ChampDto"];
export type MessageDto = components["schemas"]["MessageDto"];

export const STATUTS_MESSAGE = ["EN_ATTENTE", "ENVOYE", "ECHEC"] as const;
export const TYPES_CANAL = ["SMS", "WHATSAPP", "EMAIL", "PUSH"] as const;

/** Champs dont la valeur est un texte long (JSON du compte de service, corps modèle du SMS). */
export const CHAMPS_LONGS = new Set(["compteService", "corps"]);
