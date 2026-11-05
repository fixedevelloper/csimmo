//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'demande_saisie.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class DemandeSaisie {
  /// Returns a new [DemandeSaisie] instance.
  DemandeSaisie({

     this.type,

     this.personneId,

     this.motif,
  });

  @JsonKey(
    
    name: r'type',
    required: false,
    includeIfNull: false,
  )


  final DemandeSaisieTypeEnum? type;



  @JsonKey(
    
    name: r'personneId',
    required: false,
    includeIfNull: false,
  )


  final String? personneId;



  @JsonKey(
    
    name: r'motif',
    required: false,
    includeIfNull: false,
  )


  final String? motif;





    @override
    bool operator ==(Object other) => identical(this, other) || other is DemandeSaisie &&
      other.type == type &&
      other.personneId == personneId &&
      other.motif == motif;

    @override
    int get hashCode =>
        type.hashCode +
        personneId.hashCode +
        motif.hashCode;

  factory DemandeSaisie.fromJson(Map<String, dynamic> json) => _$DemandeSaisieFromJson(json);

  Map<String, dynamic> toJson() => _$DemandeSaisieToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum DemandeSaisieTypeEnum {
@JsonValue(r'LOCATAIRE')
LOCATAIRE(r'LOCATAIRE'),
@JsonValue(r'GARANT')
GARANT(r'GARANT'),
@JsonValue(r'PROPRIETAIRE')
PROPRIETAIRE(r'PROPRIETAIRE'),
@JsonValue(r'PRESTATAIRE')
PRESTATAIRE(r'PRESTATAIRE');

const DemandeSaisieTypeEnum(this.value);

final String value;

@override
String toString() => value;
}


