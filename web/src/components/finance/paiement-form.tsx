"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useRef, useState } from "react";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useBauxDuLocataire } from "@/hooks/use-finance";
import { useLocataires } from "@/hooks/use-tiers";
import { api } from "@/lib/api/client";
import { ApiError, deballer } from "@/lib/api/errors";
import { entierSaisi } from "@/lib/baux";
import { MODES_PAIEMENT, nouvelleCle, referenceObligatoire, type ModePaiement } from "@/lib/finance";

/** Saisie rapide d'un encaissement. Une clé d'idempotence est envoyée : un double clic ou une nouvelle tentative ne crée qu'un paiement. */
export function PaiementForm() {
  const t = useTranslations("finance");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const [recherche, setRecherche] = useState("");
  const [locataireId, setLocataireId] = useState("");
  const { data: locataires } = useLocataires(recherche, 0);
  const { data: baux } = useBauxDuLocataire(locataireId);
  const cle = useRef(nouvelleCle());
  const [bailChoisi, setBailChoisi] = useState("");
  // un seul bail : il est choisi d'office
  const bailId = bailChoisi || (baux?.length === 1 ? (baux[0].id ?? "") : "");
  const [montant, setMontant] = useState("");
  const [mode, setMode] = useState<ModePaiement>("ESPECES");
  const [reference, setReference] = useState("");
  const [date, setDate] = useState("");
  const [erreurs, setErreurs] = useState<Record<string, string>>({});
  const [erreurApi, setErreurApi] = useState<string | null>(null);
  const [envoi, setEnvoi] = useState(false);

  async function soumettre(e: React.FormEvent) {
    e.preventDefault();
    const err: Record<string, string> = {};
    if (!bailId) err.bail = t("erreurs.bail");
    if (!(entierSaisi(montant) > 0)) err.montant = t("erreurs.montant");
    if (referenceObligatoire(mode) && reference.trim() === "") err.reference = t("erreurs.reference");
    setErreurs(err);
    if (Object.keys(err).length > 0) return;
    setErreurApi(null);
    setEnvoi(true);
    try {
      deballer(
        await api.POST("/api/v1/paiements", {
          params: { header: { "Idempotency-Key": cle.current } },
          body: { bailId, montant: entierSaisi(montant), mode, reference: reference.trim() || undefined, datePaiement: date || undefined },
        }),
      );
      cle.current = nouvelleCle();
      await queryClient.invalidateQueries({ queryKey: ["baux-locataire"] });
      setMontant("");
      setReference("");
      await Promise.all(["paiements", "caisse", "compte", "echeances"].map((k) => queryClient.invalidateQueries({ queryKey: [k] })));
      toast.success(t("enregistre"));
    } catch (e2) {
      // une erreur réseau garde la même clé : le renvoi ne peut pas doubler le paiement
      if (e2 instanceof ApiError) cle.current = nouvelleCle();
      setErreurApi(message(e2));
    } finally {
      setEnvoi(false);
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("saisieRapide")}</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={(e) => void soumettre(e)} noValidate className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
          <div className="flex flex-col gap-1">
            <label htmlFor="p-recherche" className="text-sm font-medium">
              {t("rechercheLocataire")}
            </label>
            <Input id="p-recherche" value={recherche} onChange={(e) => setRecherche(e.target.value)} />
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="p-locataire" className="text-sm font-medium">
              {t("locataire")}
            </label>
            <select
              id="p-locataire"
              value={locataireId}
              onChange={(e) => { setLocataireId(e.target.value); setBailChoisi(""); }}
              className="h-8 rounded-lg border border-input bg-transparent px-2.5 text-sm"
            >
              <option value="">{t("choisirLocataire")}</option>
              {(locataires?.content ?? []).map((l) => (
                <option key={l.id} value={l.id}>
                  {l.nom}
                </option>
              ))}
            </select>
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="p-bail" className="text-sm font-medium">
              {t("bail")}
            </label>
            <select id="p-bail" value={bailId} onChange={(e) => setBailChoisi(e.target.value)} className="h-8 rounded-lg border border-input bg-transparent px-2.5 text-sm">
              <option value="">{t("choisirBail")}</option>
              {(baux ?? []).map((b) => (
                <option key={b.id} value={b.id}>
                  {b.numero} — {b.immeubleNom} {b.lotReference}
                </option>
              ))}
            </select>
            {erreurs.bail ? <p className="text-sm text-destructive">{erreurs.bail}</p> : null}
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="p-montant" className="text-sm font-medium">
              {t("montantFcfa")}
            </label>
            <Input id="p-montant" inputMode="numeric" value={montant} onChange={(e) => setMontant(e.target.value)} />
            {erreurs.montant ? <p className="text-sm text-destructive">{erreurs.montant}</p> : null}
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="p-mode" className="text-sm font-medium">
              {t("mode")}
            </label>
            <select id="p-mode" value={mode} onChange={(e) => setMode(e.target.value as ModePaiement)} className="h-8 rounded-lg border border-input bg-transparent px-2.5 text-sm">
              {MODES_PAIEMENT.map((m) => (
                <option key={m} value={m}>
                  {t(`modes.${m}`)}
                </option>
              ))}
            </select>
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="p-ref" className="text-sm font-medium">
              {referenceObligatoire(mode) ? t("reference") : t("referenceFacultative")}
            </label>
            <Input id="p-ref" value={reference} onChange={(e) => setReference(e.target.value)} />
            {erreurs.reference ? <p className="text-sm text-destructive">{erreurs.reference}</p> : null}
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="p-date" className="text-sm font-medium">
              {t("datePaiement")}
            </label>
            <Input id="p-date" type="date" value={date} onChange={(e) => setDate(e.target.value)} />
          </div>
          <div className="flex items-end">
            <Button type="submit" disabled={envoi}>
              {t("enregistrer")}
            </Button>
          </div>
          {erreurApi ? (
            <p role="alert" className="text-sm text-destructive sm:col-span-2 lg:col-span-3">
              {erreurApi}
            </p>
          ) : null}
        </form>
      </CardContent>
    </Card>
  );
}
