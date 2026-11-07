
import {
  Bell,
  BellRing,
  DatabaseBackup,
  FileSpreadsheet,
  FileText,
  KeyRound,
  ShieldCheck,
  Smartphone,
  ChevronRight,
} from "lucide-react";

import { getTranslations } from "next-intl/server";
import Link from "next/link";

import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

import { PageHeader } from "@/components/page-header";

const PARAMETRES = [
  {
    href: "/parametres/licence",
    cle: "licence",
    description: "licenceDescription",
    icon: KeyRound,
    couleur: "bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400",
  },
  {
    href: "/parametres/conformite",
    cle: "conformite",
    description: "conformiteDescription",
    icon: ShieldCheck,
    couleur: "bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400",
  },
  {
    href: "/parametres/import",
    cle: "import",
    description: "importDescription",
    icon: FileSpreadsheet,
    couleur: "bg-violet-50 text-violet-600 dark:bg-violet-500/10 dark:text-violet-400",
  },
  {
    href: "/parametres/sauvegardes",
    cle: "sauvegardes",
    description: "sauvegardesDescription",
    icon: DatabaseBackup,
    couleur: "bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400",
  },
  {
    href: "/parametres/paiement-en-ligne",
    cle: "paiementEnLigne",
    description: "paiementEnLigneDescription",
    icon: Smartphone,
    couleur: "bg-cyan-50 text-cyan-600 dark:bg-cyan-500/10 dark:text-cyan-400",
  },
  {
    href: "/parametres/relances",
    cle: "relances",
    description: "relancesDescription",
    icon: BellRing,
    couleur: "bg-orange-50 text-orange-600 dark:bg-orange-500/10 dark:text-orange-400",
  },
  {
    href: "/parametres/notifications",
    cle: "notifications",
    description: "notificationsDescription",
    icon: Bell,
    couleur: "bg-pink-50 text-pink-600 dark:bg-pink-500/10 dark:text-pink-400",
  },
  {
    href: "/parametres/modele-bail",
    cle: "modeleBail",
    description: "modeleBailDescription",
    icon: FileText,
    couleur: "bg-indigo-50 text-indigo-600 dark:bg-indigo-500/10 dark:text-indigo-400",
  },
];

export default async function ParametresPage() {
  const t = await getTranslations("parametres");

  return (
    <div className="flex flex-col gap-6">

      {/* EN-TÊTE */}
      <PageHeader titre={t("titre")} />

      {/* INTRODUCTION */}
      <div className="space-y-1">
        <h2 className="text-lg font-semibold tracking-tight">
          Configuration de votre espace
        </h2>

        <p className="text-sm text-muted-foreground">
          Gérez les paramètres, les fonctionnalités et les préférences
          de votre plateforme immobilière.
        </p>
      </div>

      {/* CARTES */}
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">

        {PARAMETRES.map((item) => {
          const Icon = item.icon;

          return (
            <Link
              key={item.href}
              href={item.href}
              className="
                group block h-full rounded-2xl
                focus-visible:outline-none
                focus-visible:ring-2
                focus-visible:ring-primary
                focus-visible:ring-offset-2
              "
            >
              <Card
                className="
                  h-full min-h-[150px]
                  border-border/60
                  shadow-sm
                  transition-all duration-200
                  group-hover:-translate-y-1
                  group-hover:border-primary/30
                  group-hover:shadow-lg
                "
              >
                <CardHeader className="flex h-full flex-col justify-between gap-5">

                  <div className="flex items-start justify-between">

                    {/* ICÔNE */}
                    <div
                      className={`
                        flex size-12 items-center
                        justify-center rounded-xl
                        ${item.couleur}
                      `}
                    >
                      <Icon className="size-5" aria-hidden />
                    </div>

                    {/* FLÈCHE */}
                    <div
                      className="
                        flex size-8 items-center
                        justify-center rounded-full
                        text-muted-foreground
                        transition-all duration-200
                        group-hover:bg-primary/10
                        group-hover:text-primary
                      "
                    >
                      <ChevronRight className="size-4 transition-transform group-hover:translate-x-0.5" />
                    </div>

                  </div>

                  {/* TEXTES */}
                  <div className="space-y-1.5">
                    <CardTitle className="text-base font-semibold transition-colors group-hover:text-primary">
                      {t(item.cle)}
                    </CardTitle>

                    <CardDescription className="text-sm leading-relaxed">
                      {t(item.description)}
                    </CardDescription>
                  </div>

                </CardHeader>
              </Card>
            </Link>
          );
        })}

      </div>
    </div>
  );
}
