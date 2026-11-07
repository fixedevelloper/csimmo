/// Rôles de l'application (identiques à ceux du backend).
enum Role {
  admin('ADMIN'),
  gestionnaire('GESTIONNAIRE'),
  proprietaireTiers('PROPRIETAIRE_TIERS'),
  locataire('LOCATAIRE'),
  prestataire('PRESTATAIRE'),
  encaisseur('ENCAISSEUR'),
  gestionnaireBiens('GESTIONNAIRE_BIENS'),
  comptable('COMPTABLE');

  const Role(this.code);

  final String code;

  static Role? fromCode(String? code) {
    for (final role in values) {
      if (role.code == code) return role;
    }
    return null;
  }

  /// Écran d'accueil du rôle : locataire, agent (ADMIN, GESTIONNAIRE, ENCAISSEUR et GESTIONNAIRE_BIENS), prestataire, propriétaire, comptable (accueil minimal : le travail du comptable se fait sur le web).
  String get homeRoute => switch (this) {
        locataire => '/locataire',
        gestionnaire || admin || encaisseur || gestionnaireBiens => '/agent',
        prestataire => '/prestataire',
        proprietaireTiers => '/proprietaire',
        comptable => '/comptable',
      };
}
