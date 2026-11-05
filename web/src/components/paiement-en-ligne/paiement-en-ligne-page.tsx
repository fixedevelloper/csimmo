"use client";

import { useTranslations } from "next-intl";

import { ErrorDisplay } from "@/components/error-display";
import { FournisseurForm } from "@/components/paiement-en-ligne/fournisseur-form";
import { TransactionsListe } from "@/components/paiement-en-ligne/transactions-liste";
import { Skeleton } from "@/components/ui/skeleton";
import { useMe } from "@/hooks/use-me";
import { useFournisseurs } from "@/hooks/use-paiement-en-ligne";
import { EnteteParametres } from "@/components/entete-parametres";

export function PaiementEnLignePage() {
  const t = useTranslations("paiementEnLigne");
  const { data: me } = useMe();
  const admin = me?.role === "ADMIN";
  const { data, error, refetch } = useFournisseurs();
  return (
    <div className="flex flex-col gap-8">
      <EnteteParametres titre={t("titre")} />
      {admin ? (
        <section className="flex flex-col gap-4" aria-label={t("fournisseursTitre")}>
          <p className="text-sm text-muted-foreground">{t("intro")}</p>
          {error ? <ErrorDisplay erreur={error} onRetry={() => void refetch()} /> : null}
          {!data && !error ? <Skeleton className="h-48 w-full" /> : null}
          <div className="grid gap-4 lg:grid-cols-2">
            {(data ?? []).map((f) => (
              <FournisseurForm key={`${f.code}-${f.actif}-${f.configure}`} fournisseur={f} />
            ))}
          </div>
        </section>
      ) : null}
      <section className="flex flex-col gap-3">
        <h2 className="text-lg font-semibold">{t("transactions.titre")}</h2>
        <TransactionsListe />
      </section>
    </div>
  );
}
