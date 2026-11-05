import { getTranslations } from "next-intl/server";

import { DashboardContenu } from "./dashboard-contenu";

export default async function TableauDeBordPage() {
  const t = await getTranslations("dashboard");
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("titre")}</h1>
      <DashboardContenu />
    </div>
  );
}
