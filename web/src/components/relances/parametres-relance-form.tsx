"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { ErrorDisplay } from "@/components/error-display";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useMe } from "@/hooks/use-me";
import { useParametresRelance } from "@/hooks/use-relances";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { lireNiveaux, type ParametresRelanceDto } from "@/lib/relances";

export function ParametresRelanceForm() {
  const { data, error, refetch } = useParametresRelance();
  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (!data) return <Skeleton className="h-40 w-full" />;
  return <Formulaire initial={data} />;
}

function Formulaire({ initial }: { initial: ParametresRelanceDto }) {
  const t = useTranslations("relances.parametres");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const { data: me } = useMe();
  const admin = me?.role === "ADMIN";
  const [actif, setActif] = useState(initial.actif ?? true);
  const [niveaux, setNiveaux] = useState((initial.niveaux ?? []).join(", "));
  const [delai, setDelai] = useState(String(initial.delaiMiseEnDemeureJours ?? 8));
  const [erreur, setErreur] = useState<string | null>(null);

  async function enregistrer() {
    setErreur(null);
    const liste = lireNiveaux(niveaux);
    const jours = Number(delai);
    if (!liste) return setErreur(t("niveauxInvalides"));
    if (!Number.isInteger(jours) || jours < 1 || jours > 90) return setErreur(t("delaiInvalide"));
    try {
      deballer(await api.PUT("/api/v1/relances/parametres", { body: { actif, niveaux: liste, delaiMiseEnDemeureJours: jours } }));
      await queryClient.invalidateQueries({ queryKey: ["relances-parametres"] });
      toast.success(t("enregistre"));
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <div className="flex max-w-xl flex-col gap-4">
      <p className="text-sm text-muted-foreground">{t("aide")}</p>
      <label className="flex items-center gap-2 text-sm">
        <input type="checkbox" checked={actif} disabled={!admin} onChange={(e) => setActif(e.target.checked)} />
        {t("actif")}
      </label>
      <div className="flex flex-col gap-1">
        <label htmlFor="r-niveaux" className="text-sm font-medium">
          {t("niveaux")}
        </label>
        <Input id="r-niveaux" value={niveaux} disabled={!admin} onChange={(e) => setNiveaux(e.target.value)} />
        <p className="text-xs text-muted-foreground">{t("niveauxAide")}</p>
      </div>
      <div className="flex flex-col gap-1">
        <label htmlFor="r-delai" className="text-sm font-medium">
          {t("delai")}
        </label>
        <Input id="r-delai" inputMode="numeric" value={delai} disabled={!admin} onChange={(e) => setDelai(e.target.value)} className="w-32" />
      </div>
      {erreur ? (
        <p role="alert" className="text-sm text-destructive">
          {erreur}
        </p>
      ) : null}
      {admin ? (
        <div>
          <Button onClick={() => void enregistrer()}>{t("enregistrer")}</Button>
        </div>
      ) : (
        <p className="text-sm text-muted-foreground">{t("lectureSeule")}</p>
      )}
    </div>
  );
}
