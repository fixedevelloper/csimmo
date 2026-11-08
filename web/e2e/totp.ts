import { createHmac } from "node:crypto";

const ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

function decoderBase32(texte: string): Buffer {
  const propre = texte.replace(/\s+/g, "").toUpperCase();
  let bits = "";
  for (const c of propre) bits += ALPHABET.indexOf(c).toString(2).padStart(5, "0");
  const octets: number[] = [];
  for (let i = 0; i + 8 <= bits.length; i += 8) octets.push(parseInt(bits.slice(i, i + 8), 2));
  return Buffer.from(octets);
}

/** Code TOTP (RFC 6238, SHA-1, 30 s, 6 chiffres) d'une clé base 32 ; `decalage` = nombre de pas de 30 s (le serveur refuse de rejouer un pas déjà utilisé). */
export function codeTotp(cleBase32: string, decalage = 0): string {
  const pas = Math.floor(Date.now() / 30000) + decalage;
  const compteur = Buffer.alloc(8);
  compteur.writeBigUInt64BE(BigInt(pas));
  const h = createHmac("sha1", decoderBase32(cleBase32)).update(compteur).digest();
  const o = h[h.length - 1] & 0x0f;
  const n = ((h[o] & 0x7f) << 24) | (h[o + 1] << 16) | (h[o + 2] << 8) | h[o + 3];
  return (n % 1_000_000).toString().padStart(6, "0");
}
