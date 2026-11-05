import 'dart:convert';

/// Identifiant du compte (« sub ») lu dans un jeton d'accès, SANS vérifier la signature : sert uniquement à rattacher des saisies
/// locales à un compte. Le serveur, lui, vérifie toujours le jeton. `null` si le jeton n'a pas la forme attendue.
String? sujetDuJwt(String jeton) {
  final parties = jeton.split('.');
  if (parties.length != 3) return null;
  try {
    final charge = utf8.decode(base64Url.decode(base64Url.normalize(parties[1])));
    final sub = (jsonDecode(charge) as Map<String, dynamic>)['sub'];
    return sub is String && sub.isNotEmpty ? sub : null;
  } on FormatException {
    return null;
  } on TypeError {
    return null;
  }
}
