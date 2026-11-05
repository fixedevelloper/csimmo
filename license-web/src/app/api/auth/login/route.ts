import { NextRequest, NextResponse } from "next/server";

import { COOKIE, memeOrigine, optionsCookie, URL_SERVEUR } from "@/lib/session";

export async function POST(requete: NextRequest): Promise<NextResponse> {
  if (!memeOrigine(requete.headers.get("origin"), requete.headers.get("x-forwarded-host") ?? requete.headers.get("host"))) {
    return NextResponse.json({ code: "ACCES_REFUSE" }, { status: 403 });
  }
  let amont: Response;
  try {
    amont = await fetch(`${URL_SERVEUR()}/api/v1/auth/login`, { method: "POST", headers: { "Content-Type": "application/json" }, body: await requete.text() });
  } catch {
    return NextResponse.json({ code: "SERVEUR_INDISPONIBLE" }, { status: 502 });
  }
  if (!amont.ok) {
    const corps = (await amont.json().catch(() => null)) as { code?: string } | null;
    return NextResponse.json({ code: corps?.code ?? `HTTP_${amont.status}` }, { status: amont.status });
  }
  const { jeton, expireDans } = (await amont.json()) as { jeton: string; expireDans: number };
  const reponse = NextResponse.json({ ok: true });
  reponse.cookies.set(COOKIE, jeton, optionsCookie(expireDans)); // jamais renvoyé au navigateur dans le corps
  return reponse;
}
