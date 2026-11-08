import { NextRequest } from "next/server";
import { z } from "zod";

import { backendJson } from "@/lib/auth/backend";
import { memeOrigine, problem, reponseDeConnexion } from "@/lib/auth/session";

const schema = z.object({ defi: z.string().min(1).max(200), code: z.string().regex(/^\d{6}$/) });

/** Seconde étape de la connexion : code de l'application d'authentification, avec le défi reçu après le mot de passe. */
export async function POST(requete: NextRequest) {
  if (!memeOrigine(requete)) return problem(403, "ACCES_REFUSE", "Origine refusée");
  const corps = schema.safeParse(await requete.json().catch(() => null));
  if (!corps.success) return problem(400, "VALIDATION_ERREUR", "Données invalides");
  try {
    return await reponseDeConnexion(await backendJson("/api/v1/auth/deux-facteurs/verifier", corps.data));
  } catch {
    return problem(502, "SERVEUR_INDISPONIBLE", "Serveur injoignable");
  }
}
