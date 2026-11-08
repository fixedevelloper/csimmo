import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/features/auth/auth_actions.dart';
import 'package:immogest_mobile/features/profil/profil_screen.dart';

import '../support/fake_adapter.dart';
import '../support/locataire_support.dart';

class _FausseSession extends AuthActions {
  _FausseSession(super.ref);

  bool deconnecte = false;

  @override
  Future<void> logout() async => deconnecte = true;
}

Map<String, Object?> _moi(String role, {bool deuxFacteurs = false, bool configure = true, String? telephone = '+237690000001'}) => {
      'id': 'u-1',
      'nom': 'Boris Chef',
      'telephone': telephone,
      'email': 'boris@x.cm',
      'role': role,
      'actif': true,
      'deuxFacteursActif': deuxFacteurs,
      'deuxFacteursConfigure': deuxFacteurs && configure,
    };

/// Serveur scripté : mémorise les appels d'écriture (méthode, chemin, corps).
class _Serveur {
  _Serveur(this.utilisateur, {this.echecs = const {}});

  final Map<String, Object?> utilisateur;
  final Map<String, Reponse> echecs; // « METHODE chemin » → réponse forcée
  final List<(String, String, Object?)> ecritures = [];

  Reponse traiter(RequestOptions o) {
    if (o.method == 'GET' && o.path == '/api/v1/auth/moi') return (status: 200, corps: utilisateur);
    ecritures.add((o.method, o.path, o.data is String ? jsonDecode(o.data as String) : o.data));
    final forcee = echecs['${o.method} ${o.path}'];
    if (forcee != null) return forcee;
    if (o.path.endsWith('/demarrer')) return (status: 200, corps: {'secret': 'ABCDEFGHIJKLMNOPABCDEFGHIJKLMNOP', 'uri': 'otpauth://totp/x'});
    if (o.path == '/api/v1/auth/mot-de-passe') return (status: 204, corps: null);
    if (o.path.endsWith('/activer')) return (status: 200, corps: {...utilisateur, 'deuxFacteursActif': true});
    if (o.path.endsWith('/desactiver')) return (status: 200, corps: {...utilisateur, 'deuxFacteursActif': false});
    return (status: 200, corps: utilisateur); // PUT /auth/moi
  }
}

