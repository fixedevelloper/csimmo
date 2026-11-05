import { useTranslations } from "next-intl";

import { StatutPastille } from "@/components/statut-pastille";


export function BailStatutBadge({ statut }: { statut?: string }) {
  const t = useTranslations("baux.statuts");
  if (!statut) return null;
  return <StatutPastille statut={statut} libelle={t(statut)} testid="statut-bail" />;
}
