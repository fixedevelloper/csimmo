import 'dart:convert';

/// Faux jeton d'accès (signature bidon) portant [charge], pour les tests qui lisent « sub ».
String jeton(Map<String, Object?> charge) {
  String b64(Object o) => base64Url.encode(utf8.encode(jsonEncode(o))).replaceAll('=', '');
  return '${b64({'alg': 'HS256'})}.${b64(charge)}.signature';
}
