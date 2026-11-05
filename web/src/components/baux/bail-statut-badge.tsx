import { useTranslations } from "next-intl";

import { Badge } from "@/components/ui/badge";

export function BailStatutBadge({ statut }: { statut?: string }) {
  const t = useTranslations("baux.statuts");
  if (!statut) return null;
  const variante = statut === "ACTIF" ? "default" : statut === "BROUILLON" ? "secondary" : "outline";
  return (
    <Badge variant={variante} data-testid="statut-bail">
      {t(statut)}
    </Badge>
  );
}
