"use client";

import Image from "next/image";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import type { ReactNode } from "react";

import { classeBoutonSecondaire } from "./ui";

const LIENS = [
  { href: "/", libelle: "Tableau de bord" },
  { href: "/clients", libelle: "Clients" },
  { href: "/licences", libelle: "Licences" },
  { href: "/versions", libelle: "Versions" },
];

export function Coque({ children }: { children: ReactNode }) {
  const chemin = usePathname();
  const routeur = useRouter();
  async function deconnecter() {
    await fetch("/api/auth/logout", { method: "POST" });
    routeur.replace("/login");
  }
  return (
    <div className="min-h-screen">
      <header className="flex flex-wrap items-center gap-4 border-b border-neutral-200 px-6 py-3 dark:border-neutral-800">
        <span className="flex items-center gap-2">
          <Image src="/brand/icone.png" alt="" width={32} height={32} priority className="size-8 rounded-lg" />
          <strong>CSImmo — Éditeur</strong>
        </span>
        <nav aria-label="Navigation principale" className="flex gap-1">
          {LIENS.map((l) => {
            const actif = l.href === "/" ? chemin === "/" : chemin.startsWith(l.href);
            return (
              <Link key={l.href} href={l.href} aria-current={actif ? "page" : undefined} className={`rounded-md px-3 py-1.5 text-sm ${actif ? "bg-neutral-200 font-medium dark:bg-neutral-800" : "hover:bg-neutral-100 dark:hover:bg-neutral-900"}`}>
                {l.libelle}
              </Link>
            );
          })}
        </nav>
        <button className={`${classeBoutonSecondaire} ml-auto`} onClick={() => void deconnecter()}>
          Se déconnecter
        </button>
      </header>
      <main className="mx-auto flex max-w-6xl flex-col gap-6 p-6">{children}</main>
    </div>
  );
}
