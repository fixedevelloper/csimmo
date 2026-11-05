import '../storage/key_value_store.dart';
import 'role.dart';

/// Jetons de la session courante.
class Tokens {
  const Tokens({required this.access, required this.refresh, required this.role});

  final String access;
  final String refresh;
  final Role role;
}

/// Persiste les jetons dans le stockage sécurisé (Android Keystore).
class TokenStore {
  TokenStore(this._store);

  final KeyValueStore _store;

  static const _access = 'auth.access';
  static const _refresh = 'auth.refresh';
  static const _role = 'auth.role';

  Future<Tokens?> read() async {
    final access = await _store.read(_access);
    final refresh = await _store.read(_refresh);
    final role = Role.fromCode(await _store.read(_role));
    if (access == null || refresh == null || role == null) return null;
    return Tokens(access: access, refresh: refresh, role: role);
  }

  Future<void> save(Tokens tokens) async {
    await _store.write(_access, tokens.access);
    await _store.write(_refresh, tokens.refresh);
    await _store.write(_role, tokens.role.code);
  }

  Future<void> clear() async {
    await _store.delete(_access);
    await _store.delete(_refresh);
    await _store.delete(_role);
  }
}
