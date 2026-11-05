"use client";

import Link from "next/link";
import { useParams } from "next/navigation";

import { BadgeLicence, MessageErreur, classeBouton } from "@/components/ui";
import { appel, lister } from "@/lib/api";
import type { Client, Licence, Page } from "@/lib/licences";
import { useCharge } from "@/lib/use-charge";

export default function FicheClient() {
  const { id } = useParams<{ id: string }>();
  const client = useCharge(() => appel<Client>("GET", `/clients/${id}`), [id]);
  const licences = useCharge(() => lister<Page<Licence>>("/licences", { clientId: id, size: 100 }), [id]);
  return (
    <>
      <MessageErreur message={client.erreur ?? licences.erreur} />
      {client.donnees ? (
        <>
          <h1 className="text-2xl font-semibold">{client.donnees.nom}</h1>
          <p className="text-sm">
            {[client.donnees.email, client.donnees.telephone, client.donnees.pays].filter(Boolean).join(" · ")}
          </p>
        </>
      ) : null}
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Licences</h2>
        <Link className={classeBouton} href={`/licences/nouvelle?clientId=${id}`}>
          Émettre une licence
        </Link>
      </div>
      {licences.donnees?.content.length === 0 ? <p>Aucune licence pour ce client.</p> : null}
      <ul className="flex flex-col gap-2">
        {licences.donnees?.content.map((l) => (
          <li key={l.id} className="flex items-center gap-3 rounded-lg border p-3 text-sm">
            <Link className="text-blue-700 underline" href={`/licences/${l.id}`}>
              {l.palierLots} lots — jusqu&apos;au {l.dateExpiration}
            </Link>
            <BadgeLicence licence={l} />
          </li>
        ))}
      </ul>
    </>
  );
}
