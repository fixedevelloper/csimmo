import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_api/immogest_api.dart';
import 'package:immogest_mobile/core/auth/role.dart';
import 'package:immogest_mobile/core/auth/token_store.dart';
import 'package:immogest_mobile/core/network/auth_interceptor.dart';
import 'package:immogest_mobile/core/storage/key_value_store.dart';

import '../support/fake_adapter.dart';

const _ressource = '/api/v1/auth/moi';
const _refresh = '/api/v1/auth/refresh';

void main() {
  late TokenStore tokens;
  late FakeAdapter serveur;
  late Dio dio;
  late int sessionsPerdues;

  /// Serveur simulé : seul le jeton `acces-N` courant est accepté ; le refresh fait tourner N.
  late int generation;
  late bool refreshRefuse;
  late bool reseauCoupePourRefresh;

  Future<Reponse> traiter(RequestOptions o) async {
    if (o.path == _refresh) {
      if (reseauCoupePourRefresh) return (status: -1, corps: null);
      if (refreshRefuse) return (status: 401, corps: {'code': 'REFRESH_TOKEN_REUTILISE'});
      generation++;
      return (
        status: 200,
        corps: {'accessToken': 'acces-$generation', 'refreshToken': 'refresh-$generation', 'role': 'LOCATAIRE'},
      );
    }
    if (o.headers['Authorization'] == 'Bearer acces-$generation') {
      return (status: 200, corps: {'id': 'u', 'nom': 'Test'});
    }
    return (status: 401, corps: {'code': 'TOKEN_EXPIRE'});
  }

  setUp(() async {
    generation = 1;
    refreshRefuse = false;
    reseauCoupePourRefresh = false;
    sessionsPerdues = 0;
    tokens = TokenStore(MemoryKeyValueStore());
    await tokens.save(const Tokens(access: 'acces-0', refresh: 'refresh-0', role: Role.locataire));

    serveur = FakeAdapter(traiter);
    dio = Dio(BaseOptions(baseUrl: 'http://serveur'))..httpClientAdapter = serveur;
    final sansAuth = Dio(BaseOptions(baseUrl: 'http://serveur'))..httpClientAdapter = serveur;
    dio.interceptors.add(AuthInterceptor(
      dio: dio,
      tokens: tokens,
      refreshApi: ImmogestApi(dio: sansAuth, interceptors: const []).getAuthControllerApi(),
      onSessionLost: () async => sessionsPerdues++,
    ));
  });

  test('ajoute le jeton d\'accès aux requêtes', () async {
    await tokens.save(Tokens(access: 'acces-$generation', refresh: 'r', role: Role.locataire));
    final reponse = await dio.get<dynamic>(_ressource);

    expect(reponse.statusCode, 200);
    expect(serveur.requetes.single.headers['Authorization'], 'Bearer acces-1');
  });

  test('sur 401 : renouvelle le jeton, rejoue la requête et mémorise la nouvelle session', () async {
    final reponse = await dio.get<dynamic>(_ressource); // acces-0 est périmé

    expect(reponse.statusCode, 200);
    expect(serveur.appels(_refresh), 1);
    final apres = await tokens.read();
    expect((apres!.access, apres.refresh), ('acces-2', 'refresh-2'));
    expect(sessionsPerdues, 0);
  });

  test('des requêtes simultanées en 401 ne déclenchent qu\'UN seul rafraîchissement', () async {
    final reponses = await Future.wait([
      dio.get<dynamic>(_ressource),
      dio.get<dynamic>(_ressource),
      dio.get<dynamic>(_ressource),
    ]);

    expect(reponses.map((r) => r.statusCode), [200, 200, 200]);
    expect(serveur.appels(_refresh), 1, reason: 'un rejeu du refresh token révoquerait toute la session');
  });

  test('une requête partie avec un ancien jeton est rejouée avec le jeton déjà renouvelé', () async {
    await dio.get<dynamic>(_ressource); // renouvelle : acces-2 / refresh-2
    expect(serveur.appels(_refresh), 1);

    // Requête lente, partie avant le renouvellement avec l'ancien jeton : elle échoue en 401
    final ancienne = RequestOptions(path: _ressource, baseUrl: 'http://serveur')
      ..headers['Authorization'] = 'Bearer acces-0'
      ..extra['immogest.token'] = 'acces-0';
    final reponse = await dio.fetch<dynamic>(ancienne);

    expect(reponse.statusCode, 200);
    expect(serveur.appels(_refresh), 1, reason: 'pas de second rafraîchissement');
  });

  test('refresh refusé : la session est perdue et l\'erreur 401 remonte', () async {
    refreshRefuse = true;

    await expectLater(
      dio.get<dynamic>(_ressource),
      throwsA(isA<DioException>().having((e) => e.response?.statusCode, 'statut', 401)),
    );
    expect(sessionsPerdues, 1);
    expect(await tokens.read(), isNull);
  });

  test('réseau coupé pendant le rafraîchissement : la session est conservée', () async {
    reseauCoupePourRefresh = true;

    await expectLater(dio.get<dynamic>(_ressource), throwsA(isA<DioException>()));
    expect(sessionsPerdues, 0);
    expect(await tokens.read(), isNotNull);
  });

  test('un 401 sur les routes publiques (login) n\'est jamais « rafraîchi »', () async {
    final adaptateur = FakeAdapter((o) => (status: 401, corps: {'code': 'IDENTIFIANTS_INVALIDES'}));
    dio.httpClientAdapter = adaptateur;

    await expectLater(dio.post<dynamic>('/api/v1/auth/login', data: {}), throwsA(isA<DioException>()));
    expect(adaptateur.appels(_refresh), 0);
    expect(adaptateur.requetes.single.headers.containsKey('Authorization'), isFalse);
  });

  test('une requête rejouée qui échoue encore en 401 ne boucle pas', () async {
    final adaptateur = FakeAdapter((o) async {
      if (o.path == _refresh) {
        return (status: 200, corps: {'accessToken': 'x', 'refreshToken': 'y', 'role': 'LOCATAIRE'});
      }
      return (status: 401, corps: {'code': 'TOKEN_EXPIRE'});
    });
    dio.httpClientAdapter = adaptateur;
    final sansAuth = Dio(BaseOptions(baseUrl: 'http://serveur'))..httpClientAdapter = adaptateur;
    dio.interceptors
      ..clear()
      ..add(AuthInterceptor(
        dio: dio,
        tokens: tokens,
        refreshApi: ImmogestApi(dio: sansAuth, interceptors: const []).getAuthControllerApi(),
        onSessionLost: () async {},
      ));

    await expectLater(dio.get<dynamic>(_ressource), throwsA(isA<DioException>()));
    expect(adaptateur.appels(_refresh), 1);
    expect(adaptateur.appels(_ressource), 2, reason: 'un essai + un seul rejeu');
  });

  test('sans session : un 401 est renvoyé tel quel', () async {
    await tokens.clear();
    await expectLater(dio.get<dynamic>(_ressource), throwsA(isA<DioException>()));
    expect(serveur.appels(_refresh), 0);
  });
}
