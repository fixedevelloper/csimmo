"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

import { champsLocataire } from "@/components/tiers/champs";
import { FicheForm } from "@/components/tiers/fiche-form";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { optionnel } from "@/lib/tiers";

export function LocataireNouveau() {
  const t = useTranslations("tiers");
  const router = useRouter();
  const queryClient = useQueryClient();
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">{t("nouveauLocataire")}</h1>
      <FicheForm
        champs={champsLocataire(t)}
        contactRequis
        onSubmit={async (v) => {
          const cree = deballer(
            await api.POST("/api/v1/locataires", {
              body: {
                nom: v.nom, profession: optionnel(v.profession), telephone: optionnel(v.telephone), email: optionnel(v.email),
                adresse: optionnel(v.adresse), dateNaissance: optionnel(v.dateNaissance), numeroCni: optionnel(v.numeroCni), notes: optionnel(v.notes),
              },
            }),
          );
          await queryClient.invalidateQueries({ queryKey: ["locataires"] });
          toast.success(t("locataireCree"));
          router.push(`/locataires/${cree.id}`);
        }}
      />
    </div>
  );
}
