import { getTranslations } from "next-intl/server";

import { ModeleBailForm } from "@/components/baux/modele-bail-form";
import { EnteteParametres } from "@/components/entete-parametres";

export default async function ModeleBailPage() {
  const t = await getTranslations("baux.modele");
  return (
    <div className="flex flex-col gap-6">
      <EnteteParametres titre={t("titre")} />
      <ModeleBailForm />
    </div>
  );
}
