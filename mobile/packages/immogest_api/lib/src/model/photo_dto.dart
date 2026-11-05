//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'photo_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PhotoDto {
  /// Returns a new [PhotoDto] instance.
  PhotoDto({

     this.id,

     this.cibleType,

     this.cibleId,

     this.fichierId,

     this.legende,

     this.ordre,
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


  final PhotoDtoCibleTypeEnum? cibleType;



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
    
    name: r'legende',
    required: false,
    includeIfNull: false,
  )


  final String? legende;



  @JsonKey(
    
    name: r'ordre',
    required: false,
    includeIfNull: false,
  )


  final int? ordre;





    @override
    bool operator ==(Object other) => identical(this, other) || other is PhotoDto &&
      other.id == id &&
      other.cibleType == cibleType &&
      other.cibleId == cibleId &&
      other.fichierId == fichierId &&
      other.legende == legende &&
      other.ordre == ordre;

    @override
    int get hashCode =>
        id.hashCode +
        cibleType.hashCode +
        cibleId.hashCode +
        fichierId.hashCode +
        legende.hashCode +
        ordre.hashCode;

  factory PhotoDto.fromJson(Map<String, dynamic> json) => _$PhotoDtoFromJson(json);

  Map<String, dynamic> toJson() => _$PhotoDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum PhotoDtoCibleTypeEnum {
@JsonValue(r'IMMEUBLE')
IMMEUBLE(r'IMMEUBLE'),
@JsonValue(r'LOT')
LOT(r'LOT');

const PhotoDtoCibleTypeEnum(this.value);

final String value;

@override
String toString() => value;
}


