export const COOKIE = "ls_jeton";
export const URL_SERVEUR = () => (process.env.LICENSE_SERVER_URL ?? "http://localhost:8090").replace(/\/+$/, "");

export const optionsCookie = (maxAge: number) => ({
  httpOnly: true,
  secure: process.env.NODE_ENV === "production",
  sameSite: "strict" as const,
  path: "/",
  maxAge,
});

/** Refuse les requêtes qui modifient des données venant d'un autre site (en plus de SameSite=Strict). */
export function memeOrigine(origine: string | null, hote: string | null): boolean {
  if (!origine) return true;
  try {
    return new URL(origine).host === hote;
  } catch {
    return false;
  }
}
