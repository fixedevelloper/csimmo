import { NextRequest } from "next/server";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { POST as connexion } from "@/app/api/auth/login/route";
import { GET, POST } from "@/app/api/proxy/[...path]/route";

const fetchMock = vi.fn();
beforeEach(() => vi.stubGlobal("fetch", fetchMock));
afterEach(() => {
  fetchMock.mockReset();
  vi.unstubAllGlobals();
});

const ctx = (...path: string[]) => ({ params: Promise.resolve({ path }) });
const requete = (url: string, init: { method?: string; cookie?: string; origin?: string; body?: string } = {}) =>
  new NextRequest(`http://portail.test${url}`, {
    method: init.method ?? "GET",
    headers: { host: "portail.test", ...(init.cookie ? { cookie: init.cookie } : {}), ...(init.origin ? { origin: init.origin } : {}), "content-type": "application/json" },
    body: init.body,
  });

describe("proxy", () => {
  it("refuse sans session, sans appeler le serveur", async () => {
    const r = await GET(requete("/api/proxy/api/v1/clients"), ctx("api", "v1", "clients"));
    expect(r.status).toBe(401);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("n'expose que clients, licences et versions", async () => {
    for (const chemin of [["api", "v1", "verification"], ["api", "v1", "auth", "login"], ["actuator", "env"], ["api", "v1", "clients", "..", "..", "auth"]]) {
      const r = await GET(requete("/x", { cookie: "ls_jeton=abc" }), ctx(...chemin));
      expect(r.status).toBe(404);
    }
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("ajoute le jeton du cookie et transmet la requête", async () => {
    fetchMock.mockResolvedValue(new Response(JSON.stringify({ content: [] }), { status: 200, headers: { "content-type": "application/json" } }));
    const r = await GET(requete("/api/proxy/api/v1/licences?statut=ACTIVE", { cookie: "ls_jeton=abc" }), ctx("api", "v1", "licences"));
    expect(r.status).toBe(200);
    const [url, init] = fetchMock.mock.calls[0];
    expect(url).toBe("http://localhost:8090/api/v1/licences?statut=ACTIVE");
    expect((init.headers as Headers).get("authorization")).toBe("Bearer abc");
  });

  it("refuse une modification venant d'un autre site", async () => {
    const r = await POST(requete("/api/proxy/api/v1/clients", { method: "POST", cookie: "ls_jeton=abc", origin: "http://pirate.test", body: "{}" }), ctx("api", "v1", "clients"));
    expect(r.status).toBe(403);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("efface le cookie quand le serveur répond 401", async () => {
    fetchMock.mockResolvedValue(new Response("{}", { status: 401 }));
    const r = await GET(requete("/x", { cookie: "ls_jeton=perime" }), ctx("api", "v1", "clients"));
    expect(r.status).toBe(401);
    expect(r.headers.get("set-cookie")).toMatch(/ls_jeton=;/);
  });

  it("signale un serveur de licences injoignable", async () => {
    fetchMock.mockRejectedValue(new Error("ECONNREFUSED"));
    const r = await GET(requete("/x", { cookie: "ls_jeton=abc" }), ctx("api", "v1", "clients"));
    expect(r.status).toBe(502);
  });
});

describe("connexion", () => {
  it("pose le jeton dans un cookie httpOnly sans le renvoyer dans le corps", async () => {
    fetchMock.mockResolvedValue(new Response(JSON.stringify({ jeton: "JWT-SECRET", expireDans: 28800 }), { status: 200 }));
    const r = await connexion(requete("/api/auth/login", { method: "POST", body: JSON.stringify({ email: "a@b.cm", motDePasse: "x" }) }));
    expect(r.status).toBe(200);
    expect(await r.text()).not.toContain("JWT-SECRET");
    const cookie = r.headers.get("set-cookie") ?? "";
    expect(cookie).toContain("ls_jeton=JWT-SECRET");
    expect(cookie).toMatch(/HttpOnly/i);
    expect(cookie).toMatch(/SameSite=strict/i);
  });

  it("relaie le code d'erreur et ne pose aucun cookie", async () => {
    fetchMock.mockResolvedValue(new Response(JSON.stringify({ code: "COMPTE_VERROUILLE", detail: "x" }), { status: 429 }));
    const r = await connexion(requete("/api/auth/login", { method: "POST", body: "{}" }));
    expect(r.status).toBe(429);
    expect(await r.json()).toEqual({ code: "COMPTE_VERROUILLE" });
    expect(r.headers.get("set-cookie")).toBeNull();
  });
});
