import { describe, expect, it } from "vitest";

import en from "../../messages/en.json";
import fr from "../../messages/fr.json";

function cles(objet: unknown, prefixe = ""): string[] {
  if (typeof objet !== "object" || objet === null) return [prefixe];
  return Object.entries(objet).flatMap(([k, v]) => cles(v, prefixe ? `${prefixe}.${k}` : k));
}

describe("traductions", () => {
  it("fr et en ont exactement les mêmes clés", () => {
    expect(cles(en).sort()).toEqual(cles(fr).sort());
  });

  it("aucun message n'est vide", () => {
    for (const messages of [fr, en]) {
      const valeurs = JSON.stringify(messages).match(/":\s*""/g);
      expect(valeurs).toBeNull();
    }
  });

  it("traduit tous les codes d'erreur émis par le backend d'authentification et d'utilisateurs", () => {
    const codes = [
      "IDENTIFIANTS_INVALIDES", "COMPTE_VERROUILLE", "COMPTE_DESACTIVE", "OTP_INVALIDE", "REFRESH_TOKEN_INVALIDE",
      "REFRESH_TOKEN_EXPIRE", "REFRESH_TOKEN_REUTILISE", "MOT_DE_PASSE_ACTUEL_INCORRECT", "UTILISATEUR_INTROUVABLE",
      "UTILISATEUR_DEJA_EXISTANT", "UTILISATEUR_AUTO_DESACTIVATION", "NON_AUTHENTIFIE", "TOKEN_EXPIRE", "ACCES_REFUSE",
      "VALIDATION_ERREUR", "ERREUR_INTERNE", "FICHIER_ACCES_REFUSE", "FICHIER_TYPE_INTERDIT", "LICENCE_EXPIREE",
      "LICENCE_PALIER_ATTEINT", "SERVEUR_INDISPONIBLE", "LICENCE_FORMAT_INVALIDE", "LICENCE_SIGNATURE_INVALIDE",
      "LICENCE_MAUVAISE_EMPREINTE", "LICENCE_PAS_ENCORE_VALIDE", "LICENCE_FICHIER_EXPIRE", "LICENCE_PLUS_ANCIENNE",
      "LICENCE_CLE_EDITEUR_ABSENTE", "INSTALLATION_DEJA_FAITE",
    ];
    for (const code of codes) {
      expect(fr.errors, code).toHaveProperty(code);
      expect(en.errors, code).toHaveProperty(code);
    }
  });
});
