import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

const photos = vi.fn();
vi.mock("@/hooks/use-patrimoine", () => ({ usePhotos: () => photos() }));
const del = vi.fn();
vi.mock("@/lib/api/client", () => ({ api: { DELETE: (...a: unknown[]) => del(...a) } }));
vi.mock("sonner", () => ({ toast: { success: vi.fn() } }));

import { PhotoGallery } from "./photo-gallery";

const rendu = () => renderIntl(<QueryClientProvider client={new QueryClient()}><PhotoGallery cible="immeubles" id="i-1" /></QueryClientProvider>);

describe("PhotoGallery", () => {
  const fetchMock = vi.fn();
  beforeEach(() => {
    vi.clearAllMocks();
    vi.stubGlobal("fetch", fetchMock);
    photos.mockReturnValue({ data: [{ id: "p-1", fichierId: "f-1", legende: "Façade", ordre: 0 }], error: null, refetch: vi.fn() });
  });

  it("affiche les photos via le proxy de l'API", () => {
    rendu();
    const image = screen.getByAltText("Façade");
    expect(image).toHaveAttribute("src", "/api/proxy/api/v1/fichiers/f-1");
    expect(screen.getByText("Façade", { selector: "span" })).toBeInTheDocument();
  });

  it("indique l'absence de photo", () => {
    photos.mockReturnValue({ data: [], error: null, refetch: vi.fn() });
    rendu();
    expect(screen.getByText("Aucune photo.")).toBeInTheDocument();
  });

  it("envoie le fichier et la légende en multipart", async () => {
    fetchMock.mockResolvedValue(new Response("{}", { status: 201 }));
    rendu();
    await userEvent.upload(screen.getByLabelText("Choisir une photo"), new File(["x"], "p.png", { type: "image/png" }));
    await userEvent.type(screen.getByLabelText("Légende (facultative)"), "Salon");
    await userEvent.click(screen.getByRole("button", { name: "Ajouter une photo" }));

    await waitFor(() => expect(fetchMock).toHaveBeenCalled());
    const [url, init] = fetchMock.mock.calls[0];
    expect(url).toBe("/api/proxy/api/v1/immeubles/i-1/photos");
    expect(init.method).toBe("POST");
    expect((init.body as FormData).get("legende")).toBe("Salon");
    expect(((init.body as FormData).get("fichier") as File).name).toBe("p.png");
  });

  it("traduit le refus d'un fichier qui n'est pas une image", async () => {
    fetchMock.mockResolvedValue(new Response(JSON.stringify({ code: "PHOTO_TYPE_INTERDIT" }), { status: 415, headers: { "Content-Type": "application/json" } }));
    rendu();
    await userEvent.upload(screen.getByLabelText("Choisir une photo"), new File(["x"], "p.png", { type: "image/png" }));
    await userEvent.click(screen.getByRole("button", { name: "Ajouter une photo" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("Une photo doit être une image");
  });

  it("supprime une photo", async () => {
    del.mockResolvedValue({ response: new Response(null, { status: 204 }) });
    rendu();
    await userEvent.click(screen.getByRole("button", { name: "Supprimer la photo" }));

    await waitFor(() => expect(del).toHaveBeenCalledWith("/api/v1/photos/{id}", { params: { path: { id: "p-1" } } }));
  });
});
