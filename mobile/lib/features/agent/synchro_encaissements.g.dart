// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'synchro_encaissements.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, type=warning

@ProviderFor(synchroEncaissements)
final synchroEncaissementsProvider = SynchroEncaissementsProvider._();

final class SynchroEncaissementsProvider
    extends
        $FunctionalProvider<
          SynchroEncaissements,
          SynchroEncaissements,
          SynchroEncaissements
        >
    with $Provider<SynchroEncaissements> {
  SynchroEncaissementsProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'synchroEncaissementsProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$synchroEncaissementsHash();

  @$internal
  @override
  $ProviderElement<SynchroEncaissements> $createElement(
    $ProviderPointer pointer,
  ) => $ProviderElement(pointer);

  @override
  SynchroEncaissements create(Ref ref) {
    return synchroEncaissements(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(SynchroEncaissements value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<SynchroEncaissements>(value),
    );
  }
}

String _$synchroEncaissementsHash() =>
    r'74b5a08c9129971910c4f842e0cfec049cf953d4';

@ProviderFor(agentId)
final agentIdProvider = AgentIdProvider._();

final class AgentIdProvider
    extends $FunctionalProvider<AsyncValue<String?>, String?, FutureOr<String?>>
    with $FutureModifier<String?>, $FutureProvider<String?> {
  AgentIdProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'agentIdProvider',
        isAutoDispose: true,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$agentIdHash();

  @$internal
  @override
  $FutureProviderElement<String?> $createElement($ProviderPointer pointer) =>
      $FutureProviderElement(pointer);

  @override
  FutureOr<String?> create(Ref ref) {
    return agentId(ref);
  }
}

String _$agentIdHash() => r'ef9e8e004bc94bf86398e4011d96efdef620451c';
