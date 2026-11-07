"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";
import {
  ShieldCheck,
  PlusCircle,
  Phone,
  UserCheck,
  Trash2,
  AlertCircle
} from "lucide-react";

import { champsGarant } from "@/components/tiers/champs";
import { FicheForm } from "@/components/tiers/fiche-form";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useGarants } from "@/hooks/use-tiers";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { optionnel } from "@/lib/tiers";

export function Garants({ locataireId, actif }: { locataireId: string; actif: boolean }) {
  const t = useTranslations("tiers");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const { data: garants } = useGarants(locataireId);
  const [ajout, setAjout] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);

  const rafraichir = () => queryClient.invalidateQueries({ queryKey: ["garants", locataireId] });

  async function retirer(id: string) {
    setErreur(null);
    try {
      deballer(await api.DELETE("/api/v1/garants/{id}", { params: { path: { id } } }));
      await rafraichir();
      toast.success(t("garantRetire") || "Garant retiré avec succès");
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <Card className="shadow-sm border-border/60">
      <CardHeader className="flex flex-row items-center justify-between border-b bg-muted/10 pb-4">
        <div className="flex items-center gap-2.5">
          <div className="flex size-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
            <ShieldCheck className="size-5" />
          </div>
          <div>
            <CardTitle className="text-base font-semibold">{t("garants")}</CardTitle>
            <CardDescription className="text-xs">
              Personnes garantes associées au locataire
            </CardDescription>
          </div>
        </div>
        {actif && !ajout ? (
          <Button size="sm" onClick={() => setAjout(true)} className="gap-1.5 h-9">
            <PlusCircle className="size-4" />
            {t("ajouterGarant")}
          </Button>
        ) : null}
      </CardHeader>

      <CardContent className="flex flex-col gap-4 pt-6">
        {/* Gestion des erreurs d'API */}
        {erreur && (
          <div role="alert" className="flex items-center gap-2 rounded-lg border border-destructive/20 bg-destructive/10 p-3 text-xs text-destructive font-medium">
            <AlertCircle className="size-4 shrink-0" />
            <span>{erreur}</span>
          </div>
        )}

        {/* État vide */}
        {garants && garants.length === 0 && !ajout ? (
          <div className="text-center py-8 text-muted-foreground text-sm italic">
            {t("aucunGarant")}
          </div>
        ) : null}

        {/* Liste des garants */}
        <div className="flex flex-col gap-2.5">
          {(garants ?? []).map((g) => (
            <div
              key={g.id}
              className="flex items-center justify-between gap-3 p-3.5 rounded-xl border border-border/50 bg-card hover:bg-muted/20 transition-all"
            >
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-lg bg-muted/40 text-muted-foreground shrink-0">
                  <UserCheck className="size-4" />
                </div>
                <div className="flex flex-col gap-0.5">
                  <div className="flex items-center gap-2 flex-wrap">
                    <span className="font-semibold text-sm text-foreground">{g.nom}</span>
                    {g.lien && (
                      <span className="text-xs px-2 py-0.5 rounded-md bg-muted text-muted-foreground font-medium">
                        {g.lien}
                      </span>
                    )}
                  </div>
                  {g.telephone && (
                    <span className="text-xs text-muted-foreground flex items-center gap-1.5 mt-0.5">
                      <Phone className="size-3" />
                      {g.telephone}
                    </span>
                  )}
                </div>
              </div>

              {actif ? (
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  className="h-8 px-2.5 text-xs text-destructive hover:bg-destructive/10 hover:text-destructive gap-1"
                  onClick={() => void retirer(g.id ?? "")}
                >
                  <Trash2 className="size-3.5" />
                  <span className="hidden sm:inline">{t("retirerGarant")}</span>
                </Button>
              ) : null}
            </div>
          ))}
        </div>

        {/* Formulaire d'ajout */}
        {ajout ? (
          <div className="mt-2 rounded-xl border border-primary/30 bg-muted/10 p-4">
            <FicheForm
              champs={champsGarant(t)}
              onAnnuler={() => setAjout(false)}
              onSubmit={async (v) => {
                deballer(
                  await api.POST("/api/v1/locataires/{id}/garants", {
                    params: { path: { id: locataireId } },
                    body: {
                      nom: v.nom,
                      lien: optionnel(v.lien),
                      profession: optionnel(v.profession),
                      telephone: v.telephone,
                      adresse: optionnel(v.adresse),
                      numeroCni: optionnel(v.numeroCni)
                    },
                  }),
                );
                await rafraichir();
                toast.success(t("garantAjoute"));
                setAjout(false);
              }}
            />
          </div>
        ) : null}
      </CardContent>
    </Card>
  );
}
