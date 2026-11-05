import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/features/locataire/payer_screen.dart';

import '../support/fake_adapter.dart';
import '../support/locataire_support.dart';

const _bail = {'id': 'b-1', 'numero': 'BAIL-1', 'immeubleNom': 'Palmiers', 'lotReference': 'A1', 'statut': 'ACTIF', 'loyer': 150000, 'charges': 10000};

Map<String, Object?> _transaction(String statut, {String? erreur}) =>
    {'id': 't-1', 'bailId': 'b-1', 'operateur': 'MTN_MOMO', 'montant': 160000, 'statut': statut, 'instructions': 'Validez le paiement sur votre téléphone', 'erreur': erreur};

/// Serveur scripté : [creation] répond aux POST, [etats] aux GET successifs de suivi.
class Serveur {
  Serveur({this.creation, this.etats = const []});

  Reponse Function(RequestOptions)? creation;
  List<Reponse> etats;
  int suivis = 0;
  final List<RequestOptions> posts = [];

  Reponse traiter(RequestOptions o) {
    if (o.path == '/api/v1/baux') return (status: 200, corps: page([_bail]));
    if (o.path == '/api/v1/paiements-en-ligne' && o.method == 'POST') {
      posts.add(o);
      return creation != null ? creation!(o) : (status: 201, corps: _transaction('EN_ATTENTE'));
    }
    if (o.path == '/api/v1/paiements-en-ligne/t-1') {
      final i = suivis < etats.length ? suivis : etats.length - 1;
      suivis++;
      return etats[i];
    }
    return (status: 404, corps: {'code': 'INTROUVABLE'});
  }
}

