"use client";

import { useCallback, useEffect, useState } from "react";

import { ErreurApi } from "./api";
import { messageErreur } from "./messages";

/** Charge des données au montage (et à chaque changement de dépendances) ; `recharger` relance l'appel. */
export function useCharge<T>(fonction: () => Promise<T>, deps: unknown[]) {
  const [donnees, setDonnees] = useState<T | null>(null);
  const [erreur, setErreur] = useState<string | null>(null);
  // eslint-disable-next-line react-hooks/exhaustive-deps, react-hooks/use-memo
  const charger = useCallback(fonction, deps);
  const recharger = useCallback(() => {
    setErreur(null);
    charger().then(setDonnees, (e) => setErreur(messageErreur(e instanceof ErreurApi ? e.code : undefined)));
  }, [charger]);
  useEffect(() => {
    // chargement au montage et à chaque changement de dépendances
    // eslint-disable-next-line react-hooks/set-state-in-effect
    recharger();
  }, [recharger]);
  return { donnees, erreur, recharger, chargement: donnees === null && erreur === null };
}

export function messageDe(e: unknown): string {
  return messageErreur(e instanceof ErreurApi ? e.code : undefined);
}
