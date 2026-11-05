import { NextRequest, NextResponse } from "next/server";

import { COOKIE, memeOrigine, URL_SERVEUR } from "@/lib/session";

/** Seules les routes de gestion passent ; la connexion a sa propre route et la vérification des installations n'a rien à faire ici. */
const AUTORISE = /^api\/v1\/(clients|licences|versions)(\/|$)/;
const ENTETES_REPONSE = ["content-type", "content-disposition", "cache-control"];

type Contexte = { params: Promise<{ path: string[] }> };

async function gerer(requete: NextRequest, { params }: Contexte): Promise<NextResponse> {
  const chemin = (await params).path.join("/");
  if (!AUTORISE.test(chemin) || chemin.includes("..")) return NextResponse.json({ code: "INTROUVABLE" }, { status: 404 });
  const modifie = requete.method !== "GET";
  if (modifie && !memeOrigine(requete.headers.get("origin"), requete.headers.get("x-forwarded-host") ?? requete.headers.get("host"))) {
    return NextResponse.json({ code: "ACCES_REFUSE" }, { status: 403 });
  }
  const jeton = requete.cookies.get(COOKIE)?.value;
  if (!jeton) return NextResponse.json({ code: "NON_AUTHENTIFIE" }, { status: 401 });

  const entetes = new Headers({ Authorization: `Bearer ${jeton}` });
  const type = requete.headers.get("content-type");
  if (type) entetes.set("content-type", type);
  let amont: Response;
  try {
    amont = await fetch(`${URL_SERVEUR()}/${chemin}${requete.nextUrl.search}`, { method: requete.method, headers: entetes, body: modifie ? await requete.arrayBuffer() : undefined });
  } catch {
    return NextResponse.json({ code: "SERVEUR_INDISPONIBLE" }, { status: 502 });
  }
  const sortie = new Headers();
  for (const nom of ENTETES_REPONSE) {
    const v = amont.headers.get(nom);
    if (v) sortie.set(nom, v);
  }
  const reponse = new NextResponse(amont.status === 204 ? null : amont.body, { status: amont.status, headers: sortie });
  if (amont.status === 401) reponse.cookies.set(COOKIE, "", { path: "/", maxAge: 0 });
  return reponse;
}

export { gerer as GET, gerer as POST, gerer as PUT };
