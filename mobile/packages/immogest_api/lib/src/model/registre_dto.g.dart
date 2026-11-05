// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'registre_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$RegistreDtoCWProxy {
  RegistreDto version(String? version);

  RegistreDto responsable(String? responsable);

  RegistreDto note(String? note);

  RegistreDto traitements(List<TraitementDto>? traitements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RegistreDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RegistreDto(...).copyWith(id: 12, name: "My name")
  /// ````
  RegistreDto call({
    String? version,
    String? responsable,
    String? note,
    List<TraitementDto>? traitements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfRegistreDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfRegistreDto.copyWith.fieldName(...)`
class _$RegistreDtoCWProxyImpl implements _$RegistreDtoCWProxy {
  const _$RegistreDtoCWProxyImpl(this._value);

  final RegistreDto _value;

  @override
  RegistreDto version(String? version) => this(version: version);

  @override
  RegistreDto responsable(String? responsable) =>
      this(responsable: responsable);

  @override
  RegistreDto note(String? note) => this(note: note);

  @override
  RegistreDto traitements(List<TraitementDto>? traitements) =>
      this(traitements: traitements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RegistreDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RegistreDto(...).copyWith(id: 12, name: "My name")
  /// ````
  RegistreDto call({
    Object? version = const $CopyWithPlaceholder(),
    Object? responsable = const $CopyWithPlaceholder(),
    Object? note = const $CopyWithPlaceholder(),
    Object? traitements = const $CopyWithPlaceholder(),
  }) {
    return RegistreDto(
      version: version == const $CopyWithPlaceholder()
          ? _value.version
          // ignore: cast_nullable_to_non_nullable
          : version as String?,
      responsable: responsable == const $CopyWithPlaceholder()
          ? _value.responsable
          // ignore: cast_nullable_to_non_nullable
          : responsable as String?,
      note: note == const $CopyWithPlaceholder()
          ? _value.note
          // ignore: cast_nullable_to_non_nullable
          : note as String?,
      traitements: traitements == const $CopyWithPlaceholder()
          ? _value.traitements
          // ignore: cast_nullable_to_non_nullable
          : traitements as List<TraitementDto>?,
    );
  }
}

extension $RegistreDtoCopyWith on RegistreDto {
  /// Returns a callable class that can be used as follows: `instanceOfRegistreDto.copyWith(...)` or like so:`instanceOfRegistreDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$RegistreDtoCWProxy get copyWith => _$RegistreDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

RegistreDto _$RegistreDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('RegistreDto', json, ($checkedConvert) {
      final val = RegistreDto(
        version: $checkedConvert('version', (v) => v as String?),
        responsable: $checkedConvert('responsable', (v) => v as String?),
        note: $checkedConvert('note', (v) => v as String?),
        traitements: $checkedConvert(
          'traitements',
          (v) => (v as List<dynamic>?)
              ?.map((e) => TraitementDto.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$RegistreDtoToJson(RegistreDto instance) =>
    <String, dynamic>{
      'version': ?instance.version,
      'responsable': ?instance.responsable,
      'note': ?instance.note,
      'traitements': ?instance.traitements?.map((e) => e.toJson()).toList(),
    };
