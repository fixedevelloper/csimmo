"use client";

import Link from "next/link";
import { useState } from "react";

import { BadgeLicence, Champ, MessageErreur, Pagination, classeBouton, classeChamp } from "@/components/ui";
import { lister } from "@/lib/api";
import type { Licence, Page } from "@/lib/licences";
import { useCharge } from "@/lib/use-charge";

export default function Licences() {
  const [statut, setStatut] = useState("");
  const [bientot, setBientot] = useState(false);
  const [page, setPage] = useState(0);
  const { donnees, erreur } = useCharge(() => lister<Page<Licence>>("/licences", { statut, expirantSous: bientot ? 30 : undefined, page, size: 20 }), [statut, bientot, page]);
  return (
    <>
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Licences</h1>
        <Link className={classeBouton} href="/licences/nouvelle">
          Émettre une licence
        </Link>
      </div>
      <div className="flex flex-wrap items-end gap-4">
        <Champ id="statut" label="Statut">
          <select id="statut" value={statut} onChange={(e) => { setPage(0); setStatut(e.target.value); }} className={classeChamp}>
            <option value="">Tous</option>
            <option value="ACTIVE">Active</option>
            <option value="REVOQUEE">Révoquée</option>
          </select>
        </Champ>
        <label className="flex items-center gap-2 pb-2 text-sm">
          <input type="checkbox" checked={bientot} onChange={(e) => { setPage(0); setBientot(e.target.checked); }} />
          Expirées ou expirant sous 30 jours
        </label>
      </div>
      <MessageErreur message={erreur} />
      {donnees ? (
        <>
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b">
                <th className="py-2">Client</th>
                <th>Palier</th>
                <th>Expiration</th>
                <th>Dernière vérification</th>
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
                  <td>{l.palierLots} lots</td>
                  <td>{l.dateExpiration}</td>
                  <td>{l.derniereVerification ? new Date(l.derniereVerification).toLocaleString("fr-FR") : "Jamais"}</td>
                  <td>
                    <BadgeLicence licence={l} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {donnees.content.length === 0 ? <p>Aucune licence.</p> : null}
          <Pagination page={page} taille={20} total={donnees.totalElements} onChange={setPage} />
        </>
      ) : null}
    </>
  );
}
