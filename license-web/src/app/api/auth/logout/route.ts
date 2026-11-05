import { NextResponse } from "next/server";

import { COOKIE, optionsCookie } from "@/lib/session";

export async function POST(): Promise<NextResponse> {
  const reponse = NextResponse.json({ ok: true });
  reponse.cookies.set(COOKIE, "", optionsCookie(0));
  return reponse;
}
