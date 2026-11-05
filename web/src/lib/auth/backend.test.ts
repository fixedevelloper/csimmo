import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

vi.mock("server-only", () => ({}));

import { rafraichir } from "./backend";

const reponseOk = (acces: string, refresh: string) =>
  new Response(JSON.stringify({ accessToken: acces, refreshToken: refresh, expiresIn: 900 }), { status: 200 });

describe("rafraichir (mutualisé)", () => {
  const fetchMock = vi.fn();
  beforeEach(() => vi.stubGlobal("fetch", fetchMock));
  afterEach(() => {
    vi.unstubAllGlobals();
    vi.useRealTimers();
    fetchMock.mockReset();
  });

  it("n'appelle le backend qu'une fois pour des requêtes parallèles portant le même jeton", async () => {
    fetchMock.mockImplementation(() => Promise.resolve(reponseOk("acces-1", "refresh-2")));

    const [a, b, c] = await Promise.all([rafraichir("ancien-A"), rafraichir("ancien-A"), rafraichir("ancien-A")]);

    expect(fetchMock).toHaveBeenCalledTimes(1);
    expect(a?.refreshToken).toBe("refresh-2");
    expect(b).toBe(a);
    expect(c).toBe(a);
  });

  it("réutilise le résultat pour une requête tardive qui porte encore l'ancien jeton", async () => {
    fetchMock.mockImplementation(() => Promise.resolve(reponseOk("acces-1", "refresh-2")));

    await rafraichir("ancien-B");
    const tardive = await rafraichir("ancien-B");

    expect(fetchMock).toHaveBeenCalledTimes(1);
    expect(tardive?.accessToken).toBe("acces-1");
  });

  it("rappelle le backend une fois la fenêtre de 30 s écoulée", async () => {
    vi.useFakeTimers();
    fetchMock.mockImplementation(() => Promise.resolve(reponseOk("a", "r")));

    await rafraichir("ancien-C");
    vi.advanceTimersByTime(31_000);
    await rafraichir("ancien-C");

    expect(fetchMock).toHaveBeenCalledTimes(2);
  });

  it("renvoie null si le backend refuse ou est injoignable", async () => {
    fetchMock.mockResolvedValueOnce(new Response("{}", { status: 401 }));
    expect(await rafraichir("ancien-D")).toBeNull();

    fetchMock.mockRejectedValueOnce(new TypeError("down"));
    expect(await rafraichir("ancien-E")).toBeNull();
  });
});
