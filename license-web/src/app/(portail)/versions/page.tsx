"use client";

import { useState } from "react";

import { Champ, MessageErreur, classeBouton, classeChamp } from "@/components/ui";
import { appel, lister } from "@/lib/api";
import type { Page, Version } from "@/lib/licences";
import { messageDe, useCharge } from "@/lib/use-charge";

const SHA256 = /^[0-9a-fA-F]{64}$/;

export default function Versions() {
  const { donnees, erreur, recharger } = useCharge(() => lister<Page<Version>>("/versions", { size: 50 }), []);
  const [erreurForm, setErreurForm] = useState<string | null>(null);

  async function publier(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const form = e.currentTarget;
    const f = new FormData(form);
    const url = String(f.get("urlTelechargement") ?? "");
    if (!url.startsWith("https://")) return setErreurForm("L'adresse de téléchargement doit commencer par https://.");
    if (!SHA256.test(String(f.get("sha256") ?? "").trim())) return setErreurForm("La somme de contrôle SHA-256 doit comporter 64 caractères hexadécimaux.");
    setErreurForm(null);
    try {
      await appel("POST", "/versions", { numero: f.get("numero"), notes: f.get("notes") || null, urlTelechargement: url, sha256: String(f.get("sha256")).trim() });
      form.reset();
      recharger();
    } catch (err) {
      setErreurForm(messageDe(err));
    }
  }

  return (
    <>
      <h1 className="text-2xl font-semibold">Versions publiées</h1>
      <form onSubmit={(e) => void publier(e)} className="grid max-w-2xl gap-3 rounded-lg border p-4" aria-label="Publier une version">
        <Champ id="numero" label="Numéro de version *">
          <input id="numero" name="numero" required maxLength={50} className={classeChamp} />
        </Champ>
        <Champ id="urlTelechargement" label="Adresse de téléchargement (https) *">
          <input id="urlTelechargement" name="urlTelechargement" type="url" required className={classeChamp} />
        </Champ>
        <Champ id="sha256" label="Somme de contrôle SHA-256 *">
          <input id="sha256" name="sha256" required spellCheck={false} className={`${classeChamp} font-mono`} />
        </Champ>
        <Champ id="notes" label="Notes de version">
          <textarea id="notes" name="notes" rows={3} maxLength={5000} className={classeChamp} />
        </Champ>
        <MessageErreur message={erreurForm} />
        <div>
          <button type="submit" className={classeBouton}>
            Publier la version
          </button>
        </div>
      </form>
      <MessageErreur message={erreur} />
      <ul className="flex flex-col gap-3">
        {donnees?.content.map((v) => (
          <li key={v.id} className="rounded-lg border p-3 text-sm">
            <p className="font-semibold">
              {v.numero} <span className="font-normal text-neutral-500">— {new Date(v.publieeLe).toLocaleDateString("fr-FR")} par {v.publieePar}</span>
            </p>
            {v.notes ? <p className="whitespace-pre-line">{v.notes}</p> : null}
            <p className="break-all">
              <a className="text-blue-700 underline" href={v.urlTelechargement} rel="noopener noreferrer">
                {v.urlTelechargement}
              </a>
            </p>
            <p className="font-mono text-xs break-all">SHA-256 : {v.sha256}</p>
          </li>
        ))}
      </ul>
      {donnees?.content.length === 0 ? <p>Aucune version publiée.</p> : null}
    </>
  );
}
