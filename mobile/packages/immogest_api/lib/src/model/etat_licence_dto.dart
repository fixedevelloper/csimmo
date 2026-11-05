//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'etat_licence_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class EtatLicenceDto {
  /// Returns a new [EtatLicenceDto] instance.
  EtatLicenceDto({

     this.etat,

     this.lectureSeule,

     this.detail,

     this.licenseId,

     this.client,

     this.palierLots,

     this.dateDebut,

     this.dateExpiration,

     this.joursRestants,

     this.alerteJours,

     this.fonctionnalites,

     this.empreinteServeur,

     this.verificationEnLigneActive,

     this.derniereVerificationEnLigne,

     this.joursDeGraceRestants,
  });

  @JsonKey(
    
    name: r'etat',
    required: false,
    includeIfNull: false,
  )


  final EtatLicenceDtoEtatEnum? etat;



  @JsonKey(
    
    name: r'lectureSeule',
    required: false,
    includeIfNull: false,
  )


  final bool? lectureSeule;



  @JsonKey(
    
    name: r'detail',
    required: false,
    includeIfNull: false,
  )


  final String? detail;



  @JsonKey(
    
    name: r'licenseId',
    required: false,
    includeIfNull: false,
  )


  final String? licenseId;



  @JsonKey(
    
    name: r'client',
    required: false,
    includeIfNull: false,
  )


  final String? client;



  @JsonKey(
    
    name: r'palierLots',
    required: false,
    includeIfNull: false,
  )


  final int? palierLots;



  @JsonKey(
    
    name: r'dateDebut',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateDebut;



  @JsonKey(
    
    name: r'dateExpiration',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateExpiration;



  @JsonKey(
    
    name: r'joursRestants',
    required: false,
    includeIfNull: false,
  )


  final int? joursRestants;



  @JsonKey(
    
    name: r'alerteJours',
    required: false,
    includeIfNull: false,
  )


  final int? alerteJours;



  @JsonKey(
    
    name: r'fonctionnalites',
    required: false,
    includeIfNull: false,
  )


  final List<String>? fonctionnalites;



  @JsonKey(
    
    name: r'empreinteServeur',
    required: false,
    includeIfNull: false,
  )


  final String? empreinteServeur;



  @JsonKey(
    
    name: r'verificationEnLigneActive',
    required: false,
    includeIfNull: false,
  )


  final bool? verificationEnLigneActive;



  @JsonKey(
    
    name: r'derniereVerificationEnLigne',
    required: false,
    includeIfNull: false,
  )


  final DateTime? derniereVerificationEnLigne;



  @JsonKey(
    
    name: r'joursDeGraceRestants',
    required: false,
    includeIfNull: false,
  )


  final int? joursDeGraceRestants;





    @override
    bool operator ==(Object other) => identical(this, other) || other is EtatLicenceDto &&
      other.etat == etat &&
      other.lectureSeule == lectureSeule &&
      other.detail == detail &&
      other.licenseId == licenseId &&
      other.client == client &&
      other.palierLots == palierLots &&
      other.dateDebut == dateDebut &&
      other.dateExpiration == dateExpiration &&
      other.joursRestants == joursRestants &&
      other.alerteJours == alerteJours &&
      other.fonctionnalites == fonctionnalites &&
      other.empreinteServeur == empreinteServeur &&
      other.verificationEnLigneActive == verificationEnLigneActive &&
      other.derniereVerificationEnLigne == derniereVerificationEnLigne &&
      other.joursDeGraceRestants == joursDeGraceRestants;

    @override
    int get hashCode =>
        etat.hashCode +
        lectureSeule.hashCode +
        detail.hashCode +
        licenseId.hashCode +
        client.hashCode +
        palierLots.hashCode +
        dateDebut.hashCode +
        dateExpiration.hashCode +
        joursRestants.hashCode +
        alerteJours.hashCode +
        fonctionnalites.hashCode +
        empreinteServeur.hashCode +
        verificationEnLigneActive.hashCode +
        derniereVerificationEnLigne.hashCode +
        joursDeGraceRestants.hashCode;

  factory EtatLicenceDto.fromJson(Map<String, dynamic> json) => _$EtatLicenceDtoFromJson(json);

  Map<String, dynamic> toJson() => _$EtatLicenceDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum EtatLicenceDtoEtatEnum {
@JsonValue(r'AUCUNE')
AUCUNE(r'AUCUNE'),
@JsonValue(r'VALIDE')
VALIDE(r'VALIDE'),
@JsonValue(r'EXPIREE')
EXPIREE(r'EXPIREE'),
@JsonValue(r'INVALIDE')
INVALIDE(r'INVALIDE'),
@JsonValue(r'REVOQUEE')
REVOQUEE(r'REVOQUEE'),
@JsonValue(r'VERIFICATION_REQUISE')
VERIFICATION_REQUISE(r'VERIFICATION_REQUISE');

const EtatLicenceDtoEtatEnum(this.value);

final String value;

@override
String toString() => value;
}


