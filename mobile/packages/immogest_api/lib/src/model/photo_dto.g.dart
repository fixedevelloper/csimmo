// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'photo_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PhotoDtoCWProxy {
  PhotoDto id(String? id);

  PhotoDto cibleType(PhotoDtoCibleTypeEnum? cibleType);

  PhotoDto cibleId(String? cibleId);

  PhotoDto fichierId(String? fichierId);

  PhotoDto legende(String? legende);

  PhotoDto ordre(int? ordre);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PhotoDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PhotoDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PhotoDto call({
    String? id,
    PhotoDtoCibleTypeEnum? cibleType,
    String? cibleId,
    String? fichierId,
    String? legende,
    int? ordre,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPhotoDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPhotoDto.copyWith.fieldName(...)`
class _$PhotoDtoCWProxyImpl implements _$PhotoDtoCWProxy {
  const _$PhotoDtoCWProxyImpl(this._value);

  final PhotoDto _value;

  @override
  PhotoDto id(String? id) => this(id: id);

  @override
  PhotoDto cibleType(PhotoDtoCibleTypeEnum? cibleType) =>
      this(cibleType: cibleType);

  @override
  PhotoDto cibleId(String? cibleId) => this(cibleId: cibleId);

  @override
  PhotoDto fichierId(String? fichierId) => this(fichierId: fichierId);

  @override
  PhotoDto legende(String? legende) => this(legende: legende);

  @override
  PhotoDto ordre(int? ordre) => this(ordre: ordre);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PhotoDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PhotoDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PhotoDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? cibleType = const $CopyWithPlaceholder(),
    Object? cibleId = const $CopyWithPlaceholder(),
    Object? fichierId = const $CopyWithPlaceholder(),
    Object? legende = const $CopyWithPlaceholder(),
    Object? ordre = const $CopyWithPlaceholder(),
  }) {
    return PhotoDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      cibleType: cibleType == const $CopyWithPlaceholder()
          ? _value.cibleType
          // ignore: cast_nullable_to_non_nullable
          : cibleType as PhotoDtoCibleTypeEnum?,
      cibleId: cibleId == const $CopyWithPlaceholder()
          ? _value.cibleId
          // ignore: cast_nullable_to_non_nullable
          : cibleId as String?,
      fichierId: fichierId == const $CopyWithPlaceholder()
          ? _value.fichierId
          // ignore: cast_nullable_to_non_nullable
          : fichierId as String?,
      legende: legende == const $CopyWithPlaceholder()
          ? _value.legende
          // ignore: cast_nullable_to_non_nullable
          : legende as String?,
      ordre: ordre == const $CopyWithPlaceholder()
          ? _value.ordre
          // ignore: cast_nullable_to_non_nullable
          : ordre as int?,
    );
  }
}

extension $PhotoDtoCopyWith on PhotoDto {
  /// Returns a callable class that can be used as follows: `instanceOfPhotoDto.copyWith(...)` or like so:`instanceOfPhotoDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PhotoDtoCWProxy get copyWith => _$PhotoDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PhotoDto _$PhotoDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('PhotoDto', json, ($checkedConvert) {
      final val = PhotoDto(
        id: $checkedConvert('id', (v) => v as String?),
        cibleType: $checkedConvert(
          'cibleType',
          (v) => $enumDecodeNullable(_$PhotoDtoCibleTypeEnumEnumMap, v),
        ),
        cibleId: $checkedConvert('cibleId', (v) => v as String?),
        fichierId: $checkedConvert('fichierId', (v) => v as String?),
        legende: $checkedConvert('legende', (v) => v as String?),
        ordre: $checkedConvert('ordre', (v) => (v as num?)?.toInt()),
      );
      return val;
    });

Map<String, dynamic> _$PhotoDtoToJson(PhotoDto instance) => <String, dynamic>{
  'id': ?instance.id,
  'cibleType': ?_$PhotoDtoCibleTypeEnumEnumMap[instance.cibleType],
  'cibleId': ?instance.cibleId,
  'fichierId': ?instance.fichierId,
  'legende': ?instance.legende,
  'ordre': ?instance.ordre,
};

const _$PhotoDtoCibleTypeEnumEnumMap = {
  PhotoDtoCibleTypeEnum.IMMEUBLE: 'IMMEUBLE',
  PhotoDtoCibleTypeEnum.LOT: 'LOT',
};
