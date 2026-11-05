//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'modele_consentement_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ModeleConsentementDto {
  /// Returns a new [ModeleConsentementDto] instance.
  ModeleConsentementDto({

     this.finalite,

     this.version,

     this.titre,

     this.texte,
  });

  @JsonKey(
    
    name: r'finalite',
    required: false,
    includeIfNull: false,
  )


  final ModeleConsentementDtoFinaliteEnum? finalite;



  @JsonKey(
    
    name: r'version',
    required: false,
    includeIfNull: false,
  )


  final String? version;



  @JsonKey(
    
    name: r'titre',
    required: false,
    includeIfNull: false,
  )


  final String? titre;



  @JsonKey(
    
    name: r'texte',
    required: false,
    includeIfNull: false,
  )


  final String? texte;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ModeleConsentementDto &&
      other.finalite == finalite &&
      other.version == version &&
      other.titre == titre &&
      other.texte == texte;

    @override
    int get hashCode =>
        finalite.hashCode +
        version.hashCode +
        titre.hashCode +
        texte.hashCode;

  factory ModeleConsentementDto.fromJson(Map<String, dynamic> json) => _$ModeleConsentementDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ModeleConsentementDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum ModeleConsentementDtoFinaliteEnum {
@JsonValue(r'NOTIFICATIONS')
NOTIFICATIONS(r'NOTIFICATIONS'),
@JsonValue(r'PIECES_IDENTITE')
PIECES_IDENTITE(r'PIECES_IDENTITE'),
@JsonValue(r'TRANSMISSION_PROPRIETAIRE')
TRANSMISSION_PROPRIETAIRE(r'TRANSMISSION_PROPRIETAIRE'),
@JsonValue(r'APPLICATION_MOBILE')
APPLICATION_MOBILE(r'APPLICATION_MOBILE');

const ModeleConsentementDtoFinaliteEnum(this.value);

final String value;

@override
String toString() => value;
}


