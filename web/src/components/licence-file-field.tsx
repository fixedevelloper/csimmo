"use client";

import { useId } from "react";

import { Field, FieldDescription, FieldError, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";

/** Taille maximale acceptée : un fichier de licence fait quelques centaines d'octets. */
export const LICENCE_TAILLE_MAX = 64 * 1024;

/** Lit un fichier texte (FileReader : disponible partout, contrairement à Blob.text()). */
function lireTexte(fichier: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const lecteur = new FileReader();
    lecteur.onload = () => resolve(String(lecteur.result ?? ""));
    lecteur.onerror = () => reject(lecteur.error);
    lecteur.readAsText(fichier);
  });
}

type Props = {
  valeur: string;
  onChange: (contenu: string) => void;
  erreur?: string;
  libelleFichier: string;
  libelleContenu: string;
  aide?: string;
  /** Appelé quand le fichier choisi est trop volumineux pour une licence (le parent vide le champ et signale l'erreur). */
  onFichierTropGros: () => void;
};

/** Choix d'un fichier .lic (lu dans le navigateur) ou collage de son contenu. */
export function LicenceFileField({ valeur, onChange, erreur, libelleFichier, libelleContenu, aide, onFichierTropGros }: Props) {
  const identifiant = useId();

  async function lire(fichier: File | undefined) {
    if (!fichier) return;
    if (fichier.size > LICENCE_TAILLE_MAX) {
      onFichierTropGros(); // le parent vide le champ et affiche l'erreur
      return;
    }
    onChange(await lireTexte(fichier));
  }

  return (
    <div className="flex flex-col gap-4">
      <Field>
        <FieldLabel htmlFor={`${identifiant}-fichier`}>{libelleFichier}</FieldLabel>
        <Input
          id={`${identifiant}-fichier`}
          type="file"
          accept=".lic,.json,application/json,text/plain"
          onChange={(e) => void lire(e.target.files?.[0])}
        />
        {aide ? <FieldDescription>{aide}</FieldDescription> : null}
      </Field>
      <Field data-invalid={Boolean(erreur)}>
        <FieldLabel htmlFor={`${identifiant}-contenu`}>{libelleContenu}</FieldLabel>
        <Textarea
          id={`${identifiant}-contenu`}
          value={valeur}
          onChange={(e) => onChange(e.target.value)}
          rows={8}
          spellCheck={false}
          className="font-mono text-xs"
          aria-invalid={Boolean(erreur)}
        />
        {erreur ? <FieldError>{erreur}</FieldError> : null}
      </Field>
    </div>
  );
}
