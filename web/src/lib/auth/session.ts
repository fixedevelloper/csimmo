import "server-only";

import { NextRequest, NextResponse } from "next/server";

import type { ReponseConnexion } from "./backend";
import { ACCESS_COOKIE, optionsCookie, REFRESH_COOKIE, REFRESH_MAX_AGE } from "./cookies";

export function problem(status: number, code: string, detail: string): NextResponse {
  return NextResponse.json(
    { type: "about:blank", status, code, detail },
    { status, headers: { "Content-Type": "application/problem+json" } },
  );
}

export function poserSession(reponse: NextResponse, jetons: ReponseConnexion): void {
  reponse.cookies.set(ACCESS_COOKIE, jetons.accessToken ?? "", optionsCookie(jetons.expiresIn ?? 900));
  reponse.cookies.set(REFRESH_COOKIE, jetons.refreshToken ?? "", optionsCookie(REFRESH_MAX_AGE));
}

export function effacerSession(reponse: NextResponse): void {
  reponse.cookies.set(ACCESS_COOKIE, "", { ...optionsCookie(0), maxAge: 0 });
  reponse.cookies.set(REFRESH_COOKIE, "", { ...optionsCookie(0), maxAge: 0 });
}

/** Refuse les requêtes qui modifient des données si elles viennent d'un autre site (défense en plus de SameSite). */
export function memeOrigine(requete: NextRequest): boolean {
  const origine = requete.headers.get("origin");
  if (!origine) return true;
  const hote = requete.headers.get("x-forwarded-host") ?? requete.headers.get("host");
  try {
    return new URL(origine).host === hote;
  } catch {
    return false;
  }
}

/**
 * Transforme la réponse du backend à /auth/login ou /auth/otp/verifier :
 * les jetons vont dans des cookies httpOnly et ne sont jamais renvoyés au navigateur.
 */
export async function reponseDeConnexion(amont: Response): Promise<NextResponse> {
  if (!amont.ok) {
    const corps = await amont.json().catch(() => null);
    return NextResponse.json(corps ?? { code: `HTTP_${amont.status}` }, {
      status: amont.status,
      headers: { "Content-Type": "application/problem+json" },
    });
  }
  const donnees = (await amont.json()) as ReponseConnexion;
  if (donnees.deuxFacteursRequis) {
    return NextResponse.json({ deuxFacteursRequis: true });
  }
  const reponse = NextResponse.json({ role: donnees.role, deuxFacteursRequis: false });
  poserSession(reponse, donnees);
  return reponse;
}
