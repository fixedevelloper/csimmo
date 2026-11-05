/** Textes affichés (le portail est interne à l'éditeur : français seulement). */
export const ERREURS: Record<string, string> = {
  IDENTIFIANTS_INVALIDES: "Identifiants invalides.",
  COMPTE_VERROUILLE: "Compte verrouillé pendant 15 minutes après trop d'échecs.",
  CLIENT_INTROUVABLE: "Client introuvable.",
  LICENCE_INTROUVABLE: "Licence introuvable.",
  LICENCE_INVALIDE: "Les données de la licence sont invalides (dates, palier…).",
  LICENCE_REVOQUEE: "Cette licence est révoquée : aucune modification n'est possible.",
  RENOUVELLEMENT_DATE_INVALIDE: "La nouvelle expiration doit être postérieure à l'expiration actuelle.",
  TRANSFERT_MEME_EMPREINTE: "La nouvelle empreinte est identique à l'actuelle.",
  TRANSFERT_LIMITE_ATTEINTE: "Nombre maximal de transferts atteint pour cette licence.",
  VERSION_EXISTE: "Cette version est déjà publiée.",
  VALIDATION: "Certains champs sont invalides.",
  DOUBLON: "Cet enregistrement existe déjà.",
  NON_AUTHENTIFIE: "Session expirée : reconnectez-vous.",
  SERVEUR_INDISPONIBLE: "Le serveur de licences est injoignable.",
};

export function messageErreur(code: string | undefined): string {
  return (code && ERREURS[code]) || "Une erreur est survenue.";
}
