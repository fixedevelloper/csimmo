"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { ErrorDisplay } from "@/components/error-display";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useMandats } from "@/hooks/use-tiers";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { formaterDate } from "@/lib/licence";
import { pointsDeBaseEnPourcentage, pourcentageEnPointsDeBase } from "@/lib/tiers";

/** Mandats de gestion d'un propriétaire : taux d'honoraires (saisis en %, stockés en points de base) et période. */
export function Mandats({ proprietaireId, actif }: { proprietaireId: string; actif: boolean }) {
  const t = useTranslations("tiers");
  const locale = useLocale();
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const { data: mandats, error, refetch } = useMandats(proprietaireId);
  const [taux, setTaux] = useState("");
  const [debut, setDebut] = useState("");
  const [fin, setFin] = useState("");
  const [erreur, setErreur] = useState<string | null>(null);

  const ajouter = useMutation({
    mutationFn: async () =>
      deballer(
        await api.POST("/api/v1/proprietaires-tiers/{id}/mandats", {
          params: { path: { id: proprietaireId } },
          body: { tauxHonorairesBp: pourcentageEnPointsDeBase(taux), dateDebut: debut, dateFin: fin || undefined },
        }),
      ),
    onSuccess: async () => {
      setTaux("");
      setDebut("");
      setFin("");
      setErreur(null);
      toast.success(t("mandatAjoute"));
      await queryClient.invalidateQueries({ queryKey: ["mandats", proprietaireId] });
    },
    onError: (e) => setErreur(message(e)),
  });

  function soumettre(e: React.FormEvent) {
    e.preventDefault();
    if (Number.isNaN(pourcentageEnPointsDeBase(taux))) return setErreur(t("tauxInvalide"));
    if (!debut) return setErreur(t("debutRequis"));
    if (fin && fin < debut) return setErreur(t("finAvantDebut"));
    setErreur(null);
    ajouter.mutate();
  }

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("mandats")}</CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-4">
        {mandats && mandats.length === 0 ? <p className="text-sm text-muted-foreground">{t("aucunMandat")}</p> : null}
        {mandats && mandats.length > 0 ? (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>{t("tauxPourcent")}</TableHead>
                <TableHead>{t("dateDebut")}</TableHead>
                <TableHead>{t("dateFin")}</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {mandats.map((m) => (
                <TableRow key={m.id}>
                  <TableCell>{pointsDeBaseEnPourcentage(m.tauxHonorairesBp ?? 0, locale)} %</TableCell>
                  <TableCell>{formaterDate(m.dateDebut, locale)}</TableCell>
                  <TableCell>{m.dateFin ? formaterDate(m.dateFin, locale) : t("enCours")}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : null}
        {actif ? (
          <form onSubmit={soumettre} noValidate className="flex flex-wrap items-end gap-2">
            <Input aria-label={t("tauxPourcent")} placeholder={t("tauxPourcent")} inputMode="decimal" value={taux} onChange={(e) => setTaux(e.target.value)} className="w-40" />
            <Input aria-label={t("dateDebut")} type="date" value={debut} onChange={(e) => setDebut(e.target.value)} className="w-40" />
            <Input aria-label={t("dateFin")} type="date" value={fin} onChange={(e) => setFin(e.target.value)} className="w-40" />
            <Button type="submit" disabled={ajouter.isPending}>
              {t("ajouterMandat")}
            </Button>
          </form>
        ) : null}
        {erreur ? (
          <p role="alert" className="text-sm text-destructive">
            {erreur}
          </p>
        ) : null}
      </CardContent>
    </Card>
  );
}
