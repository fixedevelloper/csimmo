//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/consentement_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'finalite_situation_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class FinaliteSituationDto {
  /// Returns a new [FinaliteSituationDto] instance.
  FinaliteSituationDto({

     this.finalite,

     this.statut,

     this.derniere,
  });

  @JsonKey(
    
    name: r'finalite',
    required: false,
    includeIfNull: false,
  )


  final FinaliteSituationDtoFinaliteEnum? finalite;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final String? statut;



  @JsonKey(
    
    name: r'derniere',
    required: false,
    includeIfNull: false,
  )


  final ConsentementDto? derniere;





    @override
    bool operator ==(Object other) => identical(this, other) || other is FinaliteSituationDto &&
      other.finalite == finalite &&
      other.statut == statut &&
      other.derniere == derniere;

    @override
    int get hashCode =>
        finalite.hashCode +
        statut.hashCode +
        derniere.hashCode;

  factory FinaliteSituationDto.fromJson(Map<String, dynamic> json) => _$FinaliteSituationDtoFromJson(json);

  Map<String, dynamic> toJson() => _$FinaliteSituationDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum FinaliteSituationDtoFinaliteEnum {
@JsonValue(r'NOTIFICATIONS')
NOTIFICATIONS(r'NOTIFICATIONS'),
@JsonValue(r'PIECES_IDENTITE')
PIECES_IDENTITE(r'PIECES_IDENTITE'),
@JsonValue(r'TRANSMISSION_PROPRIETAIRE')
TRANSMISSION_PROPRIETAIRE(r'TRANSMISSION_PROPRIETAIRE'),
@JsonValue(r'APPLICATION_MOBILE')
APPLICATION_MOBILE(r'APPLICATION_MOBILE');

const FinaliteSituationDtoFinaliteEnum(this.value);

final String value;

@override
String toString() => value;
}


