import { getTranslations } from "next-intl/server";

import { BauxListe } from "@/components/baux/baux-liste";

export default async function BauxPage() {
  const t = await getTranslations("baux");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("titre")}</h1>
      <BauxListe />
    </div>
  );
}
