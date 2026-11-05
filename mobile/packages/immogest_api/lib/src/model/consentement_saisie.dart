//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'consentement_saisie.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ConsentementSaisie {
  /// Returns a new [ConsentementSaisie] instance.
  ConsentementSaisie({

     this.type,

     this.personneId,

     this.finalite,

     this.accorde,

     this.modeRecueil,

     this.referenceDocument,
  });

  @JsonKey(
    
    name: r'type',
    required: false,
    includeIfNull: false,
  )


  final ConsentementSaisieTypeEnum? type;



  @JsonKey(
    
    name: r'personneId',
    required: false,
    includeIfNull: false,
  )


  final String? personneId;



  @JsonKey(
    
    name: r'finalite',
    required: false,
    includeIfNull: false,
  )


  final ConsentementSaisieFinaliteEnum? finalite;



  @JsonKey(
    
    name: r'accorde',
    required: false,
    includeIfNull: false,
  )


  final bool? accorde;



  @JsonKey(
    
    name: r'modeRecueil',
    required: false,
    includeIfNull: false,
  )


  final ConsentementSaisieModeRecueilEnum? modeRecueil;



  @JsonKey(
    
    name: r'referenceDocument',
    required: false,
    includeIfNull: false,
  )


  final String? referenceDocument;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ConsentementSaisie &&
      other.type == type &&
      other.personneId == personneId &&
      other.finalite == finalite &&
      other.accorde == accorde &&
      other.modeRecueil == modeRecueil &&
      other.referenceDocument == referenceDocument;

    @override
    int get hashCode =>
        type.hashCode +
        personneId.hashCode +
        finalite.hashCode +
        accorde.hashCode +
        modeRecueil.hashCode +
        referenceDocument.hashCode;

  factory ConsentementSaisie.fromJson(Map<String, dynamic> json) => _$ConsentementSaisieFromJson(json);

  Map<String, dynamic> toJson() => _$ConsentementSaisieToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum ConsentementSaisieTypeEnum {
@JsonValue(r'LOCATAIRE')
LOCATAIRE(r'LOCATAIRE'),
@JsonValue(r'GARANT')
GARANT(r'GARANT'),
@JsonValue(r'PROPRIETAIRE')
PROPRIETAIRE(r'PROPRIETAIRE'),
@JsonValue(r'PRESTATAIRE')
PRESTATAIRE(r'PRESTATAIRE');

const ConsentementSaisieTypeEnum(this.value);

final String value;

@override
String toString() => value;
}


enum ConsentementSaisieFinaliteEnum {
@JsonValue(r'NOTIFICATIONS')
NOTIFICATIONS(r'NOTIFICATIONS'),
@JsonValue(r'PIECES_IDENTITE')
PIECES_IDENTITE(r'PIECES_IDENTITE'),
@JsonValue(r'TRANSMISSION_PROPRIETAIRE')
TRANSMISSION_PROPRIETAIRE(r'TRANSMISSION_PROPRIETAIRE'),
@JsonValue(r'APPLICATION_MOBILE')
APPLICATION_MOBILE(r'APPLICATION_MOBILE');

const ConsentementSaisieFinaliteEnum(this.value);

final String value;

@override
String toString() => value;
}


enum ConsentementSaisieModeRecueilEnum {
@JsonValue(r'PAPIER')
PAPIER(r'PAPIER'),
@JsonValue(r'ELECTRONIQUE')
ELECTRONIQUE(r'ELECTRONIQUE'),
@JsonValue(r'ORAL')
ORAL(r'ORAL');

const ConsentementSaisieModeRecueilEnum(this.value);

final String value;

@override
String toString() => value;
}


