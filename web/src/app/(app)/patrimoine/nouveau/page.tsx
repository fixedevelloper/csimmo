import { getTranslations } from "next-intl/server";

import { ImmeubleForm } from "@/components/patrimoine/immeuble-form";

export default async function NouvelImmeublePage() {
  const t = await getTranslations("patrimoine");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("nouvelImmeuble")}</h1>
      <ImmeubleForm />
    </div>
  );
}
