import { getTranslations } from "next-intl/server";
import { Suspense } from "react";

import { Depenses } from "@/components/reversements/depenses";
import { ReversementsListe } from "@/components/reversements/reversements-liste";

export default async function ReversementsPage() {
  const t = await getTranslations("reversements");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("titre")}</h1>
      <p className="text-sm text-muted-foreground">{t("intro")}</p>
      <Suspense>
        <ReversementsListe />
      </Suspense>
      <Depenses />
    </div>
  );
}
