"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";

import { ErrorDisplay } from "@/components/error-display";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
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
          // Le brouillon existe : on l'ouvre pour corriger, l'erreur est signalée.
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
    <div className="flex flex-col gap-1">
      <label htmlFor={`c-${nom}`} className="text-sm font-medium">
        {label}
      </label>
      <Input
        id={`c-${nom}`}
        type={type}
        inputMode={mode}
        value={cond[nom]}
        aria-invalid={erreurs[nom] ? true : undefined}
        onChange={(e) => setCond({ ...cond, [nom]: e.target.value })}
      />
      {erreurs[nom] ? <p className="text-sm text-destructive">{erreurs[nom]}</p> : null}
    </div>
  );

  return (
    <div className="flex max-w-3xl flex-col gap-6">
      <ol className="flex flex-wrap gap-2 text-sm" aria-label={t("etapes.titre")}>
        {[1, 2, 3, 4].map((n) => (
          <li key={n} aria-current={n === etape ? "step" : undefined} className={n === etape ? "font-semibold" : "text-muted-foreground"}>
            {n}. {t(`etapes.e${n}`)}
          </li>
        ))}
      </ol>

      {etape === 1 ? (
        <section className="flex flex-col gap-3" aria-label={t("etapes.e1")}>
          <Input aria-label={t("rechercheLot")} placeholder={t("rechercheLot")} value={saisieLot} onChange={(e) => setSaisieLot(e.target.value)} />
          {lots.error ? <ErrorDisplay erreur={lots.error} onRetry={() => void lots.refetch()} /> : null}
          {lots.data && (lots.data.content ?? []).length === 0 ? <p className="text-muted-foreground">{t("aucunLotLibre")}</p> : null}
          <ul className="flex flex-col gap-2">
            {(lots.data?.content ?? []).map((l) => {
              const libelle = `${l.immeubleNom ?? ""} — ${l.reference ?? ""}`;
              const choisi = lot?.id === l.id;
              return (
                <li key={l.id}>
                  <Button
                    type="button"
                    variant={choisi ? "default" : "outline"}
                    aria-pressed={choisi}
                    className="w-full justify-start"
                    onClick={() => {
                      setLot({ id: l.id ?? "", libelle, loyerIndicatif: l.loyerIndicatif });
                      if (!cond.loyer && l.loyerIndicatif) setCond({ ...cond, loyer: String(l.loyerIndicatif) });
                    }}
                  >
                    {libelle}
                  </Button>
                </li>
              );
            })}
          </ul>
        </section>
      ) : null}

      {etape === 2 ? (
        <section className="flex flex-col gap-3" aria-label={t("etapes.e2")}>
          <Input aria-label={t("rechercheLocataire")} placeholder={t("rechercheLocataire")} value={saisieLoc} onChange={(e) => setSaisieLoc(e.target.value)} />
          {locataires.error ? <ErrorDisplay erreur={locataires.error} onRetry={() => void locataires.refetch()} /> : null}
          <ul className="flex flex-col gap-2">
            {(locataires.data?.content ?? []).map((l) => {
              const choisi = locataire?.id === l.id;
              return (
                <li key={l.id}>
                  <Button
                    type="button"
                    variant={choisi ? "default" : "outline"}
                    aria-pressed={choisi}
                    className="w-full justify-start"
                    onClick={() => {
                      setLocataire({ id: l.id ?? "", nom: l.nom ?? "" });
                      setGarantIds([]);
                    }}
                  >
                    {l.nom} {l.telephone ? `· ${l.telephone}` : ""}
                  </Button>
                </li>
              );
            })}
          </ul>
          {locataire ? (
            <fieldset className="flex flex-col gap-2">
              <legend className="text-sm font-medium">{t("garants")}</legend>
              {(garants.data ?? []).length === 0 ? <p className="text-sm text-muted-foreground">{t("aucunGarant")}</p> : null}
              {(garants.data ?? []).map((g) => (
                <label key={g.id} className="flex items-center gap-2 text-sm">
                  <input
                    type="checkbox"
                    checked={garantIds.includes(g.id ?? "")}
                    onChange={(e) => setGarantIds(e.target.checked ? [...garantIds, g.id ?? ""] : garantIds.filter((x) => x !== g.id))}
                  />
                  {g.nom} {g.lien ? `(${g.lien})` : ""}
                </label>
              ))}
            </fieldset>
          ) : null}
        </section>
      ) : null}

      {etape === 3 ? (
        <section className="grid gap-4 sm:grid-cols-2" aria-label={t("etapes.e3")}>
          {champ("dateDebut", t("dateDebut"), "date")}
          {champ("dateFin", t("dateFin"), "date")}
          {champ("loyer", t("loyerFcfa"), "text", "numeric")}
          {champ("charges", t("chargesFcfa"), "text", "numeric")}
          <div className="flex flex-col gap-1">
            <label htmlFor="c-periodicite" className="text-sm font-medium">
              {t("periodicite")}
            </label>
            <select
              id="c-periodicite"
              value={cond.periodicite}
              onChange={(e) => setCond({ ...cond, periodicite: e.target.value as Periodicite })}
              className="h-8 rounded-lg border border-input bg-transparent px-2.5 text-sm"
            >
              {PERIODICITES.map((p) => (
                <option key={p} value={p}>
                  {t(`periodicites.${p}`)}
                </option>
              ))}
            </select>
          </div>
          {champ("jourEcheance", t("jourEcheance"), "text", "numeric")}
          {champ("caution", t("cautionFcfa"), "text", "numeric")}
          {champ("moisAvance", t("moisAvance"), "text", "numeric")}
          {champ("preavisMois", t("preavisMois"), "text", "numeric")}
        </section>
      ) : null}

      {etape === 4 && lot && locataire ? (
        <section className="flex flex-col gap-3" aria-label={t("etapes.e4")}>
          <dl className="grid grid-cols-[12rem_1fr] gap-x-4 gap-y-2 text-sm">
            <dt className="text-muted-foreground">{t("lot")}</dt>
            <dd>{lot.libelle}</dd>
            <dt className="text-muted-foreground">{t("locataire")}</dt>
            <dd>{locataire.nom}</dd>
            <dt className="text-muted-foreground">{t("garants")}</dt>
            <dd>{garantIds.length}</dd>
            <dt className="text-muted-foreground">{t("periode")}</dt>
            <dd>
              {cond.dateDebut} → {cond.dateFin || t("sansFin")}
            </dd>
            <dt className="text-muted-foreground">{t("loyer")}</dt>
            <dd>{formaterFcfa(entierSaisi(cond.loyer), locale)}</dd>
            <dt className="text-muted-foreground">{t("charges")}</dt>
            <dd>{formaterFcfa(entierSaisi(cond.charges), locale)}</dd>
            <dt className="text-muted-foreground">{t("periodicite")}</dt>
            <dd>{t(`periodicites.${cond.periodicite}`)}</dd>
            <dt className="text-muted-foreground">{t("jourEcheance")}</dt>
            <dd>{cond.jourEcheance}</dd>
            <dt className="text-muted-foreground">{t("caution")}</dt>
            <dd>{formaterFcfa(entierSaisi(cond.caution), locale)}</dd>
            <dt className="text-muted-foreground">{t("moisAvance")}</dt>
            <dd>{cond.moisAvance}</dd>
          </dl>
          <p className="text-sm text-muted-foreground">{t("recapAide")}</p>
          {erreurApi ? (
            <p role="alert" className="text-sm text-destructive">
              {erreurApi}
            </p>
          ) : null}
        </section>
      ) : null}

      <div className="flex gap-2">
        {etape > 1 ? (
          <Button type="button" variant="outline" onClick={() => setEtape(etape - 1)}>
            {t("precedent")}
          </Button>
        ) : null}
        {etape < 4 ? (
          <Button
            type="button"
            disabled={(etape === 1 && !lot) || (etape === 2 && !locataire)}
            onClick={() => {
              if (etape === 3 && !validerConditions()) return;
              setEtape(etape + 1);
            }}
          >
            {t("suivant")}
          </Button>
        ) : (
          <>
            <Button type="button" variant="outline" disabled={envoi} onClick={() => void enregistrer(false)}>
              {t("enregistrerBrouillon")}
            </Button>
            <Button type="button" disabled={envoi} onClick={() => void enregistrer(true)}>
              {t("creerEtActiver")}
            </Button>
          </>
        )}
      </div>
    </div>
  );
}
