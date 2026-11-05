// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'synchro_automatique.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, type=warning

@ProviderFor(synchroAutomatique)
final synchroAutomatiqueProvider = SynchroAutomatiqueProvider._();

final class SynchroAutomatiqueProvider
    extends
        $FunctionalProvider<
          SynchroAutomatique,
          SynchroAutomatique,
          SynchroAutomatique
        >
    with $Provider<SynchroAutomatique> {
  SynchroAutomatiqueProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'synchroAutomatiqueProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$synchroAutomatiqueHash();

  @$internal
  @override
  $ProviderElement<SynchroAutomatique> $createElement(
    $ProviderPointer pointer,
  ) => $ProviderElement(pointer);

  @override
  SynchroAutomatique create(Ref ref) {
    return synchroAutomatique(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(SynchroAutomatique value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<SynchroAutomatique>(value),
    );
  }
}

String _$synchroAutomatiqueHash() =>
    r'8d7f90222bfbdda1fdfe6757958d83a54c26c1c1';
