import { useTranslations } from "next-intl";

import { Badge } from "@/components/ui/badge";

export function StatutBadge({ statut }: { statut?: string }) {
  const t = useTranslations("patrimoine.statuts");
  if (!statut) return null;
  const variante = statut === "LIBRE" ? "secondary" : statut === "OCCUPE" ? "default" : "outline";
  return (
    <Badge variant={variante} data-testid="statut-lot">
      {t(statut)}
    </Badge>
  );
}
