// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'file_encaissements.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, type=warning

@ProviderFor(fileEncaissements)
final fileEncaissementsProvider = FileEncaissementsProvider._();

final class FileEncaissementsProvider
    extends
        $FunctionalProvider<
          FileEncaissements,
          FileEncaissements,
          FileEncaissements
        >
    with $Provider<FileEncaissements> {
  FileEncaissementsProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'fileEncaissementsProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$fileEncaissementsHash();

  @$internal
  @override
  $ProviderElement<FileEncaissements> $createElement(
    $ProviderPointer pointer,
  ) => $ProviderElement(pointer);

  @override
  FileEncaissements create(Ref ref) {
    return fileEncaissements(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(FileEncaissements value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<FileEncaissements>(value),
    );
  }
}

String _$fileEncaissementsHash() => r'3effad2c32551d652b16837b16c31d0ccb4919fe';
