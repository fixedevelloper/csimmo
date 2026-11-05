import { NextRequest, NextResponse } from "next/server";

import { backendJson } from "@/lib/auth/backend";
import { REFRESH_COOKIE } from "@/lib/auth/cookies";
import { effacerSession, memeOrigine, problem } from "@/lib/auth/session";

export async function POST(requete: NextRequest) {
  if (!memeOrigine(requete)) return problem(403, "ACCES_REFUSE", "Origine refusée");
  const refresh = requete.cookies.get(REFRESH_COOKIE)?.value;
  if (refresh) {
    // Meilleur effort : la session locale est effacée même si le backend est injoignable.
    await backendJson("/api/v1/auth/logout", { refreshToken: refresh }).catch(() => undefined);
  }
  const reponse = new NextResponse(null, { status: 204 });
  effacerSession(reponse);
  return reponse;
}
