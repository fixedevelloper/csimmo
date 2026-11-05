import { getTranslations } from "next-intl/server";

import { BailWizard } from "@/components/baux/bail-wizard";

export default async function NouveauBailPage() {
  const t = await getTranslations("baux");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("nouveau")}</h1>
      <BailWizard />
    </div>
  );
}
