"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import {
  FileText,
  User,
  Building2,
  Calendar,
  DollarSign,
  CheckCircle2,
  Trash2,
  RefreshCw,
  LogOut,
  ExternalLink,
  PlusCircle,
  ShieldCheck,
  Clock,
  AlertCircle,
  Download,
  Users
} from "lucide-react";

import { BailStatutBadge } from "@/components/baux/bail-statut-badge";
import { EcheancesBail } from "@/components/baux/echeances-bail";
import { DocumentsPaiement } from "@/components/finance/documents-paiement";
import { ConfirmDialog } from "@/components/confirm-dialog";
import { ErrorDisplay } from "@/components/error-display";
import { Button } from "@/components/ui/button";
import { PageHeader } from "@/components/page-header";
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetDescription } from "@/components/ui/sheet";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
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

  if (isPending || !bail) {
    return (
      <div className="flex flex-col gap-6 max-w-7xl mx-auto w-full">
        <Skeleton className="h-20 w-full rounded-xl" />
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Skeleton className="h-28 rounded-xl" />
          <Skeleton className="h-28 rounded-xl" />
          <Skeleton className="h-28 rounded-xl" />
          <Skeleton className="h-28 rounded-xl" />
        </div>
        <Skeleton className="h-96 w-full rounded-xl" />
      </div>
    );
  }

  const brouillon = bail.statut === "BROUILLON";
  const actif = bail.statut === "ACTIF";

  return (
    <div className="flex flex-col gap-6 max-w-7xl mx-auto w-full">
      {/* En-tête avec titre & actions */}
      <PageHeader
        retour={{ href: "/baux", libelle: t("titre") }}
        titre={`Bail N° ${bail.numero}`}
        statut={<BailStatutBadge statut={bail.statut} />}
        actions={
          <div className="flex flex-wrap items-center gap-2">
            <a
              href={urlApercuBail(id)}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex h-9 items-center gap-1.5 rounded-lg border border-border/70 bg-background px-3 py-2 text-sm font-medium shadow-sm transition-all hover:bg-muted/50 text-foreground"
            >
              <ExternalLink className="size-4 text-muted-foreground" />
              {t("apercuPdf")}
            </a>
            {brouillon && (
              <>
                <Button onClick={() => setConfirmation("activer")} className="gap-1.5 h-9 shadow-sm">
                  <CheckCircle2 className="size-4" />
                  {t("activer")}
                </Button>
                <Button variant="outline" onClick={() => setConfirmation("supprimer")} className="gap-1.5 h-9 text-destructive border-destructive/30 hover:bg-destructive/10">
                  <Trash2 className="size-4" />
                  {t("supprimer")}
                </Button>
              </>
            )}
            {actif && (
              <>
                {bail.dateFin && (
                  <Button variant="outline" onClick={() => setPanneau("renouveler")} className="gap-1.5 h-9 border-border/70">
                    <RefreshCw className="size-4 text-muted-foreground" />
                    {t("renouveler")}
                  </Button>
                )}
                <Button variant="outline" onClick={() => setPanneau("resilier")} className="gap-1.5 h-9 text-destructive border-destructive/30 hover:bg-destructive/10">
                  <LogOut className="size-4" />
                  {t("resilier")}
                </Button>
              </>
            )}
          </div>
        }
      />

      {/* Message d'erreur global adouci */}
      {!panneau && erreur && (
        <div role="alert" className="flex items-center gap-2 rounded-xl border border-destructive/20 bg-destructive/10 p-3.5 text-xs font-medium text-destructive">
          <AlertCircle className="size-4 shrink-0" />
          <span>{erreur}</span>
        </div>
      )}

      {/* Cartes KPIs (Format Soft) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="shadow-sm border-border/60">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              {t("loyer")}
            </CardTitle>
            <div className="p-1.5 rounded-md bg-primary/10 text-primary">
              <DollarSign className="size-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold tracking-tight text-foreground">{formaterFcfa(bail.loyer, locale)}</div>
            <p className="text-xs text-muted-foreground mt-1">
              {bail.periodicite ? t(`periodicites.${bail.periodicite}`) : "Mensuel"}
            </p>
          </CardContent>
        </Card>

        <Card className="shadow-sm border-border/60">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              {t("charges")}
            </CardTitle>
            <div className="p-1.5 rounded-md bg-muted text-muted-foreground">
              <DollarSign className="size-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold tracking-tight text-foreground">{formaterFcfa(bail.charges, locale)}</div>
            <p className="text-xs text-muted-foreground mt-1">Échéance le {bail.jourEcheance} du mois</p>
          </CardContent>
        </Card>

        <Card className="shadow-sm border-border/60">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              {t("caution")}
            </CardTitle>
            <div className="p-1.5 rounded-md bg-muted text-muted-foreground">
              <ShieldCheck className="size-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold tracking-tight text-foreground">{formaterFcfa(bail.caution, locale)}</div>
            <p className="text-xs text-muted-foreground mt-1">{bail.moisAvance ?? 0} mois d&apos;avance</p>
          </CardContent>
        </Card>

        <Card className="shadow-sm border-border/60">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              {t("periode")}
            </CardTitle>
            <div className="p-1.5 rounded-md bg-muted text-muted-foreground">
              <Calendar className="size-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-base font-semibold truncate text-foreground">{bail.dateDebut}</div>
            <p className="text-xs text-muted-foreground mt-1">
              Fin : {bail.dateFin ?? t("sansFin")}
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Onglets de navigation */}
      <Tabs defaultValue="conditions" className="w-full">
        <TabsList variant="line" className="h-11 border-b w-full justify-start rounded-none bg-transparent p-0 gap-6">
          <TabsTrigger value="conditions" className="data-[state=active]:border-primary py-2.5 text-sm font-medium gap-2">
            <Building2 className="size-4" />
            {t("onglets.conditions")}
          </TabsTrigger>
          {!brouillon && (
            <TabsTrigger value="echeances" className="data-[state=active]:border-primary py-2.5 text-sm font-medium gap-2">
              <Clock className="size-4" />
              {t("onglets.echeances")}
            </TabsTrigger>
          )}
          {!brouillon && (
            <TabsTrigger value="documents" className="data-[state=active]:border-primary py-2.5 text-sm font-medium gap-2">
              <FileText className="size-4" />
              {t("onglets.documents")}
            </TabsTrigger>
          )}
        </TabsList>

        {/* Tab 1: Conditions */}
        <TabsContent value="conditions" className="pt-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Card className="shadow-sm border-border/60">
              <CardHeader className="border-b bg-muted/10 pb-3">
                <CardTitle className="text-base font-semibold flex items-center gap-2">
                  <Building2 className="size-4 text-primary" />
                  Informations Générales
                </CardTitle>
              </CardHeader>
              <CardContent className="divide-y text-sm pt-3">
                <div className="flex justify-between items-center py-3">
                  <span className="text-muted-foreground flex items-center gap-2">
                    <Building2 className="size-3.5" />
                    {t("lot")}
                  </span>
                  <span className="font-medium text-foreground text-right">{bail.immeubleNom} — {bail.lotReference}</span>
                </div>
                <div className="flex justify-between items-center py-3">
                  <span className="text-muted-foreground flex items-center gap-2">
                    <User className="size-3.5" />
                    {t("locataire")}
                  </span>
                  <span className="font-medium text-foreground text-right">{bail.locataireNom}</span>
                </div>
                <div className="flex justify-between items-center py-3">
                  <span className="text-muted-foreground flex items-center gap-2">
                    <Users className="size-3.5" />
                    {t("garants")}
                  </span>
                  <span className="font-medium text-foreground text-right">
                    {(bail.garants ?? []).length > 0
                      ? (bail.garants ?? []).map((g) => g.nom).join(", ")
                      : "Aucun"}
                  </span>
                </div>
              </CardContent>
            </Card>

            <Card className="shadow-sm border-border/60">
              <CardHeader className="border-b bg-muted/10 pb-3">
                <CardTitle className="text-base font-semibold flex items-center gap-2">
                  <Clock className="size-4 text-primary" />
                  Modalités du Contrat
                </CardTitle>
              </CardHeader>
              <CardContent className="divide-y text-sm pt-3">
                <div className="flex justify-between items-center py-3">
                  <span className="text-muted-foreground">{t("preavisMois")}</span>
                  <span className="font-medium text-foreground">{bail.preavisMois} mois</span>
                </div>
                <div className="flex justify-between items-center py-3">
                  <span className="text-muted-foreground">{t("jourEcheance")}</span>
                  <span className="font-medium text-foreground">Le {bail.jourEcheance} du mois</span>
                </div>
                <div className="flex justify-between items-center py-3">
                  <span className="text-muted-foreground">{t("moisAvance")}</span>
                  <span className="font-medium text-foreground">{bail.moisAvance} mois</span>
                </div>
                {bail.dateSortie && (
                  <div className="flex justify-between py-3 text-destructive">
                    <span>{t("dateSortie")}</span>
                    <span className="font-medium text-right">
                      {bail.dateSortie} ({t("notifieLe", { date: bail.dateNotificationPreavis ?? "" })})
                      <br />
                      <span className="text-xs text-muted-foreground">{bail.motifResiliation}</span>
                    </span>
                  </div>
                )}
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        {/* Tab 2: Échéances */}
        {!brouillon && (
          <TabsContent value="echeances" className="pt-6">
            <EcheancesBail bailId={id} />
          </TabsContent>
        )}

        {/* Tab 3: Documents */}
        {!brouillon && (
          <TabsContent value="documents" className="flex flex-col gap-6 pt-6">
            <Card className="shadow-sm border-border/60">
              <CardHeader className="border-b bg-muted/10 pb-3">
                <CardTitle className="text-base font-semibold">{t("quittancesRecus")}</CardTitle>
              </CardHeader>
              <CardContent className="pt-4">
                <DocumentsPaiement bailId={id} />
              </CardContent>
            </Card>

            <Card className="shadow-sm border-border/60">
              <CardHeader className="flex flex-row items-center justify-between border-b bg-muted/10 pb-3">
                <CardTitle className="text-base font-semibold">{t("documents")}</CardTitle>
                <Button
                  variant="outline"
                  size="sm"
                  className="gap-1.5 h-8 text-xs border-border/70"
                  onClick={() =>
                    void executer(async () => {
                      deballer(await api.POST("/api/v1/baux/{id}/documents", { params: { path: { id } } }));
                      await rafraichir();
                    })
                  }
                >
                  <PlusCircle className="size-3.5" />
                  {t("genererExemplaire")}
                </Button>
              </CardHeader>
              <CardContent className="pt-4">
                {(documents.data ?? []).length === 0 ? (
                  <p className="text-xs text-muted-foreground py-6 text-center italic">{t("aucunDocument")}</p>
                ) : (
                  <div className="divide-y divide-border/50">
                    {(documents.data ?? []).map((d) => (
                      <div key={d.id} className="flex items-center justify-between py-3">
                        <div className="flex items-center gap-3">
                          <div className="p-2 rounded-lg bg-muted/40 text-muted-foreground">
                            <FileText className="size-4" />
                          </div>
                          <div>
                            <p className="text-sm font-medium text-foreground">
                              {t("exemplaire", { version: d.version ?? 0 })}
                            </p>
                            <p className="text-xs text-muted-foreground">
                              {d.genereLe ? new Date(d.genereLe).toLocaleString(locale) : ""}
                            </p>
                          </div>
                        </div>
                        <a
                          href={urlPhoto(d.fichierId ?? "")}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="inline-flex items-center gap-1.5 text-xs font-medium text-primary hover:underline bg-primary/5 px-2.5 py-1.5 rounded-lg border border-primary/20"
                        >
                          <Download className="size-3.5" />
                          Télécharger
                        </a>
                      </div>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>
        )}
      </Tabs>

      {/* Sheet: Résiliation */}
      <Sheet open={panneau === "resilier"} onOpenChange={(o) => !o && setPanneau(null)}>
        <SheetContent side="right" className="w-full sm:max-w-md border-l-border/60">
          <SheetHeader className="pb-4 border-b">
            <SheetTitle className="text-base font-semibold">{t("resilier")}</SheetTitle>
            <SheetDescription className="text-xs">Saisissez les détails de fin de bail et de préavis.</SheetDescription>
          </SheetHeader>
          <div className="flex flex-col gap-4 py-4">
            {panneau === "resilier" && erreur && (
              <div role="alert" className="flex items-center gap-2 rounded-lg border border-destructive/20 bg-destructive/10 p-3 text-xs text-destructive font-medium">
                <AlertCircle className="size-4 shrink-0" />
                <span>{erreur}</span>
              </div>
            )}

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="notification" className="text-xs font-medium">{t("dateNotification")}</Label>
              <Input id="notification" type="date" value={notification} onChange={(e) => setNotification(e.target.value)} className="h-9 text-xs" />
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="sortie" className="text-xs font-medium">{t("dateSortieAide", { mois: bail.preavisMois ?? 3 })}</Label>
              <Input id="sortie" type="date" value={sortie} onChange={(e) => setSortie(e.target.value)} className="h-9 text-xs" />
            </div>

            <div className="flex items-center gap-2.5 pt-1">
              <Checkbox id="reduit" checked={reduit} onCheckedChange={(checked) => setReduit(!!checked)} />
              <Label htmlFor="reduit" className="text-xs font-normal cursor-pointer text-muted-foreground">
                {t("preavisReduit")}
              </Label>
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="motif" className="text-xs font-medium">{t("motif")}</Label>
              <Input id="motif" value={motif} onChange={(e) => setMotif(e.target.value)} placeholder="Motif du départ..." className="h-9 text-xs" />
            </div>

            <div className="flex gap-2.5 pt-4 border-t">
              <Button
                variant="destructive"
                className="w-full h-9 text-xs"
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
              <Button variant="outline" className="w-full h-9 text-xs border-border/70" onClick={() => setPanneau(null)}>
                {t("annuler")}
              </Button>
            </div>
          </div>
        </SheetContent>
      </Sheet>

      {/* Sheet: Renouvellement */}
      <Sheet open={panneau === "renouveler"} onOpenChange={(o) => !o && setPanneau(null)}>
        <SheetContent side="right" className="w-full sm:max-w-md border-l-border/60">
          <SheetHeader className="pb-4 border-b">
            <SheetTitle className="text-base font-semibold">{t("renouveler")}</SheetTitle>
            <SheetDescription className="text-xs">{t("renouvellementAide")}</SheetDescription>
          </SheetHeader>
          <div className="flex flex-col gap-4 py-4">
            {panneau === "renouveler" && erreur && (
              <div role="alert" className="flex items-center gap-2 rounded-lg border border-destructive/20 bg-destructive/10 p-3 text-xs text-destructive font-medium">
                <AlertCircle className="size-4 shrink-0" />
                <span>{erreur}</span>
              </div>
            )}

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="finRenouv" className="text-xs font-medium">{t("dateFin")}</Label>
              <Input id="finRenouv" type="date" value={finRenouv} onChange={(e) => setFinRenouv(e.target.value)} className="h-9 text-xs" />
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="loyerRenouv" className="text-xs font-medium">{t("nouveauLoyer")}</Label>
              <Input id="loyerRenouv" inputMode="numeric" value={loyerRenouv} onChange={(e) => setLoyerRenouv(e.target.value)} placeholder="Ex: 150000" className="h-9 text-xs" />
            </div>

            <div className="flex gap-2.5 pt-4 border-t">
              <Button
                className="w-full h-9 text-xs"
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
              <Button variant="outline" className="w-full h-9 text-xs border-border/70" onClick={() => setPanneau(null)}>
                {t("annuler")}
              </Button>
            </div>
          </div>
        </SheetContent>
      </Sheet>

      {/* Dialogues de confirmation */}
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
