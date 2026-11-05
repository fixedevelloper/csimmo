import { getTranslations } from "next-intl/server";
import Link from "next/link";

export default async function NotFound() {
  const t = await getTranslations();
  return (
    <main className="flex min-h-screen flex-col items-center justify-center gap-4 p-4 text-center">
      <h1 className="text-xl font-semibold">{t("erreur.introuvableTitre")}</h1>
      <p className="text-muted-foreground">{t("erreur.introuvable")}</p>
      <Link href="/" className="underline">
        {t("common.retourAccueil")}
      </Link>
    </main>
  );
}
