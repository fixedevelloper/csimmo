// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'resultat_import_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ResultatImportDtoCWProxy {
  ResultatImportDto importe(bool? importe);

  ResultatImportDto crees(Map<String, int>? crees);

  ResultatImportDto echeancesReprises(int? echeancesReprises);

  ResultatImportDto erreurs(List<ErreurImportDto>? erreurs);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ResultatImportDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ResultatImportDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ResultatImportDto call({
    bool? importe,
    Map<String, int>? crees,
    int? echeancesReprises,
    List<ErreurImportDto>? erreurs,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfResultatImportDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfResultatImportDto.copyWith.fieldName(...)`
class _$ResultatImportDtoCWProxyImpl implements _$ResultatImportDtoCWProxy {
  const _$ResultatImportDtoCWProxyImpl(this._value);

  final ResultatImportDto _value;

  @override
  ResultatImportDto importe(bool? importe) => this(importe: importe);

  @override
  ResultatImportDto crees(Map<String, int>? crees) => this(crees: crees);

  @override
  ResultatImportDto echeancesReprises(int? echeancesReprises) =>
      this(echeancesReprises: echeancesReprises);

  @override
  ResultatImportDto erreurs(List<ErreurImportDto>? erreurs) =>
      this(erreurs: erreurs);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ResultatImportDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ResultatImportDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ResultatImportDto call({
    Object? importe = const $CopyWithPlaceholder(),
    Object? crees = const $CopyWithPlaceholder(),
    Object? echeancesReprises = const $CopyWithPlaceholder(),
    Object? erreurs = const $CopyWithPlaceholder(),
  }) {
    return ResultatImportDto(
      importe: importe == const $CopyWithPlaceholder()
          ? _value.importe
          // ignore: cast_nullable_to_non_nullable
          : importe as bool?,
      crees: crees == const $CopyWithPlaceholder()
          ? _value.crees
          // ignore: cast_nullable_to_non_nullable
          : crees as Map<String, int>?,
      echeancesReprises: echeancesReprises == const $CopyWithPlaceholder()
          ? _value.echeancesReprises
          // ignore: cast_nullable_to_non_nullable
          : echeancesReprises as int?,
      erreurs: erreurs == const $CopyWithPlaceholder()
          ? _value.erreurs
          // ignore: cast_nullable_to_non_nullable
          : erreurs as List<ErreurImportDto>?,
    );
  }
}

extension $ResultatImportDtoCopyWith on ResultatImportDto {
  /// Returns a callable class that can be used as follows: `instanceOfResultatImportDto.copyWith(...)` or like so:`instanceOfResultatImportDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ResultatImportDtoCWProxy get copyWith =>
      _$ResultatImportDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ResultatImportDto _$ResultatImportDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ResultatImportDto', json, ($checkedConvert) {
      final val = ResultatImportDto(
        importe: $checkedConvert('importe', (v) => v as bool?),
        crees: $checkedConvert(
          'crees',
          (v) => (v as Map<String, dynamic>?)?.map(
            (k, e) => MapEntry(k, (e as num).toInt()),
          ),
        ),
        echeancesReprises: $checkedConvert(
          'echeancesReprises',
          (v) => (v as num?)?.toInt(),
        ),
        erreurs: $checkedConvert(
          'erreurs',
          (v) => (v as List<dynamic>?)
              ?.map((e) => ErreurImportDto.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$ResultatImportDtoToJson(ResultatImportDto instance) =>
    <String, dynamic>{
      'importe': ?instance.importe,
      'crees': ?instance.crees,
      'echeancesReprises': ?instance.echeancesReprises,
      'erreurs': ?instance.erreurs?.map((e) => e.toJson()).toList(),
    };
