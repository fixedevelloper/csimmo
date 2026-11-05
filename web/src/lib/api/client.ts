import createClient, { type Middleware } from "openapi-fetch";

import type { paths } from "./schema";

/** Si l'API répond 401 (session perdue), on retourne à la page de connexion. */
const sessionExpiree: Middleware = {
  onResponse({ response }) {
    if (response.status === 401 && typeof window !== "undefined" && !window.location.pathname.startsWith("/login")) {
      const retour = encodeURIComponent(window.location.pathname + window.location.search);
      window.location.assign(`/login?raison=session&next=${retour}`);
    }
    return undefined;
  },
};

/**
 * Client typé de l'API. Il passe par le proxy Next (`/api/proxy`) qui ajoute le jeton :
 * le navigateur ne manipule jamais de jeton.
 */
export const api = createClient<paths>({ baseUrl: "/api/proxy" });
api.use(sessionExpiree);
