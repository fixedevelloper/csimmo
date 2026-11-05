// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'piece_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PieceDtoCWProxy {
  PieceDto id(String? id);

  PieceDto cibleType(PieceDtoCibleTypeEnum? cibleType);

  PieceDto cibleId(String? cibleId);

  PieceDto fichierId(String? fichierId);

  PieceDto typePiece(PieceDtoTypePieceEnum? typePiece);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PieceDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PieceDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PieceDto call({
    String? id,
    PieceDtoCibleTypeEnum? cibleType,
    String? cibleId,
    String? fichierId,
    PieceDtoTypePieceEnum? typePiece,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPieceDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPieceDto.copyWith.fieldName(...)`
class _$PieceDtoCWProxyImpl implements _$PieceDtoCWProxy {
  const _$PieceDtoCWProxyImpl(this._value);

  final PieceDto _value;

  @override
  PieceDto id(String? id) => this(id: id);

  @override
  PieceDto cibleType(PieceDtoCibleTypeEnum? cibleType) =>
      this(cibleType: cibleType);

  @override
  PieceDto cibleId(String? cibleId) => this(cibleId: cibleId);

  @override
  PieceDto fichierId(String? fichierId) => this(fichierId: fichierId);

  @override
  PieceDto typePiece(PieceDtoTypePieceEnum? typePiece) =>
      this(typePiece: typePiece);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PieceDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PieceDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PieceDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? cibleType = const $CopyWithPlaceholder(),
    Object? cibleId = const $CopyWithPlaceholder(),
    Object? fichierId = const $CopyWithPlaceholder(),
    Object? typePiece = const $CopyWithPlaceholder(),
  }) {
    return PieceDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      cibleType: cibleType == const $CopyWithPlaceholder()
          ? _value.cibleType
          // ignore: cast_nullable_to_non_nullable
          : cibleType as PieceDtoCibleTypeEnum?,
      cibleId: cibleId == const $CopyWithPlaceholder()
          ? _value.cibleId
          // ignore: cast_nullable_to_non_nullable
          : cibleId as String?,
      fichierId: fichierId == const $CopyWithPlaceholder()
          ? _value.fichierId
          // ignore: cast_nullable_to_non_nullable
          : fichierId as String?,
      typePiece: typePiece == const $CopyWithPlaceholder()
          ? _value.typePiece
          // ignore: cast_nullable_to_non_nullable
          : typePiece as PieceDtoTypePieceEnum?,
    );
  }
}

extension $PieceDtoCopyWith on PieceDto {
  /// Returns a callable class that can be used as follows: `instanceOfPieceDto.copyWith(...)` or like so:`instanceOfPieceDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PieceDtoCWProxy get copyWith => _$PieceDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PieceDto _$PieceDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('PieceDto', json, ($checkedConvert) {
      final val = PieceDto(
        id: $checkedConvert('id', (v) => v as String?),
        cibleType: $checkedConvert(
          'cibleType',
          (v) => $enumDecodeNullable(_$PieceDtoCibleTypeEnumEnumMap, v),
        ),
        cibleId: $checkedConvert('cibleId', (v) => v as String?),
        fichierId: $checkedConvert('fichierId', (v) => v as String?),
        typePiece: $checkedConvert(
          'typePiece',
          (v) => $enumDecodeNullable(_$PieceDtoTypePieceEnumEnumMap, v),
        ),
      );
      return val;
    });

Map<String, dynamic> _$PieceDtoToJson(PieceDto instance) => <String, dynamic>{
  'id': ?instance.id,
  'cibleType': ?_$PieceDtoCibleTypeEnumEnumMap[instance.cibleType],
  'cibleId': ?instance.cibleId,
  'fichierId': ?instance.fichierId,
  'typePiece': ?_$PieceDtoTypePieceEnumEnumMap[instance.typePiece],
};

const _$PieceDtoCibleTypeEnumEnumMap = {
  PieceDtoCibleTypeEnum.LOCATAIRE: 'LOCATAIRE',
  PieceDtoCibleTypeEnum.GARANT: 'GARANT',
};

const _$PieceDtoTypePieceEnumEnumMap = {
  PieceDtoTypePieceEnum.CNI_RECTO: 'CNI_RECTO',
  PieceDtoTypePieceEnum.CNI_VERSO: 'CNI_VERSO',
  PieceDtoTypePieceEnum.PASSEPORT: 'PASSEPORT',
  PieceDtoTypePieceEnum.AUTRE: 'AUTRE',
};
