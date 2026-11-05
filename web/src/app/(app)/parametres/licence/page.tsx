import { getTranslations } from "next-intl/server";
import Link from "next/link";

import { LicenceContenu } from "./licence-contenu";

export default async function LicencePage() {
  const t = await getTranslations("licence");
  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href="/parametres" className="text-sm text-muted-foreground underline underline-offset-2">
          {t("retour")}
        </Link>
        <h1 className="text-2xl font-semibold">{t("titre")}</h1>
      </div>
      <LicenceContenu />
    </div>
  );
}
