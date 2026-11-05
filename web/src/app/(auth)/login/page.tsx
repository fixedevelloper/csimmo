import { getTranslations } from "next-intl/server";
import { redirect } from "next/navigation";
import { Suspense } from "react";

import { LanguageSwitcher } from "@/components/language-switcher";
import { etatInstallation } from "@/lib/auth/installation";
import { LoginForm } from "./login-form";

export default async function LoginPage() {
  // Première utilisation : l'assistant d'installation passe avant la connexion
  const installation = await etatInstallation();
  if (installation && !installation.installee) redirect("/installation");

  const t = await getTranslations();
  return (
    <main className="flex min-h-screen flex-col items-center justify-center gap-6 p-4">
      <div className="absolute right-4 top-4">
        <LanguageSwitcher />
      </div>
      <div className="text-center">
        <p className="text-2xl font-semibold">{t("app.nom")}</p>
        <p className="text-muted-foreground">{t("auth.sousTitre")}</p>
      </div>
      <Suspense>
        <LoginForm />
      </Suspense>
    </main>
  );
}
