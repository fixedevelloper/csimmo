import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";

import { renderIntl } from "@/test/render";

import { DataTable, type Colonne } from "./data-table";

type Ligne = { id: string; nom: string };
const colonnes: Colonne<Ligne>[] = [
  { id: "nom", entete: "Nom", cellule: (l) => l.nom },
];

describe("DataTable", () => {
  it("affiche les lignes, le total et la pagination", () => {
    renderIntl(
      <DataTable
        colonnes={colonnes}
        donnees={{ content: [{ id: "1", nom: "Alice" }, { id: "2", nom: "Bob" }], page: 0, size: 2, totalElements: 5 }}
        onPageChange={() => {}}
        idLigne={(l) => l.id}
      />,
    );
    expect(screen.getByText("Alice")).toBeInTheDocument();
    expect(screen.getByText("Bob")).toBeInTheDocument();
    expect(screen.getByText("5 résultats")).toBeInTheDocument();
    expect(screen.getByText("Page 1 sur 3")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Précédent" })).toBeDisabled();
    expect(screen.getByRole("button", { name: "Suivant" })).toBeEnabled();
  });

  it("demande la page suivante (index à partir de 0)", async () => {
    const onPageChange = vi.fn();
    renderIntl(
      <DataTable
        colonnes={colonnes}
        donnees={{ content: [{ id: "1", nom: "Alice" }], page: 1, size: 1, totalElements: 3 }}
        onPageChange={onPageChange}
        idLigne={(l) => l.id}
      />,
    );
    await userEvent.click(screen.getByRole("button", { name: "Suivant" }));
    expect(onPageChange).toHaveBeenCalledWith(2);
    await userEvent.click(screen.getByRole("button", { name: "Précédent" }));
    expect(onPageChange).toHaveBeenCalledWith(0);
  });

  it("désactive « Suivant » sur la dernière page", () => {
    renderIntl(
      <DataTable
        colonnes={colonnes}
        donnees={{ content: [{ id: "1", nom: "Alice" }], page: 2, size: 1, totalElements: 3 }}
        onPageChange={() => {}}
        idLigne={(l) => l.id}
      />,
    );
    expect(screen.getByRole("button", { name: "Suivant" })).toBeDisabled();
  });

  it("affiche un message quand il n'y a aucun résultat", () => {
    renderIntl(
      <DataTable colonnes={colonnes} donnees={{ content: [], page: 0, size: 20, totalElements: 0 }} onPageChange={() => {}} idLigne={(l) => l.id} />,
    );
    expect(screen.getAllByText("Aucun résultat").length).toBeGreaterThan(0);
  });

  it("n'affiche pas de lignes pendant le chargement", () => {
    renderIntl(
      <DataTable colonnes={colonnes} donnees={undefined} chargement onPageChange={() => {}} idLigne={(l) => l.id} />,
    );
    expect(screen.queryByText("Aucun résultat", { selector: "td" })).not.toBeInTheDocument();
  });
});
