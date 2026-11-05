import "server-only";

import type { components } from "@/lib/api/schema";

export type ReponseConnexion = components["schemas"]["ReponseConnexion"];

const API_URL = process.env.API_URL ?? "http://localhost:8080";

/** Appel du backend depuis le serveur Next (le navigateur ne le contacte jamais directement). */
export function backendFetch(chemin: string, init?: RequestInit): Promise<Response> {
  return fetch(`${API_URL}${chemin}`, { ...init, cache: "no-store", redirect: "manual" });
}

export function backendJson(chemin: string, corps: unknown): Promise<Response> {
  return backendFetch(chemin, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(corps),
  });
}

/**
 * Rafraîchissement mutualisé : le refresh token tourne à chaque usage (un rejeu révoque toute la session),
 * donc des requêtes parallèles portant le même ancien jeton doivent partager UN seul appel.
 * Le résultat reste en mémoire 30 s pour les requêtes qui n'ont pas encore reçu le nouveau cookie.
 */
const FENETRE_MS = 30_000;
const enCours = new Map<string, { promesse: Promise<ReponseConnexion | null>; expire: number }>();

export function rafraichir(refreshToken: string): Promise<ReponseConnexion | null> {
  const maintenant = Date.now();
  for (const [cle, valeur] of enCours) {
    if (valeur.expire < maintenant) enCours.delete(cle);
  }
  const existant = enCours.get(refreshToken);
  if (existant) return existant.promesse;

  const promesse = (async () => {
    try {
      const reponse = await backendJson("/api/v1/auth/refresh", { refreshToken });
      if (!reponse.ok) return null;
      const donnees = (await reponse.json()) as ReponseConnexion;
      return donnees.accessToken && donnees.refreshToken ? donnees : null;
    } catch {
      return null;
    }
  })();
  enCours.set(refreshToken, { promesse, expire: maintenant + FENETRE_MS });
  return promesse;
}
