//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/champ_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'canal_parametres_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class CanalParametresDto {
  /// Returns a new [CanalParametresDto] instance.
  CanalParametresDto({

     this.canal,

     this.actif,

     this.configure,

     this.champs,
  });

  @JsonKey(
    
    name: r'canal',
    required: false,
    includeIfNull: false,
  )


  final CanalParametresDtoCanalEnum? canal;



  @JsonKey(
    
    name: r'actif',
    required: false,
    includeIfNull: false,
  )


  final bool? actif;



  @JsonKey(
    
    name: r'configure',
    required: false,
    includeIfNull: false,
  )


  final bool? configure;



  @JsonKey(
    
    name: r'champs',
    required: false,
    includeIfNull: false,
  )


  final List<ChampDto>? champs;





    @override
    bool operator ==(Object other) => identical(this, other) || other is CanalParametresDto &&
      other.canal == canal &&
      other.actif == actif &&
      other.configure == configure &&
      other.champs == champs;

    @override
    int get hashCode =>
        canal.hashCode +
        actif.hashCode +
        configure.hashCode +
        champs.hashCode;

  factory CanalParametresDto.fromJson(Map<String, dynamic> json) => _$CanalParametresDtoFromJson(json);

  Map<String, dynamic> toJson() => _$CanalParametresDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum CanalParametresDtoCanalEnum {
@JsonValue(r'SMS')
SMS(r'SMS'),
@JsonValue(r'WHATSAPP')
WHATSAPP(r'WHATSAPP'),
@JsonValue(r'EMAIL')
EMAIL(r'EMAIL'),
@JsonValue(r'PUSH')
PUSH(r'PUSH');

const CanalParametresDtoCanalEnum(this.value);

final String value;

@override
String toString() => value;
}


