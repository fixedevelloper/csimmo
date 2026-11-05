import { getTranslations } from "next-intl/server";
import { Suspense } from "react";

import { Depenses } from "@/components/reversements/depenses";
import { ReversementsListe } from "@/components/reversements/reversements-liste";
import { PageHeader } from "@/components/page-header";

export default async function ReversementsPage() {
  const t = await getTranslations("reversements");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("titre")} sousTitre={t("intro")} />
      <Suspense>
        <ReversementsListe />
      </Suspense>
      <Depenses />
    </div>
  );
}
