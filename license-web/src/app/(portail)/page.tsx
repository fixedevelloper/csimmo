"use client";

import Link from "next/link";

import { BadgeLicence, MessageErreur } from "@/components/ui";
import { lister } from "@/lib/api";
import type { Licence, Page } from "@/lib/licences";
import { useCharge } from "@/lib/use-charge";

export default function TableauDeBord() {
  const { donnees, erreur, chargement } = useCharge(() => lister<Page<Licence>>("/licences", { expirantSous: 30, size: 50 }), []);
  return (
    <>
      <h1 className="text-2xl font-semibold">Tableau de bord</h1>
      <section aria-labelledby="alertes" className="flex flex-col gap-3">
        <h2 id="alertes" className="text-lg font-semibold">
          Licences à renouveler (expirées ou expirant sous 30 jours)
        </h2>
        <MessageErreur message={erreur} />
        {chargement ? <p>Chargement…</p> : null}
        {donnees && donnees.content.length === 0 ? <p>Aucune licence à renouveler.</p> : null}
        {donnees && donnees.content.length > 0 ? (
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b">
                <th className="py-2">Client</th>
                <th>Expiration</th>
                <th>Jours restants</th>
                <th>État</th>
              </tr>
            </thead>
            <tbody>
              {donnees.content.map((l) => (
                <tr key={l.id} className="border-b">
                  <td className="py-2">
                    <Link className="text-blue-700 underline" href={`/licences/${l.id}`}>
                      {l.clientNom}
                    </Link>
                  </td>
                  <td>{l.dateExpiration}</td>
                  <td>{l.joursRestants}</td>
                  <td>
                    <BadgeLicence licence={l} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : null}
        {donnees && donnees.totalElements > donnees.content.length ? <p className="text-sm">Et {donnees.totalElements - donnees.content.length} autre(s) : voir la liste des licences.</p> : null}
      </section>
    </>
  );
}
