// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'sauvegarde_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$SauvegardeDtoCWProxy {
  SauvegardeDto id(String? id);

  SauvegardeDto date(DateTime? date);

  SauvegardeDto version(String? version);

  SauvegardeDto etiquette(String? etiquette);

  SauvegardeDto tailleOctets(int? tailleOctets);

  SauvegardeDto objets(int? objets);

  SauvegardeDto externe(String? externe);

  SauvegardeDto restaurable(bool? restaurable);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `SauvegardeDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// SauvegardeDto(...).copyWith(id: 12, name: "My name")
  /// ````
  SauvegardeDto call({
    String? id,
    DateTime? date,
    String? version,
    String? etiquette,
    int? tailleOctets,
    int? objets,
    String? externe,
    bool? restaurable,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfSauvegardeDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfSauvegardeDto.copyWith.fieldName(...)`
class _$SauvegardeDtoCWProxyImpl implements _$SauvegardeDtoCWProxy {
  const _$SauvegardeDtoCWProxyImpl(this._value);

  final SauvegardeDto _value;

  @override
  SauvegardeDto id(String? id) => this(id: id);

  @override
  SauvegardeDto date(DateTime? date) => this(date: date);

  @override
  SauvegardeDto version(String? version) => this(version: version);

  @override
  SauvegardeDto etiquette(String? etiquette) => this(etiquette: etiquette);

  @override
  SauvegardeDto tailleOctets(int? tailleOctets) =>
      this(tailleOctets: tailleOctets);

  @override
  SauvegardeDto objets(int? objets) => this(objets: objets);

  @override
  SauvegardeDto externe(String? externe) => this(externe: externe);

  @override
  SauvegardeDto restaurable(bool? restaurable) =>
      this(restaurable: restaurable);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `SauvegardeDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// SauvegardeDto(...).copyWith(id: 12, name: "My name")
  /// ````
  SauvegardeDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? date = const $CopyWithPlaceholder(),
    Object? version = const $CopyWithPlaceholder(),
    Object? etiquette = const $CopyWithPlaceholder(),
    Object? tailleOctets = const $CopyWithPlaceholder(),
    Object? objets = const $CopyWithPlaceholder(),
    Object? externe = const $CopyWithPlaceholder(),
    Object? restaurable = const $CopyWithPlaceholder(),
  }) {
    return SauvegardeDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      date: date == const $CopyWithPlaceholder()
          ? _value.date
          // ignore: cast_nullable_to_non_nullable
          : date as DateTime?,
      version: version == const $CopyWithPlaceholder()
          ? _value.version
          // ignore: cast_nullable_to_non_nullable
          : version as String?,
      etiquette: etiquette == const $CopyWithPlaceholder()
          ? _value.etiquette
          // ignore: cast_nullable_to_non_nullable
          : etiquette as String?,
      tailleOctets: tailleOctets == const $CopyWithPlaceholder()
          ? _value.tailleOctets
          // ignore: cast_nullable_to_non_nullable
          : tailleOctets as int?,
      objets: objets == const $CopyWithPlaceholder()
          ? _value.objets
          // ignore: cast_nullable_to_non_nullable
          : objets as int?,
      externe: externe == const $CopyWithPlaceholder()
          ? _value.externe
          // ignore: cast_nullable_to_non_nullable
          : externe as String?,
      restaurable: restaurable == const $CopyWithPlaceholder()
          ? _value.restaurable
          // ignore: cast_nullable_to_non_nullable
          : restaurable as bool?,
    );
  }
}

extension $SauvegardeDtoCopyWith on SauvegardeDto {
  /// Returns a callable class that can be used as follows: `instanceOfSauvegardeDto.copyWith(...)` or like so:`instanceOfSauvegardeDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$SauvegardeDtoCWProxy get copyWith => _$SauvegardeDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

SauvegardeDto _$SauvegardeDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('SauvegardeDto', json, ($checkedConvert) {
      final val = SauvegardeDto(
        id: $checkedConvert('id', (v) => v as String?),
        date: $checkedConvert(
          'date',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        version: $checkedConvert('version', (v) => v as String?),
        etiquette: $checkedConvert('etiquette', (v) => v as String?),
        tailleOctets: $checkedConvert(
          'tailleOctets',
          (v) => (v as num?)?.toInt(),
        ),
        objets: $checkedConvert('objets', (v) => (v as num?)?.toInt()),
        externe: $checkedConvert('externe', (v) => v as String?),
        restaurable: $checkedConvert('restaurable', (v) => v as bool?),
      );
      return val;
    });

Map<String, dynamic> _$SauvegardeDtoToJson(SauvegardeDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'date': ?instance.date?.toIso8601String(),
      'version': ?instance.version,
      'etiquette': ?instance.etiquette,
      'tailleOctets': ?instance.tailleOctets,
      'objets': ?instance.objets,
      'externe': ?instance.externe,
      'restaurable': ?instance.restaurable,
    };
