// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'providers.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, type=warning
/// Surchargé dans `main()` après lecture du stockage sécurisé.

@ProviderFor(boot)
final bootProvider = BootProvider._();

/// Surchargé dans `main()` après lecture du stockage sécurisé.

final class BootProvider
    extends $FunctionalProvider<BootState, BootState, BootState>
    with $Provider<BootState> {
  /// Surchargé dans `main()` après lecture du stockage sécurisé.
  BootProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'bootProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$bootHash();

  @$internal
  @override
  $ProviderElement<BootState> $createElement($ProviderPointer pointer) =>
      $ProviderElement(pointer);

  @override
  BootState create(Ref ref) {
    return boot(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(BootState value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<BootState>(value),
    );
  }
}

String _$bootHash() => r'4e8b7404adf44dfd43a2d47b4d69e1e0c75d2d0b';

@ProviderFor(keyValueStore)
final keyValueStoreProvider = KeyValueStoreProvider._();

final class KeyValueStoreProvider
    extends $FunctionalProvider<KeyValueStore, KeyValueStore, KeyValueStore>
    with $Provider<KeyValueStore> {
  KeyValueStoreProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'keyValueStoreProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$keyValueStoreHash();

  @$internal
  @override
  $ProviderElement<KeyValueStore> $createElement($ProviderPointer pointer) =>
      $ProviderElement(pointer);

  @override
  KeyValueStore create(Ref ref) {
    return keyValueStore(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(KeyValueStore value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<KeyValueStore>(value),
    );
  }
}

String _$keyValueStoreHash() => r'9ffd3436760b8d239a333602675030b0359a34b2';

@ProviderFor(tokenStore)
final tokenStoreProvider = TokenStoreProvider._();

final class TokenStoreProvider
    extends $FunctionalProvider<TokenStore, TokenStore, TokenStore>
    with $Provider<TokenStore> {
  TokenStoreProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'tokenStoreProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$tokenStoreHash();

  @$internal
  @override
  $ProviderElement<TokenStore> $createElement($ProviderPointer pointer) =>
      $ProviderElement(pointer);

  @override
  TokenStore create(Ref ref) {
    return tokenStore(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(TokenStore value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<TokenStore>(value),
    );
  }
}

String _$tokenStoreHash() => r'6f64a8d2950c5ebaad24c00fddd01ea0efc596df';

/// Adresse du serveur ImmoGest du client (`null` tant que non configurée).

@ProviderFor(ServerSettings)
final serverSettingsProvider = ServerSettingsProvider._();

/// Adresse du serveur ImmoGest du client (`null` tant que non configurée).
final class ServerSettingsProvider
    extends $NotifierProvider<ServerSettings, String?> {
  /// Adresse du serveur ImmoGest du client (`null` tant que non configurée).
  ServerSettingsProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'serverSettingsProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$serverSettingsHash();

  @$internal
  @override
  ServerSettings create() => ServerSettings();

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(String? value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<String?>(value),
    );
  }
}

String _$serverSettingsHash() => r'1dbabc043061bbe4dc639a33a57528d833916d4c';

/// Adresse du serveur ImmoGest du client (`null` tant que non configurée).

abstract class _$ServerSettings extends $Notifier<String?> {
  String? build();
  @$mustCallSuper
  @override
  WhenComplete runBuild() {
    final ref = this.ref as $Ref<String?, String?>;
    final element =
        ref.element
            as $ClassProviderElement<
              AnyNotifier<String?, String?>,
              String?,
              Object?,
              Object?
            >;
    return element.handleCreate(ref, build);
  }
}

/// Rôle de l'utilisateur connecté (`null` = déconnecté).

@ProviderFor(Session)
final sessionProvider = SessionProvider._();

/// Rôle de l'utilisateur connecté (`null` = déconnecté).
final class SessionProvider extends $NotifierProvider<Session, Role?> {
  /// Rôle de l'utilisateur connecté (`null` = déconnecté).
  SessionProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'sessionProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$sessionHash();

  @$internal
  @override
  Session create() => Session();

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(Role? value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<Role?>(value),
    );
  }
}

String _$sessionHash() => r'f8e3742377a4f01f8cb7fad8783c7a16cd9bc586';

/// Rôle de l'utilisateur connecté (`null` = déconnecté).

abstract class _$Session extends $Notifier<Role?> {
  Role? build();
  @$mustCallSuper
  @override
  WhenComplete runBuild() {
    final ref = this.ref as $Ref<Role?, Role?>;
    final element =
        ref.element
            as $ClassProviderElement<
              AnyNotifier<Role?, Role?>,
              Role?,
              Object?,
              Object?
            >;
    return element.handleCreate(ref, build);
  }
}

/// Client de l'API du serveur configuré. Reconstruit si l'adresse change.

@ProviderFor(api)
final apiProvider = ApiProvider._();

/// Client de l'API du serveur configuré. Reconstruit si l'adresse change.

final class ApiProvider
    extends $FunctionalProvider<ImmogestApi, ImmogestApi, ImmogestApi>
    with $Provider<ImmogestApi> {
  /// Client de l'API du serveur configuré. Reconstruit si l'adresse change.
  ApiProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'apiProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$apiHash();

  @$internal
  @override
  $ProviderElement<ImmogestApi> $createElement($ProviderPointer pointer) =>
      $ProviderElement(pointer);

  @override
  ImmogestApi create(Ref ref) {
    return api(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(ImmogestApi value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<ImmogestApi>(value),
    );
  }
}

String _$apiHash() => r'33f1c57585312d2e44fa106bf0236c353c0dc17a';

@ProviderFor(appDatabase)
final appDatabaseProvider = AppDatabaseProvider._();

final class AppDatabaseProvider
    extends $FunctionalProvider<AppDatabase, AppDatabase, AppDatabase>
    with $Provider<AppDatabase> {
  AppDatabaseProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'appDatabaseProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$appDatabaseHash();

  @$internal
  @override
  $ProviderElement<AppDatabase> $createElement($ProviderPointer pointer) =>
      $ProviderElement(pointer);

  @override
  AppDatabase create(Ref ref) {
    return appDatabase(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(AppDatabase value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<AppDatabase>(value),
    );
  }
}

String _$appDatabaseHash() => r'b78a166d2eff53fe12248642f833fef8500e19f4';

/// Vérifie qu'une adresse répond comme un serveur ImmoGest (GET /api/v1/health).

@ProviderFor(serverProbe)
final serverProbeProvider = ServerProbeProvider._();

/// Vérifie qu'une adresse répond comme un serveur ImmoGest (GET /api/v1/health).

final class ServerProbeProvider
    extends $FunctionalProvider<ServerProbe, ServerProbe, ServerProbe>
    with $Provider<ServerProbe> {
  /// Vérifie qu'une adresse répond comme un serveur ImmoGest (GET /api/v1/health).
  ServerProbeProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'serverProbeProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$serverProbeHash();

  @$internal
  @override
  $ProviderElement<ServerProbe> $createElement($ProviderPointer pointer) =>
      $ProviderElement(pointer);

  @override
  ServerProbe create(Ref ref) {
    return serverProbe(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(ServerProbe value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<ServerProbe>(value),
    );
  }
}

String _$serverProbeHash() => r'6191d91f42acb4017d19c740e19f19b827c0df82';
