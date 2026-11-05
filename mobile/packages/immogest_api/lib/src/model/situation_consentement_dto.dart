//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/consentement_dto.dart';
import 'package:immogest_api/src/model/finalite_situation_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'situation_consentement_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class SituationConsentementDto {
  /// Returns a new [SituationConsentementDto] instance.
  SituationConsentementDto({

     this.type,

     this.personneId,

     this.finalites,

     this.historique,
  });

  @JsonKey(
    
    name: r'type',
    required: false,
    includeIfNull: false,
  )


  final SituationConsentementDtoTypeEnum? type;



  @JsonKey(
    
    name: r'personneId',
    required: false,
    includeIfNull: false,
  )


  final String? personneId;



  @JsonKey(
    
    name: r'finalites',
    required: false,
    includeIfNull: false,
  )


  final List<FinaliteSituationDto>? finalites;



  @JsonKey(
    
    name: r'historique',
    required: false,
    includeIfNull: false,
  )


  final List<ConsentementDto>? historique;





    @override
    bool operator ==(Object other) => identical(this, other) || other is SituationConsentementDto &&
      other.type == type &&
      other.personneId == personneId &&
      other.finalites == finalites &&
      other.historique == historique;

    @override
    int get hashCode =>
        type.hashCode +
        personneId.hashCode +
        finalites.hashCode +
        historique.hashCode;

  factory SituationConsentementDto.fromJson(Map<String, dynamic> json) => _$SituationConsentementDtoFromJson(json);

  Map<String, dynamic> toJson() => _$SituationConsentementDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum SituationConsentementDtoTypeEnum {
@JsonValue(r'LOCATAIRE')
LOCATAIRE(r'LOCATAIRE'),
@JsonValue(r'GARANT')
GARANT(r'GARANT'),
@JsonValue(r'PROPRIETAIRE')
PROPRIETAIRE(r'PROPRIETAIRE'),
@JsonValue(r'PRESTATAIRE')
PRESTATAIRE(r'PRESTATAIRE');

const SituationConsentementDtoTypeEnum(this.value);

final String value;

@override
String toString() => value;
}


