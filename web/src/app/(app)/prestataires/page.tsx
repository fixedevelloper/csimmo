import { getTranslations } from "next-intl/server";

import { PrestatairesListe } from "./prestataires-liste";
import { PageHeader } from "@/components/page-header";

export default async function PrestatairesPage() {
  const t = await getTranslations("tiers");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("prestataires")} />
      <PrestatairesListe />
    </div>
  );
}
