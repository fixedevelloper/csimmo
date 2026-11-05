import Image from "next/image";

/** Logo complet (pages sans menu) : posé sur fond blanc, lisible aussi en thème sombre. */
export function LogoComplet({ alt, className }: { alt: string; className?: string }) {
  return (
    <span className={`inline-block rounded-xl bg-white p-3 ${className ?? ""}`}>
      <Image src="/brand/logo.png" alt={alt} width={720} height={539} priority className="h-auto w-56" />
    </span>
  );
}

/** Icône carrée + nom, pour l'en-tête du menu. */
export function LogoCompact({ nom }: { nom: string }) {
  return (
    <span className="flex items-center gap-2">
      <Image src="/brand/icone.png" alt="" width={32} height={32} priority className="size-8 rounded-lg" />
      <span className="text-lg font-semibold">{nom}</span>
    </span>
  );
}
