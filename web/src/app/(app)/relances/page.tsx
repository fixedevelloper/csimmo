import { getTranslations } from "next-intl/server";

import { ArrieresListe } from "@/components/relances/arrieres-liste";

export default async function RelancesPage() {
  const t = await getTranslations("relances");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("titre")}</h1>
      <p className="text-sm text-muted-foreground">{t("intro")}</p>
      <ArrieresListe />
    </div>
  );
}
