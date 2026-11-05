export class ErreurApi extends Error {
  constructor(
    readonly statut: number,
    readonly code: string | undefined,
  ) {
    super(code ?? `HTTP_${statut}`);
  }
}

/** Appel du serveur de licences via le proxy du portail (le jeton reste dans un cookie httpOnly). */
export async function appel<T>(methode: "GET" | "POST" | "PUT", chemin: string, corps?: unknown): Promise<T> {
  const reponse = await fetch(`/api/proxy/api/v1${chemin}`, {
    method: methode,
    headers: corps === undefined ? undefined : { "Content-Type": "application/json" },
    body: corps === undefined ? undefined : JSON.stringify(corps),
  });
  if (!reponse.ok) {
    const probleme = (await reponse.json().catch(() => null)) as { code?: string } | null;
    if (reponse.status === 401 && typeof window !== "undefined" && !window.location.pathname.startsWith("/login")) {
      window.location.assign("/login");
    }
    throw new ErreurApi(reponse.status, probleme?.code);
  }
  return (await reponse.json()) as T;
}

export const lister = <T,>(chemin: string, params: Record<string, string | number | undefined> = {}) => {
  const q = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) if (v !== undefined && v !== "") q.set(k, String(v));
  const s = q.toString();
  return appel<T>("GET", s ? `${chemin}?${s}` : chemin);
};
