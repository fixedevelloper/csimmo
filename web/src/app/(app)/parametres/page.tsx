import { Bell, BellRing, DatabaseBackup, FileSpreadsheet, FileText, KeyRound, ShieldCheck, Smartphone } from "lucide-react";
import { getTranslations } from "next-intl/server";
import Link from "next/link";

import { Card, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { PageHeader } from "@/components/page-header";

export default async function ParametresPage() {
  const t = await getTranslations("parametres");
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("titre")} />
      <div className="grid gap-4 sm:grid-cols-2">
        <Link href="/parametres/licence" className="rounded-xl focus-visible:outline-2 focus-visible:outline-ring">
          <Card className="h-full transition-colors hover:bg-muted/50">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <KeyRound className="size-4" aria-hidden />
                {t("licence")}
              </CardTitle>
              <CardDescription>{t("licenceDescription")}</CardDescription>
            </CardHeader>
          </Card>
        </Link>
        <Link href="/parametres/conformite" className="rounded-xl focus-visible:outline-2 focus-visible:outline-ring">
          <Card className="h-full transition-colors hover:bg-muted/50">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <ShieldCheck className="size-4" aria-hidden />
                {t("conformite")}
              </CardTitle>
              <CardDescription>{t("conformiteDescription")}</CardDescription>
            </CardHeader>
          </Card>
        </Link>
        <Link href="/parametres/import" className="rounded-xl focus-visible:outline-2 focus-visible:outline-ring">
          <Card className="h-full transition-colors hover:bg-muted/50">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <FileSpreadsheet className="size-4" aria-hidden />
                {t("import")}
              </CardTitle>
              <CardDescription>{t("importDescription")}</CardDescription>
            </CardHeader>
          </Card>
        </Link>
        <Link href="/parametres/sauvegardes" className="rounded-xl focus-visible:outline-2 focus-visible:outline-ring">
          <Card className="h-full transition-colors hover:bg-muted/50">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <DatabaseBackup className="size-4" aria-hidden />
                {t("sauvegardes")}
              </CardTitle>
              <CardDescription>{t("sauvegardesDescription")}</CardDescription>
            </CardHeader>
          </Card>
        </Link>
        <Link href="/parametres/paiement-en-ligne" className="rounded-xl focus-visible:outline-2 focus-visible:outline-ring">
          <Card className="h-full transition-colors hover:bg-muted/50">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Smartphone className="size-4" aria-hidden />
                {t("paiementEnLigne")}
              </CardTitle>
              <CardDescription>{t("paiementEnLigneDescription")}</CardDescription>
            </CardHeader>
          </Card>
        </Link>
        <Link href="/parametres/relances" className="rounded-xl focus-visible:outline-2 focus-visible:outline-ring">
          <Card className="h-full transition-colors hover:bg-muted/50">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <BellRing className="size-4" aria-hidden />
                {t("relances")}
              </CardTitle>
              <CardDescription>{t("relancesDescription")}</CardDescription>
            </CardHeader>
          </Card>
        </Link>
        <Link href="/parametres/notifications" className="rounded-xl focus-visible:outline-2 focus-visible:outline-ring">
          <Card className="h-full transition-colors hover:bg-muted/50">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Bell className="size-4" aria-hidden />
                {t("notifications")}
              </CardTitle>
              <CardDescription>{t("notificationsDescription")}</CardDescription>
            </CardHeader>
          </Card>
        </Link>
        <Link href="/parametres/modele-bail" className="rounded-xl focus-visible:outline-2 focus-visible:outline-ring">
          <Card className="h-full transition-colors hover:bg-muted/50">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <FileText className="size-4" aria-hidden />
                {t("modeleBail")}
              </CardTitle>
              <CardDescription>{t("modeleBailDescription")}</CardDescription>
            </CardHeader>
          </Card>
        </Link>
      </div>
    </div>
  );
}
