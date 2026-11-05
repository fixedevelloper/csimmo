"use client";

import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useLocale, useTranslations } from "next-intl";
import { useState } from "react";
import { toast } from "sonner";

import { ConfirmDialog } from "@/components/confirm-dialog";
import { StatutPastille } from "@/components/statut-pastille";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useErrorMessage } from "@/hooks/use-error-message";
import { useMe } from "@/hooks/use-me";
import { api } from "@/lib/api/client";
import { deballer } from "@/lib/api/errors";
import type { components } from "@/lib/api/schema";
import { EnteteParametres } from "@/components/entete-parametres";

type Demande = components["schemas"]["DemandeAnonymisationDto"];

/** Conformité (loi 2024/017) : demandes d'effacement, registre des traitements, modèles de consentement. */
export function ConformitePage() {
  const t = useTranslations("conformite");
  const locale = useLocale();
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const { data: me } = useMe();
  const admin = me?.role === "ADMIN";
  const [erreur, setErreur] = useState<string | null>(null);
  const [aExecuter, setAExecuter] = useState<Demande | null>(null);
  const [aRefuser, setARefuser] = useState<Demande | null>(null);
  const [motif, setMotif] = useState("");
  const [conserve, setConserve] = useState<string[] | null>(null);

  const demandes = useQuery({ queryKey: ["demandes-anonymisation"], queryFn: async () => deballer(await api.GET("/api/v1/conformite/demandes", { params: { query: {} } })) });
  const registre = useQuery({ queryKey: ["registre"], queryFn: async () => deballer(await api.GET("/api/v1/conformite/registre")) });
  const modeles = useQuery({ queryKey: ["modeles-consentement", locale], queryFn: async () => deballer(await api.GET("/api/v1/conformite/modeles-consentement", { params: { query: { langue: locale } } })) });

  async function rafraichir() {
    await queryClient.invalidateQueries({ queryKey: ["demandes-anonymisation"] });
  }

  async function executer() {
    if (!aExecuter?.id) return;
    setErreur(null);
    try {
      const r = deballer(await api.POST("/api/v1/conformite/demandes/{id}/execution", { params: { path: { id: aExecuter.id } } }));
      setConserve(r.conserve ?? []);
      toast.success(t("anonymisee"));
      setAExecuter(null);
      await rafraichir();
    } catch (e) {
      setErreur(message(e));
      setAExecuter(null);
    }
  }

  async function refuser() {
    if (!aRefuser?.id) return;
    setErreur(null);
    try {
      deballer(await api.POST("/api/v1/conformite/demandes/{id}/refus", { params: { path: { id: aRefuser.id } }, body: { motif } }));
      setARefuser(null);
      setMotif("");
      await rafraichir();
    } catch (e) {
      setErreur(message(e));
    }
  }

  return (
    <div className="flex flex-col gap-8">
      <EnteteParametres titre={t("titre")} />
      {erreur ? (
        <p role="alert" className="text-sm text-destructive">
          {erreur}
        </p>
      ) : null}

      <section aria-labelledby="demandes" className="flex flex-col gap-3">
        <h2 id="demandes" className="text-lg font-semibold">
          {t("demandes")}
        </h2>
        <p className="text-sm text-muted-foreground">{t("demandesAide")}</p>
        {demandes.data && demandes.data.length === 0 ? <p className="text-sm">{t("aucuneDemande")}</p> : null}
        <ul className="flex flex-col gap-3">
          {demandes.data?.map((d) => (
            <li key={d.id} className="rounded-lg border p-3 text-sm" data-testid="demande">
              <div className="flex flex-wrap items-center gap-2">
                <strong>{t(`types.${d.type}`)}</strong>
                <StatutPastille statut={d.statut} libelle={t(`statutsDemande.${d.statut}`)} />
                <span className="text-muted-foreground">{d.recueLe ? new Date(d.recueLe).toLocaleDateString(locale) : ""}</span>
              </div>
              {d.motif ? <p>{d.motif}</p> : null}
              {d.motifRefus ? <p>{t("motifRefus", { motif: d.motifRefus })}</p> : null}
              {d.statut === "RECUE" && d.obstacles && d.obstacles.length > 0 ? (
                <ul className="mt-1 list-disc pl-5 text-destructive">
                  {d.obstacles.map((o) => (
                    <li key={o.code}>{t(`obstacles.${o.code}`)}</li>
                  ))}
                </ul>
              ) : null}
              {d.statut === "RECUE" && admin ? (
                <div className="mt-2 flex gap-2">
                  <Button size="sm" variant="destructive" disabled={(d.obstacles?.length ?? 0) > 0} onClick={() => setAExecuter(d)}>
                    {t("anonymiser")}
                  </Button>
                  <Button size="sm" variant="outline" onClick={() => setARefuser(d)}>
                    {t("refuser")}
                  </Button>
                </div>
              ) : null}
            </li>
          ))}
        </ul>
        {conserve ? (
          <div role="status" className="rounded-lg border bg-muted/50 p-3 text-sm">
            <p className="font-medium">{t("conserveTitre")}</p>
            <ul className="list-disc pl-5">
              {conserve.map((c) => (
                <li key={c}>{c}</li>
              ))}
            </ul>
          </div>
        ) : null}
        {aRefuser ? (
          <div className="flex max-w-xl flex-col gap-2 rounded-lg border p-3">
            <label htmlFor="motif" className="text-sm font-medium">
              {t("motifRefusLabel")}
            </label>
            <Input id="motif" value={motif} onChange={(e) => setMotif(e.target.value)} maxLength={500} />
            <div className="flex gap-2">
              <Button disabled={motif.trim() === ""} onClick={() => void refuser()}>
                {t("confirmerRefus")}
              </Button>
              <Button variant="outline" onClick={() => setARefuser(null)}>
                {t("annuler")}
              </Button>
            </div>
          </div>
        ) : null}
      </section>

      <section aria-labelledby="registre" className="flex flex-col gap-3">
        <h2 id="registre" className="text-lg font-semibold">
          {t("registre")}
        </h2>
        {registre.data ? (
          <>
            <p className="text-sm text-muted-foreground">{registre.data.responsable}</p>
            <p className="text-sm text-muted-foreground">{registre.data.note}</p>
            {registre.data.traitements?.map((x) => (
              <details key={x.id} className="rounded-lg border p-3 text-sm">
                <summary className="cursor-pointer font-medium">
                  {x.id} — {x.nom}
                </summary>
                <dl className="mt-2 grid gap-x-4 gap-y-1 sm:grid-cols-[12rem_1fr]">
                  <dt className="font-medium">{t("reg.finalite")}</dt>
                  <dd>{x.finalite}</dd>
                  <dt className="font-medium">{t("reg.baseLegale")}</dt>
                  <dd>{x.baseLegale}</dd>
                  <dt className="font-medium">{t("reg.donnees")}</dt>
                  <dd>{x.donnees?.join(" ; ")}</dd>
                  <dt className="font-medium">{t("reg.personnes")}</dt>
                  <dd>{x.personnes?.join(" ; ")}</dd>
                  <dt className="font-medium">{t("reg.destinataires")}</dt>
                  <dd>{x.destinataires?.join(" ; ")}</dd>
                  <dt className="font-medium">{t("reg.conservation")}</dt>
                  <dd>{x.conservation}</dd>
                  <dt className="font-medium">{t("reg.mesures")}</dt>
                  <dd>{x.mesures?.join(" ; ")}</dd>
                </dl>
              </details>
            ))}
          </>
        ) : null}
      </section>

      <section aria-labelledby="modeles" className="flex flex-col gap-3">
        <h2 id="modeles" className="text-lg font-semibold">
          {t("modeles")}
        </h2>
        <p className="text-sm text-muted-foreground">{t("modelesAide")}</p>
        {modeles.data?.map((m) => (
          <article key={m.finalite} className="rounded-lg border p-3 text-sm">
            <h3 className="font-medium">{m.titre}</h3>
            <p className="mt-1 whitespace-pre-line">{m.texte}</p>
            <p className="mt-1 text-xs text-muted-foreground">{t("versionModele", { version: m.version ?? "" })}</p>
          </article>
        ))}
        <div>
          <Button variant="outline" onClick={() => window.print()}>
            {t("imprimer")}
          </Button>
        </div>
      </section>

      <ConfirmDialog open={aExecuter !== null} onOpenChange={(o) => !o && setAExecuter(null)} title={t("confirmationTitre")} description={t("confirmationTexte")} confirmLabel={t("anonymiser")} onConfirm={executer} />
    </div>
  );
}
