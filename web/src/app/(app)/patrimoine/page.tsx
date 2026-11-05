import { getTranslations } from "next-intl/server";

import { ImmeublesListe } from "./immeubles-liste";

export default async function PatrimoinePage() {
  const t = await getTranslations("patrimoine");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("titre")}</h1>
      <ImmeublesListe />
    </div>
  );
}
