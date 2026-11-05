import 'dart:io';
import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_api/immogest_api.dart';
import 'package:immogest_mobile/core/error/app_error.dart';
import 'package:immogest_mobile/core/platform/plateforme.dart';
import 'package:immogest_mobile/features/locataire/locataire_repository.dart';

import '../support/locataire_support.dart';

void main() {
  late FauxFichiers fichiers;

  LocataireRepository depot(ImmogestApi api) => LocataireRepository(api, fichiers);

  setUp(() => fichiers = FauxFichiers());

  test('monCompte lit le solde du locataire connecté', () async {
    final (api, adaptateur) = apiFactice((o) => (status: 200, corps: {'locataireId': 'l-1', 'locataireNom': 'Paul', 'arrieres': 160000, 'aVenir': 160000, 'credit': 0, 'solde': 160000, 'baux': []}));
    final c = await depot(api).monCompte();
    expect(c.solde, 160000);
    expect(c.arrieres, 160000);
    expect(adaptateur.requetes.single.path, '/api/v1/mon-compte');
  });

  test('les baux en brouillon sont masqués', () async {
    final (api, _) = apiFactice((o) => (status: 200, corps: page([
          {'id': 'b-1', 'numero': 'BAIL-1', 'statut': 'ACTIF'},
          {'id': 'b-2', 'numero': 'BAIL-2', 'statut': 'BROUILLON'},
          {'id': 'b-3', 'numero': 'BAIL-3', 'statut': 'TERMINE'},
        ])));
    final baux = await depot(api).baux();
    expect(baux.map((b) => b.numero), ['BAIL-1', 'BAIL-3']);
  });

  test('les échéances annulées sont masquées et le bail est filtré côté serveur', () async {
    final (api, adaptateur) = apiFactice((o) => (status: 200, corps: page([
          {'id': 'e-1', 'statut': 'PAYEE', 'montant': 100},
          {'id': 'e-2', 'statut': 'ANNULEE', 'montant': 100},
          {'id': 'e-3', 'statut': 'EN_RETARD', 'montant': 100},
        ])));
    final liste = await depot(api).echeances('b-9');
    expect(liste.map((e) => e.id), ['e-1', 'e-3']);
    expect(adaptateur.requetes.single.queryParameters['bailId'], 'b-9');
  });

  test('le signalement part en multipart avec description nettoyée, bail et photo', () async {
    final tmp = await File('${Directory.systemTemp.path}/panne_test.png').writeAsBytes(Uint8List.fromList([1, 2, 3]));
    addTearDown(tmp.delete);
    final (api, adaptateur) = apiFactice((o) => (status: 201, corps: {'id': 's-1', 'statut': 'NOUVEAU', 'description': 'Fuite'}));
    final s = await depot(api).signalerPanne(description: '  Fuite sous l\'évier  ', bailId: 'b-1', photo: PhotoChoisie(tmp.path, 'fuite.png'));
    expect(s.statut, SignalementDtoStatutEnum.NOUVEAU);
    final requete = adaptateur.requetes.single;
    expect(requete.method, 'POST');
    expect(requete.path, '/api/v1/signalements');
    final formulaire = requete.data as FormData;
    expect(Map.fromEntries(formulaire.fields)['description'], 'Fuite sous l\'évier');
    expect(Map.fromEntries(formulaire.fields)['bailId'], 'b-1');
    expect(formulaire.files.single.key, 'photo');
    expect(formulaire.files.single.value.filename, 'fuite.png');
  });

  test('signalement sans photo : aucune partie fichier', () async {
    final (api, adaptateur) = apiFactice((o) => (status: 201, corps: {'id': 's-1'}));
    await depot(api).signalerPanne(description: 'Porte cassée');
    expect((adaptateur.requetes.single.data as FormData).files, isEmpty);
  });

  test('une erreur métier du serveur devient une AppError avec son code', () async {
    final (api, _) = apiFactice((o) => (status: 429, corps: {'code': 'SIGNALEMENT_LIMITE', 'detail': 'trop'}));
    await expectLater(
      depot(api).signalerPanne(description: 'Encore une panne'),
      throwsA(isA<AppError>().having((e) => e.code, 'code', 'SIGNALEMENT_LIMITE').having((e) => e.status, 'status', 429)),
    );
  });

  test('un réseau coupé devient SERVEUR_INDISPONIBLE', () async {
    final (api, _) = apiFactice((o) => (status: -1, corps: null));
    await expectLater(depot(api).monCompte(), throwsA(isA<AppError>().having((e) => e.code, 'code', AppError.serveurIndisponible)));
  });

  test('le téléchargement écrit le PDF sous un nom sûr et renvoie son chemin', () async {
    final pdf = Uint8List.fromList([0x25, 0x50, 0x44, 0x46, 1, 2, 3]);
    final (api, adaptateur) = apiFactice((o) => (status: 200, corps: pdf));
    final chemin = await depot(api).telechargerDocument('f-1', '../QUI-2026-000001.pdf');
    expect(adaptateur.requetes.single.path, '/api/v1/fichiers/f-1');
    expect(fichiers.ecrits.single, ('../QUI-2026-000001.pdf', 7));
    expect(chemin, '/tmp/immogest/._QUI-2026-000001.pdf');
    expect(chemin.contains('..'), isFalse);
  });

  test('un fichier vide est une erreur', () async {
    final (api, _) = apiFactice((o) => (status: 200, corps: Uint8List(0)));
    await expectLater(depot(api).telechargerDocument('f-1', 'x.pdf'), throwsA(isA<AppError>()));
  });

  test('nomSur supprime tout ce qui pourrait sortir du dossier', () {
    expect(nomSur('QUI-2026-000001.pdf'), 'QUI-2026-000001.pdf');
    expect(nomSur('../../etc/passwd'), '._._etc_passwd');
    expect(nomSur('a b/c\\d.pdf'), 'a_b_c_d.pdf');
    expect(nomSur(''), 'document.pdf');
    expect(nomSur('..'), 'document.pdf');
    expect(nomSur('..').contains('..'), isFalse);
  });
}
