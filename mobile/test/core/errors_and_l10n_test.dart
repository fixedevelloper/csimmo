import 'dart:convert';
import 'dart:io';

import 'package:dio/dio.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/core/config/server_url.dart';
import 'package:immogest_mobile/core/error/app_error.dart';
import 'package:immogest_mobile/core/error/error_messages.dart';
import 'package:immogest_mobile/l10n/app_localizations.dart';

DioException _erreur({int? status, Object? corps, DioExceptionType type = DioExceptionType.badResponse}) {
  final requete = RequestOptions(path: '/x');
  return DioException(
    requestOptions: requete,
    type: type,
    response: status == null ? null : Response(requestOptions: requete, statusCode: status, data: corps),
  );
}

void main() {
  group('AppError.fromDio', () {
    test('lit le code métier du ProblemDetail', () {
      final e = AppError.fromDio(_erreur(status: 401, corps: {'code': 'IDENTIFIANTS_INVALIDES'}));
      expect((e.code, e.status), ('IDENTIFIANTS_INVALIDES', 401));
    });

    test('retombe sur HTTP_xxx sans code, et SERVEUR_INDISPONIBLE sans réponse', () {
      expect(AppError.fromDio(_erreur(status: 502, corps: 'bad gateway')).code, 'HTTP_502');
      expect(AppError.fromDio(_erreur(type: DioExceptionType.connectionError)).code, AppError.serveurIndisponible);
      expect(AppError.fromDio(_erreur(type: DioExceptionType.connectionTimeout)).code, AppError.serveurIndisponible);
    });
  });

  group('messages traduits', () {
    final codes = [
      'SERVEUR_INDISPONIBLE', 'SERVEUR_NON_IMMOGEST', 'IDENTIFIANTS_INVALIDES', 'COMPTE_VERROUILLE',
      'COMPTE_DESACTIVE', 'OTP_INVALIDE', 'ACCES_REFUSE', 'NON_AUTHENTIFIE', 'TOKEN_EXPIRE',
      'REFRESH_TOKEN_REUTILISE', 'LICENCE_EXPIREE',
    ];

    for (final locale in ['fr', 'en']) {
      test('chaque code connu a un message propre en $locale (jamais le message générique)', () async {
        final l = await AppLocalizations.delegate.load(Locale(locale));
        for (final code in codes) {
          expect(errorMessage(l, AppError(code)), isNot(l.errInconnue), reason: code);
        }
        expect(errorMessage(l, const AppError('CODE_QUI_N_EXISTE_PAS')), l.errInconnue);
        expect(errorMessage(l, StateError('boum')), l.errInconnue);
        expect(errorMessage(l, const ServerUrlException(ServerUrlError.httpNonLocal)), l.errUrlHttpRefusee);
        expect(errorMessage(l, const ServerUrlException(ServerUrlError.invalide)), l.errUrlInvalide);
      });
    }
  });

  test('les fichiers de traduction fr et en ont les mêmes clés', () {
    Set<String> cles(String fichier) => (jsonDecode(File(fichier).readAsStringSync()) as Map<String, dynamic>)
        .keys
        .where((k) => !k.startsWith('@'))
        .toSet();

    expect(cles('lib/l10n/app_en.arb'), cles('lib/l10n/app_fr.arb'));
  });
}
