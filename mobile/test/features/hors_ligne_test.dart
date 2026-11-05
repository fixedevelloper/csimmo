import 'package:drift/native.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/core/db/app_database.dart';
import 'package:immogest_mobile/core/error/app_error.dart';
import 'package:immogest_mobile/features/agent/file_encaissements.dart';
import 'package:immogest_mobile/features/agent/synchro_encaissements.dart';

import '../support/locataire_support.dart';
import '../support/serveur_paiements.dart';

/// Scénario du critère de l'étape 7.2 : saisie hors ligne → reconnexion → le paiement est créé UNE seule fois côté serveur,
/// y compris quand une réponse se perd et que le téléphone renvoie la même saisie.
void main() {
  late AppDatabase db;
  late FileEncaissements file;
  late ServeurPaiements serveur;
  late SynchroEncaissements synchro;

  setUp(() {
    db = AppDatabase(NativeDatabase.memory());
    file = FileEncaissements(db);
    serveur = ServeurPaiements();
    final (api, _) = apiFactice(serveur.traiter);
    synchro = SynchroEncaissements(api: api, file: file, utilisateurId: () async => 'agent-1');
  });

  tearDown(() => db.close());

  Future<String> saisirEspeces(int montant) =>
      file.ajouter(utilisateurId: 'agent-1', bailId: 'b-1', bailLibelle: 'Palmiers — A1', locataireNom: 'Paul Mbarga', montant: montant);

  test('saisie hors ligne → reconnexion → un seul paiement côté serveur', () async {
    // 1. l'agent est en zone sans réseau
    serveur.reseau = Reseau.coupe;
    final id = await saisirEspeces(160000);
    expect((await synchro.synchroniser()).blocage, AppError.serveurIndisponible);
    expect(serveur.nombrePaiements, 0);
    expect(await file.aEnvoyer('agent-1'), hasLength(1));

    // 2. le réseau revient
    serveur.reseau = Reseau.ok;
    final bilan = await synchro.synchroniser();
    expect(bilan.envoyes, 1);
    expect(bilan.restants, 0);
    expect(serveur.nombrePaiements, 1);
    expect(serveur.crees.keys, [id]);
    expect(serveur.crees[id]!['montant'], 160000);

    // 3. d'autres passages (minuteur, retour au premier plan…) ne recréent rien
    await synchro.synchroniser();
    await synchro.synchroniser();
    expect(serveur.nombrePaiements, 1);
    expect(serveur.requetes.where((o) => o.method == 'POST'), hasLength(2)); // un échec réseau, un succès
  });

  test('réponse perdue après création côté serveur : le renvoi ne crée pas de doublon', () async {
    final id = await saisirEspeces(75000);

    // le serveur enregistre le paiement, mais la connexion tombe avant que le téléphone ne reçoive la réponse
    serveur.reseau = Reseau.perteReponse;
    final premier = await synchro.synchroniser();
    expect(premier.blocage, AppError.serveurIndisponible);
    expect(serveur.nombrePaiements, 1); // déjà créé…
    expect((await file.aEnvoyer('agent-1')).single.tentatives, 1); // …mais le téléphone croit à un échec

    // la saisie reste supprimable ? Non : elle a été présentée au serveur
    expect(await file.abandonner(id), isFalse);

    // reconnexion : la même clé est renvoyée, le serveur répond par le paiement d'origine
    serveur.reseau = Reseau.ok;
    final second = await synchro.synchroniser();
    expect(second.envoyes, 1);
    expect(serveur.nombrePaiements, 1); // toujours un seul
    final cles = serveur.requetes.where((o) => o.method == 'POST').map((o) => o.headers['Idempotency-Key']).toSet();
    expect(cles, {id}); // les deux envois portaient exactement la même clé
    expect((await db.select(db.encaissementsEnAttente).get()).single.statut, statutEnvoye);
  });

  test('plusieurs saisies hors ligne partent dans l\'ordre, chacune une fois', () async {
    serveur.reseau = Reseau.coupe;
    final ids = [await saisirEspeces(1000), await saisirEspeces(2000), await saisirEspeces(3000)];
    await synchro.synchroniser();
    serveur.reseau = Reseau.perteReponse; // la première réponse se perd
    await synchro.synchroniser();
    serveur.reseau = Reseau.ok;
    final bilan = await synchro.synchroniser();
    expect(bilan.envoyes, 3);
    expect(serveur.nombrePaiements, 3);
    expect(serveur.crees.keys, unorderedEquals(ids));
    expect(serveur.crees.values.map((p) => p['montant']), unorderedEquals([1000, 2000, 3000]));
  });

  test('une même saisie ne change jamais de clé d\'une tentative à l\'autre', () async {
    final id = await saisirEspeces(5000);
    serveur.reseau = Reseau.coupe;
    for (var i = 0; i < 4; i++) {
      await synchro.synchroniser();
    }
    serveur.reseau = Reseau.ok;
    await synchro.synchroniser();
    final cles = serveur.requetes.where((o) => o.method == 'POST').map((o) => o.headers['Idempotency-Key']);
    expect(cles, hasLength(5));
    expect(cles.toSet(), {id});
    expect(serveur.nombrePaiements, 1);
  });
}
