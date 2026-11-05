//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'proprietaire_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ProprietaireDto {
  /// Returns a new [ProprietaireDto] instance.
  ProprietaireDto({

     this.id,

     this.nom,

     this.telephone,

     this.email,

     this.adresse,

     this.modeReversement,

     this.coordonneesReversement,

     this.utilisateurId,

     this.notes,

     this.archive,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'nom',
    required: false,
    includeIfNull: false,
  )


  final String? nom;



  @JsonKey(
    
    name: r'telephone',
    required: false,
    includeIfNull: false,
  )


  final String? telephone;



  @JsonKey(
    
    name: r'email',
    required: false,
    includeIfNull: false,
  )


  final String? email;



  @JsonKey(
    
    name: r'adresse',
    required: false,
    includeIfNull: false,
  )


  final String? adresse;



  @JsonKey(
    
    name: r'modeReversement',
    required: false,
    includeIfNull: false,
  )


  final ProprietaireDtoModeReversementEnum? modeReversement;



  @JsonKey(
    
    name: r'coordonneesReversement',
    required: false,
    includeIfNull: false,
  )


  final String? coordonneesReversement;



  @JsonKey(
    
    name: r'utilisateurId',
    required: false,
    includeIfNull: false,
  )


  final String? utilisateurId;



  @JsonKey(
    
    name: r'notes',
    required: false,
    includeIfNull: false,
  )


  final String? notes;



  @JsonKey(
    
    name: r'archive',
    required: false,
    includeIfNull: false,
  )


  final bool? archive;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ProprietaireDto &&
      other.id == id &&
      other.nom == nom &&
      other.telephone == telephone &&
      other.email == email &&
      other.adresse == adresse &&
      other.modeReversement == modeReversement &&
      other.coordonneesReversement == coordonneesReversement &&
      other.utilisateurId == utilisateurId &&
      other.notes == notes &&
      other.archive == archive;

    @override
    int get hashCode =>
        id.hashCode +
        nom.hashCode +
        telephone.hashCode +
        email.hashCode +
        adresse.hashCode +
        modeReversement.hashCode +
        coordonneesReversement.hashCode +
        utilisateurId.hashCode +
        notes.hashCode +
        archive.hashCode;

  factory ProprietaireDto.fromJson(Map<String, dynamic> json) => _$ProprietaireDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ProprietaireDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum ProprietaireDtoModeReversementEnum {
@JsonValue(r'VIREMENT')
VIREMENT(r'VIREMENT'),
@JsonValue(r'MOBILE_MONEY')
MOBILE_MONEY(r'MOBILE_MONEY'),
@JsonValue(r'ESPECES')
ESPECES(r'ESPECES'),
@JsonValue(r'CHEQUE')
CHEQUE(r'CHEQUE');

const ProprietaireDtoModeReversementEnum(this.value);

final String value;

@override
String toString() => value;
}


