import { getTranslations } from "next-intl/server";

import { EquipeListe } from "@/components/equipe/equipe-liste";
import { PageHeader } from "@/components/page-header";

export default async function EquipePage() {
  const t = await getTranslations("equipe");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("titre")} sousTitre={t("description")} />
      <EquipeListe />
    </div>
  );
}
