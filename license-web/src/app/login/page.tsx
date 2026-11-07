"use client";

import Image from "next/image";
import { useRouter } from "next/navigation";
import { useState } from "react";

import { ChampMotDePasse } from "@/components/champ-mot-de-passe";
import { Champ, MessageErreur, classeBouton, classeChamp } from "@/components/ui";
import { messageErreur } from "@/lib/messages";

export default function Connexion() {
  const routeur = useRouter();
  const [erreur, setErreur] = useState<string | null>(null);
  const [envoi, setEnvoi] = useState(false);

  async function soumettre(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const f = new FormData(e.currentTarget);
    setEnvoi(true);
    setErreur(null);
    try {
      const r = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: f.get("email"), motDePasse: f.get("motDePasse") }),
      });
      if (!r.ok) {
        const c = (await r.json().catch(() => null)) as { code?: string } | null;
        setErreur(messageErreur(c?.code));
        return;
      }
      routeur.replace("/");
    } finally {
      setEnvoi(false);
    }
  }

  return (
    <main className="mx-auto flex min-h-screen max-w-sm flex-col justify-center gap-6 p-6">
      <Image src="/brand/icone.png" alt="" width={72} height={72} priority className="size-18 rounded-2xl" />
      <h1 className="text-2xl font-semibold">Portail éditeur CSImmo</h1>
      <form onSubmit={(e) => void soumettre(e)} className="flex flex-col gap-4">
        <Champ id="email" label="Adresse e-mail">
          <input id="email" name="email" type="email" required autoComplete="username" className={classeChamp} />
        </Champ>
        <Champ id="motDePasse" label="Mot de passe">
          <ChampMotDePasse id="motDePasse" name="motDePasse" autoComplete="current-password" />
        </Champ>
        <MessageErreur message={erreur} />
        <button type="submit" className={classeBouton} disabled={envoi}>
          Se connecter
        </button>
      </form>
    </main>
  );
}
