import { getTranslations } from "next-intl/server";

import { CaisseCard } from "@/components/finance/caisse-card";
import { PaiementForm } from "@/components/finance/paiement-form";
import { PaiementsListe } from "@/components/finance/paiements-liste";

export default async function EncaissementsPage() {
  const t = await getTranslations("finance");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("titre")}</h1>
      <PaiementForm />
      <CaisseCard />
      <PaiementsListe />
    </div>
  );
}
