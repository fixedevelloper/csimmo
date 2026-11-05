//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'document_bail_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class DocumentBailDto {
  /// Returns a new [DocumentBailDto] instance.
  DocumentBailDto({

     this.id,

     this.bailId,

     this.fichierId,

     this.version,

     this.genereLe,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'bailId',
    required: false,
    includeIfNull: false,
  )


  final String? bailId;



  @JsonKey(
    
    name: r'fichierId',
    required: false,
    includeIfNull: false,
  )


  final String? fichierId;



  @JsonKey(
    
    name: r'version',
    required: false,
    includeIfNull: false,
  )


  final int? version;



  @JsonKey(
    
    name: r'genereLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? genereLe;





    @override
    bool operator ==(Object other) => identical(this, other) || other is DocumentBailDto &&
      other.id == id &&
      other.bailId == bailId &&
      other.fichierId == fichierId &&
      other.version == version &&
      other.genereLe == genereLe;

    @override
    int get hashCode =>
        id.hashCode +
        bailId.hashCode +
        fichierId.hashCode +
        version.hashCode +
        genereLe.hashCode;

  factory DocumentBailDto.fromJson(Map<String, dynamic> json) => _$DocumentBailDtoFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentBailDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

