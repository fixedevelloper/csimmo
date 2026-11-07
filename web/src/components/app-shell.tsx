
"use client";

import { useQueryClient } from "@tanstack/react-query";
import {
  Building2,
  CalendarClock,
  ClipboardList,
  HandCoins,
  FileText,
  LayoutDashboard,
  ChevronDown,
  LogOut,
  Menu,
  Monitor,
  Moon,
  PanelLeftClose,
  PanelLeftOpen,
  Sun,
  Settings,
  Users,
  Wallet,
  Wrench,
  X,
  Contact,
  type LucideIcon,
} from "lucide-react";
import { useTheme } from "next-themes";
import { useTranslations } from "next-intl";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useCallback, useEffect, useState, type ReactNode } from "react";

import { LanguageSwitcher } from "@/components/language-switcher";
import { LicenceBanner } from "@/components/licence-banner";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { LogoCompact } from "@/components/marque-logo";
import { useInactivityLogout } from "@/hooks/use-inactivity-logout";
import { useMe } from "@/hooks/use-me";
import { cn } from "cn";

const INACTIVITE_MS = 30 * 60 * 1000;

type Entree = {
  href: string;
  cle: string;
  icone: LucideIcon;
  gestion: boolean;
};

type Groupe = {
  cle: string;
  entrees: Entree[];
};

const GROUPES: Groupe[] = [
  {
    cle: "vue",
    entrees: [
      {
        href: "/tableau-de-bord",
        cle: "dashboard",
        icone: LayoutDashboard,
        gestion: false,
      },
    ],
  },
  {
    cle: "activite",
    entrees: [
      { href: "/patrimoine", cle: "patrimoine", icone: Building2, gestion: true },
      { href: "/locataires", cle: "locataires", icone: Users, gestion: true },
      { href: "/baux", cle: "baux", icone: FileText, gestion: true },
      {
        href: "/signalements",
        cle: "signalements",
        icone: ClipboardList,
        gestion: true,
      },
    ],
  },
  {
    cle: "finance",
    entrees: [
      { href: "/encaissements", cle: "encaissements", icone: Wallet, gestion: true },
      { href: "/relances", cle: "relances", icone: CalendarClock, gestion: true },
      { href: "/reversements", cle: "reversements", icone: HandCoins, gestion: true },
    ],
  },
  {
    cle: "reference",
    entrees: [
      { href: "/proprietaires", cle: "proprietaires", icone: Contact, gestion: true },
      { href: "/prestataires", cle: "prestataires", icone: Wrench, gestion: true },
    ],
  },
  {
    cle: "administration",
    entrees: [
      { href: "/parametres", cle: "parametres", icone: Settings, gestion: true },
    ],
  },
];

const ROLES_GESTION = ["ADMIN", "GESTIONNAIRE"];
const CLE_MENU_REDUIT = "csimmo.menu-reduit";

function initiales(nom: string): string {
  return nom
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((m) => m[0]?.toUpperCase())
    .join("");
}

