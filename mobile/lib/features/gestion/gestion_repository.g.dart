// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'gestion_repository.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, type=warning

@ProviderFor(gestionRepository)
final gestionRepositoryProvider = GestionRepositoryProvider._();

final class GestionRepositoryProvider
    extends
        $FunctionalProvider<
          GestionRepository,
          GestionRepository,
          GestionRepository
        >
    with $Provider<GestionRepository> {
  GestionRepositoryProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'gestionRepositoryProvider',
        isAutoDispose: true,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$gestionRepositoryHash();

  @$internal
  @override
  $ProviderElement<GestionRepository> $createElement(
    $ProviderPointer pointer,
  ) => $ProviderElement(pointer);

  @override
  GestionRepository create(Ref ref) {
    return gestionRepository(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(GestionRepository value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<GestionRepository>(value),
    );
  }
}

String _$gestionRepositoryHash() => r'2385431d7b0a54277ce473a3bcebd56c10d1acd6';

@ProviderFor(syntheseGestion)
final syntheseGestionProvider = SyntheseGestionProvider._();

final class SyntheseGestionProvider
    extends
        $FunctionalProvider<AsyncValue<Synthese>, Synthese, FutureOr<Synthese>>
    with $FutureModifier<Synthese>, $FutureProvider<Synthese> {
  SyntheseGestionProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'syntheseGestionProvider',
        isAutoDispose: true,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$syntheseGestionHash();

  @$internal
  @override
  $FutureProviderElement<Synthese> $createElement($ProviderPointer pointer) =>
      $FutureProviderElement(pointer);

  @override
  FutureOr<Synthese> create(Ref ref) {
    return syntheseGestion(ref);
  }
}

String _$syntheseGestionHash() => r'6ebf8d862d9fca0a367ea7546f53e2f3cc92eeea';

@ProviderFor(arrieresGestion)
final arrieresGestionProvider = ArrieresGestionFamily._();

final class ArrieresGestionProvider
    extends
        $FunctionalProvider<
          AsyncValue<List<ArriereDto>>,
          List<ArriereDto>,
          FutureOr<List<ArriereDto>>
        >
    with $FutureModifier<List<ArriereDto>>, $FutureProvider<List<ArriereDto>> {
  ArrieresGestionProvider._({
    required ArrieresGestionFamily super.from,
    required String super.argument,
  }) : super(
         retry: null,
         name: r'arrieresGestionProvider',
         isAutoDispose: true,
         dependencies: null,
         $allTransitiveDependencies: null,
       );

  @override
  String debugGetCreateSourceHash() => _$arrieresGestionHash();

  @override
  String toString() {
    return r'arrieresGestionProvider'
        ''
        '($argument)';
  }

  @$internal
  @override
  $FutureProviderElement<List<ArriereDto>> $createElement(
    $ProviderPointer pointer,
  ) => $FutureProviderElement(pointer);

  @override
  FutureOr<List<ArriereDto>> create(Ref ref) {
    final argument = this.argument as String;
    return arrieresGestion(ref, argument);
  }

  @override
  bool operator ==(Object other) {
    return other is ArrieresGestionProvider && other.argument == argument;
  }

  @override
  int get hashCode {
    return argument.hashCode;
  }
}

String _$arrieresGestionHash() => r'0bf43f1fbb8683d4741b178ef92dd3e52fc7d121';

final class ArrieresGestionFamily extends $Family
    with $FunctionalFamilyOverride<FutureOr<List<ArriereDto>>, String> {
  ArrieresGestionFamily._()
    : super(
        retry: null,
        name: r'arrieresGestionProvider',
        dependencies: null,
        $allTransitiveDependencies: null,
        isAutoDispose: true,
      );

  ArrieresGestionProvider call(String q) =>
      ArrieresGestionProvider._(argument: q, from: this);

  @override
  String toString() => r'arrieresGestionProvider';
}

@ProviderFor(signalementsGestion)
final signalementsGestionProvider = SignalementsGestionFamily._();

final class SignalementsGestionProvider
    extends
        $FunctionalProvider<
          AsyncValue<List<SignalementDto>>,
          List<SignalementDto>,
          FutureOr<List<SignalementDto>>
        >
    with
        $FutureModifier<List<SignalementDto>>,
        $FutureProvider<List<SignalementDto>> {
  SignalementsGestionProvider._({
    required SignalementsGestionFamily super.from,
    required String? super.argument,
  }) : super(
         retry: null,
         name: r'signalementsGestionProvider',
         isAutoDispose: true,
         dependencies: null,
         $allTransitiveDependencies: null,
       );

  @override
  String debugGetCreateSourceHash() => _$signalementsGestionHash();

  @override
  String toString() {
    return r'signalementsGestionProvider'
        ''
        '($argument)';
  }

  @$internal
  @override
  $FutureProviderElement<List<SignalementDto>> $createElement(
    $ProviderPointer pointer,
  ) => $FutureProviderElement(pointer);

  @override
  FutureOr<List<SignalementDto>> create(Ref ref) {
    final argument = this.argument as String?;
    return signalementsGestion(ref, argument);
  }

  @override
  bool operator ==(Object other) {
    return other is SignalementsGestionProvider && other.argument == argument;
  }

  @override
  int get hashCode {
    return argument.hashCode;
  }
}

String _$signalementsGestionHash() =>
    r'f7720f2e3034922cea487f60228ca5920d92e555';

final class SignalementsGestionFamily extends $Family
    with $FunctionalFamilyOverride<FutureOr<List<SignalementDto>>, String?> {
  SignalementsGestionFamily._()
    : super(
        retry: null,
        name: r'signalementsGestionProvider',
        dependencies: null,
        $allTransitiveDependencies: null,
        isAutoDispose: true,
      );

  SignalementsGestionProvider call(String? statut) =>
      SignalementsGestionProvider._(argument: statut, from: this);

  @override
  String toString() => r'signalementsGestionProvider';
}
