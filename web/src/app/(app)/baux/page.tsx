import { getTranslations } from "next-intl/server";

import { BauxListe } from "@/components/baux/baux-liste";
import { PageHeader } from "@/components/page-header";

export default async function BauxPage() {
  const t = await getTranslations("baux");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("titre")} />
      <BauxListe />
    </div>
  );
}
