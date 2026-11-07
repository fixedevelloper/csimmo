"use client";

import { useQueryClient } from "@tanstack/react-query";
import {
  Check,
  Building2,
  UserCheck,
  FileText,
  CheckCircle2,
  ArrowRight,
  ArrowLeft,
  ShieldAlert,
  Calendar,
  DollarSign
} from "lucide-react";
import { useLocale, useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";

import { ErrorDisplay } from "@/components/error-display";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import { NativeSelect } from "@/components/ui/native-select";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useLots } from "@/hooks/use-patrimoine";
import { useGarants, useLocataires } from "@/hooks/use-tiers";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import { PERIODICITES, entierSaisi, formaterFcfa, type Periodicite } from "@/lib/baux";

type Conditions = {
  dateDebut: string;
  dateFin: string;
  loyer: string;
  charges: string;
  periodicite: Periodicite;
  jourEcheance: string;
  caution: string;
  moisAvance: string;
  preavisMois: string;
};

const CONDITIONS_VIDES: Conditions = {
  dateDebut: "",
  dateFin: "",
  loyer: "",
  charges: "0",
  periodicite: "MENSUELLE",
  jourEcheance: "5",
  caution: "0",
  moisAvance: "0",
  preavisMois: "3",
};

type LotChoisi = { id: string; libelle: string; loyerIndicatif?: number };
type LocataireChoisi = { id: string; nom: string };

