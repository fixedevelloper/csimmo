"use client";

import { useTranslations } from "next-intl";
import { useRef, useState } from "react";
import { toast } from "sonner";

import { ConfirmDialog } from "@/components/confirm-dialog";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useErrorMessage } from "@/hooks/use-error-message";
import { enApiError } from "@/lib/api/errors";
import type { components } from "@/lib/api/schema";
import { EnteteParametres } from "@/components/entete-parametres";
import { NativeSelect } from "@/components/ui/native-select";

type Previsualisation = components["schemas"]["PrevisualisationDto"];
type Resultat = components["schemas"]["ResultatImportDto"];
type Erreur = components["schemas"]["ErreurImportDto"];

const TYPES = ["IMMEUBLES", "LOTS", "LOCATAIRES", "BAUX", "SOLDES"] as const;
const AFFICHEES = 200;

async function envoyer<T>(url: string, fichier: File, type: string, empreinte?: string): Promise<{ statut: number; corps: T }> {
  const donnees = new FormData();
  donnees.append("fichier", fichier);
  if (fichier.name.toLowerCase().endsWith(".csv")) donnees.append("type", type);
  if (empreinte) donnees.append("empreinte", empreinte);
  let reponse: Response;
  try {
    reponse = await fetch(url, { method: "POST", body: donnees });
  } catch {
    throw enApiError(null, 0);
  }
  const corps = await reponse.json().catch(() => null);
  // 422 = rapport d'erreurs de l'import (pas une panne) ; toute autre erreur HTTP est une erreur d'API
  if (!reponse.ok && reponse.status !== 422) throw enApiError(corps, reponse.status);
  return { statut: reponse.status, corps: corps as T };
}

