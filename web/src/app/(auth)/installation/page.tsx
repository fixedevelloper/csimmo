import { getTranslations } from "next-intl/server";
import { redirect } from "next/navigation";

import { LanguageSwitcher } from "@/components/language-switcher";
import { etatInstallation } from "@/lib/auth/installation";
import { InstallationForm } from "./installation-form";

export default async function InstallationPage() {
  const etat = await etatInstallation();
  if (etat?.installee) redirect("/login");

  const t = await getTranslations();
  return (
    <main className="mx-auto flex min-h-screen w-full max-w-2xl flex-col gap-6 p-4 py-10">
      <div className="absolute right-4 top-4">
        <LanguageSwitcher />
      </div>
      <header className="text-center">
        <h1 className="text-2xl font-semibold">{t("installation.titre")}</h1>
        <p className="text-muted-foreground">{t("installation.sousTitre")}</p>
      </header>
      {etat ? (
        <InstallationForm empreinte={etat.empreinteServeur ?? null} />
      ) : (
        <p role="alert" className="text-center text-destructive">
          {t("installation.indisponible")}
        </p>
      )}
    </main>
  );
}
