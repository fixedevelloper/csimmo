import 'dart:io';
import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/core/platform/plateforme.dart';
import 'package:immogest_mobile/features/locataire/accueil_screen.dart';
import 'package:immogest_mobile/features/locataire/baux_screen.dart';
import 'package:immogest_mobile/features/locataire/documents_screen.dart';
import 'package:immogest_mobile/features/locataire/locataire_shell.dart';
import 'package:immogest_mobile/features/locataire/signaler_screen.dart';

import '../support/fake_adapter.dart';
import '../support/locataire_support.dart';

Reponse Function(RequestOptions) serveur(Map<String, Reponse Function(RequestOptions)> routes) =>
    (o) => (routes[o.path] ?? (_) => (status: 404, corps: {'code': 'INTROUVABLE'}))(o);

const _compteDu = {'locataireId': 'l-1', 'locataireNom': 'Paul', 'arrieres': 320000, 'aVenir': 160000, 'credit': 0, 'solde': 320000, 'baux': []};
const _moi = {'id': 'u-1', 'nom': 'Paul Mbarga', 'role': 'LOCATAIRE'};

void main() {
  group('Accueil', () {
    testWidgets('affiche le solde dû, le détail et un bouton Payer actif', (tester) async {
      final (api, _) = apiFactice(serveur({
        '/api/v1/mon-compte': (_) => (status: 200, corps: _compteDu),
        '/api/v1/auth/moi': (_) => (status: 200, corps: _moi),
      }));
      await tester.pumpWidget(appDeTest(const AccueilScreen(), api: api));
      await tester.pumpAndSettle();

      expect(find.text('Bienvenue, Paul Mbarga'), findsOneWidget);
      expect(find.text('Solde à régler'), findsOneWidget);
      expect(tester.widget<Text>(find.byKey(const Key('solde'))).data, contains('320'));
      expect(tester.widget<Text>(find.byKey(const Key('solde'))).data, endsWith('FCFA'));
      expect(find.byKey(const Key('arrieres')), findsOneWidget);
      expect(tester.widget<FilledButton>(find.byKey(const Key('bouton-payer'))).onPressed, isNotNull);
    });

    testWidgets('à jour quand le solde est nul ou créditeur', (tester) async {
      final (api, _) = apiFactice(serveur({
        '/api/v1/mon-compte': (_) => (status: 200, corps: {..._compteDu, 'arrieres': 0, 'credit': 50000, 'solde': -50000}),
        '/api/v1/auth/moi': (_) => (status: 200, corps: _moi),
      }));
      await tester.pumpWidget(appDeTest(const AccueilScreen(), api: api));
      await tester.pumpAndSettle();
      expect(find.text('Vous êtes à jour'), findsOneWidget);
      expect(tester.widget<Text>(find.byKey(const Key('solde'))).data, contains('50'));
    });

    testWidgets('une erreur serveur s\'affiche avec un bouton Réessayer qui relance la requête', (tester) async {
      var appels = 0;
      final (api, _) = apiFactice(serveur({
        '/api/v1/mon-compte': (_) => ++appels == 1 ? (status: 404, corps: {'code': 'LOCATAIRE_INTROUVABLE'}) : (status: 200, corps: _compteDu),
        '/api/v1/auth/moi': (_) => (status: 200, corps: _moi),
      }));
      await tester.pumpWidget(appDeTest(const AccueilScreen(), api: api));
      await tester.pumpAndSettle();
      expect(find.textContaining('Aucune fiche locataire'), findsOneWidget);
      await tester.tap(find.text('Réessayer'));
      await tester.pumpAndSettle();
      expect(find.byKey(const Key('solde')), findsOneWidget);
      expect(appels, 2);
    });
  });

  group('Baux et échéances', () {
    final baux = {
      '/api/v1/baux': (RequestOptions _) => (status: 200, corps: page([
            {'id': 'b-1', 'numero': 'BAIL-2026-0001', 'immeubleNom': 'Palmiers', 'lotReference': 'A1', 'statut': 'ACTIF', 'loyer': 150000, 'charges': 10000, 'caution': 300000, 'dateDebut': '2026-01-01', 'dateFin': '2026-12-31'},
          ])),
      '/api/v1/echeances': (RequestOptions o) => (status: 200, corps: page([
            {'id': 'e-1', 'bailId': o.queryParameters['bailId'], 'periodeDebut': '2026-09-01', 'periodeFin': '2026-09-30', 'dateEcheance': '2026-09-01', 'montant': 160000, 'montantPaye': 160000, 'reste': 0, 'statut': 'PAYEE'},
            {'id': 'e-2', 'periodeDebut': '2026-10-01', 'periodeFin': '2026-10-31', 'dateEcheance': '2026-10-01', 'montant': 160000, 'montantPaye': 60000, 'reste': 100000, 'statut': 'EN_RETARD'},
            {'id': 'e-3', 'periodeDebut': '2026-11-01', 'periodeFin': '2026-11-30', 'dateEcheance': '2026-11-01', 'montant': 160000, 'montantPaye': 0, 'reste': 160000, 'statut': 'ANNULEE'},
          ])),
    };

    testWidgets('liste les baux puis ouvre le détail avec ses échéances et leur statut', (tester) async {
      final (api, adaptateur) = apiFactice(serveur(baux));
      await tester.pumpWidget(appDeTest(const BauxScreen(), api: api));
      await tester.pumpAndSettle();
      expect(find.text('Palmiers — A1'), findsOneWidget);
      expect(find.textContaining('En cours'), findsOneWidget);

      await tester.tap(find.byKey(const Key('bail-b-1')));
      await tester.pumpAndSettle();
      expect(find.text('BAIL-2026-0001'), findsOneWidget);
      expect(find.text('Payée'), findsOneWidget);
      expect(find.text('En retard'), findsOneWidget);
      expect(find.byKey(const Key('echeance-e-3')), findsNothing); // annulée : masquée
      expect(find.textContaining('reste 100'), findsOneWidget); // partiellement payée : le reste est affiché
      expect(adaptateur.requetes.where((o) => o.path == '/api/v1/echeances').single.queryParameters['bailId'], 'b-1');
    });

    testWidgets('aucun bail', (tester) async {
      final (api, _) = apiFactice(serveur({'/api/v1/baux': (_) => (status: 200, corps: page([]))}));
      await tester.pumpWidget(appDeTest(const BauxScreen(), api: api));
      await tester.pumpAndSettle();
      expect(find.byKey(const Key('aucun-bail')), findsOneWidget);
    });
  });

  group('Quittances', () {
    final docs = {
      '/api/v1/documents-paiement': (RequestOptions _) => (status: 200, corps: page([
            {'id': 'd-1', 'type': 'QUITTANCE', 'numero': 'QUI-2026-000001', 'montant': 160000, 'periodeDebut': '2026-09-01', 'periodeFin': '2026-09-30', 'fichierId': 'f-1'},
            {'id': 'd-2', 'type': 'RECU', 'numero': 'REC-2026-000001', 'montant': 60000, 'dateEmission': '2026-10-05', 'fichierId': 'f-2'},
          ])),
    };

    testWidgets('un appui télécharge le PDF avec le jeton puis l\'ouvre', (tester) async {
      final fichiers = FauxFichiers();
      final (api, adaptateur) = apiFactice(serveur({...docs, '/api/v1/fichiers/f-1': (_) => (status: 200, corps: Uint8List.fromList([37, 80, 68, 70, 1]))}));
      await tester.pumpWidget(appDeTest(const DocumentsScreen(), api: api, fichiers: fichiers));
      await tester.pumpAndSettle();
      expect(find.text('Quittance QUI-2026-000001'), findsOneWidget);
      expect(find.text('Reçu REC-2026-000001'), findsOneWidget);

      await tester.tap(find.byKey(const Key('document-d-1')));
      await tester.pumpAndSettle();
      expect(adaptateur.appels('/api/v1/fichiers/f-1'), 1);
      expect(fichiers.ecrits.single.$1, 'QUI-2026-000001.pdf');
      expect(fichiers.ouverts.single, '/tmp/immogest/QUI-2026-000001.pdf');
    });

    testWidgets('un téléchargement refusé affiche un message sans rien ouvrir', (tester) async {
      final fichiers = FauxFichiers();
      final (api, _) = apiFactice(serveur({...docs, '/api/v1/fichiers/f-1': (_) => (status: 403, corps: {'code': 'FICHIER_ACCES_REFUSE'})}));
      await tester.pumpWidget(appDeTest(const DocumentsScreen(), api: api, fichiers: fichiers));
      await tester.pumpAndSettle();
      await tester.tap(find.byKey(const Key('document-d-1')));
      await tester.pumpAndSettle();
      expect(fichiers.ouverts, isEmpty);
      expect(find.byType(SnackBar), findsOneWidget);
    });
  });

  group('Signaler une panne', () {
    Map<String, Reponse Function(RequestOptions)> routes({Reponse Function(RequestOptions)? creation}) => {
          '/api/v1/signalements': (o) => o.method == 'POST'
              ? (creation ?? (_) => (status: 201, corps: {'id': 's-9', 'statut': 'NOUVEAU'}))(o)
              : (status: 200, corps: page([
                  {'id': 's-1', 'description': 'Prise cassée', 'statut': 'EN_COURS', 'creeLe': '2026-10-10T10:00:00Z', 'noteGestion': 'Électricien prévenu'},
                ])),
        };

    testWidgets('refuse une description trop courte sans appeler le serveur', (tester) async {
      final (api, adaptateur) = apiFactice(serveur(routes()));
      await tester.pumpWidget(appDeTest(const SignalerScreen(), api: api));
      await tester.pumpAndSettle();
      await tester.enterText(find.byKey(const Key('champ-description')), 'ab');
      await tester.ensureVisible(find.byKey(const Key('envoyer-signalement')));
      await tester.tap(find.byKey(const Key('envoyer-signalement')));
      await tester.pumpAndSettle();
      expect(find.textContaining('5 caractères minimum'), findsOneWidget);
      expect(adaptateur.requetes.where((o) => o.method == 'POST'), isEmpty);
    });

    testWidgets('envoie la panne avec la photo choisie, vide le formulaire et actualise la liste', (tester) async {
      final photo = await tester.runAsync(() => File('${Directory.systemTemp.path}/photo_panne_test.png').writeAsBytes([1, 2, 3]));
      addTearDown(() => photo!.deleteSync());
      final selecteur = FauxSelecteur(PhotoChoisie(photo!.path, 'fuite.png'));
      final (api, adaptateur) = apiFactice(serveur(routes()));
      await tester.pumpWidget(appDeTest(const SignalerScreen(), api: api, selecteur: selecteur));
      await tester.pumpAndSettle();
      expect(find.text('Prise cassée'), findsOneWidget);
      expect(find.text('En cours'), findsOneWidget);
      expect(find.textContaining('Électricien prévenu'), findsOneWidget);

      await tester.enterText(find.byKey(const Key('champ-description')), 'Fuite sous l\'évier');
      await tester.tap(find.byKey(const Key('photo-galerie')));
      await tester.pumpAndSettle();
      expect(selecteur.derniereSource, SourcePhoto.galerie);
      expect(find.byKey(const Key('photo-choisie')), findsOneWidget);
      await tester.ensureVisible(find.byKey(const Key('envoyer-signalement')));
      await tester.runAsync(() async {
        await tester.tap(find.byKey(const Key('envoyer-signalement')));
        await Future<void>.delayed(const Duration(milliseconds: 300));
      });
      await tester.pumpAndSettle();

      final envoi = adaptateur.requetes.singleWhere((o) => o.method == 'POST');
      final formulaire = envoi.data as FormData;
      expect(Map.fromEntries(formulaire.fields)['description'], 'Fuite sous l\'évier');
      expect(formulaire.files.single.value.filename, 'fuite.png');
      expect(find.byKey(const Key('photo-choisie')), findsNothing); // photo retirée après l'envoi
    });

    testWidgets('une photo illisible donne un message d\'erreur et ne perd pas le texte', (tester) async {
      final selecteur = FauxSelecteur(const PhotoChoisie('/chemin/introuvable.png', 'fantome.png'));
      final (api, adaptateur) = apiFactice(serveur(routes()));
      await tester.pumpWidget(appDeTest(const SignalerScreen(), api: api, selecteur: selecteur));
      await tester.pumpAndSettle();
      await tester.enterText(find.byKey(const Key('champ-description')), 'Texte à conserver');
      await tester.tap(find.byKey(const Key('photo-camera')));
      await tester.pumpAndSettle();
      await tester.ensureVisible(find.byKey(const Key('envoyer-signalement')));
      await tester.runAsync(() async {
        await tester.tap(find.byKey(const Key('envoyer-signalement')));
        await Future<void>.delayed(const Duration(milliseconds: 100));
      });
      await tester.pumpAndSettle();
      expect(adaptateur.requetes.where((o) => o.method == 'POST'), isEmpty);
      expect(find.byKey(const Key('erreur-signalement')), findsOneWidget);
      expect(tester.widget<TextFormField>(find.byKey(const Key('champ-description'))).controller!.text, 'Texte à conserver');
    });

    testWidgets('sans photo : envoi, confirmation et nouvelle lecture de la liste', (tester) async {
      final (api, adaptateur) = apiFactice(serveur(routes()));
      await tester.pumpWidget(appDeTest(const SignalerScreen(), api: api));
      await tester.pumpAndSettle();
      await tester.enterText(find.byKey(const Key('champ-description')), 'Porte qui ne ferme plus');
      await tester.ensureVisible(find.byKey(const Key('envoyer-signalement')));
      await tester.tap(find.byKey(const Key('envoyer-signalement')));
      await tester.pumpAndSettle();

      final envoi = adaptateur.requetes.singleWhere((o) => o.method == 'POST');
      expect(Map.fromEntries((envoi.data as FormData).fields)['description'], 'Porte qui ne ferme plus');
      expect(find.text('Signalement envoyé. Votre gestionnaire va le traiter.'), findsOneWidget);
      expect(tester.widget<TextFormField>(find.byKey(const Key('champ-description'))).controller!.text, isEmpty);
      expect(adaptateur.requetes.where((o) => o.method == 'GET' && o.path == '/api/v1/signalements').length, 2); // liste relue
    });

    testWidgets('une erreur du serveur est affichée et le texte saisi est conservé', (tester) async {
      final (api, _) = apiFactice(serveur(routes(creation: (_) => (status: 429, corps: {'code': 'SIGNALEMENT_LIMITE'}))));
      await tester.pumpWidget(appDeTest(const SignalerScreen(), api: api));
      await tester.pumpAndSettle();
      await tester.enterText(find.byKey(const Key('champ-description')), 'Encore une panne');
      await tester.ensureVisible(find.byKey(const Key('envoyer-signalement')));
      await tester.tap(find.byKey(const Key('envoyer-signalement')));
      await tester.pumpAndSettle();
      expect(find.byKey(const Key('erreur-signalement')), findsOneWidget);
      expect(find.textContaining('Trop de signalements'), findsOneWidget);
      expect(tester.widget<TextFormField>(find.byKey(const Key('champ-description'))).controller!.text, 'Encore une panne');
    });
  });

  testWidgets('la coque du locataire navigue entre ses quatre onglets', (tester) async {
    final (api, _) = apiFactice(serveur({
      '/api/v1/mon-compte': (_) => (status: 200, corps: _compteDu),
      '/api/v1/auth/moi': (_) => (status: 200, corps: _moi),
      '/api/v1/baux': (_) => (status: 200, corps: page([])),
      '/api/v1/documents-paiement': (_) => (status: 200, corps: page([])),
      '/api/v1/signalements': (_) => (status: 200, corps: page([])),
    }));
    await tester.pumpWidget(appDeTest(const LocataireShell(), api: api));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('titre-LOCATAIRE')), findsOneWidget);
    expect(find.text('Mon espace locataire'), findsOneWidget);
    await tester.tap(find.byKey(const Key('onglet-baux')));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('aucun-bail')), findsOneWidget);
    await tester.tap(find.byKey(const Key('onglet-documents')));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('aucun-document')), findsOneWidget);
    await tester.tap(find.byKey(const Key('onglet-signaler')));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('aucun-signalement')), findsOneWidget);
  });
}
