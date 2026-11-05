import 'dart:math';

final _aleatoire = Random.secure();

/// UUID v4 aléatoire (RFC 4122). Sert de clé d'idempotence : généré UNE fois à la saisie, réutilisé à chaque nouvel envoi.
String uuidV4() {
  final b = List<int>.generate(16, (_) => _aleatoire.nextInt(256));
  b[6] = (b[6] & 0x0f) | 0x40;
  b[8] = (b[8] & 0x3f) | 0x80;
  String h(int o, int n) => b.sublist(o, o + n).map((x) => x.toRadixString(16).padLeft(2, '0')).join();
  return '${h(0, 4)}-${h(4, 2)}-${h(6, 2)}-${h(8, 2)}-${h(10, 6)}';
}
