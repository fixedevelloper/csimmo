"use client";

import { Check, Copy } from "lucide-react";
import { useTranslations } from "next-intl";
import { useState } from "react";

import { Button } from "@/components/ui/button";

/** Copie un texte dans le presse-papiers ; indique « Copiée » pendant 2 secondes. */
export function CopyButton({ texte, namespace }: { texte: string; namespace: "licence" | "installation" }) {
  const t = useTranslations(namespace);
  const [copiee, setCopiee] = useState(false);

  async function copier() {
    try {
      await navigator.clipboard.writeText(texte);
      setCopiee(true);
      setTimeout(() => setCopiee(false), 2000);
    } catch {
      // Presse-papiers indisponible (page non sécurisée) : la valeur reste sélectionnable à la main
    }
  }

  return (
    <Button type="button" variant="outline" size="sm" onClick={() => void copier()}>
      {copiee ? <Check aria-hidden /> : <Copy aria-hidden />}
      {copiee ? t("copiee") : t("copier")}
    </Button>
  );
}
