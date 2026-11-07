import { Building2, ReceiptText, ShieldCheck } from "lucide-react";
import { getTranslations } from "next-intl/server";
import type { ReactNode } from "react";

import { LanguageSwitcher } from "@/components/language-switcher";
import { LogoComplet } from "@/components/marque-logo";

/** Page d'authentification en deux zones (sousTitre : texte sous le logo, `null` pour le masquer) : présentation de la marque à gauche (écrans larges), formulaire à droite. */
export async function AuthSplit({ children, sousTitre }: { children: ReactNode; sousTitre?: string | null }) {
  const t = await getTranslations();
  const points = [
    { icone: Building2, texte: t("auth.point1") },
    { icone: ReceiptText, texte: t("auth.point2") },
    { icone: ShieldCheck, texte: t("auth.point3") },
  ];
  return (
    <main className="grid min-h-screen lg:grid-cols-[minmax(0,5fr)_minmax(0,4fr)]">
      {/* Zone 1 : marque */}
      <section className="relative hidden overflow-hidden bg-gradient-to-br from-primary via-primary to-[oklch(0.3_0.12_262)] text-primary-foreground lg:flex lg:flex-col lg:justify-between lg:p-12">
        <div aria-hidden className="pointer-events-none absolute -right-24 -top-24 size-80 rounded-full bg-marque/25 blur-2xl" />
        <div aria-hidden className="pointer-events-none absolute -bottom-32 -left-16 size-96 rounded-full bg-white/10 blur-2xl" />
        <LogoComplet alt={t("app.nom")} className="relative w-fit shadow-carte" />
        <div className="relative flex max-w-md flex-col gap-8">
          <div className="flex flex-col gap-3">
            <span aria-hidden className="h-1 w-12 rounded-full bg-marque" />
            <h2 className="text-3xl font-semibold leading-tight tracking-tight">{t("auth.accroche")}</h2>
            <p className="text-primary-foreground/80">{t("auth.accrocheTexte")}</p>
          </div>
          <ul className="flex flex-col gap-4">
            {points.map(({ icone: Icone, texte }) => (
              <li key={texte} className="flex items-center gap-3 text-sm">
                <span aria-hidden className="flex size-9 shrink-0 items-center justify-center rounded-xl bg-white/15">
                  <Icone className="size-4" />
                </span>
                {texte}
              </li>
            ))}
          </ul>
        </div>
        <p className="relative text-xs text-primary-foreground/60">© {new Date().getFullYear()} {t("app.nom")}</p>
      </section>

      {/* Zone 2 : formulaire */}
      <section className="relative flex flex-col items-center justify-center gap-6 p-4 py-16">
        <div className="absolute right-4 top-4">
          <LanguageSwitcher />
        </div>
        <div className="text-center lg:hidden">
          <LogoComplet alt={t("app.nom")} className="mb-2" />
        </div>
        {sousTitre === null ? null : <p className="text-center text-muted-foreground">{sousTitre ?? t("auth.sousTitre")}</p>}
        {children}
      </section>
    </main>
  );
}
