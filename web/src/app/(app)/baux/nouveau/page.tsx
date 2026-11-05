import { getTranslations } from "next-intl/server";

import { BailWizard } from "@/components/baux/bail-wizard";
import { PageHeader } from "@/components/page-header";

export default async function NouveauBailPage() {
  const t = await getTranslations("baux");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader retour={{ href: "/baux", libelle: t("titre") }} titre={t("nouveau")} />
      <BailWizard />
    </div>
  );
}
