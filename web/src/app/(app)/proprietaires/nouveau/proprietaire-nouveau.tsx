"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

import { champsProprietaire } from "@/components/tiers/champs";
import { FicheForm } from "@/components/tiers/fiche-form";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { optionnel } from "@/lib/tiers";

export function ProprietaireNouveau() {
  const t = useTranslations("tiers");
  const router = useRouter();
  const queryClient = useQueryClient();
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("nouveauProprietaire")}</h1>
      <FicheForm
        champs={champsProprietaire(t)}
        contactRequis
        onSubmit={async (v) => {
          const cree = deballer(
            await api.POST("/api/v1/proprietaires-tiers", {
              body: {
                nom: v.nom, telephone: optionnel(v.telephone), email: optionnel(v.email), adresse: optionnel(v.adresse),
                modeReversement: v.modeReversement as "VIREMENT", coordonneesReversement: optionnel(v.coordonneesReversement), notes: optionnel(v.notes),
              },
            }),
          );
          await queryClient.invalidateQueries({ queryKey: ["proprietaires"] });
          toast.success(t("proprietaireCree"));
          router.push(`/proprietaires/${cree.id}`);
        }}
      />
    </div>
  );
}
