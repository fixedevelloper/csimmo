import { getTranslations } from "next-intl/server";

import { EnteteParametres } from "@/components/entete-parametres";
import { UtilisateursListe } from "@/components/utilisateurs/utilisateurs-liste";

export default async function UtilisateursPage() {
  const t = await getTranslations("utilisateurs");
  return (
    <div className="flex flex-col gap-6">
      <EnteteParametres titre={t("titre")} sousTitre={t("description")} />
      <UtilisateursListe />
    </div>
  );
}
