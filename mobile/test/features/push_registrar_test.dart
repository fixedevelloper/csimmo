import 'dart:async';
import 'dart:convert';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_api/immogest_api.dart';
import 'package:immogest_mobile/core/auth/role.dart';
import 'package:immogest_mobile/core/auth/token_store.dart';
import 'package:immogest_mobile/core/providers.dart';
import 'package:immogest_mobile/features/push/push_registrar.dart';
import 'package:immogest_mobile/features/push/push_service.dart';

import '../support/fake_adapter.dart';
import '../support/locataire_support.dart';

class FauxPush implements PushService {
  FauxPush({this.autorise = true, this.jetonInitial = 'jeton-1'});

  bool autorise;
  String? jetonInitial;
  PushMobile? configRecue;
  int demarrages = 0;
  final renouvellements = StreamController<String>.broadcast();
  final premierPlan = StreamController<PushMessage>.broadcast();

  @override
  Future<bool> demarrer(PushMobile config) async {
    demarrages++;
    configRecue = config;
    return autorise;
  }

  @override
  Future<String?> jeton() async => jetonInitial;

  @override
  Stream<String> get jetonRenouvele => renouvellements.stream;

  @override
  Stream<PushMessage> get messages => premierPlan.stream;
}

const _push = {'push': {'projectId': 'mon-projet', 'apiKey': 'AIza-public', 'appId': '1:123:android:abc', 'messagingSenderId': '123'}};

void main() {
  late FauxPush faux;

  ProviderContainer conteneur(FutureOr<Reponse> Function(dynamic) handler, {bool session = false}) {
    final (api, _) = apiFactice((o) => handler(o));
    final c = ProviderContainer(overrides: [
      apiProvider.overrideWithValue(api),
      pushServiceProvider.overrideWithValue(faux),
      if (session) bootProvider.overrideWithValue(const BootState(serverUrl: 'http://x', tokens: Tokens(access: 'a', refresh: 'r', role: Role.locataire))),
    ]);
    addTearDown(c.dispose);
    return c;
  }

  setUp(() => faux = FauxPush());

  test('enregistre l\'appareil avec les identifiants Firebase reçus du serveur', () async {
    final requetes = <dynamic>[];
    final c = conteneur((o) {
      requetes.add(o);
      return o.path == '/api/v1/notifications/mobile-config' ? (status: 200, corps: _push) : (status: 204, corps: null);
    });
    await c.read(pushRegistrarProvider).enregistrer();

    expect(faux.configRecue!.projectId, 'mon-projet');
    expect(faux.configRecue!.apiKey, 'AIza-public');
    final enregistrement = requetes.singleWhere((o) => o.path == '/api/v1/notifications/appareils');
    expect(enregistrement.method, 'POST');
    expect(jsonDecode(enregistrement.data as String), {'token': 'jeton-1', 'plateforme': 'ANDROID'});
  });

  test('push non configuré chez le client : Firebase n\'est jamais initialisé', () async {
    final c = conteneur((o) => (status: 200, corps: <String, Object?>{'push': null}));
    await c.read(pushRegistrarProvider).enregistrer();
    expect(faux.demarrages, 0);
  });

  test('autorisation refusée par l\'utilisateur : aucun appareil enregistré', () async {
    faux.autorise = false;
    final requetes = <String>[];
    final c = conteneur((o) {
      requetes.add(o.path as String);
      return (status: 200, corps: _push);
    });
    await c.read(pushRegistrarProvider).enregistrer();
    expect(faux.demarrages, 1);
    expect(requetes, isNot(contains('/api/v1/notifications/appareils')));
  });

  test('toute erreur est absorbée : le push est facultatif', () async {
    final c = conteneur((o) => (status: 500, corps: {'code': 'ERREUR'}));
    await expectLater(c.read(pushRegistrarProvider).enregistrer(), completes);
    final c2 = conteneur((o) => (status: -1, corps: null));
    await expectLater(c2.read(pushRegistrarProvider).enregistrer(), completes);
  });

  test('un jeton renouvelé est ré-enregistré, et les messages du premier plan sont relayés', () async {
    final enregistres = <Object?>[];
    final c = conteneur((o) {
      if (o.path == '/api/v1/notifications/appareils') enregistres.add(o.data);
      return o.path == '/api/v1/notifications/mobile-config' ? (status: 200, corps: _push) : (status: 204, corps: null);
    });
    final registrar = c.read(pushRegistrarProvider);
    final recus = <PushMessage>[];
    registrar.messages.listen(recus.add);
    await registrar.enregistrer();
    faux.renouvellements.add('jeton-2');
    faux.premierPlan.add(const PushMessage(titre: 'Quittance', corps: 'Disponible'));
    await Future<void>.delayed(const Duration(milliseconds: 50));

    expect(enregistres.map((d) => (jsonDecode(d! as String) as Map)['token']), ['jeton-1', 'jeton-2']);
    expect(recus.single.titre, 'Quittance');
    // un second appel ne double pas les abonnements
    await registrar.enregistrer();
    faux.renouvellements.add('jeton-3');
    await Future<void>.delayed(const Duration(milliseconds: 50));
    expect(enregistres.where((d) => (jsonDecode(d! as String) as Map)['token'] == 'jeton-3'), hasLength(1));
  });

  test('retirer supprime l\'appareil côté serveur une seule fois (puis plus rien à retirer)', () async {
    final requetes = <dynamic>[];
    final c = conteneur((o) {
      requetes.add(o);
      return o.path == '/api/v1/notifications/mobile-config' ? (status: 200, corps: _push) : (status: 204, corps: null);
    });
    final registrar = c.read(pushRegistrarProvider);
    await registrar.retirer(); // rien d'enregistré : aucun appel
    expect(requetes.where((o) => o.method == 'DELETE'), isEmpty);

    await registrar.enregistrer();
    await registrar.retirer();
    await registrar.retirer();
    final suppression = requetes.where((o) => o.method == 'DELETE').toList();
    expect(suppression, hasLength(1));
    expect(suppression.single.queryParameters['token'], 'jeton-1');
  });

  test('retirer ne lève jamais, même si le serveur est injoignable (la déconnexion doit aboutir)', () async {
    var coupe = false;
    final c = conteneur((o) => coupe ? (status: -1, corps: null) : o.path == '/api/v1/notifications/mobile-config' ? (status: 200, corps: _push) : (status: 204, corps: null));
    final registrar = c.read(pushRegistrarProvider);
    await registrar.enregistrer();
    coupe = true;
    await expectLater(registrar.retirer(), completes);
  });

  test('avec une session déjà ouverte au démarrage, l\'enregistrement part tout seul', () async {
    final appareils = <Object?>[];
    final c = conteneur((o) {
      if (o.path == '/api/v1/notifications/appareils') appareils.add(o.data);
      return o.path == '/api/v1/notifications/mobile-config' ? (status: 200, corps: _push) : (status: 204, corps: null);
    }, session: true);
    c.read(pushRegistrarProvider); // créé par l'application au démarrage
    await Future<void>.delayed(const Duration(milliseconds: 50));
    expect(appareils, hasLength(1));
  });

  test('sans session, rien n\'est tenté', () async {
    final requetes = <String>[];
    final c = conteneur((o) {
      requetes.add(o.path as String);
      return (status: 200, corps: _push);
    });
    c.read(pushRegistrarProvider);
    await Future<void>.delayed(const Duration(milliseconds: 50));
    expect(requetes, isEmpty);
  });
}
