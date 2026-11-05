import "server-only";

import type { components } from "@/lib/api/schema";

import { backendFetch } from "./backend";

export type EtatInstallation = components["schemas"]["EtatInstallationDto"];

/** État de l'assistant de premier lancement ; `null` si le backend est injoignable. */
export async function etatInstallation(): Promise<EtatInstallation | null> {
  try {
    const reponse = await backendFetch("/api/v1/installation");
    return reponse.ok ? ((await reponse.json()) as EtatInstallation) : null;
  } catch {
    return null;
  }
}
