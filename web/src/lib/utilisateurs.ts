export const ROLES = ["ADMIN", "GESTIONNAIRE", "PROPRIETAIRE_TIERS", "LOCATAIRE", "PRESTATAIRE"] as const;
export type RoleUtilisateur = (typeof ROLES)[number];

/** Rôles qui doivent toujours se connecter par mot de passe (le serveur leur interdit l'OTP seul). */
export const ROLES_MOT_DE_PASSE_OBLIGATOIRE: readonly RoleUtilisateur[] = ["ADMIN", "GESTIONNAIRE"];

const MINUSCULES = "abcdefghijkmnpqrstuvwxyz"; // sans l, o (confusion avec 1, 0)
const MAJUSCULES = "ABCDEFGHJKLMNPQRSTUVWXYZ";
const CHIFFRES = "23456789";
const SPECIAUX = "!#$%*+-=?@";

function tirage(max: number): number {
  // tirage sans biais de modulo (rejet des valeurs au-delà du plus grand multiple de max)
  const limite = Math.floor(0x1_0000_0000 / max) * max;
  const buf = new Uint32Array(1);
  do crypto.getRandomValues(buf);
  while (buf[0] >= limite);
  return buf[0] % max;
}

const piocher = (alphabet: string) => alphabet[tirage(alphabet.length)];

/** Mot de passe aléatoire (crypto), avec au moins une minuscule, une majuscule, un chiffre et un caractère spécial ; sans caractères ambigus. */
export function genererMotDePasse(longueur = 12): string {
  const n = Math.max(longueur, 8);
  const tous = MINUSCULES + MAJUSCULES + CHIFFRES + SPECIAUX;
  const caracteres = [piocher(MINUSCULES), piocher(MAJUSCULES), piocher(CHIFFRES), piocher(SPECIAUX)];
  while (caracteres.length < n) caracteres.push(piocher(tous));
  for (let i = caracteres.length - 1; i > 0; i--) {
    const j = tirage(i + 1);
    [caracteres[i], caracteres[j]] = [caracteres[j], caracteres[i]];
  }
  return caracteres.join("");
}
