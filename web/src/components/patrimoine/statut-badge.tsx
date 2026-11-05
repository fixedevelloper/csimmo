import { useTranslations } from "next-intl";

import { StatutPastille } from "@/components/statut-pastille";


export function StatutBadge({ statut }: { statut?: string }) {
  const t = useTranslations("patrimoine.statuts");
  if (!statut) return null;
  return <StatutPastille statut={statut} libelle={t(statut)} testid="statut-lot" />;
}
