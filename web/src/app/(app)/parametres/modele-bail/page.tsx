import { getTranslations } from "next-intl/server";

import { ModeleBailForm } from "@/components/baux/modele-bail-form";

export default async function ModeleBailPage() {
  const t = await getTranslations("baux.modele");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("titre")}</h1>
      <ModeleBailForm />
    </div>
  );
}
