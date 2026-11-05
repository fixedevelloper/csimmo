import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/core/auth/role.dart';
import 'package:immogest_mobile/core/auth/token_store.dart';
import 'package:immogest_mobile/core/storage/key_value_store.dart';

void main() {
  test('enregistre, relit et efface les jetons', () async {
    final store = TokenStore(MemoryKeyValueStore());
    expect(await store.read(), isNull);

    await store.save(const Tokens(access: 'a', refresh: 'r', role: Role.locataire));
    final lus = await store.read();
    expect((lus!.access, lus.refresh, lus.role), ('a', 'r', Role.locataire));

    await store.clear();
    expect(await store.read(), isNull);
  });

  test('des données incomplètes ou un rôle inconnu donnent « pas de session »', () async {
    final incomplet = TokenStore(MemoryKeyValueStore({'auth.access': 'a', 'auth.refresh': 'r'}));
    expect(await incomplet.read(), isNull);

    final roleInconnu = TokenStore(MemoryKeyValueStore({'auth.access': 'a', 'auth.refresh': 'r', 'auth.role': 'ROOT'}));
    expect(await roleInconnu.read(), isNull);
  });
}
