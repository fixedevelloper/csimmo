// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'compte_locataire_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$CompteLocataireDtoCWProxy {
  CompteLocataireDto locataireId(String? locataireId);

  CompteLocataireDto locataireNom(String? locataireNom);

  CompteLocataireDto arrieres(int? arrieres);

  CompteLocataireDto aVenir(int? aVenir);

  CompteLocataireDto credit(int? credit);

  CompteLocataireDto solde(int? solde);

  CompteLocataireDto baux(List<BailCompte>? baux);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CompteLocataireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CompteLocataireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  CompteLocataireDto call({
    String? locataireId,
    String? locataireNom,
    int? arrieres,
    int? aVenir,
    int? credit,
    int? solde,
    List<BailCompte>? baux,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfCompteLocataireDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfCompteLocataireDto.copyWith.fieldName(...)`
class _$CompteLocataireDtoCWProxyImpl implements _$CompteLocataireDtoCWProxy {
  const _$CompteLocataireDtoCWProxyImpl(this._value);

  final CompteLocataireDto _value;

  @override
  CompteLocataireDto locataireId(String? locataireId) =>
      this(locataireId: locataireId);

  @override
  CompteLocataireDto locataireNom(String? locataireNom) =>
      this(locataireNom: locataireNom);

  @override
  CompteLocataireDto arrieres(int? arrieres) => this(arrieres: arrieres);

  @override
  CompteLocataireDto aVenir(int? aVenir) => this(aVenir: aVenir);

  @override
  CompteLocataireDto credit(int? credit) => this(credit: credit);

  @override
  CompteLocataireDto solde(int? solde) => this(solde: solde);

  @override
  CompteLocataireDto baux(List<BailCompte>? baux) => this(baux: baux);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CompteLocataireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CompteLocataireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  CompteLocataireDto call({
    Object? locataireId = const $CopyWithPlaceholder(),
    Object? locataireNom = const $CopyWithPlaceholder(),
    Object? arrieres = const $CopyWithPlaceholder(),
    Object? aVenir = const $CopyWithPlaceholder(),
    Object? credit = const $CopyWithPlaceholder(),
    Object? solde = const $CopyWithPlaceholder(),
    Object? baux = const $CopyWithPlaceholder(),
  }) {
    return CompteLocataireDto(
      locataireId: locataireId == const $CopyWithPlaceholder()
          ? _value.locataireId
          // ignore: cast_nullable_to_non_nullable
          : locataireId as String?,
      locataireNom: locataireNom == const $CopyWithPlaceholder()
          ? _value.locataireNom
          // ignore: cast_nullable_to_non_nullable
          : locataireNom as String?,
      arrieres: arrieres == const $CopyWithPlaceholder()
          ? _value.arrieres
          // ignore: cast_nullable_to_non_nullable
          : arrieres as int?,
      aVenir: aVenir == const $CopyWithPlaceholder()
          ? _value.aVenir
          // ignore: cast_nullable_to_non_nullable
          : aVenir as int?,
      credit: credit == const $CopyWithPlaceholder()
          ? _value.credit
          // ignore: cast_nullable_to_non_nullable
          : credit as int?,
      solde: solde == const $CopyWithPlaceholder()
          ? _value.solde
          // ignore: cast_nullable_to_non_nullable
          : solde as int?,
      baux: baux == const $CopyWithPlaceholder()
          ? _value.baux
          // ignore: cast_nullable_to_non_nullable
          : baux as List<BailCompte>?,
    );
  }
}

extension $CompteLocataireDtoCopyWith on CompteLocataireDto {
  /// Returns a callable class that can be used as follows: `instanceOfCompteLocataireDto.copyWith(...)` or like so:`instanceOfCompteLocataireDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$CompteLocataireDtoCWProxy get copyWith =>
      _$CompteLocataireDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CompteLocataireDto _$CompteLocataireDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('CompteLocataireDto', json, ($checkedConvert) {
      final val = CompteLocataireDto(
        locataireId: $checkedConvert('locataireId', (v) => v as String?),
        locataireNom: $checkedConvert('locataireNom', (v) => v as String?),
        arrieres: $checkedConvert('arrieres', (v) => (v as num?)?.toInt()),
        aVenir: $checkedConvert('aVenir', (v) => (v as num?)?.toInt()),
        credit: $checkedConvert('credit', (v) => (v as num?)?.toInt()),
        solde: $checkedConvert('solde', (v) => (v as num?)?.toInt()),
        baux: $checkedConvert(
          'baux',
          (v) => (v as List<dynamic>?)
              ?.map((e) => BailCompte.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$CompteLocataireDtoToJson(CompteLocataireDto instance) =>
    <String, dynamic>{
      'locataireId': ?instance.locataireId,
      'locataireNom': ?instance.locataireNom,
      'arrieres': ?instance.arrieres,
      'aVenir': ?instance.aVenir,
      'credit': ?instance.credit,
      'solde': ?instance.solde,
      'baux': ?instance.baux?.map((e) => e.toJson()).toList(),
    };
