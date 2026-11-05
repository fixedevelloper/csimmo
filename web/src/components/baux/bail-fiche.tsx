"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";

import { BailStatutBadge } from "@/components/baux/bail-statut-badge";
import { EcheancesBail } from "@/components/baux/echeances-bail";
import { DocumentsPaiement } from "@/components/finance/documents-paiement";
import { ConfirmDialog } from "@/components/confirm-dialog";
import { ErrorDisplay } from "@/components/error-display";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { useBail, useDocumentsBail } from "@/hooks/use-baux";
import { useErrorMessage } from "@/hooks/use-error-message";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { entierSaisi, formaterFcfa, urlApercuBail } from "@/lib/baux";
import { urlPhoto } from "@/lib/patrimoine";

export function BailFiche({ id }: { id: string }) {
  const t = useTranslations("baux");
  const locale = useLocale();
  const message = useErrorMessage();
  const router = useRouter();
  const queryClient = useQueryClient();
  const { data: bail, isPending, error, refetch } = useBail(id);
  const documents = useDocumentsBail(id);
  const [confirmation, setConfirmation] = useState<"activer" | "supprimer" | null>(null);
  const [panneau, setPanneau] = useState<"resilier" | "renouveler" | null>(null);
  const [erreur, setErreur] = useState<string | null>(null);

  // résiliation
  const [notification, setNotification] = useState("");
  const [sortie, setSortie] = useState("");
  const [motif, setMotif] = useState("");
  const [reduit, setReduit] = useState(false);
  // renouvellement
  const [finRenouv, setFinRenouv] = useState("");
  const [loyerRenouv, setLoyerRenouv] = useState("");

  async function rafraichir() {
    await queryClient.invalidateQueries({ queryKey: ["bail", id] });
    await queryClient.invalidateQueries({ queryKey: ["bail-documents", id] });
    await queryClient.invalidateQueries({ queryKey: ["baux"] });
    await queryClient.invalidateQueries({ queryKey: ["lots"] });
  }

  async function executer(action: () => Promise<void>) {
    setErreur(null);
    try {
      await action();
    } catch (e) {
      setErreur(message(e));
    }
  }

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;
  if (isPending || !bail) return <Skeleton className="h-64 w-full" />;

  const brouillon = bail.statut === "BROUILLON";
  const actif = bail.statut === "ACTIF";

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="flex items-center gap-3 text-2xl font-semibold">
          {bail.numero} <BailStatutBadge statut={bail.statut} />
        </h1>
        <div className="flex flex-wrap gap-2">
          <a href={urlApercuBail(id)} target="_blank" rel="noopener noreferrer" className="inline-flex h-8 items-center rounded-lg border px-3 text-sm">
            {t("apercuPdf")}
          </a>
          {brouillon ? (
            <>
              <Button onClick={() => setConfirmation("activer")}>{t("activer")}</Button>
              <Button variant="outline" onClick={() => setConfirmation("supprimer")}>
                {t("supprimer")}
              </Button>
            </>
          ) : null}
          {actif ? (
            <>
              {bail.dateFin ? (
                <Button variant="outline" onClick={() => setPanneau("renouveler")}>
                  {t("renouveler")}
                </Button>
              ) : null}
              <Button variant="outline" onClick={() => setPanneau("resilier")}>
                {t("resilier")}
              </Button>
            </>
          ) : null}
        </div>
      </div>

      {erreur ? (
        <p role="alert" className="text-sm text-destructive">
          {erreur}
        </p>
      ) : null}

      <Card>
        <CardHeader>
          <CardTitle>{t("conditions")}</CardTitle>
        </CardHeader>
        <CardContent>
          <dl className="grid grid-cols-[12rem_1fr] gap-x-4 gap-y-2 text-sm">
            <dt className="text-muted-foreground">{t("lot")}</dt>
            <dd>
              {bail.immeubleNom} — {bail.lotReference}
            </dd>
            <dt className="text-muted-foreground">{t("locataire")}</dt>
            <dd>{bail.locataireNom}</dd>
            <dt className="text-muted-foreground">{t("garants")}</dt>
            <dd>{(bail.garants ?? []).map((g) => g.nom).join(", ") || "—"}</dd>
            <dt className="text-muted-foreground">{t("periode")}</dt>
            <dd>
              {bail.dateDebut} → {bail.dateFin ?? t("sansFin")}
            </dd>
            <dt className="text-muted-foreground">{t("loyer")}</dt>
            <dd>{formaterFcfa(bail.loyer, locale)}</dd>
            <dt className="text-muted-foreground">{t("charges")}</dt>
            <dd>{formaterFcfa(bail.charges, locale)}</dd>
            <dt className="text-muted-foreground">{t("periodicite")}</dt>
            <dd>{bail.periodicite ? t(`periodicites.${bail.periodicite}`) : "—"}</dd>
            <dt className="text-muted-foreground">{t("jourEcheance")}</dt>
            <dd>{bail.jourEcheance}</dd>
            <dt className="text-muted-foreground">{t("caution")}</dt>
            <dd>{formaterFcfa(bail.caution, locale)}</dd>
            <dt className="text-muted-foreground">{t("moisAvance")}</dt>
            <dd>{bail.moisAvance}</dd>
            <dt className="text-muted-foreground">{t("preavisMois")}</dt>
            <dd>{bail.preavisMois}</dd>
            {bail.dateSortie ? (
              <>
                <dt className="text-muted-foreground">{t("dateSortie")}</dt>
                <dd>
                  {bail.dateSortie} ({t("notifieLe", { date: bail.dateNotificationPreavis ?? "" })}) — {bail.motifResiliation}
                </dd>
              </>
            ) : null}
          </dl>
        </CardContent>
      </Card>

      {panneau === "resilier" ? (
        <Card>
          <CardHeader>
            <CardTitle>{t("resilier")}</CardTitle>
          </CardHeader>
          <CardContent className="flex max-w-xl flex-col gap-3">
            <label className="flex flex-col gap-1 text-sm">
              {t("dateNotification")}
              <Input type="date" value={notification} onChange={(e) => setNotification(e.target.value)} />
            </label>
            <label className="flex flex-col gap-1 text-sm">
              {t("dateSortieAide", { mois: bail.preavisMois ?? 3 })}
              <Input type="date" value={sortie} onChange={(e) => setSortie(e.target.value)} />
            </label>
            <label className="flex items-center gap-2 text-sm">
              <input type="checkbox" checked={reduit} onChange={(e) => setReduit(e.target.checked)} />
              {t("preavisReduit")}
            </label>
            <label className="flex flex-col gap-1 text-sm">
              {t("motif")}
              <Input value={motif} onChange={(e) => setMotif(e.target.value)} />
            </label>
            <div className="flex gap-2">
              <Button
                onClick={() =>
                  void executer(async () => {
                    deballer(
                      await api.POST("/api/v1/baux/{id}/resilier", {
                        params: { path: { id } },
                        body: {
                          dateNotification: notification || undefined,
                          dateSortie: sortie || undefined,
                          preavisReduitAccepte: reduit,
                          motif,
                        },
                      }),
                    );
                    await rafraichir();
                    setPanneau(null);
                    toast.success(t("resilie"));
                  })
                }
              >
                {t("confirmerResiliation")}
              </Button>
              <Button variant="outline" onClick={() => setPanneau(null)}>
                {t("annuler")}
              </Button>
            </div>
          </CardContent>
        </Card>
      ) : null}

      {panneau === "renouveler" ? (
        <Card>
          <CardHeader>
            <CardTitle>{t("renouveler")}</CardTitle>
          </CardHeader>
          <CardContent className="flex max-w-xl flex-col gap-3">
            <p className="text-sm text-muted-foreground">{t("renouvellementAide")}</p>
            <label className="flex flex-col gap-1 text-sm">
              {t("dateFin")}
              <Input type="date" value={finRenouv} onChange={(e) => setFinRenouv(e.target.value)} />
            </label>
            <label className="flex flex-col gap-1 text-sm">
              {t("nouveauLoyer")}
              <Input inputMode="numeric" value={loyerRenouv} onChange={(e) => setLoyerRenouv(e.target.value)} />
            </label>
            <div className="flex gap-2">
              <Button
                onClick={() =>
                  void executer(async () => {
                    const loyer = loyerRenouv.trim() === "" ? undefined : entierSaisi(loyerRenouv);
                    if (loyer !== undefined && !(loyer > 0)) throw new TypeError("loyer");
                    const nouveau = deballer(
                      await api.POST("/api/v1/baux/{id}/renouveler", {
                        params: { path: { id } },
                        body: { dateFin: finRenouv || undefined, loyer },
                      }),
                    );
                    await rafraichir();
                    toast.success(t("renouvele"));
                    router.push(`/baux/${nouveau.id}`);
                  })
                }
              >
                {t("confirmerRenouvellement")}
              </Button>
              <Button variant="outline" onClick={() => setPanneau(null)}>
                {t("annuler")}
              </Button>
            </div>
          </CardContent>
        </Card>
      ) : null}

      {!brouillon ? <EcheancesBail bailId={id} /> : null}

      {!brouillon ? (
        <Card>
          <CardHeader>
            <CardTitle>{t("quittancesRecus")}</CardTitle>
          </CardHeader>
          <CardContent>
            <DocumentsPaiement bailId={id} />
          </CardContent>
        </Card>
      ) : null}

      {!brouillon ? (
        <Card>
          <CardHeader>
            <CardTitle>{t("documents")}</CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col gap-2 text-sm">
            {(documents.data ?? []).length === 0 ? <p className="text-muted-foreground">{t("aucunDocument")}</p> : null}
            {(documents.data ?? []).map((d) => (
              <a key={d.id} href={urlPhoto(d.fichierId ?? "")} target="_blank" rel="noopener noreferrer" className="underline-offset-2 hover:underline">
                {t("exemplaire", { version: d.version ?? 0 })} — {d.genereLe ? new Date(d.genereLe).toLocaleString(locale) : ""}
              </a>
            ))}
            <div>
              <Button
                variant="outline"
                size="sm"
                onClick={() =>
                  void executer(async () => {
                    deballer(await api.POST("/api/v1/baux/{id}/documents", { params: { path: { id } } }));
                    await rafraichir();
                  })
                }
              >
                {t("genererExemplaire")}
              </Button>
            </div>
          </CardContent>
        </Card>
      ) : null}

      <ConfirmDialog
        open={confirmation === "activer"}
        onOpenChange={(o) => !o && setConfirmation(null)}
        title={t("activerTitre")}
        description={t("activerDescription")}
        confirmLabel={t("activer")}
        onConfirm={() => {
          setConfirmation(null);
          return executer(async () => {
            deballer(await api.POST("/api/v1/baux/{id}/activer", { params: { path: { id } } }));
            await rafraichir();
            toast.success(t("active"));
          });
        }}
      />
      <ConfirmDialog
        open={confirmation === "supprimer"}
        onOpenChange={(o) => !o && setConfirmation(null)}
        title={t("supprimerTitre")}
        confirmLabel={t("supprimer")}
        onConfirm={() => {
          setConfirmation(null);
          return executer(async () => {
            deballer(await api.DELETE("/api/v1/baux/{id}", { params: { path: { id } } }));
            await queryClient.invalidateQueries({ queryKey: ["baux"] });
            router.push("/baux");
          });
        }}
      />
    </div>
  );
}
