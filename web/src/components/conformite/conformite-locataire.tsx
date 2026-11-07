"use client";

import { useQuery, useQueryClient } from "@tanstack/react-query";
import {
  ShieldCheck,
  Check,
  X,
  Trash2,
  Clock,
  FileCheck2,
  AlertCircle,
  Smartphone,
  Bell,
  UserCheck,
  Share2
} from "lucide-react";
import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { StatutPastille } from "@/components/statut-pastille";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { NativeSelect } from "@/components/ui/native-select";
import { useErrorMessage } from "@/hooks/use-error-message";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";

const MODES = ["PAPIER", "ELECTRONIQUE", "ORAL"] as const;

type FinaliteType = "NOTIFICATIONS" | "PIECES_IDENTITE" | "TRANSMISSION_PROPRIETAIRE" | "APPLICATION_MOBILE";

/** Consentements d'un locataire (registre en ajout seul) et demande d'effacement (anonymisation). */
export function ConformiteLocataire({ locataireId }: { locataireId: string }) {
  const t = useTranslations("conformite");
  const locale = useLocale();
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [mode, setMode] = useState<(typeof MODES)[number]>("PAPIER");
  const [erreur, setErreur] = useState<string | null>(null);

  const { data } = useQuery({
    queryKey: ["consentements", "LOCATAIRE", locataireId],
    queryFn: async () =>
      deballer(
        await api.GET("/api/v1/conformite/consentements/{type}/{personneId}", {
          params: { path: { type: "LOCATAIRE", personneId: locataireId } }
        })
      ),
  });

  async function decider(finalite: FinaliteType, accorde: boolean) {
    setErreur(null);
    try {
      deballer(
        await api.POST("/api/v1/conformite/consentements", {
          body: { type: "LOCATAIRE", personneId: locataireId, finalite, accorde, modeRecueil: mode }
        })
      );
      toast.success(t("consentementEnregistre"));
      await queryClient.invalidateQueries({ queryKey: ["consentements", "LOCATAIRE", locataireId] });
    } catch (e) {
      setErreur(message(e));
    }
  }

  async function demanderEffacement() {
    setErreur(null);
    try {
      deballer(
        await api.POST("/api/v1/conformite/demandes", {
          body: { type: "LOCATAIRE", personneId: locataireId }
        })
      );
      toast.success(t("demandeEnregistree"));
    } catch (e) {
      setErreur(message(e));
    }
  }

  // Association d'icônes par finalité
  const getFinaliteIcon = (finalite?: string) => {
    switch (finalite) {
      case "NOTIFICATIONS": return <Bell className="size-4 text-muted-foreground" />;
      case "PIECES_IDENTITE": return <UserCheck className="size-4 text-muted-foreground" />;
      case "TRANSMISSION_PROPRIETAIRE": return <Share2 className="size-4 text-muted-foreground" />;
      case "APPLICATION_MOBILE": return <Smartphone className="size-4 text-muted-foreground" />;
      default: return <FileCheck2 className="size-4 text-muted-foreground" />;
    }
  };

  return (
    <Card className="shadow-sm border-border/60">
      <CardHeader className="border-b bg-muted/10 pb-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="flex size-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
              <ShieldCheck className="size-5" />
            </div>
            <div>
              <CardTitle className="text-base font-semibold">{t("donneesPersonnelles")}</CardTitle>
              <CardDescription className="text-xs">
                Gestion des consentements et droits RGPD du locataire
              </CardDescription>
            </div>
          </div>

          {/* Sélection du mode de recueil */}
          <div className="flex items-center gap-2">
            <Label htmlFor="mode-recueil" className="text-xs font-medium text-muted-foreground shrink-0 hidden sm:inline">
              {t("modeRecueil")} :
            </Label>
            <NativeSelect
              id="mode-recueil"
              value={mode}
              onChange={(e) => setMode(e.target.value as (typeof MODES)[number])}
              className="h-8 text-xs w-36 bg-background"
            >
              {MODES.map((m) => (
                <option key={m} value={m}>
                  {t(`modes.${m}`)}
                </option>
              ))}
            </NativeSelect>
          </div>
        </div>
      </CardHeader>

      <CardContent className="flex flex-col gap-6 pt-6">
        {/* Affichage d'erreur globale */}
        {erreur && (
          <div role="alert" className="flex items-center gap-2 rounded-lg border border-destructive/20 bg-destructive/10 p-3 text-xs text-destructive font-medium">
            <AlertCircle className="size-4 shrink-0" />
            <span>{erreur}</span>
          </div>
        )}

        {/* Liste des finalités de traitement */}
        <div className="flex flex-col gap-2.5">
          {data?.finalites?.map((f) => {
            return (
              <div
                key={f.finalite}
                className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 p-3.5 rounded-xl border border-border/50 bg-card hover:bg-muted/20 transition-all"
              >
                {/* Information Finalité & Statut */}
                <div className="flex items-center gap-3">
                  <div className="p-2 rounded-lg bg-muted/40 shrink-0">
                    {getFinaliteIcon(f.finalite)}
                  </div>
                  <div className="flex flex-col gap-1">
                    <div className="flex items-center gap-2.5 flex-wrap">
                      <span className="text-sm font-medium text-foreground">
                        {t(`finalites.${f.finalite}`)}
                      </span>
                      <StatutPastille statut={f.statut} libelle={t(`statuts.${f.statut}`)} />
                    </div>
                    {f.derniere?.decideLe && (
                      <span className="text-xs text-muted-foreground flex items-center gap-1">
                        <Clock className="size-3" />
                        Mis à jour le {new Date(f.derniere.decideLe).toLocaleDateString(locale)}
                      </span>
                    )}
                  </div>
                </div>

                {/* Boutons d'action rapides et doux */}
                <div className="flex items-center gap-1.5 self-end sm:self-center">
                  <Button
                    size="sm"
                    variant="ghost"
                    className="h-8 px-2.5 text-xs gap-1.5 text-emerald-700 bg-emerald-50 hover:bg-emerald-100 dark:bg-emerald-950/40 dark:text-emerald-400 dark:hover:bg-emerald-900/60"
                    onClick={() => void decider(f.finalite as FinaliteType, true)}
                  >
                    <Check className="size-3.5" />
                    {t("accord")}
                  </Button>

                  <Button
                    size="sm"
                    variant="ghost"
                    className="h-8 px-2.5 text-xs gap-1.5 text-rose-700 bg-rose-50 hover:bg-rose-100 dark:bg-rose-950/40 dark:text-rose-400 dark:hover:bg-rose-900/60"
                    onClick={() => void decider(f.finalite as FinaliteType, false)}
                  >
                    <X className="size-3.5" />
                    {t("refusRetrait")}
                  </Button>
                </div>
              </div>
            );
          })}
        </div>

        {/* Section Droits RGPD / Effacement (Soft Danger Zone) */}
        <div className="rounded-xl border border-border/60 bg-muted/20 p-4 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 mt-2">
          <div className="flex flex-col gap-0.5">
            <span className="text-xs font-semibold text-foreground flex items-center gap-1.5">
              <Trash2 className="size-3.5 text-muted-foreground" />
              Droit à l&apos;oubli & Anonymisation
            </span>
            <p className="text-xs text-muted-foreground max-w-lg">
              {t("effacementAide")}
            </p>
          </div>
          <Button
            variant="outline"
            size="sm"
            className="h-8 text-xs shrink-0 border-destructive/30 text-destructive hover:bg-destructive/10 gap-1.5"
            onClick={() => void demanderEffacement()}
          >
            <Trash2 className="size-3.5" />
            {t("demanderEffacement")}
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
