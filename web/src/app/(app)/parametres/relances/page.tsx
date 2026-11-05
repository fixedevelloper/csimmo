import { getTranslations } from "next-intl/server";

import { ParametresRelanceForm } from "@/components/relances/parametres-relance-form";

export default async function ParametresRelancesPage() {
  const t = await getTranslations("relances.parametres");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("titre")}</h1>
      <ParametresRelanceForm />
    </div>
  );
}
