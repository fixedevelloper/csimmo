//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'resume_feuille_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ResumeFeuilleDto {
  /// Returns a new [ResumeFeuilleDto] instance.
  ResumeFeuilleDto({

     this.type,

     this.feuille,

     this.lignes,
  });

  @JsonKey(
    
    name: r'type',
    required: false,
    includeIfNull: false,
  )


  final ResumeFeuilleDtoTypeEnum? type;



  @JsonKey(
    
    name: r'feuille',
    required: false,
    includeIfNull: false,
  )


  final String? feuille;



  @JsonKey(
    
    name: r'lignes',
    required: false,
    includeIfNull: false,
  )


  final int? lignes;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ResumeFeuilleDto &&
      other.type == type &&
      other.feuille == feuille &&
      other.lignes == lignes;

    @override
    int get hashCode =>
        type.hashCode +
        feuille.hashCode +
        lignes.hashCode;

  factory ResumeFeuilleDto.fromJson(Map<String, dynamic> json) => _$ResumeFeuilleDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ResumeFeuilleDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum ResumeFeuilleDtoTypeEnum {
@JsonValue(r'IMMEUBLES')
IMMEUBLES(r'IMMEUBLES'),
@JsonValue(r'LOTS')
LOTS(r'LOTS'),
@JsonValue(r'LOCATAIRES')
LOCATAIRES(r'LOCATAIRES'),
@JsonValue(r'BAUX')
BAUX(r'BAUX'),
@JsonValue(r'SOLDES')
SOLDES(r'SOLDES');

const ResumeFeuilleDtoTypeEnum(this.value);

final String value;

@override
String toString() => value;
}


