"use client";

import { useParams } from "next/navigation";
import { useState } from "react";

import { BadgeLicence, Champ, MessageErreur, classeBouton, classeBoutonSecondaire, classeChamp } from "@/components/ui";
import { appel } from "@/lib/api";
import { EMPREINTE_VALIDE, formaterFcfa, telechargerLic, type Emission, type HistoriqueEmission, type Licence, type Page, type Paiement } from "@/lib/licences";
import { messageDe, useCharge } from "@/lib/use-charge";

export default function FicheLicence() {
  const { id } = useParams<{ id: string }>();
  const licence = useCharge(() => appel<Licence>("GET", `/licences/${id}`), [id]);
  const historique = useCharge(() => appel<HistoriqueEmission[]>("GET", `/licences/${id}/emissions`), [id]);
  const paiements = useCharge(() => appel<Page<Paiement>>("GET", `/licences/${id}/paiements?size=100`), [id]);
  const [erreur, setErreur] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);

  const l = licence.donnees;
  const active = l?.statut === "ACTIVE";

  async function action(f: () => Promise<Emission | Licence>, message: string) {
    setErreur(null);
    setInfo(null);
    try {
      const r = await f();
      if ("fichier" in r) telechargerLic(id, r.fichier);
      setInfo(message);
      licence.recharger();
      historique.recharger();
    } catch (e) {
      setErreur(messageDe(e));
    }
  }

  const renouveler = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const f = new FormData(e.currentTarget);
    const palier = String(f.get("palierLots") ?? "");
    void action(() => appel<Emission>("POST", `/licences/${id}/renouvellement`, { dateExpiration: f.get("dateExpiration"), palierLots: palier ? Number(palier) : null, fonctionnalites: null }), "Licence renouvelée : le nouveau fichier est téléchargé.");
  };
  const transferer = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const empreinte = String(new FormData(e.currentTarget).get("empreinteServeur") ?? "").trim().toLowerCase();
    if (!EMPREINTE_VALIDE.test(empreinte)) return setErreur("L'empreinte du serveur doit comporter 64 caractères hexadécimaux.");
    void action(() => appel<Emission>("POST", `/licences/${id}/transfert`, { empreinteServeur: empreinte }), "Licence transférée : l'ancien serveur ne sera plus reconnu.");
  };
  const revoquer = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!window.confirm("Révoquer cette licence ? Cette action est définitive.")) return;
    void action(() => appel<Licence>("POST", `/licences/${id}/revocation`, { motif: new FormData(e.currentTarget).get("motif") }), "Licence révoquée.");
  };
  const payer = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = e.currentTarget;
    const f = new FormData(form);
    setErreur(null);
    appel("POST", `/licences/${id}/paiements`, { montant: Number(f.get("montant")), mode: f.get("mode"), reference: f.get("reference") || null, datePaiement: f.get("datePaiement"), note: null }).then(
      () => {
        form.reset();
        paiements.recharger();
      },
      (err) => setErreur(messageDe(err)),
    );
  };

  return (
    <>
      <MessageErreur message={erreur ?? licence.erreur} />
      {info ? (
        <p role="status" className="rounded-md bg-green-50 px-3 py-2 text-sm text-green-800">
          {info}
        </p>
      ) : null}
      {l ? (
        <>
          <div className="flex flex-wrap items-center gap-3">
            <h1 className="text-2xl font-semibold">{l.clientNom}</h1>
            <BadgeLicence licence={l} />
          </div>
          <dl className="grid gap-x-6 gap-y-2 text-sm sm:grid-cols-2">
            <dt className="font-medium">Identifiant de licence</dt>
            <dd className="font-mono break-all">{l.id}</dd>
            <dt className="font-medium">Période</dt>
            <dd>
              du {l.dateDebut} au {l.dateExpiration} ({l.joursRestants} jours restants)
            </dd>
            <dt className="font-medium">Palier</dt>
            <dd>{l.palierLots} lots</dd>
            <dt className="font-medium">Fonctionnalités</dt>
            <dd>{l.fonctionnalites.join(", ") || "—"}</dd>
            <dt className="font-medium">Empreinte du serveur</dt>
            <dd className="font-mono break-all">{l.empreinteServeur}</dd>
            <dt className="font-medium">Transferts effectués</dt>
            <dd>{l.transferts}</dd>
            <dt className="font-medium">Dernière vérification</dt>
            <dd>{l.derniereVerification ? `${new Date(l.derniereVerification).toLocaleString("fr-FR")} (version ${l.versionApplication ?? "?"})` : "Jamais"}</dd>
            {l.motifRevocation ? (
              <>
                <dt className="font-medium">Motif de révocation</dt>
                <dd>{l.motifRevocation}</dd>
              </>
            ) : null}
          </dl>
          <div>
            <a className={classeBouton} href={`/api/proxy/api/v1/licences/${id}/fichier`} download={`licence-${id}.lic`}>
              Télécharger le fichier .lic
            </a>
          </div>

          {active ? (
            <section className="grid gap-6 lg:grid-cols-3" aria-label="Actions">
              <form onSubmit={renouveler} className="flex flex-col gap-3 rounded-lg border p-4" aria-label="Renouveler">
                <h2 className="font-semibold">Renouveler</h2>
                <Champ id="dateExpiration" label="Nouvelle expiration *">
                  <input id="dateExpiration" name="dateExpiration" type="date" required min={l.dateExpiration} className={classeChamp} />
                </Champ>
                <Champ id="palierLots" label="Nouveau palier" aide="Laisser vide pour le conserver.">
                  <input id="palierLots" name="palierLots" type="number" min={1} className={classeChamp} />
                </Champ>
                <button className={classeBouton}>Renouveler</button>
              </form>
              <form onSubmit={transferer} className="flex flex-col gap-3 rounded-lg border p-4" aria-label="Transférer">
                <h2 className="font-semibold">Transférer vers un autre serveur</h2>
                <Champ id="nouvelleEmpreinte" label="Nouvelle empreinte *">
                  <input id="nouvelleEmpreinte" name="empreinteServeur" autoComplete="off" spellCheck={false} className={`${classeChamp} font-mono`} />
                </Champ>
                <button className={classeBoutonSecondaire}>Transférer</button>
              </form>
              <form onSubmit={revoquer} className="flex flex-col gap-3 rounded-lg border border-red-300 p-4" aria-label="Révoquer">
                <h2 className="font-semibold">Révoquer</h2>
                <Champ id="motif" label="Motif *">
                  <input id="motif" name="motif" required maxLength={500} className={classeChamp} />
                </Champ>
                <button className="rounded-md bg-red-700 px-4 py-2 text-sm font-medium text-white hover:bg-red-800">Révoquer</button>
              </form>
            </section>
          ) : null}

          <section className="flex flex-col gap-2" aria-label="Historique des fichiers émis">
            <h2 className="text-lg font-semibold">Fichiers émis</h2>
            <ul className="text-sm">
              {historique.donnees?.map((h) => (
                <li key={h.id} className="border-b py-1">
                  {new Date(h.creeLe).toLocaleDateString("fr-FR")} — {h.motif} — {h.palierLots} lots jusqu&apos;au {h.dateExpiration} — par {h.emisePar}
                </li>
              ))}
            </ul>
          </section>

          <section className="flex flex-col gap-3" aria-label="Paiements">
            <h2 className="text-lg font-semibold">Paiements de licence</h2>
            <form onSubmit={payer} className="grid gap-3 rounded-lg border p-4 sm:grid-cols-5" aria-label="Enregistrer un paiement">
              <Champ id="montant" label="Montant (FCFA) *">
                <input id="montant" name="montant" type="number" min={1} required className={classeChamp} />
              </Champ>
              <Champ id="mode" label="Mode *">
                <select id="mode" name="mode" className={classeChamp}>
                  <option>VIREMENT</option>
                  <option>MTN_MOMO</option>
                  <option>ORANGE_MONEY</option>
                  <option>ESPECES</option>
                </select>
              </Champ>
              <Champ id="reference" label="Référence">
                <input id="reference" name="reference" className={classeChamp} />
              </Champ>
              <Champ id="datePaiement" label="Date *">
                <input id="datePaiement" name="datePaiement" type="date" required className={classeChamp} />
              </Champ>
              <div className="flex items-end">
                <button className={classeBouton}>Enregistrer</button>
              </div>
            </form>
            <ul className="text-sm">
              {paiements.donnees?.content.map((p) => (
                <li key={p.id} className="border-b py-1">
                  {p.datePaiement} — {formaterFcfa(p.montant)} — {p.mode}
                  {p.reference ? ` (${p.reference})` : ""}
                </li>
              ))}
            </ul>
            {paiements.donnees?.content.length === 0 ? <p className="text-sm">Aucun paiement enregistré.</p> : null}
          </section>
        </>
      ) : null}
    </>
  );
}
