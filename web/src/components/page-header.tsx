import { ChevronLeft } from "lucide-react";
import Link from "next/link";
import type { ReactNode } from "react";

type Props = {
  titre: ReactNode;
  /** Lien de retour (libellé déjà traduit). */
  retour?: { href: string; libelle: string };
  /** Pastille de statut affichée à côté du titre. */
  statut?: ReactNode;
  sousTitre?: ReactNode;
  actions?: ReactNode;
};

/** En-tête de page ou de fiche : retour, titre, statut, actions. Le titre reste le h1 de la page. */
export function PageHeader({ titre, retour, statut, sousTitre, actions }: Props) {
  return (
    <header className="flex flex-col gap-2">
      {retour ? (
        <Link href={retour.href} className="inline-flex w-fit items-center gap-1 text-sm text-muted-foreground transition-colors hover:text-foreground">
          <ChevronLeft className="size-4" aria-hidden />
          {retour.libelle}
        </Link>
      ) : null}
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex min-w-0 flex-col gap-1">
          <h1 className="flex flex-wrap items-center gap-3 text-2xl font-semibold tracking-tight">
            {titre}
            {statut}
          </h1>
          {sousTitre ? <p className="text-sm text-muted-foreground">{sousTitre}</p> : null}
        </div>
        {actions ? <div className="flex flex-wrap items-center gap-2">{actions}</div> : null}
      </div>
    </header>
  );
}
