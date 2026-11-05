// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'previsualisation_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PrevisualisationDtoCWProxy {
  PrevisualisationDto empreinte(String? empreinte);

  PrevisualisationDto feuilles(List<ResumeFeuilleDto>? feuilles);

  PrevisualisationDto erreurs(List<ErreurImportDto>? erreurs);

  PrevisualisationDto apercu(Map<String, List<Map<String, String>>>? apercu);

  PrevisualisationDto importable(bool? importable);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PrevisualisationDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PrevisualisationDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PrevisualisationDto call({
    String? empreinte,
    List<ResumeFeuilleDto>? feuilles,
    List<ErreurImportDto>? erreurs,
    Map<String, List<Map<String, String>>>? apercu,
    bool? importable,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPrevisualisationDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPrevisualisationDto.copyWith.fieldName(...)`
class _$PrevisualisationDtoCWProxyImpl implements _$PrevisualisationDtoCWProxy {
  const _$PrevisualisationDtoCWProxyImpl(this._value);

  final PrevisualisationDto _value;

  @override
  PrevisualisationDto empreinte(String? empreinte) =>
      this(empreinte: empreinte);

  @override
  PrevisualisationDto feuilles(List<ResumeFeuilleDto>? feuilles) =>
      this(feuilles: feuilles);

  @override
  PrevisualisationDto erreurs(List<ErreurImportDto>? erreurs) =>
      this(erreurs: erreurs);

  @override
  PrevisualisationDto apercu(Map<String, List<Map<String, String>>>? apercu) =>
      this(apercu: apercu);

  @override
  PrevisualisationDto importable(bool? importable) =>
      this(importable: importable);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PrevisualisationDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PrevisualisationDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PrevisualisationDto call({
    Object? empreinte = const $CopyWithPlaceholder(),
    Object? feuilles = const $CopyWithPlaceholder(),
    Object? erreurs = const $CopyWithPlaceholder(),
    Object? apercu = const $CopyWithPlaceholder(),
    Object? importable = const $CopyWithPlaceholder(),
  }) {
    return PrevisualisationDto(
      empreinte: empreinte == const $CopyWithPlaceholder()
          ? _value.empreinte
          // ignore: cast_nullable_to_non_nullable
          : empreinte as String?,
      feuilles: feuilles == const $CopyWithPlaceholder()
          ? _value.feuilles
          // ignore: cast_nullable_to_non_nullable
          : feuilles as List<ResumeFeuilleDto>?,
      erreurs: erreurs == const $CopyWithPlaceholder()
          ? _value.erreurs
          // ignore: cast_nullable_to_non_nullable
          : erreurs as List<ErreurImportDto>?,
      apercu: apercu == const $CopyWithPlaceholder()
          ? _value.apercu
          // ignore: cast_nullable_to_non_nullable
          : apercu as Map<String, List<Map<String, String>>>?,
      importable: importable == const $CopyWithPlaceholder()
          ? _value.importable
          // ignore: cast_nullable_to_non_nullable
          : importable as bool?,
    );
  }
}

extension $PrevisualisationDtoCopyWith on PrevisualisationDto {
  /// Returns a callable class that can be used as follows: `instanceOfPrevisualisationDto.copyWith(...)` or like so:`instanceOfPrevisualisationDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PrevisualisationDtoCWProxy get copyWith =>
      _$PrevisualisationDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PrevisualisationDto _$PrevisualisationDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('PrevisualisationDto', json, ($checkedConvert) {
      final val = PrevisualisationDto(
        empreinte: $checkedConvert('empreinte', (v) => v as String?),
        feuilles: $checkedConvert(
          'feuilles',
          (v) => (v as List<dynamic>?)
              ?.map((e) => ResumeFeuilleDto.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
        erreurs: $checkedConvert(
          'erreurs',
          (v) => (v as List<dynamic>?)
              ?.map((e) => ErreurImportDto.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
        apercu: $checkedConvert(
          'apercu',
          (v) => (v as Map<String, dynamic>?)?.map(
            (k, e) => MapEntry(
              k,
              (e as List<dynamic>)
                  .map((e) => Map<String, String>.from(e as Map))
                  .toList(),
            ),
          ),
        ),
        importable: $checkedConvert('importable', (v) => v as bool?),
      );
      return val;
    });

Map<String, dynamic> _$PrevisualisationDtoToJson(
  PrevisualisationDto instance,
) => <String, dynamic>{
  'empreinte': ?instance.empreinte,
  'feuilles': ?instance.feuilles?.map((e) => e.toJson()).toList(),
  'erreurs': ?instance.erreurs?.map((e) => e.toJson()).toList(),
  'apercu': ?instance.apercu,
  'importable': ?instance.importable,
};
