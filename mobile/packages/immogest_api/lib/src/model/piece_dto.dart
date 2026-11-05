//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'piece_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PieceDto {
  /// Returns a new [PieceDto] instance.
  PieceDto({

     this.id,

     this.cibleType,

     this.cibleId,

     this.fichierId,

     this.typePiece,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'cibleType',
    required: false,
    includeIfNull: false,
  )


  final PieceDtoCibleTypeEnum? cibleType;



  @JsonKey(
    
    name: r'cibleId',
    required: false,
    includeIfNull: false,
  )


  final String? cibleId;



  @JsonKey(
    
    name: r'fichierId',
    required: false,
    includeIfNull: false,
  )


  final String? fichierId;



  @JsonKey(
    
    name: r'typePiece',
    required: false,
    includeIfNull: false,
  )


  final PieceDtoTypePieceEnum? typePiece;





    @override
    bool operator ==(Object other) => identical(this, other) || other is PieceDto &&
      other.id == id &&
      other.cibleType == cibleType &&
      other.cibleId == cibleId &&
      other.fichierId == fichierId &&
      other.typePiece == typePiece;

    @override
    int get hashCode =>
        id.hashCode +
        cibleType.hashCode +
        cibleId.hashCode +
        fichierId.hashCode +
        typePiece.hashCode;

  factory PieceDto.fromJson(Map<String, dynamic> json) => _$PieceDtoFromJson(json);

  Map<String, dynamic> toJson() => _$PieceDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum PieceDtoCibleTypeEnum {
@JsonValue(r'LOCATAIRE')
LOCATAIRE(r'LOCATAIRE'),
@JsonValue(r'GARANT')
GARANT(r'GARANT');

const PieceDtoCibleTypeEnum(this.value);

final String value;

@override
String toString() => value;
}


enum PieceDtoTypePieceEnum {
@JsonValue(r'CNI_RECTO')
CNI_RECTO(r'CNI_RECTO'),
@JsonValue(r'CNI_VERSO')
CNI_VERSO(r'CNI_VERSO'),
@JsonValue(r'PASSEPORT')
PASSEPORT(r'PASSEPORT'),
@JsonValue(r'AUTRE')
AUTRE(r'AUTRE');

const PieceDtoTypePieceEnum(this.value);

final String value;

@override
String toString() => value;
}


