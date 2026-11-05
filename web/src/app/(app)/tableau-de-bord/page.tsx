import { getTranslations } from "next-intl/server";

import { DashboardContenu } from "./dashboard-contenu";
import { PageHeader } from "@/components/page-header";

export default async function TableauDeBordPage() {
  const t = await getTranslations("dashboard");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("titre")} />
      <DashboardContenu />
    </div>
  );
}
