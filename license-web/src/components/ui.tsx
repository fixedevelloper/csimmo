import type { ReactNode } from "react";

import { LIBELLE_NIVEAU, niveau, type Licence } from "@/lib/licences";

export const classeChamp = "w-full rounded-md border border-neutral-300 bg-transparent px-3 py-2 text-sm dark:border-neutral-700";
export const classeBouton = "rounded-md bg-blue-700 px-4 py-2 text-sm font-medium text-white hover:bg-blue-800 disabled:opacity-50";
export const classeBoutonSecondaire = "rounded-md border border-neutral-300 px-4 py-2 text-sm font-medium hover:bg-neutral-100 dark:border-neutral-700 dark:hover:bg-neutral-900";

export function Champ({ id, label, children, aide }: { id: string; label: string; children: ReactNode; aide?: string }) {
  return (
    <div className="flex flex-col gap-1">
      <label htmlFor={id} className="text-sm font-medium">
        {label}
      </label>
      {children}
      {aide ? <p className="text-xs text-neutral-500">{aide}</p> : null}
    </div>
  );
}

export function MessageErreur({ message }: { message: string | null }) {
  return message ? (
    <p role="alert" className="rounded-md border border-red-300 bg-red-50 px-3 py-2 text-sm text-red-800 dark:bg-red-950 dark:text-red-200">
      {message}
    </p>
  ) : null;
}

const COULEURS = {
  expiree: "bg-red-100 text-red-800",
  critique: "bg-orange-100 text-orange-800",
  proche: "bg-yellow-100 text-yellow-800",
  ok: "bg-green-100 text-green-800",
  revoquee: "bg-neutral-200 text-neutral-800",
} as const;

/** Statut d'une licence : toujours en texte (la couleur seule ne porte pas l'information). */
export function BadgeLicence({ licence }: { licence: Pick<Licence, "statut" | "joursRestants"> }) {
  const n = niveau(licence);
  return (
    <span className={`inline-block rounded-full px-2 py-0.5 text-xs font-medium ${COULEURS[n]}`} data-niveau={n}>
      {LIBELLE_NIVEAU[n]}
    </span>
  );
}

export function Pagination({ page, taille, total, onChange }: { page: number; taille: number; total: number; onChange: (p: number) => void }) {
  const pages = Math.max(1, Math.ceil(total / taille));
  if (pages <= 1) return null;
  return (
    <nav className="flex items-center gap-3 text-sm" aria-label="Pagination">
      <button className={classeBoutonSecondaire} disabled={page === 0} onClick={() => onChange(page - 1)}>
        Précédent
      </button>
      <span>
        Page {page + 1} / {pages}
      </span>
      <button className={classeBoutonSecondaire} disabled={page + 1 >= pages} onClick={() => onChange(page + 1)}>
        Suivant
      </button>
    </nav>
  );
}
