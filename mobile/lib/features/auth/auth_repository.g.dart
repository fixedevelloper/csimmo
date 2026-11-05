// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'auth_repository.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, type=warning

@ProviderFor(authRepository)
final authRepositoryProvider = AuthRepositoryProvider._();

final class AuthRepositoryProvider
    extends $FunctionalProvider<AuthRepository, AuthRepository, AuthRepository>
    with $Provider<AuthRepository> {
  AuthRepositoryProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'authRepositoryProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$authRepositoryHash();

  @$internal
  @override
  $ProviderElement<AuthRepository> $createElement($ProviderPointer pointer) =>
      $ProviderElement(pointer);

  @override
  AuthRepository create(Ref ref) {
    return authRepository(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(AuthRepository value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<AuthRepository>(value),
    );
  }
}

String _$authRepositoryHash() => r'afd6722668c10dcf1c6a4dbfc1d7c31b64c83f4e';

/// Utilisateur connecté (GET /auth/moi) : exerce aussi le jeton et son renouvellement.

@ProviderFor(currentUser)
final currentUserProvider = CurrentUserProvider._();

/// Utilisateur connecté (GET /auth/moi) : exerce aussi le jeton et son renouvellement.

final class CurrentUserProvider
    extends
        $FunctionalProvider<
          AsyncValue<UtilisateurDto>,
          UtilisateurDto,
          FutureOr<UtilisateurDto>
        >
    with $FutureModifier<UtilisateurDto>, $FutureProvider<UtilisateurDto> {
  /// Utilisateur connecté (GET /auth/moi) : exerce aussi le jeton et son renouvellement.
  CurrentUserProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'currentUserProvider',
        isAutoDispose: true,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$currentUserHash();

  @$internal
  @override
  $FutureProviderElement<UtilisateurDto> $createElement(
    $ProviderPointer pointer,
  ) => $FutureProviderElement(pointer);

  @override
  FutureOr<UtilisateurDto> create(Ref ref) {
    return currentUser(ref);
  }
}

String _$currentUserHash() => r'558063a197c66fdfe624a3db7a143e5faf0a2b44';
