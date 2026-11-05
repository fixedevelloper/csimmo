"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { Suspense } from "react";

import { FormulaireLicence } from "@/components/formulaire-licence";
import { MessageErreur } from "@/components/ui";
import { appel, lister } from "@/lib/api";
import { telechargerLic, type Client, type Emission, type Page } from "@/lib/licences";
import { messageDe, useCharge } from "@/lib/use-charge";

function Contenu() {
  const routeur = useRouter();
  const clientId = useSearchParams().get("clientId") ?? undefined;
  const { donnees, erreur } = useCharge(() => lister<Page<Client>>("/clients", { size: 100 }), []);
  return (
    <>
      <h1 className="text-2xl font-semibold">Émettre une licence</h1>
      <MessageErreur message={erreur} />
      {donnees ? (
        <FormulaireLicence
          clients={donnees.content}
          clientId={clientId}
          onSoumettre={async (s) => {
            try {
              const e = await appel<Emission>("POST", "/licences", s);
              telechargerLic(e.licence.id, e.fichier);
              routeur.push(`/licences/${e.licence.id}`);
            } catch (err) {
              throw new Error(messageDe(err));
            }
          }}
        />
      ) : null}
    </>
  );
}

export default function Nouvelle() {
  return (
    <Suspense>
      <Contenu />
    </Suspense>
  );
}
