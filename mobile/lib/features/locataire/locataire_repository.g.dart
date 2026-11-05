// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'locataire_repository.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, type=warning

@ProviderFor(locataireRepository)
final locataireRepositoryProvider = LocataireRepositoryProvider._();

final class LocataireRepositoryProvider
    extends
        $FunctionalProvider<
          LocataireRepository,
          LocataireRepository,
          LocataireRepository
        >
    with $Provider<LocataireRepository> {
  LocataireRepositoryProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'locataireRepositoryProvider',
        isAutoDispose: false,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$locataireRepositoryHash();

  @$internal
  @override
  $ProviderElement<LocataireRepository> $createElement(
    $ProviderPointer pointer,
  ) => $ProviderElement(pointer);

  @override
  LocataireRepository create(Ref ref) {
    return locataireRepository(ref);
  }

  /// {@macro riverpod.override_with_value}
  Override overrideWithValue(LocataireRepository value) {
    return $ProviderOverride(
      origin: this,
      providerOverride: $SyncValueProvider<LocataireRepository>(value),
    );
  }
}

String _$locataireRepositoryHash() =>
    r'821da29246fb5b987c6024c9a5073b90252c7091';

@ProviderFor(monCompte)
final monCompteProvider = MonCompteProvider._();

final class MonCompteProvider
    extends
        $FunctionalProvider<
          AsyncValue<CompteLocataireDto>,
          CompteLocataireDto,
          FutureOr<CompteLocataireDto>
        >
    with
        $FutureModifier<CompteLocataireDto>,
        $FutureProvider<CompteLocataireDto> {
  MonCompteProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'monCompteProvider',
        isAutoDispose: true,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$monCompteHash();

  @$internal
  @override
  $FutureProviderElement<CompteLocataireDto> $createElement(
    $ProviderPointer pointer,
  ) => $FutureProviderElement(pointer);

  @override
  FutureOr<CompteLocataireDto> create(Ref ref) {
    return monCompte(ref);
  }
}

String _$monCompteHash() => r'99634e54db95404cce64d87d77fcfee664e51217';

@ProviderFor(mesBaux)
final mesBauxProvider = MesBauxProvider._();

final class MesBauxProvider
    extends
        $FunctionalProvider<
          AsyncValue<List<BailDto>>,
          List<BailDto>,
          FutureOr<List<BailDto>>
        >
    with $FutureModifier<List<BailDto>>, $FutureProvider<List<BailDto>> {
  MesBauxProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'mesBauxProvider',
        isAutoDispose: true,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$mesBauxHash();

  @$internal
  @override
  $FutureProviderElement<List<BailDto>> $createElement(
    $ProviderPointer pointer,
  ) => $FutureProviderElement(pointer);

  @override
  FutureOr<List<BailDto>> create(Ref ref) {
    return mesBaux(ref);
  }
}

String _$mesBauxHash() => r'2e7bb2bddf2231f343663831c35ed0763af75ca7';

@ProviderFor(mesEcheances)
final mesEcheancesProvider = MesEcheancesFamily._();

final class MesEcheancesProvider
    extends
        $FunctionalProvider<
          AsyncValue<List<EcheanceDto>>,
          List<EcheanceDto>,
          FutureOr<List<EcheanceDto>>
        >
    with
        $FutureModifier<List<EcheanceDto>>,
        $FutureProvider<List<EcheanceDto>> {
  MesEcheancesProvider._({
    required MesEcheancesFamily super.from,
    required String super.argument,
  }) : super(
         retry: null,
         name: r'mesEcheancesProvider',
         isAutoDispose: true,
         dependencies: null,
         $allTransitiveDependencies: null,
       );

  @override
  String debugGetCreateSourceHash() => _$mesEcheancesHash();

  @override
  String toString() {
    return r'mesEcheancesProvider'
        ''
        '($argument)';
  }

  @$internal
  @override
  $FutureProviderElement<List<EcheanceDto>> $createElement(
    $ProviderPointer pointer,
  ) => $FutureProviderElement(pointer);

  @override
  FutureOr<List<EcheanceDto>> create(Ref ref) {
    final argument = this.argument as String;
    return mesEcheances(ref, argument);
  }

  @override
  bool operator ==(Object other) {
    return other is MesEcheancesProvider && other.argument == argument;
  }

  @override
  int get hashCode {
    return argument.hashCode;
  }
}

String _$mesEcheancesHash() => r'c97002d9283a67de98428ce8ef32a587686b34d9';

final class MesEcheancesFamily extends $Family
    with $FunctionalFamilyOverride<FutureOr<List<EcheanceDto>>, String> {
  MesEcheancesFamily._()
    : super(
        retry: null,
        name: r'mesEcheancesProvider',
        dependencies: null,
        $allTransitiveDependencies: null,
        isAutoDispose: true,
      );

  MesEcheancesProvider call(String bailId) =>
      MesEcheancesProvider._(argument: bailId, from: this);

  @override
  String toString() => r'mesEcheancesProvider';
}

@ProviderFor(mesDocuments)
final mesDocumentsProvider = MesDocumentsProvider._();

final class MesDocumentsProvider
    extends
        $FunctionalProvider<
          AsyncValue<List<DocumentDto>>,
          List<DocumentDto>,
          FutureOr<List<DocumentDto>>
        >
    with
        $FutureModifier<List<DocumentDto>>,
        $FutureProvider<List<DocumentDto>> {
  MesDocumentsProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'mesDocumentsProvider',
        isAutoDispose: true,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$mesDocumentsHash();

  @$internal
  @override
  $FutureProviderElement<List<DocumentDto>> $createElement(
    $ProviderPointer pointer,
  ) => $FutureProviderElement(pointer);

  @override
  FutureOr<List<DocumentDto>> create(Ref ref) {
    return mesDocuments(ref);
  }
}

String _$mesDocumentsHash() => r'f68455005de857757d6a5c2a0cf9cf79e2171c20';

@ProviderFor(mesSignalements)
final mesSignalementsProvider = MesSignalementsProvider._();

final class MesSignalementsProvider
    extends
        $FunctionalProvider<
          AsyncValue<List<SignalementDto>>,
          List<SignalementDto>,
          FutureOr<List<SignalementDto>>
        >
    with
        $FutureModifier<List<SignalementDto>>,
        $FutureProvider<List<SignalementDto>> {
  MesSignalementsProvider._()
    : super(
        from: null,
        argument: null,
        retry: null,
        name: r'mesSignalementsProvider',
        isAutoDispose: true,
        dependencies: null,
        $allTransitiveDependencies: null,
      );

  @override
  String debugGetCreateSourceHash() => _$mesSignalementsHash();

  @$internal
  @override
  $FutureProviderElement<List<SignalementDto>> $createElement(
    $ProviderPointer pointer,
  ) => $FutureProviderElement(pointer);

  @override
  FutureOr<List<SignalementDto>> create(Ref ref) {
    return mesSignalements(ref);
  }
}

String _$mesSignalementsHash() => r'885ff694cdfc4af9966bc50414619f394d7c2fcf';
