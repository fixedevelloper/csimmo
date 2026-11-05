"use client";

import { useLocale, useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { useTransition } from "react";

import { changerLangue } from "@/app/actions/locale";
import { Button } from "@/components/ui/button";
import { LOCALES } from "@/i18n/locales";

export function LanguageSwitcher() {
  const t = useTranslations("common");
  const locale = useLocale();
  const router = useRouter();
  const [, demarrer] = useTransition();

  return (
    <div role="group" aria-label={t("langue")} className="flex gap-1">
      {LOCALES.map((l) => (
        <Button
          key={l}
          size="sm"
          variant={l === locale ? "default" : "ghost"}
          aria-pressed={l === locale}
          lang={l}
          onClick={() =>
            demarrer(async () => {
              await changerLangue(l);
              router.refresh();
            })
          }
        >
          {l.toUpperCase()}
        </Button>
      ))}
    </div>
  );
}
