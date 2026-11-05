import type { ReactNode } from "react";

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

/** Carte de section : titre, description facultative, actions à droite du titre. */
export function Section({ titre, description, actions, children, className }: { titre?: ReactNode; description?: ReactNode; actions?: ReactNode; children: ReactNode; className?: string }) {
  return (
    <Card className={`shadow-carte ${className ?? ""}`}>
      {titre ? (
        <CardHeader className="flex-row items-start justify-between gap-3">
          <div className="flex flex-col gap-1">
            <CardTitle className="text-base">{titre}</CardTitle>
            {description ? <CardDescription>{description}</CardDescription> : null}
          </div>
          {actions}
        </CardHeader>
      ) : null}
      <CardContent>{children}</CardContent>
    </Card>
  );
}

/** Liste « libellé / valeur » sur une ou deux colonnes ; une valeur vide s'affiche « — ». */
export function InfoList({ items, colonnes = 2 }: { items: { libelle: string; valeur?: ReactNode; testid?: string }[]; colonnes?: 1 | 2 }) {
  return (
    <dl className={`grid gap-x-8 gap-y-4 text-sm ${colonnes === 2 ? "sm:grid-cols-2" : ""}`}>
      {items.map((i) => (
        <div key={i.libelle} className="flex flex-col gap-0.5">
          <dt className="text-xs font-medium uppercase tracking-wide text-muted-foreground">{i.libelle}</dt>
          <dd data-testid={i.testid} className="tabular">
            {i.valeur === undefined || i.valeur === null || i.valeur === "" ? "—" : i.valeur}
          </dd>
        </div>
      ))}
    </dl>
  );
}
