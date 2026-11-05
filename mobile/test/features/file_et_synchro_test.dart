import 'dart:convert';

import 'package:drift/native.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/core/db/app_database.dart';
import 'package:immogest_mobile/core/error/app_error.dart';
import 'package:immogest_mobile/core/id/uuid.dart';
import 'package:immogest_mobile/features/agent/file_encaissements.dart';
import 'package:immogest_mobile/features/agent/synchro_encaissements.dart';

import '../support/locataire_support.dart';
import '../support/serveur_paiements.dart';

void main() {
  late AppDatabase db;
  late FileEncaissements file;
  late ServeurPaiements serveur;
  late SynchroEncaissements synchro;
  var horloge = DateTime(2026, 11, 4, 9, 30);
  var compteur = 0;

  Future<String> saisir({String agent = 'agent-1', String bail = 'b-1', int montant = 160000}) =>
      file.ajouter(utilisateurId: agent, bailId: bail, bailLibelle: 'Palmiers — A1', locataireNom: 'Paul', montant: montant);

  setUp(() {
    db = AppDatabase(NativeDatabase.memory());
    horloge = DateTime(2026, 11, 4, 9, 30);
    compteur = 0;
    file = FileEncaissements(db, maintenant: () => horloge, genererId: () => 'cle-${++compteur}');
    serveur = ServeurPaiements();
    final (api, _) = apiFactice(serveur.traiter);
    synchro = SynchroEncaissements(api: api, file: file, utilisateurId: () async => 'agent-1');
  });

  tearDown(() => db.close());

  group('file locale', () {
    test('une saisie reçoit sa clé d\'idempotence et la date du jour de saisie', () async {
      final id = await saisir();
      final ligne = (await file.aEnvoyer('agent-1')).single;
      expect(id, 'cle-1');
      expect(ligne.id, 'cle-1');
      expect(ligne.datePaiement, '2026-11-04');
      expect(ligne.statut, statutEnAttente);
      expect(ligne.tentatives, 0);
      expect(ligne.montant, 160000);
    });

    test('un montant nul ou négatif est refusé', () async {
      await expectLater(saisir(montant: 0), throwsArgumentError);
      await expectLater(saisir(montant: -5), throwsArgumentError);
      expect(await file.aEnvoyer('agent-1'), isEmpty);
    });

    test('chaque agent ne voit et n\'envoie que sa propre file', () async {
      await saisir(agent: 'agent-1');
      await saisir(agent: 'agent-2');
      expect(await file.aEnvoyer('agent-1'), hasLength(1));
      expect(await file.aEnvoyer('agent-2'), hasLength(1));
      await synchro.synchroniser();
      expect(serveur.nombrePaiements, 1); // seul le compte courant (agent-1) est envoyé
      expect(await file.aEnvoyer('agent-2'), hasLength(1));
    });

    test('l\'ordre de saisie est conservé', () async {
      await saisir(montant: 1000);
      horloge = horloge.add(const Duration(minutes: 5));
      await saisir(montant: 2000);
      expect((await file.aEnvoyer('agent-1')).map((e) => e.montant), [1000, 2000]);
    });

    test('abandon : possible seulement pour une saisie jamais tentée ou rejetée', () async {
      final jamaisTentee = await saisir();
      expect(await file.abandonner(jamaisTentee), isTrue);

      final tentee = await saisir();
      await file.noterEchecTemporaire(tentee, AppError.serveurIndisponible);
      expect(await file.abandonner(tentee), isFalse); // a pu être créée côté serveur
      expect((await file.aEnvoyer('agent-1')).map((e) => e.id), [tentee]);

      await file.marquerRejete(tentee, 'BAIL_INTROUVABLE');
      expect(await file.abandonner(tentee), isTrue);

      final envoyee = await saisir();
      await file.marquerEnvoye(envoyee);
      expect(await file.abandonner(envoyee), isFalse);
      expect(await file.abandonner('inconnue'), isFalse);
    });

    test('les envois anciens sont purgés, pas les récents ni les en attente', () async {
      final ancien = await saisir();
      await file.marquerEnvoye(ancien);
      horloge = horloge.add(const Duration(days: 8));
      final recent = await saisir();
      final recentEnvoye = await saisir();
      await file.marquerEnvoye(recentEnvoye);
      expect(await file.purgerEnvoyes(), 1);
      final restant = await (db.select(db.encaissementsEnAttente)).get();
      expect(restant.map((e) => e.id), unorderedEquals([recent, recentEnvoye]));
    });

    test('le nombre en attente est suivi en direct', () async {
      final flux = file.suivreNombreEnAttente('agent-1');
      final recus = <int>[];
      final abonnement = flux.listen(recus.add);
      await Future<void>.delayed(const Duration(milliseconds: 20));
      final id = await saisir();
      await Future<void>.delayed(const Duration(milliseconds: 20));
      await file.marquerEnvoye(id);
      await Future<void>.delayed(const Duration(milliseconds: 20));
      await abonnement.cancel();
      expect(recus, containsAllInOrder([0, 1, 0]));
    });
  });

  group('synchronisation', () {
    test('envoie en espèces avec la clé d\'idempotence, la date de saisie et le bon montant', () async {
      await saisir();
      final bilan = await synchro.synchroniser();
      expect(bilan.envoyes, 1);
      expect(bilan.restants, 0);
      expect(bilan.blocage, isNull);
      final requete = serveur.requetes.single;
      expect(requete.headers['Idempotency-Key'], 'cle-1');
      final corps = jsonDecode(requete.data as String) as Map<String, dynamic>;
      expect(corps['mode'], 'ESPECES');
      expect(corps['montant'], 160000);
      expect(corps['bailId'], 'b-1');
      expect(corps['datePaiement'], startsWith('2026-11-04T00:00:00')); // minuit UTC : le serveur lit bien le 4 novembre
      expect(corps['datePaiement'], endsWith('Z'));
      final ligne = (await db.select(db.encaissementsEnAttente).get()).single;
      expect(ligne.statut, statutEnvoye);
      expect(ligne.envoyeLe, isNotNull);
    });

    test('rien à envoyer : aucun appel réseau', () async {
      final bilan = await synchro.synchroniser();
      expect(bilan.envoyes, 0);
      expect(serveur.requetes, isEmpty);
    });

    test('réseau coupé : la saisie reste en file, le passage s\'arrête sans insister sur les suivantes', () async {
      serveur.reseau = Reseau.coupe;
      await saisir(montant: 1000);
      await saisir(montant: 2000);
      final bilan = await synchro.synchroniser();
      expect(bilan.blocage, AppError.serveurIndisponible);
      expect(bilan.restants, 2);
      expect(serveur.requetes, hasLength(1)); // une seule tentative : inutile de marteler un réseau absent
      final lignes = await file.aEnvoyer('agent-1');
      expect(lignes.first.tentatives, 1);
      expect(lignes.first.derniereErreur, AppError.serveurIndisponible);
      expect(lignes.last.tentatives, 0);
    });

    test('un refus définitif (bail introuvable) est rejeté, sans bloquer les suivants', () async {
      await saisir(bail: 'b-disparu', montant: 1000);
      await saisir(bail: 'b-1', montant: 2000);
      final bilan = await synchro.synchroniser();
      expect(bilan.rejetes, 1);
      expect(bilan.envoyes, 1);
      final lignes = await db.select(db.encaissementsEnAttente).get();
      final rejetee = lignes.firstWhere((e) => e.montant == 1000);
      expect(rejetee.statut, statutRejete);
      expect(rejetee.derniereErreur, 'BAIL_INTROUVABLE');
      expect(lignes.firstWhere((e) => e.montant == 2000).statut, statutEnvoye);
      // une ligne rejetée n'est plus jamais renvoyée automatiquement
      await synchro.synchroniser();
      expect(serveur.requetes, hasLength(2));
    });

    test('licence expirée, session expirée ou serveur en panne : on réessaiera, rien n\'est rejeté', () async {
      for (final (status, code) in [(403, 'LICENCE_EXPIREE'), (401, 'TOKEN_EXPIRE'), (500, 'ERREUR_INTERNE'), (503, 'X'), (429, 'TROP'), (408, 'DELAI')]) {
        final (api, _) = apiFactice((o) => (status: status, corps: {'code': code}));
        final s = SynchroEncaissements(api: api, file: file, utilisateurId: () async => 'agent-1');
        final id = await saisir();
        final bilan = await s.synchroniser();
        expect(bilan.blocage, code, reason: '$status $code');
        expect(bilan.rejetes, 0, reason: '$status $code');
        expect((await file.aEnvoyer('agent-1')).map((e) => e.id), contains(id));
        await db.delete(db.encaissementsEnAttente).go();
      }
    });

    test('classification des erreurs', () {
      expect(classer(const AppError('X', status: 404)), Decision.rejeter);
      expect(classer(const AppError('X', status: 400)), Decision.rejeter);
      expect(classer(const AppError('X', status: 409)), Decision.rejeter);
      expect(classer(const AppError('ACCES_REFUSE', status: 403)), Decision.rejeter);
      expect(classer(const AppError('LICENCE_EXPIREE', status: 403)), Decision.reessayer);
      expect(classer(const AppError(AppError.serveurIndisponible)), Decision.reessayer);
      expect(classer(const AppError('X', status: 502)), Decision.reessayer);
      expect(classer(const AppError('X', status: 401)), Decision.reessayer);
    });

    test('deux demandes simultanées ne font qu\'un seul passage', () async {
      await saisir();
      final resultats = await Future.wait([synchro.synchroniser(), synchro.synchroniser(), synchro.synchroniser()]);
      expect(serveur.requetes, hasLength(1));
      expect(resultats.map((b) => b.envoyes), everyElement(1)); // même bilan pour tous
      expect(serveur.nombrePaiements, 1);
    });

    test('sans compte connecté, rien n\'est envoyé', () async {
      await saisir();
      final (api, _) = apiFactice(serveur.traiter);
      final bilan = await SynchroEncaissements(api: api, file: file, utilisateurId: () async => null).synchroniser();
      expect(bilan.envoyes, 0);
      expect(serveur.requetes, isEmpty);
    });
  });

  group('utilitaires', () {
    test('uuidV4 respecte le format v4 et ne se répète pas', () {
      final ids = {for (var i = 0; i < 500; i++) uuidV4()};
      expect(ids, hasLength(500));
      final motif = RegExp(r'^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$');
      expect(ids.every(motif.hasMatch), isTrue);
    });
  });
}
