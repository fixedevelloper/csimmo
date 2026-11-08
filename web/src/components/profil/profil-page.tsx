"use client";

import { useTranslations } from "next-intl";

import { ErrorDisplay } from "@/components/error-display";
import { PageHeader } from "@/components/page-header";
import { DoubleAuthentification } from "@/components/profil/double-authentification";
import { InfosPersonnelles } from "@/components/profil/infos-personnelles";
import { MotDePasseForm } from "@/components/profil/mot-de-passe-form";
import { Skeleton } from "@/components/ui/skeleton";
import { useMe } from "@/hooks/use-me";

/** Profil de l'utilisateur connecté, ouvert à tous les rôles. */
export function ProfilPage() {
  const t = useTranslations("profil");
  const { data: moi, error, refetch } = useMe();
  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <div className="flex flex-col gap-6">
      <PageHeader titre={t("titre")} sousTitre={t("description")} />
      {!moi ? (
        <Skeleton className="h-64 w-full max-w-xl" />
      ) : (
        <>
          {/* la clé relance le formulaire quand le profil change (valeurs initiales) */}
          <InfosPersonnelles key={`${moi.id}-${moi.nom}-${moi.telephone}-${moi.email}`} moi={moi} />
          <DoubleAuthentification moi={moi} />
          <MotDePasseForm />
        </>
      )}
    </div>
  );
}
