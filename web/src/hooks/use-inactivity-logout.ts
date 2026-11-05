"use client";

import { useEffect, useRef } from "react";

const EVENEMENTS = ["mousemove", "mousedown", "keydown", "scroll", "touchstart"] as const;

/** Appelle `onTimeout` après `delaiMs` sans activité (30 min d'inactivité, CLAUDE.md §6). */
export function useInactivityLogout(delaiMs: number, onTimeout: () => void) {
  const rappel = useRef(onTimeout);
  useEffect(() => {
    rappel.current = onTimeout;
  }, [onTimeout]);

  useEffect(() => {
    let minuteur = setTimeout(() => rappel.current(), delaiMs);
    let dernier = Date.now();
    const activite = () => {
      const maintenant = Date.now();
      if (maintenant - dernier < 1000) return; // évite de recréer le minuteur à chaque mouvement
      dernier = maintenant;
      clearTimeout(minuteur);
      minuteur = setTimeout(() => rappel.current(), delaiMs);
    };
    EVENEMENTS.forEach((e) => window.addEventListener(e, activite, { passive: true }));
    return () => {
      clearTimeout(minuteur);
      EVENEMENTS.forEach((e) => window.removeEventListener(e, activite));
    };
  }, [delaiMs]);
}
