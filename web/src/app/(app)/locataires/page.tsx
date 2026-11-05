import { getTranslations } from "next-intl/server";

import { LocatairesListe } from "./locataires-liste";

export default async function LocatairesPage() {
  const t = await getTranslations("tiers");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("locataires")}</h1>
      <LocatairesListe />
    </div>
  );
}