/** Assistant en 4 étapes : lot → locataire et garants → conditions → récapitulatif. */
export function BailWizard() {
  const t = useTranslations("baux");
  const locale = useLocale();
  const message = useErrorMessage();
  const router = useRouter();
  const queryClient = useQueryClient();
  const [etape, setEtape] = useState(1);
  const [lot, setLot] = useState<LotChoisi | null>(null);
  const [locataire, setLocataire] = useState<LocataireChoisi | null>(null);
  const [garantIds, setGarantIds] = useState<string[]>([]);
  const [cond, setCond] = useState<Conditions>(CONDITIONS_VIDES);
  const [erreurs, setErreurs] = useState<Partial<Record<keyof Conditions, string>>>({});
  const [erreurApi, setErreurApi] = useState<string | null>(null);
  const [envoi, setEnvoi] = useState(false);

  const [saisieLot, setSaisieLot] = useState("");
  const lots = useLots({ statut: "LIBRE", q: saisieLot }, 0);
  const [saisieLoc, setSaisieLoc] = useState("");
  const locataires = useLocataires(saisieLoc, 0);
  const garants = useGarants(locataire?.id ?? "");

  function validerConditions(): boolean {
    const e: Partial<Record<keyof Conditions, string>> = {};
    if (!cond.dateDebut) e.dateDebut = t("erreurs.dateDebut");
    if (cond.dateFin && cond.dateFin <= cond.dateDebut) e.dateFin = t("erreurs.dateFin");
    if (!(entierSaisi(cond.loyer) > 0)) e.loyer = t("erreurs.montant");
    for (const c of ["charges", "caution"] as const) if (Number.isNaN(entierSaisi(cond[c]))) e[c] = t("erreurs.montant");
    const jour = entierSaisi(cond.jourEcheance);
    if (!(jour >= 1 && jour <= 31)) e.jourEcheance = t("erreurs.jour");
    const avance = entierSaisi(cond.moisAvance);
    if (!(avance >= 0 && avance <= 24)) e.moisAvance = t("erreurs.avance");
    const preavis = entierSaisi(cond.preavisMois);
    if (!(preavis >= 0 && preavis <= 12)) e.preavisMois = t("erreurs.preavis");
    setErreurs(e);
    return Object.keys(e).length === 0;
  }

  async function enregistrer(activer: boolean) {
    if (!lot || !locataire) return;
    setErreurApi(null);
    setEnvoi(true);
    try {
      const bail = deballer(
        await api.POST("/api/v1/baux", {
          body: {
            lotId: lot.id,
            locataireId: locataire.id,
            garantIds,
            dateDebut: cond.dateDebut,
            dateFin: cond.dateFin || undefined,
            loyer: entierSaisi(cond.loyer),
            charges: entierSaisi(cond.charges),
            periodicite: cond.periodicite,
            jourEcheance: entierSaisi(cond.jourEcheance),
            caution: entierSaisi(cond.caution),
            moisAvance: entierSaisi(cond.moisAvance),
            preavisMois: entierSaisi(cond.preavisMois),
          },
        }),
      );
      if (activer && bail.id) {
        try {
          deballer(await api.POST("/api/v1/baux/{id}/activer", { params: { path: { id: bail.id } } }));
        } catch (e) {
          toast.error(message(e));
        }
      }
      await queryClient.invalidateQueries({ queryKey: ["baux"] });
      await queryClient.invalidateQueries({ queryKey: ["lots"] });
      toast.success(t("cree"));
      router.push(`/baux/${bail.id}`);
    } catch (e) {
      setErreurApi(message(e));
    } finally {
      setEnvoi(false);
    }
  }

  const champ = (nom: keyof Conditions, label: string, type = "text", mode?: "numeric") => (
    <div className="flex flex-col gap-1.5">
      <Label htmlFor={`c-${nom}`} className="text-sm font-medium">
        {label}
      </Label>
      <Input
        id={`c-${nom}`}
        type={type}
        inputMode={mode}
        value={cond[nom]}
        aria-invalid={erreurs[nom] ? true : undefined}
        onChange={(e) => setCond({ ...cond, [nom]: e.target.value })}
      />
      {erreurs[nom] ? <p className="text-xs text-destructive font-medium">{erreurs[nom]}</p> : null}
    </div>
  );

  const getStepIcon = (n: number) => {
    switch(n) {
      case 1: return <Building2 className="size-4" />;
      case 2: return <UserCheck className="size-4" />;
      case 3: return <Calendar className="size-4" />;
      case 4: return <FileText className="size-4" />;
      default: return null;
    }
  };

  return (
    <div className="flex max-w-4xl flex-col gap-8 mx-auto w-full pb-10">
      {/* Barre de progression moderne */}
      <nav aria-label={t("etapes.titre")} className="w-full bg-card p-4 rounded-xl border shadow-sm">
        <ol className="flex items-center justify-between gap-2 text-sm">
          {[1, 2, 3, 4].map((n) => {
            const estPasse = n < etape;
            const estActif = n === etape;
            return (
              <li key={n} aria-current={estActif ? "step" : undefined} className="flex items-center gap-3 flex-1 last:flex-none">
                <div className="flex items-center gap-2">
                  <span
                    className={`flex size-9 shrink-0 items-center justify-center rounded-full text-xs font-semibold transition-all ${
                      estPasse
                        ? "bg-primary text-primary-foreground shadow-sm"
                        : estActif
                          ? "bg-primary text-primary-foreground ring-4 ring-primary/20 shadow-sm"
                          : "bg-muted text-muted-foreground"
                    }`}
                  >
                    {estPasse ? <Check className="size-4" aria-hidden /> : getStepIcon(n)}
                  </span>
                  <span className={`hidden md:inline text-xs font-medium ${estActif ? "font-bold text-foreground" : "text-muted-foreground"}`}>
                    {t(`etapes.e${n}`)}
                  </span>
                </div>
                {n < 4 ? <div aria-hidden className={`h-0.5 flex-1 mx-2 transition-colors ${estPasse ? "bg-primary" : "bg-border"}`} /> : null}
              </li>
            );
          })}
        </ol>
      </nav>

      {/* Contenu des étapes dans une Carte */}
      <Card className="shadow-sm border">
        <CardHeader className="border-b bg-muted/20 pb-4">
          <CardTitle className="text-lg font-semibold flex items-center gap-2">
            {getStepIcon(etape)}
            {t(`etapes.e${etape}`)}
          </CardTitle>
          <CardDescription>
            {etape === 1 && "Sélectionnez un bien immobilier libre pour ce nouveau bail."}
            {etape === 2 && "Associez un locataire principal et désignez éventuellement ses garants."}
            {etape === 3 && "Définissez les conditions financières, les dates et la périodicité."}
            {etape === 4 && "Vérifiez l'ensemble des informations avant la validation finale."}
          </CardDescription>
        </CardHeader>

        <CardContent className="pt-6">
          {/* Étape 1 : Choix du Lot */}
          {etape === 1 && (
            <section className="flex flex-col gap-4" aria-label={t("etapes.e1")}>
              <div className="relative">
                <Input
                  aria-label={t("rechercheLot")}
                  placeholder={t("rechercheLot")}
                  value={saisieLot}
                  onChange={(e) => setSaisieLot(e.target.value)}
                  className="max-w-md"
                />
              </div>
              {lots.error ? <ErrorDisplay erreur={lots.error} onRetry={() => void lots.refetch()} /> : null}
              {lots.data && (lots.data.content ?? []).length === 0 ? (
                <p className="text-sm text-muted-foreground py-4 text-center">{t("aucunLotLibre")}</p>
              ) : null}
              <ul className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {(lots.data?.content ?? []).map((l) => {
                  const libelle = `${l.immeubleNom ?? ""} — ${l.reference ?? ""}`;
                  const choisi = lot?.id === l.id;
                  return (
                    <li key={l.id}>
                      <button
                        type="button"
                        aria-pressed={choisi}
                        onClick={() => {
                          setLot({ id: l.id ?? "", libelle, loyerIndicatif: l.loyerIndicatif });
                          if (!cond.loyer && l.loyerIndicatif) setCond({ ...cond, loyer: String(l.loyerIndicatif) });
                        }}
                        className={`w-full text-left p-4 rounded-xl border transition-all flex flex-col justify-between gap-2 h-full ${
                          choisi
                            ? "border-primary bg-primary/5 ring-1 ring-primary shadow-sm"
                            : "border-border hover:border-primary/50 bg-card hover:bg-muted/30"
                        }`}
                      >
                        <div className="flex items-start justify-between gap-2">
                          <span className="font-semibold text-sm line-clamp-2">{libelle}</span>
                          {choisi && <CheckCircle2 className="size-5 text-primary shrink-0" />}
                        </div>
                        {l.loyerIndicatif && (
                          <div className="text-xs text-muted-foreground flex items-center gap-1 mt-auto pt-2 border-t border-border/50 w-full">
                            <DollarSign className="size-3.5" />
                            <span>Indicatif : {formaterFcfa(l.loyerIndicatif, locale)}</span>
                          </div>
                        )}
                      </button>
                    </li>
                  );
                })}
              </ul>
            </section>
          )}

          {/* Étape 2 : Locataire & Garants */}
          {etape === 2 && (
            <section className="flex flex-col gap-6" aria-label={t("etapes.e2")}>
              <div className="flex flex-col gap-3">
                <Label className="text-sm font-medium">Rechercher le locataire</Label>
                <Input
                  aria-label={t("rechercheLocataire")}
                  placeholder={t("rechercheLocataire")}
                  value={saisieLoc}
                  onChange={(e) => setSaisieLoc(e.target.value)}
                  className="max-w-md"
                />
                {locataires.error ? <ErrorDisplay erreur={locataires.error} onRetry={() => void locataires.refetch()} /> : null}
                <ul className="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-2">
                  {(locataires.data?.content ?? []).map((l) => {
                    const choisi = locataire?.id === l.id;
                    return (
                      <li key={l.id}>
                        <button
                          type="button"
                          aria-pressed={choisi}
                          onClick={() => {
                            setLocataire({ id: l.id ?? "", nom: l.nom ?? "" });
                            setGarantIds([]);
                          }}
                          className={`w-full text-left p-4 rounded-xl border transition-all flex items-center justify-between gap-3 ${
                            choisi
                              ? "border-primary bg-primary/5 ring-1 ring-primary shadow-sm"
                              : "border-border hover:border-primary/50 bg-card hover:bg-muted/30"
                          }`}
                        >
                          <div>
                            <p className="font-semibold text-sm">{l.nom}</p>
                            {l.telephone && <p className="text-xs text-muted-foreground mt-0.5">{l.telephone}</p>}
                          </div>
                          {choisi && <CheckCircle2 className="size-5 text-primary shrink-0" />}
                        </button>
                      </li>
                    );
                  })}
                </ul>
              </div>

              {locataire ? (
                <fieldset className="flex flex-col gap-3 pt-4 border-t">
                  <legend className="text-sm font-semibold flex items-center gap-2 text-foreground">
                    <span>{t("garants")}</span>
                    <span className="text-xs font-normal text-muted-foreground">(Optionnel)</span>
                  </legend>
                  {(garants.data ?? []).length === 0 ? (
                    <p className="text-sm text-muted-foreground italic">Aucun garant enregistré pour ce locataire.</p>
                  ) : null}
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    {(garants.data ?? []).map((g) => {
                      const estCoche = garantIds.includes(g.id ?? "");
                      return (
                        <label
                          key={g.id}
                          className={`flex items-center justify-between p-3 rounded-lg border cursor-pointer transition-all ${
                            estCoche ? "border-primary bg-primary/5" : "border-border bg-card hover:bg-muted/30"
                          }`}
                        >
                          <div className="flex items-center gap-3">
                            <Checkbox
                              checked={estCoche}
                              onCheckedChange={(checked) => setGarantIds(checked ? [...garantIds, g.id ?? ""] : garantIds.filter((x) => x !== g.id))}
                            />
                            <div className="text-sm">
                              <p className="font-medium">{g.nom}</p>
                              {g.lien && <p className="text-xs text-muted-foreground">{g.lien}</p>}
                            </div>
                          </div>
                        </label>
                      );
                    })}
                  </div>
                </fieldset>
              ) : null}
            </section>
          )}

          {/* Étape 3 : Conditions financières */}
          {etape === 3 && (
            <section className="grid gap-4 sm:grid-cols-2" aria-label={t("etapes.e3")}>
              {champ("dateDebut", t("dateDebut"), "date")}
              {champ("dateFin", t("dateFin"), "date")}
              {champ("loyer", t("loyerFcfa"), "text", "numeric")}
              {champ("charges", t("chargesFcfa"), "text", "numeric")}
              <div className="flex flex-col gap-1.5">
                <Label htmlFor="c-periodicite" className="text-sm font-medium">
                  {t("periodicite")}
                </Label>
                <NativeSelect
                  id="c-periodicite"
                  value={cond.periodicite}
                  onChange={(e) => setCond({ ...cond, periodicite: e.target.value as Periodicite })}
                  className="w-full"
                >
                  {PERIODICITES.map((p) => (
                    <option key={p} value={p}>
                      {t(`periodicites.${p}`)}
                    </option>
                  ))}
                </NativeSelect>
              </div>
              {champ("jourEcheance", t("jourEcheance"), "text", "numeric")}
              {champ("caution", t("cautionFcfa"), "text", "numeric")}
              {champ("moisAvance", t("moisAvance"), "text", "numeric")}
              {champ("preavisMois", t("preavisMois"), "text", "numeric")}
            </section>
          )}

          {/* Étape 4 : Récapitulatif */}
          {etape === 4 && lot && locataire && (
            <section className="flex flex-col gap-6" aria-label={t("etapes.e4")}>
              <div className="rounded-xl border bg-muted/20 p-5">
                <dl className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4 text-sm">
                  <div className="flex flex-col">
                    <dt className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">{t("lot")}</dt>
                    <dd className="font-medium mt-0.5">{lot.libelle}</dd>
                  </div>
                  <div className="flex flex-col">
                    <dt className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">{t("locataire")}</dt>
                    <dd className="font-medium mt-0.5">{locataire.nom}</dd>
                  </div>
                  <div className="flex flex-col">
                    <dt className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">{t("garants")}</dt>
                    <dd className="font-medium mt-0.5">{garantIds.length} garant(s) associé(s)</dd>
                  </div>
                  <div className="flex flex-col">
                    <dt className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">{t("periode")}</dt>
                    <dd className="font-medium mt-0.5">{cond.dateDebut} → {cond.dateFin || t("sansFin")}</dd>
                  </div>
                  <div className="flex flex-col">
                    <dt className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">{t("loyer")}</dt>
                    <dd className="font-medium mt-0.5 text-primary text-base">{formaterFcfa(entierSaisi(cond.loyer), locale)}</dd>
                  </div>
                  <div className="flex flex-col">
                    <dt className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">{t("charges")}</dt>
                    <dd className="font-medium mt-0.5">{formaterFcfa(entierSaisi(cond.charges), locale)}</dd>
                  </div>
                  <div className="flex flex-col">
                    <dt className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">{t("periodicite")}</dt>
                    <dd className="font-medium mt-0.5">{t(`periodicites.${cond.periodicite}`)} (Échéance le {cond.jourEcheance})</dd>
                  </div>
                  <div className="flex flex-col">
                    <dt className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">{t("caution")}</dt>
                    <dd className="font-medium mt-0.5">{formaterFcfa(entierSaisi(cond.caution), locale)} ({cond.moisAvance} mois d&apos;avance)</dd>
                  </div>
                </dl>
              </div>

              <p className="text-xs text-muted-foreground">{t("recapAide")}</p>

              {erreurApi ? (
                <div role="alert" className="flex items-center gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive font-medium">
                  <ShieldAlert className="size-4 shrink-0" />
                  <span>{erreurApi}</span>
                </div>
              ) : null}
            </section>
          )}
        </CardContent>
      </Card>

      {/* Boutons de navigation bas de page */}
      <div className="flex items-center justify-between pt-2">
        {etape > 1 ? (
          <Button type="button" variant="outline" onClick={() => setEtape(etape - 1)} className="gap-2">
            <ArrowLeft className="size-4" />
            {t("precedent")}
          </Button>
        ) : <div />}

        {etape < 4 ? (
          <Button
            type="button"
            disabled={(etape === 1 && !lot) || (etape === 2 && !locataire)}
            onClick={() => {
              if (etape === 3 && !validerConditions()) return;
              setEtape(etape + 1);
            }}
            className="gap-2 ml-auto"
          >
            {t("suivant")}
            <ArrowRight className="size-4" />
          </Button>
        ) : (
          <div className="flex items-center gap-3 ml-auto">
            <Button type="button" variant="outline" disabled={envoi} onClick={() => void enregistrer(false)}>
              {t("enregistrerBrouillon")}
            </Button>
            <Button type="button" disabled={envoi} onClick={() => void enregistrer(true)} className="gap-2">
              <CheckCircle2 className="size-4" />
              {t("creerEtActiver")}
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}
