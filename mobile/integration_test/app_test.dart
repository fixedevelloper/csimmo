import 'package:drift/native.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/app.dart';
import 'package:immogest_mobile/core/auth/role.dart';
import 'package:immogest_mobile/core/auth/token_store.dart';
import 'package:immogest_mobile/core/db/app_database.dart';
import 'package:immogest_mobile/core/providers.dart';
import 'package:immogest_mobile/core/storage/key_value_store.dart';
import 'package:integration_test/integration_test.dart';

import 'support/fake_backend.dart';

/// Parcours complet : configuration du serveur → connexion → accueil selon le rôle,
/// contre un faux backend HTTP local (vraie socket, vrai Dio).
void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  late FakeBackend backend;
  late MemoryKeyValueStore store;

  final gestionnaire = FakeUser(
      nom: 'Gestionnaire Test', role: 'GESTIONNAIRE', email: 'gest@immo.local', telephone: '+237611111111', motDePasse: 'Secret123');
  final locataire = FakeUser(nom: 'Locataire Test', role: 'LOCATAIRE', telephone: '+237622222222', motDePasse: 'Secret123');
  final prestataire = FakeUser(nom: 'Prestataire Test', role: 'PRESTATAIRE', email: 'presta@immo.local', motDePasse: 'Secret123');
  final admin2fa = FakeUser(
      nom: 'Admin 2FA', role: 'ADMIN', email: 'admin@immo.local', telephone: '+237633333333', motDePasse: 'Secret123', deuxFacteurs: true);

  setUp(() async {
    backend = await FakeBackend.demarrer();
    for (final u in [gestionnaire, locataire, prestataire, admin2fa]) {
      backend.ajouter(u);
    }
    store = MemoryKeyValueStore();
  });

  tearDown(() => backend.arreter());

  Future<void> lancer(WidgetTester tester) async {
    // Le test vérifie les textes français, quelle que soit la langue de l'appareil
    tester.platformDispatcher.localesTestValue = const [Locale('fr')];
    addTearDown(tester.platformDispatcher.clearLocalesTestValue);
    final boot = await ServerSettings.load(store);
    await tester.pumpWidget(ProviderScope(
      retry: (_, _) => null,
      overrides: [
        keyValueStoreProvider.overrideWithValue(store),
        bootProvider.overrideWithValue(boot),
        // base en mémoire : la vraie base fichier passe par un plugin absent de la VM de test, et ce parcours n'en dépend pas
        appDatabaseProvider.overrideWith((ref) {
          final db = AppDatabase(NativeDatabase.memory());
          ref.onDispose(db.close);
          return db;
        }),
      ],
      child: const ImmoGestApp(),
    ));
    await tester.pump(const Duration(milliseconds: 300));
  }

  /// Avance les images jusqu'à ce que [finder] apparaisse (les appels réseau sont réels).
  Future<void> attendre(WidgetTester tester, Finder finder, {Duration delai = const Duration(seconds: 10)}) async {
    final fin = DateTime.now().add(delai);
    while (DateTime.now().isBefore(fin)) {
      await tester.pump(const Duration(milliseconds: 100));
      if (finder.evaluate().isNotEmpty) return;
    }
    final textes = find.byType(Text).evaluate().map((e) => (e.widget as Text).data).whereType<String>().toList();
    fail('Introuvable après ${delai.inSeconds} s : $finder\nTextes affichés : $textes');
  }

  Future<void> appuyerPrincipal(WidgetTester tester) async {
    final bouton = find.byKey(const Key('bouton-principal'));
    await tester.ensureVisible(bouton);
    await tester.pump();
    await tester.tap(bouton);
  }

  Future<void> configurerServeur(WidgetTester tester, [String? adresse]) async {
    await attendre(tester, find.byKey(const Key('champ-serveur')));
    await tester.enterText(find.byKey(const Key('champ-serveur')), adresse ?? backend.url);
    await tester.tap(find.byKey(const Key('bouton-continuer')));
    await attendre(tester, find.byKey(const Key('champ-identifiant')));
  }

  Future<void> seConnecter(WidgetTester tester, String identifiant, String motDePasse) async {
    await tester.enterText(find.byKey(const Key('champ-identifiant')), identifiant);
    await tester.enterText(find.byKey(const Key('champ-mot-de-passe')), motDePasse);
    await appuyerPrincipal(tester);
  }

  testWidgets('configuration du serveur → connexion → espace gestion, puis déconnexion', (tester) async {
    await lancer(tester);
    expect(find.text('Votre serveur CSImmo'), findsOneWidget);

    await configurerServeur(tester);
    expect(await store.read('server.url'), backend.url);
    expect(find.text('Serveur : ${backend.url}'), findsOneWidget);

    await seConnecter(tester, 'gest@immo.local', 'Secret123');
    await attendre(tester, find.byKey(const Key('titre-GESTIONNAIRE')));
    expect(find.text('Espace gestion'), findsOneWidget);
    await attendre(tester, find.text('Bienvenue, Gestionnaire Test'));

    // Les jetons sont dans le stockage sécurisé
    final jetons = await TokenStore(store).read();
    expect(jetons?.role, Role.gestionnaire);

    await tester.tap(find.byKey(const Key('bouton-deconnexion')));
    await attendre(tester, find.byKey(const Key('champ-identifiant')));
    expect(await TokenStore(store).read(), isNull);
    expect(await store.read('server.url'), backend.url, reason: 'le serveur reste configuré après déconnexion');
  });

  testWidgets('le locataire se connecte par code SMS et arrive dans son espace', (tester) async {
    await lancer(tester);
    await configurerServeur(tester);

    await tester.tap(find.byKey(const Key('bouton-mode-sms')));
    await tester.pump();
    expect(find.byKey(const Key('champ-mot-de-passe')), findsNothing);
    await tester.enterText(find.byKey(const Key('champ-identifiant')), '+237622222222');
    await appuyerPrincipal(tester);
    await attendre(tester, find.byKey(const Key('champ-code')));

    await tester.enterText(find.byKey(const Key('champ-code')), FakeBackend.codeOtp);
    await appuyerPrincipal(tester);

    await attendre(tester, find.byKey(const Key('titre-LOCATAIRE')));
    expect(find.text('Mon espace locataire'), findsOneWidget);
    await attendre(tester, find.text('Bienvenue, Locataire Test'));
  });

  testWidgets('le prestataire arrive dans l\'espace prestataire', (tester) async {
    await lancer(tester);
    await configurerServeur(tester);
    await seConnecter(tester, 'presta@immo.local', 'Secret123');

    await attendre(tester, find.byKey(const Key('titre-PRESTATAIRE')));
    expect(find.text('Espace prestataire'), findsOneWidget);
  });

  testWidgets('double authentification : mot de passe puis code de l\'application', (tester) async {
    await lancer(tester);
    await configurerServeur(tester);
    await seConnecter(tester, 'admin@immo.local', 'Secret123');

    await attendre(tester, find.byKey(const Key('champ-code')));
    expect(find.text('Saisissez le code à 6 chiffres affiché par votre application d\'authentification.'), findsOneWidget);
    expect(await TokenStore(store).read(), isNull, reason: 'pas de session avant le second facteur');

    await tester.enterText(find.byKey(const Key('champ-code')), '000000');
    await appuyerPrincipal(tester);
    await attendre(tester, find.text('Code invalide ou expiré.'));

    await tester.enterText(find.byKey(const Key('champ-code')), FakeBackend.codeOtp);
    await appuyerPrincipal(tester);
    await attendre(tester, find.text('Espace gestion'));
    expect(find.byKey(const Key('titre-GESTIONNAIRE')), findsOneWidget, reason: 'ADMIN et GESTIONNAIRE partagent l\'espace agent');
  });

  testWidgets('un mot de passe incorrect affiche l\'erreur traduite et reste sur la connexion', (tester) async {
    await lancer(tester);
    await configurerServeur(tester);
    await seConnecter(tester, 'gest@immo.local', 'faux');

    await attendre(tester, find.text('Identifiant ou mot de passe incorrect.'));
    expect(find.byKey(const Key('champ-identifiant')), findsOneWidget);
  });

  testWidgets('les champs vides sont signalés sans appel réseau', (tester) async {
    await lancer(tester);
    await configurerServeur(tester);
    await appuyerPrincipal(tester);
    await tester.pump();

    expect(find.text('Ce champ est obligatoire'), findsNWidgets(2));
    expect(backend.loginsAppeles, 0);
  });

  Future<void> continuer(WidgetTester tester) async {
    // Avec un message d'erreur affiché, le bouton peut sortir de la zone visible sur un petit écran
    await tester.ensureVisible(find.byKey(const Key('bouton-continuer')));
    await tester.pump();
    await tester.tap(find.byKey(const Key('bouton-continuer')));
  }

  testWidgets('adresses de serveur refusées : injoignable, http public, pas un serveur ImmoGest', (tester) async {
    await lancer(tester);
    await attendre(tester, find.byKey(const Key('champ-serveur')));

    await tester.enterText(find.byKey(const Key('champ-serveur')), 'http://127.0.0.1:1'); // port fermé
    await continuer(tester);
    await attendre(tester, find.textContaining('Impossible de joindre le serveur'));

    await tester.enterText(find.byKey(const Key('champ-serveur')), 'http://immo.exemple.cm');
    await continuer(tester);
    await attendre(tester, find.textContaining('doit commencer par https://'));

    await tester.enterText(find.byKey(const Key('champ-serveur')), '');
    await continuer(tester);
    await attendre(tester, find.textContaining('Adresse invalide'));

    expect(await store.read('server.url'), isNull);
  });

  testWidgets('relance de l\'application : la session est restaurée sans nouvelle connexion', (tester) async {
    final (access, refresh) = backend.ouvrirSession(locataire);
    await store.write('server.url', backend.url);
    await TokenStore(store).save(Tokens(access: access, refresh: refresh, role: Role.locataire));

    await lancer(tester);

    await attendre(tester, find.byKey(const Key('titre-LOCATAIRE')));
    await attendre(tester, find.text('Bienvenue, Locataire Test'));
    expect(backend.loginsAppeles, 0);
  });

  testWidgets('jeton d\'accès expiré : renouvelé de façon transparente (un seul rafraîchissement)', (tester) async {
    final (access, refresh) = backend.ouvrirSession(locataire, accesExpire: true);
    await store.write('server.url', backend.url);
    await TokenStore(store).save(Tokens(access: access, refresh: refresh, role: Role.locataire));

    await lancer(tester);

    await attendre(tester, find.text('Bienvenue, Locataire Test'));
    expect(backend.refreshAppeles, 1);
    final apres = await TokenStore(store).read();
    expect(apres?.refresh, isNot(refresh), reason: 'le refresh token a tourné et a été mémorisé');
  });

  testWidgets('session révoquée côté serveur : retour à la connexion et jetons effacés', (tester) async {
    await store.write('server.url', backend.url);
    await TokenStore(store).save(const Tokens(access: 'perime', refresh: 'revoque', role: Role.locataire));

    await lancer(tester);

    await attendre(tester, find.byKey(const Key('champ-identifiant')));
    expect(await TokenStore(store).read(), isNull);
  });

  testWidgets('« Changer de serveur » oublie le serveur et la session', (tester) async {
    await lancer(tester);
    await configurerServeur(tester);

    await tester.tap(find.byKey(const Key('bouton-changer-serveur')));
    await attendre(tester, find.byKey(const Key('champ-serveur')));
    expect(await store.read('server.url'), isNull);
  });
}
