//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'diagnostic_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class DiagnosticDto {
  /// Returns a new [DiagnosticDto] instance.
  DiagnosticDto({

     this.versionApplication,

     this.versionJava,

     this.versionBase,

     this.tailleBaseOctets,

     this.disqueTotalOctets,

     this.disqueLibreOctets,

     this.etatLicence,

     this.joursLicenceRestants,

     this.derniereSauvegarde,

     this.ageDerniereSauvegardeHeures,

     this.copieExterne,

     this.alertes,
  });

  @JsonKey(
    
    name: r'versionApplication',
    required: false,
    includeIfNull: false,
  )


  final String? versionApplication;



  @JsonKey(
    
    name: r'versionJava',
    required: false,
    includeIfNull: false,
  )


  final String? versionJava;



  @JsonKey(
    
    name: r'versionBase',
    required: false,
    includeIfNull: false,
  )


  final String? versionBase;



  @JsonKey(
    
    name: r'tailleBaseOctets',
    required: false,
    includeIfNull: false,
  )


  final int? tailleBaseOctets;



  @JsonKey(
    
    name: r'disqueTotalOctets',
    required: false,
    includeIfNull: false,
  )


  final int? disqueTotalOctets;



  @JsonKey(
    
    name: r'disqueLibreOctets',
    required: false,
    includeIfNull: false,
  )


  final int? disqueLibreOctets;



  @JsonKey(
    
    name: r'etatLicence',
    required: false,
    includeIfNull: false,
  )


  final String? etatLicence;



  @JsonKey(
    
    name: r'joursLicenceRestants',
    required: false,
    includeIfNull: false,
  )


  final int? joursLicenceRestants;



  @JsonKey(
    
    name: r'derniereSauvegarde',
    required: false,
    includeIfNull: false,
  )


  final DateTime? derniereSauvegarde;



  @JsonKey(
    
    name: r'ageDerniereSauvegardeHeures',
    required: false,
    includeIfNull: false,
  )


  final int? ageDerniereSauvegardeHeures;



  @JsonKey(
    
    name: r'copieExterne',
    required: false,
    includeIfNull: false,
  )


  final String? copieExterne;



  @JsonKey(
    
    name: r'alertes',
    required: false,
    includeIfNull: false,
  )


  final List<String>? alertes;





    @override
    bool operator ==(Object other) => identical(this, other) || other is DiagnosticDto &&
      other.versionApplication == versionApplication &&
      other.versionJava == versionJava &&
      other.versionBase == versionBase &&
      other.tailleBaseOctets == tailleBaseOctets &&
      other.disqueTotalOctets == disqueTotalOctets &&
      other.disqueLibreOctets == disqueLibreOctets &&
      other.etatLicence == etatLicence &&
      other.joursLicenceRestants == joursLicenceRestants &&
      other.derniereSauvegarde == derniereSauvegarde &&
      other.ageDerniereSauvegardeHeures == ageDerniereSauvegardeHeures &&
      other.copieExterne == copieExterne &&
      other.alertes == alertes;

    @override
    int get hashCode =>
        versionApplication.hashCode +
        versionJava.hashCode +
        versionBase.hashCode +
        tailleBaseOctets.hashCode +
        disqueTotalOctets.hashCode +
        disqueLibreOctets.hashCode +
        etatLicence.hashCode +
        joursLicenceRestants.hashCode +
        derniereSauvegarde.hashCode +
        ageDerniereSauvegardeHeures.hashCode +
        copieExterne.hashCode +
        alertes.hashCode;

  factory DiagnosticDto.fromJson(Map<String, dynamic> json) => _$DiagnosticDtoFromJson(json);

  Map<String, dynamic> toJson() => _$DiagnosticDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

