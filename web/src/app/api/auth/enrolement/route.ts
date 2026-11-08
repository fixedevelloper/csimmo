import { NextRequest, NextResponse } from "next/server";
import { z } from "zod";

import { backendJson } from "@/lib/auth/backend";
import { memeOrigine, problem } from "@/lib/auth/session";

const schema = z.object({ defi: z.string().min(1).max(200) });

/** Compte sans application d'authentification : renvoie le secret et l'adresse à scanner (aucun cookie : la session n'existe pas encore). */
export async function POST(requete: NextRequest) {
  if (!memeOrigine(requete)) return problem(403, "ACCES_REFUSE", "Origine refusée");
  const corps = schema.safeParse(await requete.json().catch(() => null));
  if (!corps.success) return problem(400, "VALIDATION_ERREUR", "Données invalides");
  try {
    const amont = await backendJson("/api/v1/auth/deux-facteurs/enrolement/demarrer", corps.data);
    const donnees = await amont.json().catch(() => null);
    return NextResponse.json(donnees ?? { code: `HTTP_${amont.status}` }, {
      status: amont.status,
      headers: { "Content-Type": amont.ok ? "application/json" : "application/problem+json", "Cache-Control": "no-store" },
    });
  } catch {
    return problem(502, "SERVEUR_INDISPONIBLE", "Serveur injoignable");
  }
}
