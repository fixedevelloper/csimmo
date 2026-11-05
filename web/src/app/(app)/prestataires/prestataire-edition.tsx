"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useParams, useRouter } from "next/navigation";
import { toast } from "sonner";

import { ErrorDisplay } from "@/components/error-display";
import { ArchiverBouton } from "@/components/tiers/archiver-bouton";
import { champsPrestataire } from "@/components/tiers/champs";
import { FicheForm } from "@/components/tiers/fiche-form";
import { Skeleton } from "@/components/ui/skeleton";
import { usePrestataire } from "@/hooks/use-tiers";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { optionnel } from "@/lib/tiers";

/** Création (sans `modifier`) ou modification d'un prestataire, avec archivage. */
export function PrestataireEdition({ modifier = false }: { modifier?: boolean }) {
  const t = useTranslations("tiers");
  const router = useRouter();
  const queryClient = useQueryClient();
  const { id } = useParams<{ id?: string }>();
  const { data, error, refetch } = usePrestataire(id ?? "");

  if (modifier && error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (modifier && !data) return <Skeleton className="h-64 w-full max-w-2xl" />;
  const corps = (v: Record<string, string>) => ({
    nom: v.nom, metier: optionnel(v.metier), telephone: optionnel(v.telephone), email: optionnel(v.email), adresse: optionnel(v.adresse),
    notes: optionnel(v.notes), utilisateurId: data?.utilisateurId,
  });
  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-semibold">{modifier ? `${t("modifier")} — ${data?.nom}` : t("nouveauPrestataire")}</h1>
        {modifier && id && !data?.archive ? (
          <ArchiverBouton
            titre={t("archiverPrestataire")}
            action={async () => deballer(await api.DELETE("/api/v1/prestataires/{id}", { params: { path: { id } } }))}
            apres={() => {
              void queryClient.invalidateQueries({ queryKey: ["prestataires"] });
              router.push("/prestataires");
            }}
          />
        ) : null}
      </div>
      <FicheForm
        champs={champsPrestataire(t)}
        contactRequis
        valeurs={data ? { nom: data.nom ?? "", metier: data.metier ?? "", telephone: data.telephone ?? "", email: data.email ?? "", adresse: data.adresse ?? "", notes: data.notes ?? "" } : {}}
        onSubmit={async (v) => {
          if (modifier && id) {
            deballer(await api.PUT("/api/v1/prestataires/{id}", { params: { path: { id } }, body: corps(v) }));
            toast.success(t("prestataireModifie"));
          } else {
            deballer(await api.POST("/api/v1/prestataires", { body: corps(v) }));
            toast.success(t("prestataireCree"));
          }
          await queryClient.invalidateQueries({ queryKey: ["prestataires"] });
          await queryClient.invalidateQueries({ queryKey: ["prestataire"] });
          router.push("/prestataires");
        }}
      />
    </div>
  );
}
