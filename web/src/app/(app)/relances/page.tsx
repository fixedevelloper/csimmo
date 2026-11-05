import { getTranslations } from "next-intl/server";

import { ArrieresListe } from "@/components/relances/arrieres-liste";
import { PageHeader } from "@/components/page-header";

export default async function RelancesPage() {
  const t = await getTranslations("relances");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("titre")} sousTitre={t("intro")} />
      <ArrieresListe />
    </div>
  );
}
