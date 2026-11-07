"use client";

import type { ReactNode } from "react";

import { useMe } from "@/hooks/use-me";
import { type Action, peut } from "@/lib/droits";

/** N'affiche son contenu que si le rôle de l'utilisateur permet l'action (rien tant que le profil n'est pas chargé). */
export function SiPeut({ action, children }: { action: Action; children: ReactNode }) {
  const { data: moi } = useMe();
  return peut(moi?.role, action) ? <>{children}</> : null;
}