void main() {
  Future<void> ouvrir(WidgetTester tester, Serveur s, {int? suggere = 160000, Duration intervalle = const Duration(seconds: 1), Duration delaiMax = const Duration(seconds: 10)}) async {
    final (api, _) = apiFactice(s.traiter);
    await tester.pumpWidget(appDeTest(PayerScreen(montantSuggere: suggere, intervalleSuivi: intervalle, delaiMax: delaiMax), api: api));
    await tester.pumpAndSettle(); // charge les baux (aucun minuteur ni animation infinie à ce stade)
  }

  Future<void> saisir(WidgetTester tester, {String montant = '', String tel = '+237 699 11 22 33'}) async {
    if (montant.isNotEmpty) await tester.enterText(find.byKey(const Key('champ-montant-paiement')), montant);
    await tester.enterText(find.byKey(const Key('champ-telephone-paiement')), tel);
  }

  /// Laisse les appels (asynchrones, sans minuteur de plus de 10 ms) aboutir sans faire avancer le suivi de plusieurs secondes.
  Future<void> laisserPasser(WidgetTester tester) async {
    for (var i = 0; i < 20; i++) {
      await tester.pump(const Duration(milliseconds: 10));
    }
  }

  Future<void> appuyer(WidgetTester tester) async {
    await tester.ensureVisible(find.byKey(const Key('confirmer-paiement')));
    await tester.tap(find.byKey(const Key('confirmer-paiement')));
    await laisserPasser(tester);
  }

  Future<void> fin(WidgetTester tester) async {
    await tester.pumpWidget(const SizedBox.shrink());
    await tester.pump(const Duration(milliseconds: 50));
  }

  testWidgets('le montant dû est proposé ; une saisie invalide n\'appelle pas le serveur', (tester) async {
    final s = Serveur();
    await ouvrir(tester, s);
    expect(tester.widget<TextFormField>(find.byKey(const Key('champ-montant-paiement'))).controller!.text, '160000');

    await saisir(tester, montant: '0', tel: '12');
    await appuyer(tester);
    expect(find.text('Saisissez un montant entier en FCFA.'), findsOneWidget);
    expect(find.text('Saisissez un numéro valide, au format international.'), findsOneWidget);
    expect(s.posts, isEmpty);
    await fin(tester);
  });

  testWidgets('succès : demande avec clé d\'idempotence, attente de l\'opérateur, puis confirmation du serveur', (tester) async {
    final s = Serveur(etats: [(status: 200, corps: _transaction('EN_ATTENTE')), (status: 200, corps: _transaction('CONFIRMEE'))]);
    await ouvrir(tester, s);
    await saisir(tester);
    await appuyer(tester);

    final post = s.posts.single;
    expect(post.headers['Idempotency-Key'], isNotEmpty);
    final corps = jsonDecode(post.data as String) as Map<String, dynamic>;
    expect(corps, {'bailId': 'b-1', 'montant': 160000, 'operateur': 'MTN_MOMO', 'telephone': '+237 699 11 22 33'});
    expect(find.byKey(const Key('paiement-en-attente')), findsOneWidget);
    expect(find.byKey(const Key('paiement-confirme')), findsNothing); // tant que l'opérateur n'a rien dit

    await tester.pump(const Duration(seconds: 1));
    await laisserPasser(tester);
    await tester.pump(const Duration(seconds: 1));
    await laisserPasser(tester);
    expect(find.byKey(const Key('paiement-confirme')), findsOneWidget);
    expect(find.textContaining('confirmé'), findsOneWidget);
    await fin(tester);
  });

  testWidgets('l\'opérateur refuse : message d\'échec, rien n\'est présenté comme payé', (tester) async {
    final s = Serveur(etats: [(status: 200, corps: _transaction('ECHOUEE', erreur: 'REFUSEE_PAR_OPERATEUR'))]);
    await ouvrir(tester, s);
    await saisir(tester);
    await appuyer(tester);
    await tester.pump(const Duration(seconds: 1));
    await laisserPasser(tester);
    expect(find.byKey(const Key('paiement-echoue')), findsOneWidget);
    expect(find.byKey(const Key('paiement-confirme')), findsNothing);
    expect(find.textContaining('Aucun montant n\'a été enregistré'), findsOneWidget);
    await fin(tester);
  });

  testWidgets('montant différent confirmé par l\'opérateur : le locataire est prévenu qu\'une vérification a lieu', (tester) async {
    final s = Serveur(etats: [(status: 200, corps: _transaction('ANOMALIE'))]);
    await ouvrir(tester, s);
    await saisir(tester);
    await appuyer(tester);
    await tester.pump(const Duration(seconds: 1));
    await laisserPasser(tester);
    expect(find.byKey(const Key('paiement-echoue')), findsOneWidget);
    expect(find.textContaining('diffère de celui demandé'), findsOneWidget);
    await fin(tester);
  });

  testWidgets('sans réponse au bout du délai : message rassurant et possibilité de continuer à attendre', (tester) async {
    final s = Serveur(etats: [(status: 200, corps: _transaction('EN_ATTENTE'))]);
    await ouvrir(tester, s, intervalle: const Duration(seconds: 1), delaiMax: const Duration(seconds: 3));
    await saisir(tester);
    await appuyer(tester);
    for (var i = 0; i < 6; i++) {
      await tester.pump(const Duration(seconds: 1));
      await laisserPasser(tester);
    }
    expect(find.byKey(const Key('paiement-delai')), findsOneWidget);
    final suivisAvant = s.suivis;
    await tester.pump(const Duration(seconds: 3));
    expect(s.suivis, suivisAvant); // le suivi est arrêté, pas d'appels inutiles

    await tester.tap(find.byKey(const Key('continuer-suivi')));
    await tester.pump(const Duration(seconds: 1));
    await laisserPasser(tester);
    expect(s.suivis, greaterThan(suivisAvant));
    await fin(tester);
  });

  testWidgets('coupure réseau pendant la demande : le renvoi réutilise la MÊME clé ; une demande modifiée en reçoit une nouvelle', (tester) async {
    var essai = 0;
    final s = Serveur(creation: (o) => ++essai == 1 ? (status: -1, corps: null) : (status: 201, corps: _transaction('EN_ATTENTE')), etats: [(status: 200, corps: _transaction('EN_ATTENTE'))]);
    await ouvrir(tester, s);
    await saisir(tester);
    await appuyer(tester);
    expect(find.byKey(const Key('erreur-paiement')), findsOneWidget);
    expect(find.textContaining('Impossible de joindre le serveur'), findsOneWidget);

    await appuyer(tester); // « Payer » à nouveau, mêmes données
    expect(s.posts, hasLength(2));
    expect(s.posts[1].headers['Idempotency-Key'], s.posts[0].headers['Idempotency-Key']);
    await fin(tester);

    // autre demande (montant modifié) : nouvelle clé
    final s2 = Serveur(creation: (o) => (status: -1, corps: null));
    await ouvrir(tester, s2);
    await saisir(tester);
    await appuyer(tester);
    await tester.enterText(find.byKey(const Key('champ-montant-paiement')), '5000');
    await appuyer(tester);
    expect(s2.posts, hasLength(2));
    expect(s2.posts[1].headers['Idempotency-Key'], isNot(s2.posts[0].headers['Idempotency-Key']));
    await fin(tester);
  });

  testWidgets('refus du serveur (paiement en ligne indisponible) : message clair, formulaire conservé', (tester) async {
    final s = Serveur(creation: (o) => (status: 409, corps: {'code': 'PAIEMENT_EN_LIGNE_INDISPONIBLE'}));
    await ouvrir(tester, s);
    await saisir(tester);
    await appuyer(tester);
    expect(find.textContaining('pas disponible chez votre gestionnaire'), findsOneWidget);
    expect(find.byKey(const Key('confirmer-paiement')), findsOneWidget);
    expect(tester.widget<TextFormField>(find.byKey(const Key('champ-telephone-paiement'))).controller!.text, '+237 699 11 22 33');
    await fin(tester);
  });
}
