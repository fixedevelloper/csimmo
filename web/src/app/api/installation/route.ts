import { NextRequest, NextResponse } from "next/server";

import { backendFetch } from "@/lib/auth/backend";
import { memeOrigine, problem } from "@/lib/auth/session";

/** Assistant de premier lancement : public (aucune session n'existe encore), utilisable une seule fois côté backend. */
export async function POST(requete: NextRequest) {
  if (!memeOrigine(requete)) return problem(403, "ACCES_REFUSE", "Origine refusée");
  const corps = await requete.text();
  if (corps.length > 200_000) return problem(413, "HTTP_413", "Requête trop volumineuse");
  try {
    const amont = await backendFetch("/api/v1/installation", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: corps,
    });
    return new NextResponse(amont.status === 204 ? null : amont.body, {
      status: amont.status,
      headers: { "Content-Type": amont.headers.get("content-type") ?? "application/json" },
    });
  } catch {
    return problem(502, "SERVEUR_INDISPONIBLE", "Serveur injoignable");
  }
}
