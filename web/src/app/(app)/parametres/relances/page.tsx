import { getTranslations } from "next-intl/server";

import { ParametresRelanceForm } from "@/components/relances/parametres-relance-form";
import { EnteteParametres } from "@/components/entete-parametres";

export default async function ParametresRelancesPage() {
  const t = await getTranslations("relances.parametres");
  return (
    <div className="flex flex-col gap-6">
      <EnteteParametres titre={t("titre")} />
      <ParametresRelanceForm />
    </div>
  );
}
