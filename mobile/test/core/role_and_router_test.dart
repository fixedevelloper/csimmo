import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/core/auth/role.dart';
import 'package:immogest_mobile/router.dart';

void main() {
  test('chaque rôle a son accueil (agent = gestionnaire et admin)', () {
    expect(Role.locataire.homeRoute, '/locataire');
    expect(Role.gestionnaire.homeRoute, '/agent');
    expect(Role.admin.homeRoute, '/agent');
    expect(Role.encaisseur.homeRoute, '/agent');
    expect(Role.gestionnaireBiens.homeRoute, '/agent');
    expect(Role.prestataire.homeRoute, '/prestataire');
    expect(Role.proprietaireTiers.homeRoute, '/proprietaire');
  });

  test('Role.fromCode lit les codes du backend', () {
    expect(Role.fromCode('PROPRIETAIRE_TIERS'), Role.proprietaireTiers);
    expect(Role.fromCode('ENCAISSEUR'), Role.encaisseur);
    expect(Role.fromCode('GESTIONNAIRE_BIENS'), Role.gestionnaireBiens);
    expect(Role.fromCode('INCONNU'), isNull);
    expect(Role.fromCode(null), isNull);
  });

  group('redirection', () {
    test('sans serveur : configuration (et le scan QR reste accessible)', () {
      expect(redirection(serveur: null, role: null, chemin: '/connexion'), '/serveur');
      expect(redirection(serveur: null, role: null, chemin: '/agent'), '/serveur');
      expect(redirection(serveur: null, role: null, chemin: '/serveur'), isNull);
      expect(redirection(serveur: null, role: null, chemin: '/serveur/scan'), isNull);
    });

    test('serveur configuré mais déconnecté : connexion (changer de serveur reste possible)', () {
      expect(redirection(serveur: 'https://x', role: null, chemin: '/'), '/connexion');
      expect(redirection(serveur: 'https://x', role: null, chemin: '/locataire'), '/connexion');
      expect(redirection(serveur: 'https://x', role: null, chemin: '/connexion'), isNull);
      expect(redirection(serveur: 'https://x', role: null, chemin: '/serveur'), isNull);
    });

    test('connecté : uniquement l\'accueil de son rôle', () {
      expect(redirection(serveur: 'https://x', role: Role.locataire, chemin: '/locataire'), isNull);
      expect(redirection(serveur: 'https://x', role: Role.locataire, chemin: '/agent'), '/locataire');
      expect(redirection(serveur: 'https://x', role: Role.locataire, chemin: '/connexion'), '/locataire');
      expect(redirection(serveur: 'https://x', role: Role.prestataire, chemin: '/serveur'), '/prestataire');
      expect(redirection(serveur: 'https://x', role: Role.admin, chemin: '/'), '/agent');
    });
  });
}
