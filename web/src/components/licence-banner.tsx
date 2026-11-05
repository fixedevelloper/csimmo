"use client";

import { AlertTriangle, ShieldAlert } from "lucide-react";
import { useLocale, useTranslations } from "next-intl";
import Link from "next/link";

import { useLicence } from "@/hooks/use-licence";
import { useMe } from "@/hooks/use-me";
import { bandeauLicence, formaterDate, ROLES_LICENCE } from "@/lib/licence";
import { cn } from "cn";

/** Bandeau global : licence expirée/invalide (rouge), expiration proche ou grâce hors ligne bientôt finie (orange). */
export function LicenceBanner() {
  const t = useTranslations("bandeau");
  const locale = useLocale();
  const { data: moi } = useMe();
  const autorise = moi?.role !== undefined && (ROLES_LICENCE as readonly string[]).includes(moi.role);
  const { data: etat } = useLicence({ enabled: autorise });

  const bandeau = bandeauLicence(etat);
  if (!autorise || !bandeau) return null;

  const Icone = bandeau.niveau === "erreur" ? ShieldAlert : AlertTriangle;
  return (
    <div
      role={bandeau.niveau === "erreur" ? "alert" : "status"}
      data-testid="bandeau-licence"
      className={cn(
        "flex flex-wrap items-center gap-3 border-b px-4 py-2 text-sm",
        bandeau.niveau === "erreur"
          ? "border-destructive/30 bg-destructive/10 text-destructive"
          : "border-amber-500/40 bg-amber-100 text-amber-950 dark:bg-amber-950/40 dark:text-amber-100",
      )}
    >
      <Icone className="size-4 shrink-0" aria-hidden />
      <span className="flex-1">
        {t(bandeau.cle, { jours: bandeau.valeurs.jours ?? 0, date: formaterDate(bandeau.valeurs.date, locale) })}
      </span>
      <Link href="/parametres/licence" className="font-medium underline underline-offset-2">
        {t("voir")}
      </Link>
    </div>
  );
}
