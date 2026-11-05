import { screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

vi.mock("@/hooks/use-finance", () => ({
  useDocumentsPaiement: () => ({
    isPending: false,
    error: null,
    refetch: vi.fn(),
    data: {
      content: [
        { id: "d-1", type: "QUITTANCE", numero: "QUI-2026-000001", statut: "EMIS", montant: 160000, dateEmission: "2026-11-04", periodeDebut: "2026-10-01", periodeFin: "2026-10-31", fichierId: "f-1" },
        { id: "d-2", type: "RECU", numero: "REC-2026-000001", statut: "ANNULE", motifAnnulation: "Contre-passation", montant: 50000, dateEmission: "2026-11-04", fichierId: "f-2" },
      ],
      page: 0, size: 20, totalElements: 2,
    },
  }),
}));

import { DocumentsPaiement } from "./documents-paiement";

describe("DocumentsPaiement", () => {
  it("liste quittances et reçus avec un lien de téléchargement passant par l'API des fichiers", () => {
    renderIntl(<DocumentsPaiement bailId="b-1" />, "fr");
    expect(screen.getByText("QUI-2026-000001")).toBeInTheDocument();
    expect(screen.getByText("Quittance")).toBeInTheDocument();
    expect(screen.getByText("Reçu")).toBeInTheDocument();
    expect(screen.getByText("Annulé")).toBeInTheDocument();
    const lien = screen.getByRole("link", { name: "Télécharger QUI-2026-000001" });
    expect(lien).toHaveAttribute("href", "/api/proxy/api/v1/fichiers/f-1");
  });
});
