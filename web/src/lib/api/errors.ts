/** Erreur renvoyée par l'API (ProblemDetail RFC 7807 avec un `code` métier stable). */
export class ApiError extends Error {
  readonly code: string;
  readonly status: number;
  readonly erreurs: string[];

  constructor(status: number, code: string, detail?: string, erreurs: string[] = []) {
    super(detail ?? code);
    this.name = "ApiError";
    this.status = status;
    this.code = code;
    this.erreurs = erreurs;
  }
}

type Problem = { code?: unknown; detail?: unknown; erreurs?: unknown };

/** Construit une ApiError à partir du corps d'une réponse en erreur (ou d'un échec réseau : status 0). */
export function enApiError(corps: unknown, status: number): ApiError {
  const p = (typeof corps === "object" && corps !== null ? corps : {}) as Problem;
  const code = typeof p.code === "string" ? p.code : status === 0 ? "SERVEUR_INDISPONIBLE" : `HTTP_${status}`;
  const erreurs = Array.isArray(p.erreurs) ? p.erreurs.filter((e): e is string => typeof e === "string") : [];
  return new ApiError(status, code, typeof p.detail === "string" ? p.detail : undefined, erreurs);
}

/** Déballe un résultat openapi-fetch : renvoie `data` ou lève une ApiError. */
export function deballer<T>(resultat: { data?: T; error?: unknown; response: Response }): T {
  if (resultat.error !== undefined || resultat.data === undefined) {
    if (resultat.response.ok) return undefined as T; // 204 No Content
    throw enApiError(resultat.error, resultat.response.status);
  }
  return resultat.data;
}

/** Message traduit pour un code d'erreur ; retombe sur HTTP_xxx puis INCONNUE. */
export function cleMessageErreur(erreur: unknown, existe: (cle: string) => boolean): string {
  if (erreur instanceof ApiError) {
    if (existe(`errors.${erreur.code}`)) return `errors.${erreur.code}`;
    if (existe(`errors.HTTP_${erreur.status}`)) return `errors.HTTP_${erreur.status}`;
  }
  if (erreur instanceof TypeError) return "errors.SERVEUR_INDISPONIBLE";
  return "errors.INCONNUE";
}
