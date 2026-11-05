import { getTranslations } from "next-intl/server";

import { LocatairesListe } from "./locataires-liste";
import { PageHeader } from "@/components/page-header";

export default async function LocatairesPage() {
  const t = await getTranslations("tiers");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("locataires")} />
      <LocatairesListe />
    </div>
  );
}
