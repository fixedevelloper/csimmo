// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'fournisseur_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$FournisseurDtoCWProxy {
  FournisseurDto code(String? code);

  FournisseurDto actif(bool? actif);

  FournisseurDto configure(bool? configure);

  FournisseurDto champs(List<ChampDto>? champs);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `FournisseurDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// FournisseurDto(...).copyWith(id: 12, name: "My name")
  /// ````
  FournisseurDto call({
    String? code,
    bool? actif,
    bool? configure,
    List<ChampDto>? champs,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfFournisseurDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfFournisseurDto.copyWith.fieldName(...)`
class _$FournisseurDtoCWProxyImpl implements _$FournisseurDtoCWProxy {
  const _$FournisseurDtoCWProxyImpl(this._value);

  final FournisseurDto _value;

  @override
  FournisseurDto code(String? code) => this(code: code);

  @override
  FournisseurDto actif(bool? actif) => this(actif: actif);

  @override
  FournisseurDto configure(bool? configure) => this(configure: configure);

  @override
  FournisseurDto champs(List<ChampDto>? champs) => this(champs: champs);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `FournisseurDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// FournisseurDto(...).copyWith(id: 12, name: "My name")
  /// ````
  FournisseurDto call({
    Object? code = const $CopyWithPlaceholder(),
    Object? actif = const $CopyWithPlaceholder(),
    Object? configure = const $CopyWithPlaceholder(),
    Object? champs = const $CopyWithPlaceholder(),
  }) {
    return FournisseurDto(
      code: code == const $CopyWithPlaceholder()
          ? _value.code
          // ignore: cast_nullable_to_non_nullable
          : code as String?,
      actif: actif == const $CopyWithPlaceholder()
          ? _value.actif
          // ignore: cast_nullable_to_non_nullable
          : actif as bool?,
      configure: configure == const $CopyWithPlaceholder()
          ? _value.configure
          // ignore: cast_nullable_to_non_nullable
          : configure as bool?,
      champs: champs == const $CopyWithPlaceholder()
          ? _value.champs
          // ignore: cast_nullable_to_non_nullable
          : champs as List<ChampDto>?,
    );
  }
}

extension $FournisseurDtoCopyWith on FournisseurDto {
  /// Returns a callable class that can be used as follows: `instanceOfFournisseurDto.copyWith(...)` or like so:`instanceOfFournisseurDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$FournisseurDtoCWProxy get copyWith => _$FournisseurDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FournisseurDto _$FournisseurDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('FournisseurDto', json, ($checkedConvert) {
      final val = FournisseurDto(
        code: $checkedConvert('code', (v) => v as String?),
        actif: $checkedConvert('actif', (v) => v as bool?),
        configure: $checkedConvert('configure', (v) => v as bool?),
        champs: $checkedConvert(
          'champs',
          (v) => (v as List<dynamic>?)
              ?.map((e) => ChampDto.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$FournisseurDtoToJson(FournisseurDto instance) =>
    <String, dynamic>{
      'code': ?instance.code,
      'actif': ?instance.actif,
      'configure': ?instance.configure,
      'champs': ?instance.champs?.map((e) => e.toJson()).toList(),
    };
