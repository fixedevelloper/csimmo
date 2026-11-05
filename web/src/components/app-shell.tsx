"use client";

import { useQueryClient } from "@tanstack/react-query";
import {
  Building2,
  CalendarClock,
  ClipboardList,
  HandCoins,
  FileText,
  LayoutDashboard,
  LogOut,
  Menu,
  Settings,
  Users,
  Wallet,
  Wrench,
  X,
  Contact,
  type LucideIcon,
} from "lucide-react";
import { useTranslations } from "next-intl";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useCallback, useState, type ReactNode } from "react";

import { LanguageSwitcher } from "@/components/language-switcher";
import { LicenceBanner } from "@/components/licence-banner";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { useInactivityLogout } from "@/hooks/use-inactivity-logout";
import { useMe } from "@/hooks/use-me";
import { cn } from "cn";

const INACTIVITE_MS = 30 * 60 * 1000;

type Entree = { href: string; cle: string; icone: LucideIcon; gestion: boolean };

const MENU: Entree[] = [
  { href: "/tableau-de-bord", cle: "dashboard", icone: LayoutDashboard, gestion: false },
  { href: "/patrimoine", cle: "patrimoine", icone: Building2, gestion: true },
  { href: "/locataires", cle: "locataires", icone: Users, gestion: true },
  { href: "/baux", cle: "baux", icone: FileText, gestion: true },
  { href: "/encaissements", cle: "encaissements", icone: Wallet, gestion: true },
  { href: "/relances", cle: "relances", icone: CalendarClock, gestion: true },
  { href: "/reversements", cle: "reversements", icone: HandCoins, gestion: true },
  { href: "/signalements", cle: "signalements", icone: ClipboardList, gestion: true },
  { href: "/proprietaires", cle: "proprietaires", icone: Contact, gestion: true },
  { href: "/prestataires", cle: "prestataires", icone: Wrench, gestion: true },
  { href: "/parametres", cle: "parametres", icone: Settings, gestion: true },
];

/** Rôles qui utilisent le web pour la gestion (les autres n'ont que le tableau de bord pour l'instant). */
const ROLES_GESTION = ["ADMIN", "GESTIONNAIRE"];

export function AppShell({ children }: { children: ReactNode }) {
  const t = useTranslations();
  const pathname = usePathname();
  const router = useRouter();
  const queryClient = useQueryClient();
  const { data: moi } = useMe();
  const [menuOuvert, setMenuOuvert] = useState(false);

  const deconnecter = useCallback(
    async (raison?: string) => {
      await fetch("/api/auth/logout", { method: "POST" }).catch(() => undefined);
      queryClient.clear();
      router.replace(raison ? `/login?raison=${raison}` : "/login");
      router.refresh();
    },
    [queryClient, router],
  );

  useInactivityLogout(INACTIVITE_MS, () => void deconnecter("inactivite"));

  const entrees = MENU.filter((e) => !e.gestion || (moi?.role !== undefined && ROLES_GESTION.includes(moi.role)));

  return (
    <div className="flex min-h-screen">
      <aside
        className={cn(
          "fixed inset-y-0 left-0 z-40 flex w-64 flex-col border-r bg-sidebar text-sidebar-foreground transition-transform md:static md:translate-x-0",
          menuOuvert ? "translate-x-0" : "-translate-x-full",
        )}
      >
        <div className="flex h-14 items-center justify-between px-4">
          <span className="text-lg font-semibold">{t("app.nom")}</span>
          <Button
            variant="ghost"
            size="icon"
            className="md:hidden"
            aria-label={t("nav.fermerMenu")}
            onClick={() => setMenuOuvert(false)}
          >
            <X />
          </Button>
        </div>
        <Separator />
        <nav aria-label={t("nav.principale")} className="flex flex-1 flex-col gap-1 p-2">
          {entrees.map(({ href, cle, icone: Icone }) => {
            const actif = pathname === href || pathname.startsWith(`${href}/`);
            return (
              <Link
                key={href}
                href={href}
                onClick={() => setMenuOuvert(false)}
                aria-current={actif ? "page" : undefined}
                className={cn(
                  "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium hover:bg-sidebar-accent",
                  actif && "bg-sidebar-accent text-sidebar-accent-foreground",
                )}
              >
                <Icone className="size-4" aria-hidden />
                {t(`nav.${cle}`)}
              </Link>
            );
          })}
        </nav>
      </aside>
      {menuOuvert ? (
        <div className="fixed inset-0 z-30 bg-black/40 md:hidden" aria-hidden onClick={() => setMenuOuvert(false)} />
      ) : null}

      <div className="flex min-w-0 flex-1 flex-col">
        <header className="flex h-14 items-center justify-between gap-2 border-b px-4">
          <Button
            variant="ghost"
            size="icon"
            className="md:hidden"
            aria-label={t("nav.ouvrirMenu")}
            onClick={() => setMenuOuvert(true)}
          >
            <Menu />
          </Button>
          <div className="ml-auto flex items-center gap-3">
            {moi ? (
              <div className="hidden text-right text-sm leading-tight sm:block">
                <div className="font-medium">{moi.nom}</div>
                <div className="text-xs text-muted-foreground">{t(`roles.${moi.role}`)}</div>
              </div>
            ) : null}
            <LanguageSwitcher />
            <Button variant="outline" size="sm" onClick={() => void deconnecter()}>
              <LogOut aria-hidden />
              {t("auth.deconnexion")}
            </Button>
          </div>
        </header>
        <LicenceBanner />
        <main className="flex-1 p-6">{children}</main>
      </div>
    </div>
  );
}
