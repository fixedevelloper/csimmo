import { getTranslations } from "next-intl/server";

import { ProprietairesListe } from "./proprietaires-liste";

export default async function ProprietairesPage() {
  const t = await getTranslations("tiers");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("proprietaires")}</h1>
      <ProprietairesListe />
    </div>
  );
}
