"use client";

import { useTranslations } from "next-intl";
import { useDeferredValue, useState } from "react";

import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { useImmeublesChoix } from "@/hooks/use-equipe";

type Props = {
  valeur: string[];
  onChange: (ids: string[]) => void;
};

/** Choix des immeubles affectés à un employé : recherche, cases à cocher ; la sélection est conservée d'une recherche à l'autre. */
export function ChoixImmeubles({ valeur, onChange }: Props) {
  const t = useTranslations("equipe");
  const [saisie, setSaisie] = useState("");
  const q = useDeferredValue(saisie.trim());
  const { data: immeubles, isPending } = useImmeublesChoix(q);
  const choisis = new Set(valeur);

  function basculer(id: string, coche: boolean) {
    const suivant = new Set(choisis);
    if (coche) suivant.add(id);
    else suivant.delete(id);
    onChange([...suivant]);
  }

  return (
    <fieldset className="flex flex-col gap-2">
      <legend className="text-sm font-medium">{t("immeublesAffectes")}</legend>
      <p className="text-xs text-muted-foreground">{t("immeublesAide")}</p>
      <Input aria-label={t("rechercherImmeuble")} placeholder={t("rechercherImmeuble")} value={saisie} onChange={(e) => setSaisie(e.target.value)} />
      <ul className="max-h-56 overflow-auto rounded-lg border">
        {(immeubles ?? []).map((i) => (
          <li key={i.id} className="border-b last:border-b-0">
            <label className="flex cursor-pointer items-center gap-3 px-3 py-2 text-sm hover:bg-muted/40">
              <Checkbox checked={choisis.has(i.id ?? "")} onCheckedChange={(c) => basculer(i.id ?? "", c === true)} />
              <span className="flex flex-col">
                <span className="font-medium">{i.nom}</span>
                <span className="text-xs text-muted-foreground">{[i.adresse, i.ville].filter(Boolean).join(", ")}</span>
              </span>
            </label>
          </li>
        ))}
        {!isPending && (immeubles ?? []).length === 0 ? <li className="px-3 py-2 text-sm text-muted-foreground">{t("aucunImmeuble")}</li> : null}
      </ul>
      <p className="text-xs text-muted-foreground" aria-live="polite">
        {t("selection", { count: choisis.size })}
      </p>
    </fieldset>
  );
}
