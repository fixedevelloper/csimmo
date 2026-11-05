// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'auth_actions.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, type=warning

@ProviderFor(authActions)
final authActionsProvider = AuthActionsProvider._();

final class AuthActionsProvider
    extends $FunctionalProvider<AuthActions, AuthActions, AuthActions>
    with $Provider<AuthActions> {
  AuthActionsProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'authActionsProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$authActionsHash();

  @$internal
  @override
  $ProviderElement<AuthActions> $createElement($ProviderPointer pointer) =>
      $ProviderElement(pointer);

  @override
  AuthActions create(Ref ref) {
    return authActions(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(AuthActions value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<AuthActions>(value),
    );
  }
}

String _$authActionsHash() => r'3ac14f51ab9f4fed361cdb12981db1f5466a57ec';
