import 'package:dio/dio.dart';

/// Erreur d'API normalisée : le `code` métier stable du ProblemDetail (ex. `IDENTIFIANTS_INVALIDES`).
class AppError implements Exception {
  const AppError(this.code, {this.status});

  final String code;
  final int? status;

  static const serveurIndisponible = 'SERVEUR_INDISPONIBLE';
  static const inconnue = 'INCONNUE';

  factory AppError.fromDio(DioException e) {
    final data = e.response?.data;
    final status = e.response?.statusCode;
    if (data is Map && data['code'] is String) return AppError(data['code'] as String, status: status);
    if (status != null) return AppError('HTTP_$status', status: status);
    return switch (e.type) {
      DioExceptionType.cancel => const AppError(inconnue),
      _ => const AppError(serveurIndisponible),
    };
  }

  @override
  String toString() => 'AppError($code, status: $status)';
}

/// Exécute [action] en convertissant les erreurs Dio en [AppError].
Future<T> guard<T>(Future<T> Function() action) async {
  try {
    return await action();
  } on DioException catch (e) {
    throw AppError.fromDio(e);
  }
}
