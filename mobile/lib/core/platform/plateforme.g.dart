// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'plateforme.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, type=warning

@ProviderFor(fichiersLocaux)
final fichiersLocauxProvider = FichiersLocauxProvider._();

final class FichiersLocauxProvider
    extends $FunctionalProvider<FichiersLocaux, FichiersLocaux, FichiersLocaux>
    with $Provider<FichiersLocaux> {
  FichiersLocauxProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'fichiersLocauxProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$fichiersLocauxHash();

  @$internal
  @override
  $ProviderElement<FichiersLocaux> $createElement($ProviderPointer pointer) =>
      $ProviderElement(pointer);

  @override
  FichiersLocaux create(Ref ref) {
    return fichiersLocaux(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(FichiersLocaux value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<FichiersLocaux>(value),
    );
  }
}

String _$fichiersLocauxHash() => r'f2a2f514f5363a483bbd45c19eb1b25a6dd18fc6';

@ProviderFor(selecteurPhoto)
final selecteurPhotoProvider = SelecteurPhotoProvider._();

final class SelecteurPhotoProvider
    extends $FunctionalProvider<SelecteurPhoto, SelecteurPhoto, SelecteurPhoto>
    with $Provider<SelecteurPhoto> {
  SelecteurPhotoProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'selecteurPhotoProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$selecteurPhotoHash();

  @$internal
  @override
  $ProviderElement<SelecteurPhoto> $createElement($ProviderPointer pointer) =>
      $ProviderElement(pointer);

  @override
  SelecteurPhoto create(Ref ref) {
    return selecteurPhoto(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(SelecteurPhoto value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<SelecteurPhoto>(value),
    );
  }
}

String _$selecteurPhotoHash() => r'c474af3a8e2e08f951e7528762c2f6751b6cd052';
