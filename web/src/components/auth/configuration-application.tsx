"use client";

import { Copy } from "lucide-react";
import QRCode from "qrcode";
import { useTranslations } from "next-intl";
import { useEffect, useState } from "react";

import { Button } from "@/components/ui/button";

type Props = {
  /** Clé en base 32 (à saisir à la main dans l'application d'authentification). */
  secret: string;
  /** Adresse `otpauth://` (code QR). */
  uri: string;
};

/**
 * Enregistrement d'un compte dans une application d'authentification (Google Authenticator, Microsoft Authenticator, Authy, FreeOTP…) :
 * code QR à scanner, ou clé à saisir. Le code à 6 chiffres qui confirme est demandé par le formulaire qui l'inclut.
 */
export function ConfigurationApplication({ secret, uri }: Props) {
  const t = useTranslations("configurationApplication");
  const [qr, setQr] = useState<string | null>(null);
  const [copie, setCopie] = useState(false);

  useEffect(() => {
    let annule = false;
    QRCode.toDataURL(uri, { margin: 1, width: 192, errorCorrectionLevel: "M" })
      .then((url) => {
        if (!annule) setQr(url);
      })
      .catch(() => {
        if (!annule) setQr(null);
      });
    return () => {
      annule = true;
    };
  }, [uri]);

  async function copier() {
    try {
      await navigator.clipboard.writeText(secret);
      setCopie(true);
    } catch {
      setCopie(false);
    }
  }

  // clé affichée par groupes de 4 : plus facile à recopier
  const groupes = secret.match(/.{1,4}/g)?.join(" ") ?? secret;

  return (
    <div className="flex flex-col gap-4 rounded-xl border border-primary/15 bg-primary/5 p-4">
      <ol className="list-decimal space-y-1 pl-5 text-sm">
        <li>{t("etape1")}</li>
        <li>{t("etape2")}</li>
        <li>{t("etape3")}</li>
      </ol>
      <div className="flex flex-col items-center gap-3 sm:flex-row sm:items-start">
        {qr ? (
          // eslint-disable-next-line @next/next/no-img-element -- code QR généré localement (data:), rien à optimiser
          <img src={qr} alt={t("qrAlt")} width={192} height={192} className="rounded-lg border bg-white p-1" />
        ) : (
          <div className="size-48 rounded-lg border bg-muted/40" aria-hidden />
        )}
        <div className="flex min-w-0 flex-col gap-2">
          <p className="text-xs text-muted-foreground">{t("cleManuelle")}</p>
          <code data-testid="cle-application" className="break-all rounded-md bg-background px-2 py-1.5 font-mono text-sm tracking-wider">
            {groupes}
          </code>
          <div>
            <Button type="button" size="sm" variant="outline" onClick={() => void copier()}>
              <Copy className="size-4" aria-hidden />
              {copie ? t("copiee") : t("copier")}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
