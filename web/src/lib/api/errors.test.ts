import { describe, expect, it } from "vitest";

import { ApiError, cleMessageErreur, deballer, enApiError } from "./errors";

const existe = (cles: string[]) => (cle: string) => cles.includes(cle);

describe("enApiError", () => {
  it("lit le code et le détail d'un ProblemDetail", () => {
    const e = enApiError({ code: "BAIL_LOT_DEJA_OCCUPE", detail: "Lot occupé", erreurs: ["a: x", 3] }, 422);
    expect(e).toBeInstanceOf(ApiError);
    expect(e.code).toBe("BAIL_LOT_DEJA_OCCUPE");
    expect(e.status).toBe(422);
    expect(e.erreurs).toEqual(["a: x"]);
  });

  it("déduit un code HTTP_xxx quand le corps n'en a pas, et SERVEUR_INDISPONIBLE pour un échec réseau", () => {
    expect(enApiError(null, 404).code).toBe("HTTP_404");
    expect(enApiError(undefined, 0).code).toBe("SERVEUR_INDISPONIBLE");
    expect(enApiError("texte", 500).code).toBe("HTTP_500");
  });
});

describe("deballer", () => {
  it("renvoie les données d'une réponse réussie", () => {
    expect(deballer({ data: { a: 1 }, response: new Response() })).toEqual({ a: 1 });
  });

  it("renvoie undefined pour un 204", () => {
    expect(deballer({ response: new Response(null, { status: 204 }) })).toBeUndefined();
  });

  it("lève une ApiError avec le code du serveur", () => {
    const appel = () => deballer({ error: { code: "ACCES_REFUSE" }, response: new Response(null, { status: 403 }) });
    expect(appel).toThrowError(ApiError);
    try {
      appel();
    } catch (e) {
      expect((e as ApiError).code).toBe("ACCES_REFUSE");
    }
  });
});

describe("cleMessageErreur", () => {
  const connues = existe(["errors.ACCES_REFUSE", "errors.HTTP_404", "errors.INCONNUE", "errors.SERVEUR_INDISPONIBLE"]);

  it("utilise le code métier s'il est traduit", () => {
    expect(cleMessageErreur(new ApiError(403, "ACCES_REFUSE"), connues)).toBe("errors.ACCES_REFUSE");
  });

  it("retombe sur le statut HTTP puis sur INCONNUE", () => {
    expect(cleMessageErreur(new ApiError(404, "CODE_NON_TRADUIT"), connues)).toBe("errors.HTTP_404");
    expect(cleMessageErreur(new ApiError(418, "CODE_NON_TRADUIT"), connues)).toBe("errors.INCONNUE");
    expect(cleMessageErreur(new Error("boum"), connues)).toBe("errors.INCONNUE");
  });

  it("signale un serveur injoignable pour une erreur réseau (fetch)", () => {
    expect(cleMessageErreur(new TypeError("Failed to fetch"), connues)).toBe("errors.SERVEUR_INDISPONIBLE");
  });
});
