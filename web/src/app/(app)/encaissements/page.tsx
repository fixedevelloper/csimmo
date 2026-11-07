import { getTranslations } from "next-intl/server";

import { CaisseCard } from "@/components/finance/caisse-card";
import { PaiementForm } from "@/components/finance/paiement-form";
import { PaiementsListe } from "@/components/finance/paiements-liste";
import { PageHeader } from "@/components/page-header";
import { SiPeut } from "@/components/si-peut";

export default async function EncaissementsPage() {
  const t = await getTranslations("finance");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("titre")} />
      <SiPeut action="encaisser">
        <PaiementForm />
        <CaisseCard />
      </SiPeut>
      <PaiementsListe />
    </div>
  );
}