export function AppShell({ children }: { children: ReactNode }) {
  const t = useTranslations();
  const pathname = usePathname();
  const router = useRouter();
  const queryClient = useQueryClient();

  const { data: moi } = useMe();
  const { theme, setTheme } = useTheme();

  const [menuOuvert, setMenuOuvert] = useState(false);
  const [reduit, setReduit] = useState(false);

  // Préférence du menu
  useEffect(() => {
    // lecture après le montage : localStorage n'existe pas côté serveur (sinon écart d'hydratation)
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setReduit(
      window.localStorage.getItem(CLE_MENU_REDUIT) === "1",
    );
  }, []);

  // Fermeture automatique du menu mobile
  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setMenuOuvert(false);
  }, [pathname]);

  const basculerReduit = useCallback(() => {
    setReduit((v) => {
      const suivant = !v;

      window.localStorage.setItem(
        CLE_MENU_REDUIT,
        suivant ? "1" : "0",
      );

      return suivant;
    });
  }, []);

  const deconnecter = useCallback(
    async (raison?: string) => {
      await fetch("/api/auth/logout", {
        method: "POST",
      }).catch(() => undefined);

      queryClient.clear();

      router.replace(
        raison ? `/login?raison=${raison}` : "/login",
      );

      router.refresh();
    },
    [queryClient, router],
  );

  useInactivityLogout(
    INACTIVITE_MS,
    () => void deconnecter("inactivite"),
  );

  const gestion =
    moi?.role !== undefined &&
    ROLES_GESTION.includes(moi.role);

  const groupes = GROUPES.map((g) => ({
    ...g,
    entrees: g.entrees.filter(
      (e) => !e.gestion || gestion,
    ),
  })).filter((g) => g.entrees.length > 0);

  const courante = groupes
    .flatMap((g) => g.entrees)
    .find(
      (e) =>
        pathname === e.href ||
        pathname.startsWith(`${e.href}/`),
    );

  return (
    <TooltipProvider delay={300}>
      <div className="flex min-h-screen bg-muted/30">

        {/* SIDEBAR */}
        <aside
          className={cn(
            "fixed inset-y-0 left-0 z-40 flex flex-col",
            "border-r border-sidebar-border/70",
            "bg-sidebar text-sidebar-foreground",
            "shadow-[4px_0_24px_-16px_rgba(0,0,0,0.15)]",
            "transition-[transform,width] duration-300 ease-in-out",
            "md:sticky md:top-0 md:h-screen md:translate-x-0",
            reduit ? "md:w-16" : "md:w-64",
            menuOuvert ? "translate-x-0" : "-translate-x-full",
          )}
        >

          {/* LOGO */}
          <div
            className={cn(
              "flex h-16 shrink-0 items-center justify-between",
              "border-b border-sidebar-border/70 px-4",
              reduit && "md:justify-center md:px-0",
            )}
          >
            {reduit && (
              <span className="hidden md:block">
                <LogoCompact nom="" />
              </span>
            )}

            <span className={cn(reduit && "md:hidden")}>
              <LogoCompact nom={t("app.nom")} />
            </span>

            <Button
              variant="ghost"
              size="icon"
              className="md:hidden"
              aria-label={t("nav.fermerMenu")}
              onClick={() => setMenuOuvert(false)}
            >
              <X className="size-5" />
            </Button>
          </div>

          {/* NAVIGATION */}
          <nav
            aria-label={t("nav.principale")}
            className="flex flex-1 flex-col gap-5 overflow-y-auto p-3"
          >
            {groupes.map((g) => (
              <div key={g.cle} className="flex flex-col gap-1">

                <p
                  className={cn(
                    "px-3 pb-1 text-[10px] font-bold",
                    "uppercase tracking-[0.12em]",
                    "text-muted-foreground/80",
                    reduit && "md:hidden",
                  )}
                >
                  {t(`nav.groupes.${g.cle}`)}
                </p>

                {g.entrees.map(({ href, cle, icone: Icone }) => {
                  const actif =
                    pathname === href ||
                    pathname.startsWith(`${href}/`);

                  const lien = (
                    <Link
                      href={href}
                      onClick={() => setMenuOuvert(false)}
                      aria-current={actif ? "page" : undefined}
                      aria-label={
                        reduit ? t(`nav.${cle}`) : undefined
                      }
                      className={cn(
                        "group relative flex items-center gap-3",
                        "rounded-xl px-3 py-2.5",
                        "text-sm font-medium",
                        "text-sidebar-foreground/70",
                        "transition-all duration-200",
                        "hover:bg-sidebar-accent",
                        "hover:text-sidebar-accent-foreground",
                        actif && "bg-primary/10 text-primary shadow-sm",
                        reduit && "md:justify-center md:px-0",
                      )}
                    >
                      {actif && (
                        <span
                          aria-hidden
                          className="
                            absolute left-0 top-1/2
                            h-6 w-1 -translate-y-1/2
                            rounded-r-full bg-primary
                          "
                        />
                      )}

                      <Icone
                        className={cn(
                          "size-[18px] shrink-0",
                          "transition-transform duration-200",
                          "group-hover:scale-110",
                          actif && "text-primary",
                        )}
                        aria-hidden
                      />

                      <span className={cn(reduit && "md:hidden")}>
                        {t(`nav.${cle}`)}
                      </span>
                    </Link>
                  );

                  return reduit ? (
                    <Tooltip key={href}>
                      <TooltipTrigger render={lien} />
                      <TooltipContent side="right">
                        {t(`nav.${cle}`)}
                      </TooltipContent>
                    </Tooltip>
                  ) : (
                    <div key={href}>{lien}</div>
                  );
                })}
              </div>
            ))}
          </nav>

          {/* REDUCTION SIDEBAR */}
          <div className="hidden border-t border-sidebar-border/70 p-3 md:block">
            <Button
              variant="ghost"
              size="sm"
              className={cn(
                "w-full justify-start gap-3",
                "text-muted-foreground",
                "hover:bg-sidebar-accent",
                reduit && "justify-center px-0",
              )}
              onClick={basculerReduit}
              aria-label={
                reduit ? t("nav.developper") : t("nav.reduire")
              }
              aria-expanded={!reduit}
            >
              {reduit ? (
                <PanelLeftOpen className="size-5" aria-hidden />
              ) : (
                <PanelLeftClose className="size-5" aria-hidden />
              )}

              {!reduit && <span>{t("nav.reduire")}</span>}
            </Button>
          </div>
        </aside>

        {/* OVERLAY MOBILE */}
        {menuOuvert && (
          <div
            className="
              fixed inset-0 z-30 bg-black/40
              backdrop-blur-[2px] md:hidden
            "
            aria-hidden="true"
            onClick={() => setMenuOuvert(false)}
          />
        )}

        {/* CONTENU */}
        <div className="flex min-w-0 flex-1 flex-col">

          {/* HEADER */}
          <header
            className="
              sticky top-0 z-20 flex h-16
              items-center gap-3
              border-b border-border/60
              bg-background/85 px-4 sm:px-6
              shadow-sm backdrop-blur-xl
            "
          >
            <Button
              variant="ghost"
              size="icon"
              className="md:hidden"
              aria-label={t("nav.ouvrirMenu")}
              aria-expanded={menuOuvert}
              onClick={() => setMenuOuvert(true)}
            >
              <Menu className="size-5" />
            </Button>

            {courante && (
              <h2 className="truncate text-sm font-semibold tracking-tight">
                {t(`nav.${courante.cle}`)}
              </h2>
            )}

            <div className="ml-auto flex items-center gap-2">
              <LanguageSwitcher />

              {moi && (
                <DropdownMenu>
                  <DropdownMenuTrigger
                    aria-label={t("nav.compte")}
                    className="
                      flex items-center gap-2
                      rounded-full border border-border/70
                      bg-background py-1 pl-1 pr-2
                      text-sm outline-none
                      transition-colors hover:bg-accent
                      focus-visible:ring-2
                      focus-visible:ring-ring
                    "
                  >
                    <span
                      className="
                        flex size-8 items-center
                        justify-center rounded-full
                        bg-primary text-xs font-bold
                        text-primary-foreground
                      "
                    >
                      {initiales(moi.nom ?? "")}
                    </span>

                    <span className="hidden max-w-32 truncate font-medium sm:block">
                      {moi.nom}
                    </span>

                    <ChevronDown
                      className="size-3.5 text-muted-foreground"
                      aria-hidden
                    />
                  </DropdownMenuTrigger>

                  <DropdownMenuContent
                    align="end"
                    className="w-60 rounded-xl p-2 shadow-xl"
                  >
                    <DropdownMenuGroup>
                      <DropdownMenuLabel className="flex flex-col gap-1 p-3">
                        <span className="font-semibold text-foreground">
                          {moi.nom ?? ""}
                        </span>

                        <span className="text-xs font-normal text-muted-foreground">
                          {t(`roles.${moi.role}`)}
                        </span>
                      </DropdownMenuLabel>
                    </DropdownMenuGroup>

                    <DropdownMenuSeparator />

                    <DropdownMenuGroup>
                      <DropdownMenuLabel className="px-3 text-xs uppercase tracking-wider text-muted-foreground">
                        {t("theme.titre")}
                      </DropdownMenuLabel>

                      <DropdownMenuRadioGroup
                        value={theme ?? "system"}
                        onValueChange={(v) => setTheme(v)}
                      >
                        <DropdownMenuRadioItem value="light">
                          <Sun aria-hidden />
                          {t("theme.clair")}
                        </DropdownMenuRadioItem>

                        <DropdownMenuRadioItem value="dark">
                          <Moon aria-hidden />
                          {t("theme.sombre")}
                        </DropdownMenuRadioItem>

                        <DropdownMenuRadioItem value="system">
                          <Monitor aria-hidden />
                          {t("theme.systeme")}
                        </DropdownMenuRadioItem>
                      </DropdownMenuRadioGroup>
                    </DropdownMenuGroup>

                    <DropdownMenuSeparator />

                    <DropdownMenuItem
                      onClick={() => void deconnecter()}
                      className="
                        cursor-pointer rounded-lg
                        text-destructive
                        focus:bg-destructive/10
                        focus:text-destructive
                      "
                    >
                      <LogOut aria-hidden />
                      {t("auth.deconnexion")}
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              )}
            </div>
          </header>

          {/* LICENCE */}
          <LicenceBanner />

          {/* PAGE */}
          <main
            key={pathname}
            className="
              animate-page-in mx-auto
              w-full max-w-[1800px]
              flex-1 p-4 sm:p-6 lg:p-8
            "
          >
            {children}
          </main>
        </div>
      </div>
    </TooltipProvider>
  );
}