/** Assistant d'import : modèle → fichier → prévisualisation (erreurs ligne par ligne) → import tout ou rien. */
export function ImportPage() {
  const t = useTranslations("import");
  const message = useErrorMessage();
  const champ = useRef<HTMLInputElement>(null);
  const [fichier, setFichier] = useState<File | null>(null);
  const [typeCsv, setTypeCsv] = useState<string>("IMMEUBLES");
  const [previsu, setPrevisu] = useState<Previsualisation | null>(null);
  const [resultat, setResultat] = useState<Resultat | null>(null);
  const [erreur, setErreur] = useState<string | null>(null);
  const [occupe, setOccupe] = useState(false);
  const [confirmer, setConfirmer] = useState(false);

  const estCsv = fichier?.name.toLowerCase().endsWith(".csv") ?? false;

  function reinitialiser(f: File | null) {
    setFichier(f);
    setPrevisu(null);
    setResultat(null);
    setErreur(null);
  }

  async function analyser() {
    if (!fichier) return;
    setOccupe(true);
    setErreur(null);
    setResultat(null);
    try {
      const { corps } = await envoyer<Previsualisation>("/api/proxy/api/v1/import/previsualisation", fichier, typeCsv);
      setPrevisu(corps);
    } catch (e) {
      setErreur(message(e));
    } finally {
      setOccupe(false);
    }
  }

  async function importer() {
    if (!fichier || !previsu) return;
    setOccupe(true);
    setErreur(null);
    try {
      const { corps } = await envoyer<Resultat>("/api/proxy/api/v1/import", fichier, typeCsv, previsu.empreinte);
      setResultat(corps);
      if (corps.importe) {
        toast.success(t("importe"));
        setPrevisu(null);
      }
    } catch (e) {
      setErreur(message(e));
    } finally {
      setOccupe(false);
    }
  }

  const erreurs: Erreur[] = resultat && !resultat.importe ? (resultat.erreurs ?? []) : (previsu?.erreurs ?? []);

  return (
    <div className="flex flex-col gap-6">
      <EnteteParametres titre={t("titre")} sousTitre={t("intro")} />

      <section aria-labelledby="etape1" className="flex flex-col gap-2">
        <h2 id="etape1" className="text-lg font-semibold">
          {t("etape1")}
        </h2>
        <div className="flex flex-wrap items-center gap-3">
          <Button render={<a href="/api/proxy/api/v1/import/modele?format=xlsx" download />} variant="outline">
            {t("modeleExcel")}
          </Button>
          {TYPES.map((x) => (
            <a key={x} className="text-sm underline" href={`/api/proxy/api/v1/import/modele?format=csv&type=${x}`} download>
              {t("modeleCsv", { type: t(`types.${x}`) })}
            </a>
          ))}
        </div>
      </section>

      <section aria-labelledby="etape2" className="flex flex-col gap-3">
        <h2 id="etape2" className="text-lg font-semibold">
          {t("etape2")}
        </h2>
        <div className="flex flex-wrap items-end gap-3">
          <label className="flex flex-col gap-1 text-sm">
            {t("fichier")}
            <input ref={champ} type="file" accept=".xlsx,.csv" onChange={(e) => reinitialiser(e.target.files?.[0] ?? null)} />
          </label>
          {estCsv ? (
            <label className="flex flex-col gap-1 text-sm">
              {t("typeCsv")}
              <NativeSelect value={typeCsv} onChange={(e) => { setTypeCsv(e.target.value); setPrevisu(null); }}>
                {TYPES.map((x) => (
                  <option key={x} value={x}>
                    {t(`types.${x}`)}
                  </option>
                ))}
              </NativeSelect>
            </label>
          ) : null}
          <Button onClick={() => void analyser()} disabled={!fichier || occupe}>
            {t("analyser")}
          </Button>
        </div>
      </section>

      {erreur ? (
        <p role="alert" className="text-sm text-destructive">
          {erreur}
        </p>
      ) : null}

      {previsu ? (
        <section aria-labelledby="etape3" className="flex flex-col gap-3">
          <h2 id="etape3" className="text-lg font-semibold">
            {t("etape3")}
          </h2>
          <ul className="flex flex-wrap gap-2">
            {previsu.feuilles?.map((f) => (
              <li key={f.type}>
                <Badge variant="secondary">{t("lignes", { type: t(`types.${f.type}`), n: f.lignes ?? 0 })}</Badge>
              </li>
            ))}
          </ul>
          {previsu.importable ? (
            <p role="status" className="rounded-lg border bg-muted/50 p-3 text-sm">
              {t("pret")}
            </p>
          ) : null}
        </section>
      ) : null}

      {erreurs.length > 0 ? (
        <section aria-labelledby="erreurs" className="flex flex-col gap-2">
          <h2 id="erreurs" className="text-lg font-semibold text-destructive">
            {t("erreurs", { n: erreurs.length })}
          </h2>
          {resultat && !resultat.importe ? <p className="text-sm">{t("rienEcrit")}</p> : null}
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="border-b">
                  <th className="py-2">{t("col.feuille")}</th>
                  <th>{t("col.ligne")}</th>
                  <th>{t("col.colonne")}</th>
                  <th>{t("col.probleme")}</th>
                </tr>
              </thead>
              <tbody data-testid="erreurs-import">
                {erreurs.slice(0, AFFICHEES).map((e, i) => (
                  <tr key={i} className="border-b">
                    <td className="py-1">{e.feuille}</td>
                    <td>{e.ligne || "—"}</td>
                    <td>{e.colonne ?? "—"}</td>
                    <td>{e.message}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {erreurs.length > AFFICHEES ? <p className="text-sm">{t("tronque", { n: AFFICHEES, total: erreurs.length })}</p> : null}
        </section>
      ) : null}

      {previsu?.importable ? (
        <div>
          <Button onClick={() => setConfirmer(true)} disabled={occupe}>
            {t("importer")}
          </Button>
        </div>
      ) : null}

      {resultat?.importe ? (
        <section aria-labelledby="resultat" className="flex flex-col gap-2">
          <h2 id="resultat" className="text-lg font-semibold">
            {t("resultat")}
          </h2>
          <ul className="text-sm" data-testid="resultat-import">
            {Object.entries(resultat.crees ?? {}).map(([type, n]) => (
              <li key={type}>{t("cree", { type: t(`types.${type}`), n })}</li>
            ))}
            <li>{t("echeancesReprises", { n: resultat.echeancesReprises ?? 0 })}</li>
          </ul>
        </section>
      ) : null}

      <ConfirmDialog
        open={confirmer}
        onOpenChange={setConfirmer}
        title={t("confirmationTitre")}
        description={t("confirmationTexte")}
        confirmLabel={t("importer")}
        onConfirm={async () => {
          setConfirmer(false);
          await importer();
        }}
      />
    </div>
  );
}
