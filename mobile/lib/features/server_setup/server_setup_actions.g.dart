// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'server_setup_actions.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, type=warning

@ProviderFor(serverSetupActions)
final serverSetupActionsProvider = ServerSetupActionsProvider._();

final class ServerSetupActionsProvider
    extends
        $FunctionalProvider<
          ServerSetupActions,
          ServerSetupActions,
          ServerSetupActions
        >
    with $Provider<ServerSetupActions> {
  ServerSetupActionsProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'serverSetupActionsProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$serverSetupActionsHash();

  @$internal
  @override
  $ProviderElement<ServerSetupActions> $createElement(
    $ProviderPointer pointer,
  ) => $ProviderElement(pointer);

  @override
  ServerSetupActions create(Ref ref) {
    return serverSetupActions(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(ServerSetupActions value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<ServerSetupActions>(value),
    );
  }
}

String _$serverSetupActionsHash() =>
    r'731fbc72a4284ac7490906e97e5c40e04ef1bbb8';
