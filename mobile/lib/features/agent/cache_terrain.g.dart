// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'cache_terrain.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, type=warning

@ProviderFor(cacheTerrain)
final cacheTerrainProvider = CacheTerrainProvider._();

final class CacheTerrainProvider
    extends $FunctionalProvider<CacheTerrain, CacheTerrain, CacheTerrain>
    with $Provider<CacheTerrain> {
  CacheTerrainProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'cacheTerrainProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$cacheTerrainHash();

  @$internal
  @override
  $ProviderElement<CacheTerrain> $createElement($ProviderPointer pointer) =>
      $ProviderElement(pointer);

  @override
  CacheTerrain create(Ref ref) {
    return cacheTerrain(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(CacheTerrain value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<CacheTerrain>(value),
    );
  }
}

String _$cacheTerrainHash() => r'2c9fb6606ecf9553e7c6bc68caaeb66fa887c0ae';
