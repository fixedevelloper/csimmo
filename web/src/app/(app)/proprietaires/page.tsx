import { getTranslations } from "next-intl/server";

import { ProprietairesListe } from "./proprietaires-liste";
import { PageHeader } from "@/components/page-header";

export default async function ProprietairesPage() {
  const t = await getTranslations("tiers");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("proprietaires")} />
      <ProprietairesListe />
    </div>
  );
}
