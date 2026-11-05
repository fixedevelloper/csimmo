import { getTranslations } from "next-intl/server";

import { SignalementsListe } from "@/components/signalements/signalements-liste";
import { PageHeader } from "@/components/page-header";

export default async function SignalementsPage() {
  const t = await getTranslations("signalements");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("titre")} sousTitre={t("intro")} />
      <SignalementsListe />
    </div>
  );
}
