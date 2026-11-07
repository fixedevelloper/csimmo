"use client";

import { useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useEffect, useRef, useState } from "react";
import { toast } from "sonner";
import {
  Receipt,
  Search,
  User,
  FileText,
  DollarSign,
  CreditCard,
  Hash,
  Calendar,
  CheckCircle2,
  AlertCircle
} from "lucide-react";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { NativeSelect } from "@/components/ui/native-select";
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
  const [locataireNom, setLocataireNom] = useState("");

  // anti-rebond : une requête par pause de frappe, pas une par lettre
  const [rechercheEnvoyee, setRechercheEnvoyee] = useState("");
  useEffect(() => {
    const id = setTimeout(() => setRechercheEnvoyee(recherche.trim()), 300);
    return () => clearTimeout(id);
  }, [recherche]);

  const { data: locataires } = useLocataires(rechercheEnvoyee, 0);
  const liste = locataires?.content ?? [];

  // un seul résultat : il est choisi d'office ; sinon le locataire déjà choisi n'est jamais retiré de la liste
  const unique = rechercheEnvoyee !== "" && liste.length === 1 ? (liste[0].id ?? "") : "";
  const locataireActif = locataireId || unique;
  const { data: baux } = useBauxDuLocataire(locataireActif);

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
    <Card className="shadow-sm border-border/60">
      <CardHeader className="border-b bg-muted/10 pb-4">
        <div className="flex items-center gap-2.5">
          <div className="flex size-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
            <Receipt className="size-5" />
          </div>
          <div>
            <CardTitle className="text-base font-semibold">{t("saisieRapide")}</CardTitle>
            <CardDescription className="text-xs">
              Enregistrement rapide d&apos;un encaissement sécurisé par idempotence
            </CardDescription>
          </div>
        </div>
      </CardHeader>

      <CardContent className="pt-6">
        <form onSubmit={(e) => void soumettre(e)} noValidate className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">

          {/* Recherche Locataire */}
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="p-recherche" className="text-xs font-medium flex items-center gap-1.5 text-muted-foreground">
              <Search className="size-3.5" />
              {t("rechercheLocataire")}
            </Label>
            <Input
              id="p-recherche"
              value={recherche}
              onChange={(e) => setRecherche(e.target.value)}
              placeholder="Nom du locataire..."
              className="h-9 text-xs"
            />
          </div>

          {/* Sélection Locataire */}
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="p-locataire" className="text-xs font-medium flex items-center gap-1.5 text-muted-foreground">
              <User className="size-3.5" />
              {t("locataire")}
            </Label>
            <NativeSelect
              id="p-locataire"
              value={locataireActif}
              onChange={(e) => { setLocataireId(e.target.value); setLocataireNom(e.target.selectedOptions[0]?.text ?? ""); setBailChoisi(""); }}
              className="h-9 text-xs bg-background"
            >
              <option value="">{t("choisirLocataire")}</option>
              {locataireId && !liste.some((l) => l.id === locataireId) ? <option value={locataireId}>{locataireNom}</option> : null}
              {liste.map((l) => (
                <option key={l.id} value={l.id}>
                  {l.nom}
                </option>
              ))}
            </NativeSelect>
          </div>

          {/* Sélection Bail */}
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="p-bail" className="text-xs font-medium flex items-center gap-1.5 text-muted-foreground">
              <FileText className="size-3.5" />
              {t("bail")}
            </Label>
            <NativeSelect
              id="p-bail"
              value={bailId}
              onChange={(e) => setBailChoisi(e.target.value)}
              className="h-9 text-xs bg-background"
            >
              <option value="">{t("choisirBail")}</option>
              {(baux ?? []).map((b) => (
                <option key={b.id} value={b.id}>
                  {b.numero} — {b.immeubleNom} {b.lotReference}
                </option>
              ))}
            </NativeSelect>
            {erreurs.bail ? <p className="text-xs text-destructive font-medium">{erreurs.bail}</p> : null}
          </div>

          {/* Montant */}
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="p-montant" className="text-xs font-medium flex items-center gap-1.5 text-muted-foreground">
              <DollarSign className="size-3.5" />
              {t("montantFcfa")}
            </Label>
            <Input
              id="p-montant"
              inputMode="numeric"
              value={montant}
              onChange={(e) => setMontant(e.target.value)}
              placeholder="Ex: 150000"
              className="h-9 text-xs"
            />
            {erreurs.montant ? <p className="text-xs text-destructive font-medium">{erreurs.montant}</p> : null}
          </div>

          {/* Mode de Paiement */}
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="p-mode" className="text-xs font-medium flex items-center gap-1.5 text-muted-foreground">
              <CreditCard className="size-3.5" />
              {t("mode")}
            </Label>
            <NativeSelect
              id="p-mode"
              value={mode}
              onChange={(e) => setMode(e.target.value as ModePaiement)}
              className="h-9 text-xs bg-background"
            >
              {MODES_PAIEMENT.map((m) => (
                <option key={m} value={m}>
                  {t(`modes.${m}`)}
                </option>
              ))}
            </NativeSelect>
          </div>

          {/* Référence */}
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="p-ref" className="text-xs font-medium flex items-center gap-1.5 text-muted-foreground">
              <Hash className="size-3.5" />
              {referenceObligatoire(mode) ? t("reference") : t("referenceFacultative")}
            </Label>
            <Input
              id="p-ref"
              value={reference}
              onChange={(e) => setReference(e.target.value)}
              placeholder="N° de transaction, chèque..."
              className="h-9 text-xs"
            />
            {erreurs.reference ? <p className="text-xs text-destructive font-medium">{erreurs.reference}</p> : null}
          </div>

          {/* Date de Paiement */}
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="p-date" className="text-xs font-medium flex items-center gap-1.5 text-muted-foreground">
              <Calendar className="size-3.5" />
              {t("datePaiement")}
            </Label>
            <Input
              id="p-date"
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              className="h-9 text-xs"
            />
          </div>

          {/* Bouton de Soumission */}
          <div className="flex items-end sm:col-span-2 lg:col-span-2 pt-2">
            <Button type="submit" disabled={envoi} className="gap-2 h-9 text-xs w-full sm:w-auto shadow-sm">
              <CheckCircle2 className="size-4" />
              {t("enregistrer")}
            </Button>
          </div>

          {/* Erreur API globale */}
          {erreurApi ? (
            <div role="alert" className="flex items-center gap-2 rounded-lg border border-destructive/20 bg-destructive/10 p-3 text-xs text-destructive font-medium sm:col-span-2 lg:col-span-3 mt-2">
              <AlertCircle className="size-4 shrink-0" />
              <span>{erreurApi}</span>
            </div>
          ) : null}

        </form>
      </CardContent>
    </Card>
  );
}
