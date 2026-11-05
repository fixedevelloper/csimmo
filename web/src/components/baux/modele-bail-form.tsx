"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { ErrorDisplay } from "@/components/error-display";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { Textarea } from "@/components/ui/textarea";
import { useModeleBail } from "@/hooks/use-baux";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useMe } from "@/hooks/use-me";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import type { ModeleBailDto } from "@/lib/baux";

export function ModeleBailForm() {
  const { data, isPending, error, refetch } = useModeleBail();
  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (isPending || !data) return <Skeleton className="h-64 w-full" />;
  return <Formulaire modele={data} />;
}

function Formulaire({ modele }: { modele: ModeleBailDto }) {
  const t = useTranslations("baux.modele");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const { data: me } = useMe();
  const admin = me?.role === "ADMIN";
  const [v, setV] = useState({
    bailleurNom: modele.bailleurNom ?? "",
    bailleurAdresse: modele.bailleurAdresse ?? "",
    preambule: modele.preambule ?? "",
    clauses: modele.clauses ?? "",
    piedDePage: modele.piedDePage ?? "",
  });
  const [erreur, setErreur] = useState<string | null>(null);

  async function enregistrer() {
    setErreur(null);
    try {
      deballer(await api.PUT("/api/v1/baux/modele", { body: v }));
      await queryClient.invalidateQueries({ queryKey: ["bail-modele"] });
      toast.success(t("enregistre"));
    } catch (e) {
      setErreur(message(e));
    }
  }

  const champ = (nom: keyof typeof v, label: string, long = false) => (
    <div className="flex flex-col gap-1">
      <label htmlFor={`m-${nom}`} className="text-sm font-medium">
        {label}
      </label>
      {long ? (
        <Textarea id={`m-${nom}`} rows={nom === "clauses" ? 14 : 4} value={v[nom]} disabled={!admin} onChange={(e) => setV({ ...v, [nom]: e.target.value })} />
      ) : (
        <Input id={`m-${nom}`} value={v[nom]} disabled={!admin} onChange={(e) => setV({ ...v, [nom]: e.target.value })} />
      )}
    </div>
  );

  return (
    <div className="flex max-w-3xl flex-col gap-4">
      <p className="text-sm text-muted-foreground">{t("aide")}</p>
      <p className="text-sm">
        {t("variables")} <code>{(modele.variables ?? []).map((x) => `{{${x}}}`).join("  ")}</code>
      </p>
      {champ("bailleurNom", t("bailleurNom"))}
      {champ("bailleurAdresse", t("bailleurAdresse"))}
      {champ("preambule", t("preambule"), true)}
      {champ("clauses", t("clauses"), true)}
      {champ("piedDePage", t("piedDePage"))}
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
