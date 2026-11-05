import { getTranslations } from "next-intl/server";

import { PrestatairesListe } from "./prestataires-liste";

export default async function PrestatairesPage() {
  const t = await getTranslations("tiers");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("prestataires")}</h1>
      <PrestatairesListe />
    </div>
  );
}
