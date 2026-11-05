import { getTranslations } from "next-intl/server";

import { ImmeubleForm } from "@/components/patrimoine/immeuble-form";
import { PageHeader } from "@/components/page-header";

export default async function NouvelImmeublePage() {
  const t = await getTranslations("patrimoine");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader retour={{ href: "/patrimoine", libelle: t("titre") }} titre={t("nouvelImmeuble")} />
      <ImmeubleForm />
    </div>
  );
}
