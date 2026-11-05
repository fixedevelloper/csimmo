import 'package:drift/drift.dart' hide isNull, isNotNull;
import 'package:drift/native.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/core/auth/role.dart';
import 'package:immogest_mobile/core/auth/token_store.dart';
import 'package:immogest_mobile/core/db/app_database.dart';
import 'package:immogest_mobile/core/providers.dart';
import 'package:immogest_mobile/core/storage/key_value_store.dart';
import 'package:immogest_mobile/features/agent/agent_shell.dart';
import 'package:immogest_mobile/features/agent/a_envoyer_screen.dart';
import 'package:immogest_mobile/features/agent/encaisser_screen.dart';
import 'package:immogest_mobile/features/agent/file_encaissements.dart';
import 'package:immogest_mobile/features/agent/listes_screens.dart';
import 'package:immogest_mobile/features/agent/synchro_automatique.dart';

import '../support/jwt_support.dart';
import '../support/locataire_support.dart';
import '../support/serveur_paiements.dart';

void main() {
  late AppDatabase db;
  late ServeurPaiements serveur;
  late MemoryKeyValueStore store;
  late String jetonAgent;

  setUp(() async {
    db = AppDatabase(NativeDatabase.memory());
    serveur = ServeurPaiements();
    store = MemoryKeyValueStore();
    jetonAgent = jeton({'sub': 'agent-1'});
    await TokenStore(store).save(Tokens(access: jetonAgent, refresh: 'r', role: Role.gestionnaire));
    // données déjà présentes sur le téléphone (copie locale)
    await db.batch((b) {
      b.insertAll(db.locatairesCache, [
        LocatairesCacheCompanion.insert(id: 'l-1', nom: 'Paul Mbarga', telephone: const Value('+237699000001')),
        LocatairesCacheCompanion.insert(id: 'l-2', nom: 'Marie Ngo'),
      ]);
      b.insertAll(db.bauxCache, [
        BauxCacheCompanion.insert(id: 'b-1', numero: 'BAIL-1', locataireId: 'l-1', libelle: 'Palmiers — A1', statut: 'ACTIF', montantMensuel: 160000),
        BauxCacheCompanion.insert(id: 'b-2', numero: 'BAIL-2', locataireId: 'l-2', libelle: 'Palmiers — B2', statut: 'ACTIF', montantMensuel: 90000),
        BauxCacheCompanion.insert(id: 'b-3', numero: 'BAIL-3', locataireId: 'l-2', libelle: 'Baobabs — C3', statut: 'ACTIF', montantMensuel: 70000),
      ]);
      b.insertAll(db.lotsCache, [
        LotsCacheCompanion.insert(id: 'lot-1', reference: 'A1', immeubleNom: 'Palmiers', statut: 'OCCUPE'),
        LotsCacheCompanion.insert(id: 'lot-2', reference: 'T1', immeubleNom: 'Baobabs', statut: 'EN_TRAVAUX'),
      ]);
    });
  });

  tearDown(() => db.close());

  /// Démonte l'arbre et laisse passer les minuteries de nettoyage des flux drift (sinon : « Timer still pending »).
  Future<void> fin(WidgetTester tester) async {
    await tester.pumpWidget(const SizedBox.shrink());
    await tester.pump(const Duration(milliseconds: 50));
  }

  Future<Widget> app(Widget ecran) async {
    // le réseau est coupé : l'actualisation silencieuse échoue et l'écran travaille sur la copie locale
    serveur.reseau = Reseau.coupe;
    final (api, _) = apiFactice(serveur.traiter);
    return appDeTest(ecran, api: api, extra: [
      appDatabaseProvider.overrideWithValue(db),
      // pas de minuteur périodique en test (il resterait en attente après la fin du test) : seul « declencher » est utilisé
      synchroAutomatiqueProvider.overrideWith((ref) => SynchroAutomatique(ref)),
      keyValueStoreProvider.overrideWithValue(store),
      bootProvider.overrideWithValue(BootState(serverUrl: 'http://serveur.test', tokens: Tokens(access: jetonAgent, refresh: 'r', role: Role.gestionnaire))),
    ]);
  }

  testWidgets('encaisser sans réseau : recherche locale, bail choisi d\'office, saisie mise en file', (tester) async {
    await tester.pumpWidget(await app(const EncaisserScreen()));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('locataire-l-1')), findsOneWidget);

    await tester.enterText(find.byKey(const Key('recherche-locataire')), 'mbarga');
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('locataire-l-2')), findsNothing);
    await tester.tap(find.byKey(const Key('locataire-l-1')));
    await tester.pumpAndSettle();
    expect(find.text('Palmiers — A1'), findsOneWidget); // un seul bail : sélectionné sans action

    await tester.enterText(find.byKey(const Key('champ-montant')), '160 000');
    await tester.ensureVisible(find.byKey(const Key('enregistrer-encaissement')));
    await tester.tap(find.byKey(const Key('enregistrer-encaissement')));
    await tester.pumpAndSettle();

    final lignes = await db.select(db.encaissementsEnAttente).get();
    expect(lignes, hasLength(1));
    expect(lignes.single.utilisateurId, 'agent-1');
    expect(lignes.single.bailId, 'b-1');
    expect(lignes.single.montant, 160000);
    expect(lignes.single.locataireNom, 'Paul Mbarga');
    expect(find.text('Encaissement enregistré sur le téléphone. Il sera envoyé dès que possible.'), findsOneWidget);
    expect(find.byKey(const Key('locataire-choisi')), findsNothing); // formulaire remis à zéro
    // le réseau est coupé : l'envoi automatique a échoué sans rien perdre
    expect(lignes.single.statut, statutEnAttente);
    await fin(tester);
  });

  testWidgets('plusieurs baux : il faut en choisir un ; montant invalide refusé', (tester) async {
    await tester.pumpWidget(await app(const EncaisserScreen()));
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const Key('locataire-l-2')));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('bail-b-2')), findsOneWidget);
    expect(find.byKey(const Key('bail-b-3')), findsOneWidget);

    await tester.enterText(find.byKey(const Key('champ-montant')), '0');
    await tester.ensureVisible(find.byKey(const Key('enregistrer-encaissement')));
    await tester.tap(find.byKey(const Key('enregistrer-encaissement')));
    await tester.pumpAndSettle();
    expect(find.text('Saisissez un montant entier en FCFA.'), findsOneWidget);
    expect(await db.select(db.encaissementsEnAttente).get(), isEmpty);

    await tester.enterText(find.byKey(const Key('champ-montant')), '50000');
    await tester.ensureVisible(find.byKey(const Key('enregistrer-encaissement')));
    await tester.tap(find.byKey(const Key('enregistrer-encaissement')));
    await tester.pumpAndSettle();
    expect(find.text('Choisissez le bail concerné.'), findsOneWidget);
    expect(await db.select(db.encaissementsEnAttente).get(), isEmpty);

    await tester.tap(find.byKey(const Key('bail-b-3')));
    await tester.pumpAndSettle();
    await tester.ensureVisible(find.byKey(const Key('enregistrer-encaissement')));
    await tester.tap(find.byKey(const Key('enregistrer-encaissement')));
    await tester.pumpAndSettle();
    expect((await db.select(db.encaissementsEnAttente).get()).single.bailId, 'b-3');
    await fin(tester);
  });

  testWidgets('sans aucune donnée locale, l\'écran explique comment s\'en procurer', (tester) async {
    await db.delete(db.locatairesCache).go();
    await tester.pumpWidget(await app(const EncaisserScreen()));
    await tester.pumpAndSettle();
    expect(find.text('Actualisez les données (avec du réseau) pour pouvoir saisir sans réseau ensuite.'), findsOneWidget);
    expect(find.text('Aucune donnée enregistrée sur ce téléphone.'), findsOneWidget);
    await fin(tester);
  });

  testWidgets('la file affiche les trois états ; seules les saisies jamais tentées ou rejetées se suppriment', (tester) async {
    final file = FileEncaissements(db);
    final neuve = await file.ajouter(utilisateurId: 'agent-1', bailId: 'b-1', bailLibelle: 'Palmiers — A1', locataireNom: 'Paul', montant: 1000);
    final tentee = await file.ajouter(utilisateurId: 'agent-1', bailId: 'b-1', bailLibelle: 'Palmiers — A1', locataireNom: 'Marie', montant: 2000);
    await file.noterEchecTemporaire(tentee, 'SERVEUR_INDISPONIBLE');
    final rejetee = await file.ajouter(utilisateurId: 'agent-1', bailId: 'b-x', bailLibelle: 'Disparu', locataireNom: 'Luc', montant: 3000);
    await file.marquerRejete(rejetee, 'BAIL_INTROUVABLE');
    final envoyee = await file.ajouter(utilisateurId: 'agent-1', bailId: 'b-1', bailLibelle: 'Palmiers — A1', locataireNom: 'Zoé', montant: 4000);
    await file.marquerEnvoye(envoyee);
    await file.ajouter(utilisateurId: 'autre-agent', bailId: 'b-1', bailLibelle: 'x', locataireNom: 'Invisible', montant: 5000);

    await tester.pumpWidget(await app(const AEnvoyerScreen()));
    await tester.pumpAndSettle();
    expect(find.text('Invisible · 5 000 FCFA'.replaceAll(' ', ' ')), findsNothing);
    expect(find.textContaining('Invisible'), findsNothing);
    expect(tester.widget<Text>(find.byKey(Key('statut-$neuve'))).data, 'En attente');
    expect(tester.widget<Text>(find.byKey(Key('statut-$rejetee'))).data, 'Refusé');
    expect(tester.widget<Text>(find.byKey(Key('statut-$envoyee'))).data, 'Envoyé');
    expect(find.textContaining('Bail introuvable'), findsOneWidget); // motif du refus, en clair
    expect(find.byKey(Key('abandonner-$envoyee')), findsNothing);

    await tester.tap(find.byKey(Key('abandonner-$tentee')));
    await tester.pumpAndSettle();
    expect(find.textContaining('déjà été présenté au serveur'), findsOneWidget);
    expect(await db.select(db.encaissementsEnAttente).get().then((l) => l.map((e) => e.id)), contains(tentee));

    await tester.tap(find.byKey(Key('abandonner-$rejetee')));
    await tester.pumpAndSettle();
    expect(await db.select(db.encaissementsEnAttente).get().then((l) => l.map((e) => e.id)), isNot(contains(rejetee)));
    await fin(tester);
  });

  testWidgets('« Envoyer maintenant » synchronise, et signale un réseau absent', (tester) async {
    final file = FileEncaissements(db);
    await file.ajouter(utilisateurId: 'agent-1', bailId: 'b-1', bailLibelle: 'Palmiers — A1', locataireNom: 'Paul', montant: 1000);
    await tester.pumpWidget(await app(const AEnvoyerScreen()));
    await tester.pumpAndSettle();

    await tester.tap(find.byKey(const Key('synchroniser')));
    await tester.pumpAndSettle();
    expect(find.textContaining('Impossible de joindre le serveur'), findsOneWidget);
    expect(find.textContaining('1 encaissement(s) restent en attente'), findsOneWidget);

    serveur.reseau = Reseau.ok;
    await tester.pump(const Duration(seconds: 5)); // laisse disparaître le message précédent
    await tester.tap(find.byKey(const Key('synchroniser')));
    await tester.pumpAndSettle();
    expect(find.textContaining('1 envoyé(s), 0 refusé(s)'), findsOneWidget);
    expect(serveur.nombrePaiements, 1);
    await fin(tester);
  });

  testWidgets('lots et locataires se consultent et se filtrent sans réseau', (tester) async {
    await tester.pumpWidget(await app(const LotsScreen()));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('lot-lot-1')), findsOneWidget);
    expect(find.text('En travaux'), findsOneWidget);
    await tester.enterText(find.byKey(const Key('recherche-lots')), 'baob');
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('lot-lot-1')), findsNothing);
    expect(find.byKey(const Key('lot-lot-2')), findsOneWidget);

    await tester.pumpWidget(await app(const LocatairesScreen()));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('liste-locataire-l-1')), findsOneWidget);
    await tester.enterText(find.byKey(const Key('recherche-locataires')), 'ngo');
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('liste-locataire-l-1')), findsNothing);
    expect(find.byKey(const Key('liste-locataire-l-2')), findsOneWidget);
    await fin(tester);
  });

  testWidgets('la coque affiche le nombre d\'envois en attente et prévient avant la déconnexion', (tester) async {
    final file = FileEncaissements(db);
    await file.ajouter(utilisateurId: 'agent-1', bailId: 'b-1', bailLibelle: 'Palmiers — A1', locataireNom: 'Paul', montant: 1000);
    await file.ajouter(utilisateurId: 'agent-1', bailId: 'b-1', bailLibelle: 'Palmiers — A1', locataireNom: 'Marie', montant: 2000);
    await tester.pumpWidget(await app(const AgentShell()));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('titre-GESTIONNAIRE')), findsOneWidget);
    expect(tester.widget<Text>(find.byKey(const Key('badge-a-envoyer'))).data, '2');

    await tester.tap(find.byKey(const Key('bouton-deconnexion')));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('dialogue-deconnexion')), findsOneWidget);
    expect(find.textContaining('2 encaissement(s) non envoyé(s) seront conservés'), findsOneWidget);
    await tester.tap(find.text('Annuler'));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('dialogue-deconnexion')), findsNothing);
    expect((await TokenStore(store).read()), isNotNull); // toujours connecté
    await fin(tester);
  });
}
