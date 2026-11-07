/// Rôles de l'application (identiques à ceux du backend).
enum Role {
  admin('ADMIN'),
  gestionnaire('GESTIONNAIRE'),
  proprietaireTiers('PROPRIETAIRE_TIERS'),
  locataire('LOCATAIRE'),
  prestataire('PRESTATAIRE'),
  encaisseur('ENCAISSEUR');

  const Role(this.code);

  final String code;

  static Role? fromCode(String? code) {
    for (final role in values) {
      if (role.code == code) return role;
    }
    return null;
  }

  /// Écran d'accueil du rôle : locataire, agent (ADMIN, GESTIONNAIRE et ENCAISSEUR), prestataire, propriétaire.
  String get homeRoute => switch (this) {
        locataire => '/locataire',
        gestionnaire || admin || encaisseur => '/agent',
        prestataire => '/prestataire',
        proprietaireTiers => '/proprietaire',
      };
}
