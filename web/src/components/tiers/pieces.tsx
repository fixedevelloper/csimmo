"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useRef, useState } from "react";
import { toast } from "sonner";

import { SelectField } from "@/components/patrimoine/select-field";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { useErrorMessage } from "@/hooks/use-error-message";
import { usePieces } from "@/hooks/use-tiers";
import { api } from "@/lib/api/client";
import { deballer, enApiError } from "@/lib/api/errors";
import { TYPES_PIECE } from "@/lib/tiers";
import { urlPhoto } from "@/lib/patrimoine";
import { useForm } from "react-hook-form";

/** Pièces d'identité d'un locataire : envoyées chiffrées, ouvertes (déchiffrées) par l'API après contrôle des droits. */
export function Pieces({ locataireId, actif }: { locataireId: string; actif: boolean }) {
  const t = useTranslations("tiers");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const { data: pieces } = usePieces("locataires", locataireId);
  const champ = useRef<HTMLInputElement>(null);
  const [choisi, setChoisi] = useState<File | null>(null);
  const [erreur, setErreur] = useState<string | null>(null);
  const { control, getValues } = useForm({ defaultValues: { type: "CNI_RECTO" } });

  const envoyer = useMutation({
    mutationFn: async (fichier: File) => {
      const donnees = new FormData();
      donnees.append("type", getValues("type"));
      donnees.append("fichier", fichier);
      let reponse: Response;
      try {
        reponse = await fetch(`/api/proxy/api/v1/locataires/${locataireId}/pieces`, { method: "POST", body: donnees });
      } catch {
        throw enApiError(null, 0);
      }
      if (!reponse.ok) throw enApiError(await reponse.json().catch(() => null), reponse.status);
    },
    onSuccess: async () => {
      setErreur(null);
      setChoisi(null);
      if (champ.current) champ.current.value = "";
      toast.success(t("pieceAjoutee"));
      await queryClient.invalidateQueries({ queryKey: ["pieces", "locataires", locataireId] });
    },
    onError: (e) => setErreur(message(e)),
  });

  const supprimer = useMutation({
    mutationFn: async (id: string) => deballer(await api.DELETE("/api/v1/pieces/{id}", { params: { path: { id } } })),
    onSuccess: async () => queryClient.invalidateQueries({ queryKey: ["pieces", "locataires", locataireId] }),
    onError: (e) => setErreur(message(e)),
  });

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("pieces")}</CardTitle>
        <CardDescription>{t("piecesAide")}</CardDescription>
      </CardHeader>
      <CardContent className="flex flex-col gap-4">
        {pieces && pieces.length === 0 ? <p className="text-sm text-muted-foreground">{t("aucunePiece")}</p> : null}
        <ul className="flex flex-col gap-2">
          {(pieces ?? []).map((p) => (
            <li key={p.id} className="flex items-center justify-between gap-2 text-sm">
              <span>{t(`typesPiece.${p.typePiece ?? "AUTRE"}`)}</span>
              <span className="flex gap-2">
                <a href={urlPhoto(p.fichierId ?? "")} target="_blank" rel="noreferrer" className="underline underline-offset-2">
                  {t("voirPiece")}
                </a>
                {actif ? (
                  <Button type="button" variant="outline" size="sm" onClick={() => supprimer.mutate(p.id ?? "")}>
                    {t("supprimerPiece")}
                  </Button>
                ) : null}
              </span>
            </li>
          ))}
        </ul>
        {actif ? (
          <form
            className="flex flex-wrap items-end gap-2"
            onSubmit={(e) => {
              e.preventDefault();
              if (choisi) envoyer.mutate(choisi);
            }}
          >
            <div className="w-48">
              <SelectField control={control} name="type" label={t("typePiece")} options={TYPES_PIECE.map((v) => ({ valeur: v, libelle: t(`typesPiece.${v}`) }))} />
            </div>
            <input
              ref={champ}
              type="file"
              accept="image/jpeg,image/png,image/webp,application/pdf"
              aria-label={t("choisirPiece")}
              onChange={(e) => setChoisi(e.target.files?.[0] ?? null)}
              className="h-8 min-w-0 rounded-lg border border-input bg-transparent px-2.5 py-1 text-sm file:mr-2 file:border-0 file:bg-transparent file:text-sm file:font-medium"
            />
            <Button type="submit" disabled={envoyer.isPending || !choisi}>
              {t("ajouterPiece")}
            </Button>
          </form>
        ) : null}
        {erreur ? (
          <p role="alert" className="text-sm text-destructive">
            {erreur}
          </p>
        ) : null}
      </CardContent>
    </Card>
  );
}
