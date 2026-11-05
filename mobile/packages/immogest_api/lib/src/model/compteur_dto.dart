//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'compteur_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class CompteurDto {
  /// Returns a new [CompteurDto] instance.
  CompteurDto({

     this.id,

     this.type,

     this.numero,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'type',
    required: false,
    includeIfNull: false,
  )


  final CompteurDtoTypeEnum? type;



  @JsonKey(
    
    name: r'numero',
    required: false,
    includeIfNull: false,
  )


  final String? numero;





    @override
    bool operator ==(Object other) => identical(this, other) || other is CompteurDto &&
      other.id == id &&
      other.type == type &&
      other.numero == numero;

    @override
    int get hashCode =>
        id.hashCode +
        type.hashCode +
        numero.hashCode;

  factory CompteurDto.fromJson(Map<String, dynamic> json) => _$CompteurDtoFromJson(json);

  Map<String, dynamic> toJson() => _$CompteurDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum CompteurDtoTypeEnum {
@JsonValue(r'EAU')
EAU(r'EAU'),
@JsonValue(r'ELECTRICITE')
ELECTRICITE(r'ELECTRICITE'),
@JsonValue(r'GAZ')
GAZ(r'GAZ');

const CompteurDtoTypeEnum(this.value);

final String value;

@override
String toString() => value;
}


