// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'push_registrar.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, type=warning

@ProviderFor(pushRegistrar)
final pushRegistrarProvider = PushRegistrarProvider._();

final class PushRegistrarProvider
    extends $FunctionalProvider<PushRegistrar, PushRegistrar, PushRegistrar>
    with $Provider<PushRegistrar> {
  PushRegistrarProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'pushRegistrarProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$pushRegistrarHash();

  @$internal
  @override
  $ProviderElement<PushRegistrar> $createElement($ProviderPointer pointer) =>
      $ProviderElement(pointer);

  @override
  PushRegistrar create(Ref ref) {
    return pushRegistrar(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(PushRegistrar value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<PushRegistrar>(value),
    );
  }
}

String _$pushRegistrarHash() => r'5a95f6b71cf1032fb1066f6828f6ac9cb19d4cd6';
