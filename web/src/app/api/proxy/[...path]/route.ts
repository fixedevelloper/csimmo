import { NextRequest, NextResponse } from "next/server";

import { backendFetch, rafraichir, type ReponseConnexion } from "@/lib/auth/backend";
import { ACCESS_COOKIE, REFRESH_COOKIE } from "@/lib/auth/cookies";
import { effacerSession, memeOrigine, poserSession, problem } from "@/lib/auth/session";

/**
 * Proxy vers l'API : ajoute le jeton d'accès (cookie httpOnly), le rafraîchit au besoin, et ne laisse
 * passer que /api/v1/**. Les routes d'authentification passent par /api/auth/* (qui gèrent les cookies).
 */
const INTERDITS = /^api\/v1\/auth\/(login|refresh|logout|otp\/)/;
const ENTETES_REQUETE = ["content-type", "accept", "accept-language", "idempotency-key"];
const ENTETES_REPONSE = ["content-type", "content-disposition", "content-length", "cache-control", "x-content-type-options"];

type Contexte = { params: Promise<{ path: string[] }> };

async function gerer(requete: NextRequest, { params }: Contexte): Promise<NextResponse> {
  const { path } = await params;
  const chemin = path.join("/");
  if (!chemin.startsWith("api/v1/") || chemin.includes("..") || INTERDITS.test(chemin)) {
    return problem(404, "HTTP_404", "Introuvable");
  }
  const modifie = !["GET", "HEAD"].includes(requete.method);
  if (modifie && !memeOrigine(requete)) return problem(403, "ACCES_REFUSE", "Origine refusée");

  const refresh = requete.cookies.get(REFRESH_COOKIE)?.value;
  let acces = requete.cookies.get(ACCESS_COOKIE)?.value;
  let nouveaux: ReponseConnexion | null = null;

  if (!acces && refresh) {
    nouveaux = await rafraichir(refresh);
    acces = nouveaux?.accessToken ?? undefined;
  }
  if (!acces) return sessionPerdue();

  const corps = modifie ? await requete.arrayBuffer() : undefined;
  const appeler = (jeton: string) => {
    const entetes = new Headers({ Authorization: `Bearer ${jeton}` });
    for (const nom of ENTETES_REQUETE) {
      const valeur = requete.headers.get(nom);
      if (valeur) entetes.set(nom, valeur);
    }
    return backendFetch(`/${chemin}${requete.nextUrl.search}`, { method: requete.method, headers: entetes, body: corps });
  };

  let amont: Response;
  try {
    amont = await appeler(acces);
    // Jeton refusé alors que le cookie existait encore (horloges, révocation) : un seul essai de rafraîchissement.
    if (amont.status === 401 && refresh && !nouveaux) {
      nouveaux = await rafraichir(refresh);
      if (!nouveaux?.accessToken) return sessionPerdue();
      amont = await appeler(nouveaux.accessToken);
    }
  } catch {
    return problem(502, "SERVEUR_INDISPONIBLE", "Serveur injoignable");
  }

  const entetes = new Headers();
  for (const nom of ENTETES_REPONSE) {
    const valeur = amont.headers.get(nom);
    if (valeur) entetes.set(nom, valeur);
  }
  const reponse = new NextResponse(amont.status === 204 ? null : amont.body, { status: amont.status, headers: entetes });
  if (nouveaux) poserSession(reponse, nouveaux);
  if (amont.status === 401) effacerSession(reponse);
  return reponse;
}

function sessionPerdue(): NextResponse {
  const reponse = problem(401, "NON_AUTHENTIFIE", "Session expirée");
  effacerSession(reponse);
  return reponse;
}

export { gerer as GET, gerer as POST, gerer as PUT, gerer as PATCH, gerer as DELETE };
