import { getTranslations } from "next-intl/server";

import { SignalementsListe } from "@/components/signalements/signalements-liste";

export default async function SignalementsPage() {
  const t = await getTranslations("signalements");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("titre")}</h1>
      <p className="text-sm text-muted-foreground">{t("intro")}</p>
      <SignalementsListe />
    </div>
  );
}
