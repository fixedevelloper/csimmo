"use client";

import Link from "next/link";
import { useState } from "react";

import { Champ, MessageErreur, Pagination, classeBouton, classeChamp } from "@/components/ui";
import { appel, lister } from "@/lib/api";
import type { Client, Page } from "@/lib/licences";
import { messageDe, useCharge } from "@/lib/use-charge";

export default function Clients() {
  const [q, setQ] = useState("");
  const [page, setPage] = useState(0);
  const { donnees, erreur, recharger } = useCharge(() => lister<Page<Client>>("/clients", { q, page, size: 20 }), [q, page]);
  const [erreurForm, setErreurForm] = useState<string | null>(null);

  async function creer(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const form = e.currentTarget;
    const f = new FormData(form);
    setErreurForm(null);
    try {
      await appel("POST", "/clients", { nom: f.get("nom"), email: f.get("email") || null, telephone: f.get("telephone") || null, pays: f.get("pays") || null, notes: null });
      form.reset();
      recharger();
    } catch (err) {
      setErreurForm(messageDe(err));
    }
  }

  return (
    <>
      <h1 className="text-2xl font-semibold">Clients</h1>
      <form onSubmit={(e) => void creer(e)} className="grid gap-3 rounded-lg border p-4 sm:grid-cols-5" aria-label="Nouveau client">
        <Champ id="nom" label="Nom *">
          <input id="nom" name="nom" required maxLength={200} className={classeChamp} />
        </Champ>
        <Champ id="email" label="E-mail">
          <input id="email" name="email" type="email" className={classeChamp} />
        </Champ>
        <Champ id="telephone" label="Téléphone">
          <input id="telephone" name="telephone" className={classeChamp} />
        </Champ>
        <Champ id="pays" label="Pays">
          <input id="pays" name="pays" className={classeChamp} />
        </Champ>
        <div className="flex items-end">
          <button type="submit" className={classeBouton}>
            Ajouter le client
          </button>
        </div>
        <div className="sm:col-span-5">
          <MessageErreur message={erreurForm} />
        </div>
      </form>
      <Champ id="recherche" label="Rechercher un client">
        <input id="recherche" value={q} onChange={(e) => { setPage(0); setQ(e.target.value); }} className={`${classeChamp} max-w-sm`} />
      </Champ>
      <MessageErreur message={erreur} />
      {donnees ? (
        <>
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b">
                <th className="py-2">Nom</th>
                <th>E-mail</th>
                <th>Téléphone</th>
                <th>Pays</th>
              </tr>
            </thead>
            <tbody>
              {donnees.content.map((c) => (
                <tr key={c.id} className="border-b">
                  <td className="py-2">
                    <Link className="text-blue-700 underline" href={`/clients/${c.id}`}>
                      {c.nom}
                    </Link>
                  </td>
                  <td>{c.email}</td>
                  <td>{c.telephone}</td>
                  <td>{c.pays}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {donnees.content.length === 0 ? <p>Aucun client.</p> : null}
          <Pagination page={page} taille={20} total={donnees.totalElements} onChange={setPage} />
        </>
      ) : null}
    </>
  );
}