void main() {
  late _FausseSession session;

  Future<_Serveur> ouvrir(WidgetTester tester, Map<String, Object?> utilisateur, {Map<String, Reponse> echecs = const {}}) async {
    tester.view.physicalSize = const Size(900, 3000);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.reset);
    final serveur = _Serveur(utilisateur, echecs: echecs);
    final (api, _) = apiFactice(serveur.traiter);
    await tester.pumpWidget(appDeTest(const ProfilScreen(), api: api, extra: [
      authActionsProvider.overrideWith((ref) => session = _FausseSession(ref)),
    ]));
    await tester.pumpAndSettle();
    return serveur;
  }

  Future<void> taper(WidgetTester tester, String cle, String texte) async {
    await tester.ensureVisible(find.byKey(Key(cle)));
    await tester.enterText(find.byKey(Key(cle)), texte);
  }

  Future<void> appuyer(WidgetTester tester, String cle) async {
    await tester.ensureVisible(find.byKey(Key(cle)));
    await tester.tap(find.byKey(Key(cle)));
    await tester.pumpAndSettle();
  }

  group('informations personnelles', () {
    testWidgets('préremplit le profil et exige le mot de passe actuel', (tester) async {
      final serveur = await ouvrir(tester, _moi('GESTIONNAIRE'));
      expect(find.widgetWithText(TextField, 'Boris Chef'), findsOneWidget);
      expect(find.widgetWithText(TextField, '+237690000001'), findsOneWidget);
      await appuyer(tester, 'profil-enregistrer');
      expect(find.text('Saisissez votre mot de passe actuel.'), findsOneWidget);
      expect(serveur.ecritures, isEmpty);
    });

    testWidgets('refuse un profil sans contact', (tester) async {
      final serveur = await ouvrir(tester, _moi('GESTIONNAIRE'));
      await taper(tester, 'profil-telephone', '');
      await taper(tester, 'profil-email', '');
      await taper(tester, 'profil-mdp-actuel', 'Motdepasse123');
      await appuyer(tester, 'profil-enregistrer');
      expect(find.text('Un téléphone ou un e-mail est obligatoire.'), findsOneWidget);
      expect(serveur.ecritures, isEmpty);
    });

    testWidgets("envoie les informations avec le mot de passe actuel", (tester) async {
      final serveur = await ouvrir(tester, _moi('GESTIONNAIRE'));
      await taper(tester, 'profil-nom', 'Boris Directeur');
      await taper(tester, 'profil-email', '');
      await taper(tester, 'profil-mdp-actuel', 'Motdepasse123');
      await appuyer(tester, 'profil-enregistrer');
      expect(serveur.ecritures, hasLength(1));
      final (methode, chemin, corps) = serveur.ecritures.single;
      expect((methode, chemin), ('PUT', '/api/v1/auth/moi'));
      expect(corps, containsPair('nom', 'Boris Directeur'));
      expect(corps, containsPair('motDePasse', 'Motdepasse123'));
      expect((corps as Map).containsKey('email'), isFalse); // champ vide : non envoyé
      expect(find.text('Profil mis à jour.'), findsOneWidget);
    });

    testWidgets('affiche le message du serveur quand le mot de passe est faux', (tester) async {
      await ouvrir(tester, _moi('GESTIONNAIRE'), echecs: {'PUT /api/v1/auth/moi': (status: 422, corps: {'code': 'MOT_DE_PASSE_ACTUEL_INCORRECT'})});
      await taper(tester, 'profil-mdp-actuel', 'faux');
      await appuyer(tester, 'profil-enregistrer');
      expect(find.byKey(const Key('profil-erreur')), findsOneWidget);
      expect(find.text('Mot de passe actuel incorrect.'), findsOneWidget);
    });

    testWidgets('le bouton œil affiche le mot de passe', (tester) async {
      await ouvrir(tester, _moi('GESTIONNAIRE'));
      TextField champ() => tester.widget<TextField>(find.byKey(const Key('profil-mdp-actuel')));
      expect(champ().obscureText, isTrue);
      await appuyer(tester, 'profil-mdp-actuel-voir');
      expect(champ().obscureText, isFalse);
    });
  });

  group('mot de passe', () {
    testWidgets('exige 12 caractères, une confirmation identique et un mot de passe différent', (tester) async {
      final serveur = await ouvrir(tester, _moi('LOCATAIRE'));
      await taper(tester, 'mdp-ancien', 'Ancien-motdepasse1');
      await taper(tester, 'mdp-nouveau', 'court');
      await taper(tester, 'mdp-confirmation', 'autre');
      await appuyer(tester, 'mdp-changer');
      expect(find.text('12 caractères minimum'), findsOneWidget);
      expect(find.text('La confirmation ne correspond pas.'), findsOneWidget);
      await taper(tester, 'mdp-nouveau', 'Ancien-motdepasse1');
      await taper(tester, 'mdp-confirmation', 'Ancien-motdepasse1');
      await appuyer(tester, 'mdp-changer');
      expect(find.text("Le nouveau mot de passe doit être différent de l'actuel."), findsOneWidget);
      expect(serveur.ecritures, isEmpty);
    });

    testWidgets('change le mot de passe puis ferme la session', (tester) async {
      final serveur = await ouvrir(tester, _moi('LOCATAIRE'));
      await taper(tester, 'mdp-ancien', 'Ancien-motdepasse1');
      await taper(tester, 'mdp-nouveau', 'Nouveau-motdepasse2');
      await taper(tester, 'mdp-confirmation', 'Nouveau-motdepasse2');
      await appuyer(tester, 'mdp-changer');
      final (methode, chemin, corps) = serveur.ecritures.single;
      expect((methode, chemin), ('POST', '/api/v1/auth/mot-de-passe'));
      expect(corps, {'ancienMotDePasse': 'Ancien-motdepasse1', 'nouveauMotDePasse': 'Nouveau-motdepasse2'});
      expect(session.deconnecte, isTrue);
    });

    testWidgets("n'ouvre pas de double authentification à un locataire", (tester) async {
      await ouvrir(tester, _moi('LOCATAIRE'));
      expect(find.text('Double authentification'), findsNothing);
    });
  });

  group('double authentification (application)', () {
    testWidgets('configure en deux étapes : mot de passe, puis clé et premier code', (tester) async {
      final serveur = await ouvrir(tester, _moi('GESTIONNAIRE'));
      expect(find.text('Désactivée'), findsOneWidget);
      await appuyer(tester, 'deux-configurer');
      await taper(tester, 'deux-mdp', 'Motdepasse123');
      await appuyer(tester, 'deux-valider-mdp');
      expect(serveur.ecritures.single.$2, '/api/v1/auth/moi/double-authentification/demarrer');
      expect(serveur.ecritures.single.$3, {'motDePasse': 'Motdepasse123'});
      expect(find.byKey(const Key('cle-application')), findsOneWidget);

      expect(tester.widget<FilledButton>(find.byKey(const Key('deux-confirmer'))).onPressed, isNull); // code incomplet
      await taper(tester, 'deux-code', '123456');
      await tester.pump();
      await appuyer(tester, 'deux-confirmer');
      expect(serveur.ecritures.last.$2, '/api/v1/auth/moi/double-authentification/activer');
      expect(serveur.ecritures.last.$3, {'code': '123456'});
    });

    testWidgets("un code refusé reste à l'étape du code", (tester) async {
      await ouvrir(tester, _moi('GESTIONNAIRE'), echecs: {'POST /api/v1/auth/moi/double-authentification/activer': (status: 401, corps: {'code': 'OTP_INVALIDE'})});
      await appuyer(tester, 'deux-configurer');
      await taper(tester, 'deux-mdp', 'Motdepasse123');
      await appuyer(tester, 'deux-valider-mdp');
      await taper(tester, 'deux-code', '000000');
      await tester.pump();
      await appuyer(tester, 'deux-confirmer');
      expect(find.byKey(const Key('deux-erreur')), findsOneWidget);
      expect(find.byKey(const Key('deux-confirmer')), findsOneWidget);
    });

    testWidgets("désactive avec le mot de passe ET le code de l'application", (tester) async {
      final serveur = await ouvrir(tester, _moi('ADMIN', deuxFacteurs: true));
      expect(find.text('Activée'), findsOneWidget);
      await appuyer(tester, 'deux-desactiver');
      await taper(tester, 'deux-mdp', 'Motdepasse123');
      await taper(tester, 'deux-code-desactiver', '123456');
      await tester.pump();
      await appuyer(tester, 'deux-valider-mdp');
      expect(serveur.ecritures.single.$2, '/api/v1/auth/moi/double-authentification/desactiver');
      expect(serveur.ecritures.single.$3, {'motDePasse': 'Motdepasse123', 'code': '123456'});
    });

    testWidgets('un employé restreint peut la désactiver (facultative pour tous)', (tester) async {
      await ouvrir(tester, _moi('ENCAISSEUR', deuxFacteurs: true));
      expect(find.byKey(const Key('deux-obligatoire')), findsNothing);
      expect(find.byKey(const Key('deux-desactiver')), findsOneWidget);
    });

    testWidgets('propose de configurer maintenant quand elle est exigée sans application', (tester) async {
      await ouvrir(tester, _moi('COMPTABLE', deuxFacteurs: true, configure: false));
      expect(find.text('À configurer'), findsOneWidget);
      expect(find.text('Configurer maintenant'), findsOneWidget);
    });

    testWidgets('sans téléphone, la configuration reste possible (plus de SMS)', (tester) async {
      await ouvrir(tester, _moi('GESTIONNAIRE', telephone: null));
      expect(find.byKey(const Key('deux-configurer')), findsOneWidget);
    });
  });
}
