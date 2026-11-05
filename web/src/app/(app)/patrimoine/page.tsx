import { getTranslations } from "next-intl/server";

import { ImmeublesListe } from "./immeubles-liste";
import { PageHeader } from "@/components/page-header";

export default async function PatrimoinePage() {
  const t = await getTranslations("patrimoine");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("titre")} />
      <ImmeublesListe />
    </div>
  );
}
