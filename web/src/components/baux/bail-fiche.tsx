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
import { PageHeader } from "@/components/page-header";
import { InfoList, Section } from "@/components/section";
import { Sheet, SheetContent, SheetHeader, SheetTitle } from "@/components/ui/sheet";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
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
      <PageHeader
        retour={{ href: "/baux", libelle: t("titre") }}
        titre={bail.numero}
        statut={<BailStatutBadge statut={bail.statut} />}
        actions={
          <>
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
          </>
        }
      />

      {!panneau && erreur ? (
        <p role="alert" className="text-sm text-destructive">
          {erreur}
        </p>
      ) : null}

      <Tabs defaultValue="conditions">
        <TabsList variant="line" className="h-10 border-b">
          <TabsTrigger value="conditions">{t("onglets.conditions")}</TabsTrigger>
          {!brouillon ? <TabsTrigger value="echeances">{t("onglets.echeances")}</TabsTrigger> : null}
          {!brouillon ? <TabsTrigger value="documents">{t("onglets.documents")}</TabsTrigger> : null}
        </TabsList>
        <TabsContent value="conditions" className="pt-4">
          <Section titre={t("conditions")}>
          <InfoList
            items={[
              { libelle: t("lot"), valeur: `${bail.immeubleNom} — ${bail.lotReference}` },
              { libelle: t("locataire"), valeur: bail.locataireNom },
              { libelle: t("garants"), valeur: (bail.garants ?? []).map((g) => g.nom).join(", ") },
              { libelle: t("periode"), valeur: `${bail.dateDebut} → ${bail.dateFin ?? t("sansFin")}` },
              { libelle: t("loyer"), valeur: formaterFcfa(bail.loyer, locale) },
              { libelle: t("charges"), valeur: formaterFcfa(bail.charges, locale) },
              { libelle: t("periodicite"), valeur: bail.periodicite ? t(`periodicites.${bail.periodicite}`) : undefined },
              { libelle: t("jourEcheance"), valeur: bail.jourEcheance },
              { libelle: t("caution"), valeur: formaterFcfa(bail.caution, locale) },
              { libelle: t("moisAvance"), valeur: bail.moisAvance },
              { libelle: t("preavisMois"), valeur: bail.preavisMois },
              ...(bail.dateSortie
                ? [{ libelle: t("dateSortie"), valeur: `${bail.dateSortie} (${t("notifieLe", { date: bail.dateNotificationPreavis ?? "" })}) — ${bail.motifResiliation}` }]
                : []),
            ]}
          />
          </Section>
        </TabsContent>
        {!brouillon ? (
          <TabsContent value="echeances" className="pt-4">
            <EcheancesBail bailId={id} />
          </TabsContent>
        ) : null}
        {!brouillon ? (
          <TabsContent value="documents" className="flex flex-col gap-6 pt-4">
      {!brouillon ? (
        <Section titre={t("quittancesRecus")}>
            <DocumentsPaiement bailId={id} />
          </Section>
      ) : null}

      {!brouillon ? (
        <Section titre={t("documents")}>
          <div className="flex flex-col gap-2 text-sm">
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
          </div>
        </Section>
      ) : null}

          </TabsContent>
        ) : null}
      </Tabs>

      <Sheet open={panneau === "resilier"} onOpenChange={(o) => !o && setPanneau(null)}>
        <SheetContent side="right" className="w-full overflow-y-auto sm:max-w-md">
          <SheetHeader>
            <SheetTitle>{t("resilier")}</SheetTitle>
          </SheetHeader>
          <div className="flex flex-col gap-3 px-4 pb-4">{panneau === "resilier" && erreur ? <p role="alert" className="text-sm text-destructive">{erreur}</p> : null}
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
          </div>
        </SheetContent>
      </Sheet>
      <Sheet open={panneau === "renouveler"} onOpenChange={(o) => !o && setPanneau(null)}>
        <SheetContent side="right" className="w-full overflow-y-auto sm:max-w-md">
          <SheetHeader>
            <SheetTitle>{t("renouveler")}</SheetTitle>
          </SheetHeader>
          <div className="flex flex-col gap-3 px-4 pb-4">{panneau === "renouveler" && erreur ? <p role="alert" className="text-sm text-destructive">{erreur}</p> : null}
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
          </div>
        </SheetContent>
      </Sheet>

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
