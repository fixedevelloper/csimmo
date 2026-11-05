import { NextRequest, NextResponse } from "next/server";

import { COOKIE } from "@/lib/session";

export function middleware(requete: NextRequest) {
  if (requete.nextUrl.pathname.startsWith("/login") || requete.cookies.has(COOKIE)) return NextResponse.next();
  const url = requete.nextUrl.clone();
  url.pathname = "/login";
  url.search = "";
  return NextResponse.redirect(url);
}

export const config = { matcher: ["/((?!api|_next/static|_next/image|favicon.ico|.*\\..*).*)"] };
