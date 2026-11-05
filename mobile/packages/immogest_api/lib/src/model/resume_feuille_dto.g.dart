// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'resume_feuille_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ResumeFeuilleDtoCWProxy {
  ResumeFeuilleDto type(ResumeFeuilleDtoTypeEnum? type);

  ResumeFeuilleDto feuille(String? feuille);

  ResumeFeuilleDto lignes(int? lignes);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ResumeFeuilleDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ResumeFeuilleDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ResumeFeuilleDto call({
    ResumeFeuilleDtoTypeEnum? type,
    String? feuille,
    int? lignes,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfResumeFeuilleDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfResumeFeuilleDto.copyWith.fieldName(...)`
class _$ResumeFeuilleDtoCWProxyImpl implements _$ResumeFeuilleDtoCWProxy {
  const _$ResumeFeuilleDtoCWProxyImpl(this._value);

  final ResumeFeuilleDto _value;

  @override
  ResumeFeuilleDto type(ResumeFeuilleDtoTypeEnum? type) => this(type: type);

  @override
  ResumeFeuilleDto feuille(String? feuille) => this(feuille: feuille);

  @override
  ResumeFeuilleDto lignes(int? lignes) => this(lignes: lignes);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ResumeFeuilleDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ResumeFeuilleDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ResumeFeuilleDto call({
    Object? type = const $CopyWithPlaceholder(),
    Object? feuille = const $CopyWithPlaceholder(),
    Object? lignes = const $CopyWithPlaceholder(),
  }) {
    return ResumeFeuilleDto(
      type: type == const $CopyWithPlaceholder()
          ? _value.type
          // ignore: cast_nullable_to_non_nullable
          : type as ResumeFeuilleDtoTypeEnum?,
      feuille: feuille == const $CopyWithPlaceholder()
          ? _value.feuille
          // ignore: cast_nullable_to_non_nullable
          : feuille as String?,
      lignes: lignes == const $CopyWithPlaceholder()
          ? _value.lignes
          // ignore: cast_nullable_to_non_nullable
          : lignes as int?,
    );
  }
}

extension $ResumeFeuilleDtoCopyWith on ResumeFeuilleDto {
  /// Returns a callable class that can be used as follows: `instanceOfResumeFeuilleDto.copyWith(...)` or like so:`instanceOfResumeFeuilleDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ResumeFeuilleDtoCWProxy get copyWith => _$ResumeFeuilleDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ResumeFeuilleDto _$ResumeFeuilleDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ResumeFeuilleDto', json, ($checkedConvert) {
      final val = ResumeFeuilleDto(
        type: $checkedConvert(
          'type',
          (v) => $enumDecodeNullable(_$ResumeFeuilleDtoTypeEnumEnumMap, v),
        ),
        feuille: $checkedConvert('feuille', (v) => v as String?),
        lignes: $checkedConvert('lignes', (v) => (v as num?)?.toInt()),
      );
      return val;
    });

Map<String, dynamic> _$ResumeFeuilleDtoToJson(ResumeFeuilleDto instance) =>
    <String, dynamic>{
      'type': ?_$ResumeFeuilleDtoTypeEnumEnumMap[instance.type],
      'feuille': ?instance.feuille,
      'lignes': ?instance.lignes,
    };

const _$ResumeFeuilleDtoTypeEnumEnumMap = {
  ResumeFeuilleDtoTypeEnum.IMMEUBLES: 'IMMEUBLES',
  ResumeFeuilleDtoTypeEnum.LOTS: 'LOTS',
  ResumeFeuilleDtoTypeEnum.LOCATAIRES: 'LOCATAIRES',
  ResumeFeuilleDtoTypeEnum.BAUX: 'BAUX',
  ResumeFeuilleDtoTypeEnum.SOLDES: 'SOLDES',
};
