"use client";

import { useState } from "react";

import { Champ, MessageErreur, classeBouton, classeChamp } from "@/components/ui";
import { EMPREINTE_VALIDE } from "@/lib/licences";

export type SaisieLicence = { clientId: string; palierLots: number; dateDebut: string; dateExpiration: string; empreinteServeur: string; fonctionnalites: string[] };

/** Validation côté client (le serveur revalide) : renvoie le message d'erreur ou null. */
export function validerLicence(s: SaisieLicence): string | null {
  if (!s.clientId) return "Choisissez un client.";
  if (!Number.isInteger(s.palierLots) || s.palierLots <= 0) return "Le palier doit être un nombre de lots positif.";
  if (!s.dateDebut || !s.dateExpiration) return "Les dates de début et d'expiration sont obligatoires.";
  if (s.dateExpiration < s.dateDebut) return "L'expiration ne peut pas précéder le début.";
  if (!EMPREINTE_VALIDE.test(s.empreinteServeur.trim())) return "L'empreinte du serveur doit comporter 64 caractères hexadécimaux.";
  return null;
}

export function FormulaireLicence({ clients, clientId, onSoumettre }: { clients: { id: string; nom: string }[]; clientId?: string; onSoumettre: (s: SaisieLicence) => Promise<void> }) {
  const [erreur, setErreur] = useState<string | null>(null);
  const [envoi, setEnvoi] = useState(false);
  const aujourdhui = new Date().toISOString().slice(0, 10);

  async function soumettre(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const f = new FormData(e.currentTarget);
    const saisie: SaisieLicence = {
      clientId: String(f.get("clientId") ?? ""),
      palierLots: Number(f.get("palierLots")),
      dateDebut: String(f.get("dateDebut") ?? ""),
      dateExpiration: String(f.get("dateExpiration") ?? ""),
      empreinteServeur: String(f.get("empreinteServeur") ?? "").trim().toLowerCase(),
      fonctionnalites: String(f.get("fonctionnalites") ?? "").split(",").map((x) => x.trim()).filter(Boolean),
    };
    const invalide = validerLicence(saisie);
    setErreur(invalide);
    if (invalide) return;
    setEnvoi(true);
    try {
      await onSoumettre(saisie);
    } catch (err) {
      setErreur(err instanceof Error ? err.message : "Une erreur est survenue.");
    } finally {
      setEnvoi(false);
    }
  }

  return (
    <form onSubmit={(e) => void soumettre(e)} className="flex max-w-xl flex-col gap-4" aria-label="Nouvelle licence">
      <Champ id="clientId" label="Client *">
        <select id="clientId" name="clientId" defaultValue={clientId ?? ""} className={classeChamp}>
          <option value="">— choisir —</option>
          {clients.map((c) => (
            <option key={c.id} value={c.id}>
              {c.nom}
            </option>
          ))}
        </select>
      </Champ>
      <Champ id="palierLots" label="Palier (nombre de lots) *">
        <input id="palierLots" name="palierLots" type="number" min={1} defaultValue={50} className={classeChamp} />
      </Champ>
      <div className="grid grid-cols-2 gap-4">
        <Champ id="dateDebut" label="Début *">
          <input id="dateDebut" name="dateDebut" type="date" defaultValue={aujourdhui} className={classeChamp} />
        </Champ>
        <Champ id="dateExpiration" label="Expiration *">
          <input id="dateExpiration" name="dateExpiration" type="date" className={classeChamp} />
        </Champ>
      </div>
      <Champ id="empreinteServeur" label="Empreinte du serveur *" aide="Affichée par l'installation du client (64 caractères hexadécimaux).">
        <input id="empreinteServeur" name="empreinteServeur" autoComplete="off" spellCheck={false} className={`${classeChamp} font-mono`} />
      </Champ>
      <Champ id="fonctionnalites" label="Fonctionnalités" aide="Séparées par des virgules.">
        <input id="fonctionnalites" name="fonctionnalites" className={classeChamp} />
      </Champ>
      <MessageErreur message={erreur} />
      <div>
        <button type="submit" className={classeBouton} disabled={envoi}>
          Émettre la licence
        </button>
      </div>
    </form>
  );
}
