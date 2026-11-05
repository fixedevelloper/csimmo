export const ACCESS_COOKIE = "immogest_access";
export const REFRESH_COOKIE = "immogest_refresh";

/** Durée de vie du refresh token côté backend (30 jours) : le cookie suit. */
export const REFRESH_MAX_AGE = 60 * 60 * 24 * 30;

export function optionsCookie(maxAge: number) {
  return {
    httpOnly: true,
    sameSite: "lax" as const,
    // HTTPS via Caddy en production ; COOKIE_SECURE=false pour une installation en réseau local (HTTP).
    secure: process.env.NODE_ENV === "production" && process.env.COOKIE_SECURE !== "false",
    path: "/",
    maxAge,
  };
}
