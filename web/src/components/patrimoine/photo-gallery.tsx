"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { useRef, useState } from "react";
import { toast } from "sonner";

import { ErrorDisplay } from "@/components/error-display";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useErrorMessage } from "@/hooks/use-error-message";
import { usePhotos } from "@/hooks/use-patrimoine";
import { api } from "@/lib/api/client";
import { deballer, enApiError } from "@/lib/api/errors";
import { urlPhoto } from "@/lib/patrimoine";

/** Galerie d'un immeuble ou d'un lot. Les images passent par l'API (contrôle d'accès), jamais par S3. */
export function PhotoGallery({ cible, id }: { cible: "immeubles" | "lots"; id: string }) {
  const t = useTranslations("patrimoine");
  const message = useErrorMessage();
  const queryClient = useQueryClient();
  const { data: photos, error, refetch } = usePhotos(cible, id);
  const champFichier = useRef<HTMLInputElement>(null);
  const [choisi, setChoisi] = useState<File | null>(null);
  const [legende, setLegende] = useState("");
  const [erreur, setErreur] = useState<string | null>(null);

  const envoyer = useMutation({
    mutationFn: async (file: File) => {
      const donnees = new FormData();
      donnees.append("fichier", file);
      if (legende.trim() !== "") donnees.append("legende", legende.trim());
      let reponse: Response;
      try {
        reponse = await fetch(`/api/proxy/api/v1/${cible}/${id}/photos`, { method: "POST", body: donnees });
      } catch {
        throw enApiError(null, 0);
      }
      if (!reponse.ok) throw enApiError(await reponse.json().catch(() => null), reponse.status);
    },
    onSuccess: async () => {
      setLegende("");
      setErreur(null);
      if (champFichier.current) champFichier.current.value = "";
      setChoisi(null);
      toast.success(t("photoAjoutee"));
      await queryClient.invalidateQueries({ queryKey: ["photos", cible, id] });
    },
    onError: (e) => setErreur(message(e)),
  });

  const supprimer = useMutation({
    mutationFn: async (photoId: string) => deballer(await api.DELETE("/api/v1/photos/{id}", { params: { path: { id: photoId } } })),
    onSuccess: async () => {
      toast.success(t("photoSupprimee"));
      await queryClient.invalidateQueries({ queryKey: ["photos", cible, id] });
    },
    onError: (e) => setErreur(message(e)),
  });

  if (error) return <ErrorDisplay erreur={error} onRetry={() => void refetch()} />;

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("photos")}</CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col gap-4">
        {photos && photos.length === 0 ? <p className="text-sm text-muted-foreground">{t("aucunePhoto")}</p> : null}
        <ul className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-4">
          {(photos ?? []).map((p) => (
            <li key={p.id} className="flex flex-col gap-1">
              {/* eslint-disable-next-line @next/next/no-img-element -- l'image vient de l'API authentifiée, pas d'optimisation Next */}
              <img src={urlPhoto(p.fichierId ?? "")} alt={p.legende ?? t("photos")} loading="lazy" className="aspect-[4/3] w-full rounded-md border object-cover" />
              {p.legende ? <span className="text-xs text-muted-foreground">{p.legende}</span> : null}
              <Button type="button" variant="outline" size="sm" onClick={() => supprimer.mutate(p.id ?? "")} disabled={supprimer.isPending}>
                {t("supprimerPhoto")}
              </Button>
            </li>
          ))}
        </ul>
        <form
          className="flex flex-col gap-2 sm:flex-row sm:items-end"
          onSubmit={(e) => {
            e.preventDefault();
            if (choisi) envoyer.mutate(choisi);
          }}
        >
          <input
            ref={champFichier}
            type="file"
            accept="image/jpeg,image/png,image/webp"
            aria-label={t("choisirPhoto")}
            onChange={(e) => setChoisi(e.target.files?.[0] ?? null)}
            className="h-8 w-full min-w-0 rounded-lg border border-input bg-transparent px-2.5 py-1 text-sm file:mr-2 file:border-0 file:bg-transparent file:text-sm file:font-medium"
          />
          <Input value={legende} onChange={(e) => setLegende(e.target.value)} placeholder={t("legende")} aria-label={t("legende")} maxLength={200} />
          <Button type="submit" disabled={envoyer.isPending || !choisi}>
            {envoyer.isPending ? t("envoi") : t("ajouterPhoto")}
          </Button>
        </form>
        {erreur ? (
          <p role="alert" className="text-sm text-destructive">
            {erreur}
          </p>
        ) : null}
      </CardContent>
    </Card>
  );
}
