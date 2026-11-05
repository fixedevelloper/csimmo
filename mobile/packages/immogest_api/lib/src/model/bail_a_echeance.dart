//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'bail_a_echeance.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class BailAEcheance {
  /// Returns a new [BailAEcheance] instance.
  BailAEcheance({

     this.bailId,

     this.numero,

     this.locataireNom,

     this.logement,

     this.dateFin,

     this.joursRestants,

     this.statut,
  });

  @JsonKey(
    
    name: r'bailId',
    required: false,
    includeIfNull: false,
  )


  final String? bailId;



  @JsonKey(
    
    name: r'numero',
    required: false,
    includeIfNull: false,
  )


  final String? numero;



  @JsonKey(
    
    name: r'locataireNom',
    required: false,
    includeIfNull: false,
  )


  final String? locataireNom;



  @JsonKey(
    
    name: r'logement',
    required: false,
    includeIfNull: false,
  )


  final String? logement;



  @JsonKey(
    
    name: r'dateFin',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateFin;



  @JsonKey(
    
    name: r'joursRestants',
    required: false,
    includeIfNull: false,
  )


  final int? joursRestants;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final String? statut;





    @override
    bool operator ==(Object other) => identical(this, other) || other is BailAEcheance &&
      other.bailId == bailId &&
      other.numero == numero &&
      other.locataireNom == locataireNom &&
      other.logement == logement &&
      other.dateFin == dateFin &&
      other.joursRestants == joursRestants &&
      other.statut == statut;

    @override
    int get hashCode =>
        bailId.hashCode +
        numero.hashCode +
        locataireNom.hashCode +
        logement.hashCode +
        dateFin.hashCode +
        joursRestants.hashCode +
        statut.hashCode;

  factory BailAEcheance.fromJson(Map<String, dynamic> json) => _$BailAEcheanceFromJson(json);

  Map<String, dynamic> toJson() => _$BailAEcheanceToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

