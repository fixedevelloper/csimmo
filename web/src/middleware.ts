import { NextRequest, NextResponse } from "next/server";

import { ACCESS_COOKIE, REFRESH_COOKIE } from "@/lib/auth/cookies";

/**
 * Protège les pages : sans cookie de session on renvoie vers /login.
 * Le rafraîchissement du jeton se fait dans le proxy (une seule fois, mutualisé), pas ici.
 */
export function middleware(requete: NextRequest) {
  const { pathname, search } = requete.nextUrl;
  // Pages publiques : connexion et assistant de premier lancement (aucune session n'existe encore)
  if (pathname.startsWith("/login") || pathname.startsWith("/installation")) return NextResponse.next();

  const session = requete.cookies.has(ACCESS_COOKIE) || requete.cookies.has(REFRESH_COOKIE);
  if (!session) {
    const url = requete.nextUrl.clone();
    url.pathname = "/login";
    url.search = "";
    if (pathname !== "/") url.searchParams.set("next", pathname + search);
    return NextResponse.redirect(url);
  }
  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico|.*\\..*).*)"],
};
