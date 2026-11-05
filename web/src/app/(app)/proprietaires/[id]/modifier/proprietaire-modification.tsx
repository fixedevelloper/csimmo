"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useParams, useRouter } from "next/navigation";
import { toast } from "sonner";

import { ErrorDisplay } from "@/components/error-display";
import { champsProprietaire } from "@/components/tiers/champs";
import { FicheForm } from "@/components/tiers/fiche-form";
import { Skeleton } from "@/components/ui/skeleton";
import { useProprietaire } from "@/hooks/use-tiers";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { optionnel } from "@/lib/tiers";

export function ProprietaireModification() {
  const t = useTranslations("tiers");
  const router = useRouter();
  const queryClient = useQueryClient();
  const { id } = useParams<{ id: string }>();
  const { data, error, refetch } = useProprietaire(id);

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (!data) return <Skeleton className="h-64 w-full max-w-2xl" />;
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">
        {t("modifier")} — {data.nom}
      </h1>
      <FicheForm
        champs={champsProprietaire(t)}
        contactRequis
        valeurs={{
          nom: data.nom ?? "", telephone: data.telephone ?? "", email: data.email ?? "", adresse: data.adresse ?? "",
          modeReversement: data.modeReversement ?? "VIREMENT", coordonneesReversement: data.coordonneesReversement ?? "", notes: data.notes ?? "",
        }}
        onSubmit={async (v) => {
          deballer(
            await api.PUT("/api/v1/proprietaires-tiers/{id}", {
              params: { path: { id } },
              body: {
                nom: v.nom, telephone: optionnel(v.telephone), email: optionnel(v.email), adresse: optionnel(v.adresse),
                modeReversement: v.modeReversement as "VIREMENT", coordonneesReversement: optionnel(v.coordonneesReversement), notes: optionnel(v.notes),
                utilisateurId: data.utilisateurId,
              },
            }),
          );
          await queryClient.invalidateQueries({ queryKey: ["proprietaire"] });
          await queryClient.invalidateQueries({ queryKey: ["proprietaires"] });
          toast.success(t("proprietaireModifie"));
          router.push(`/proprietaires/${id}`);
        }}
      />
    </div>
  );
}
