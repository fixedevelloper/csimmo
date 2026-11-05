//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'depense_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class DepenseDto {
  /// Returns a new [DepenseDto] instance.
  DepenseDto({

     this.id,

     this.immeubleId,

     this.immeubleNom,

     this.lotId,

     this.proprietaireId,

     this.dateDepense,

     this.nature,

     this.montant,

     this.reference,

     this.statut,

     this.motifAnnulation,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'immeubleId',
    required: false,
    includeIfNull: false,
  )


  final String? immeubleId;



  @JsonKey(
    
    name: r'immeubleNom',
    required: false,
    includeIfNull: false,
  )


  final String? immeubleNom;



  @JsonKey(
    
    name: r'lotId',
    required: false,
    includeIfNull: false,
  )


  final String? lotId;



  @JsonKey(
    
    name: r'proprietaireId',
    required: false,
    includeIfNull: false,
  )


  final String? proprietaireId;



  @JsonKey(
    
    name: r'dateDepense',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateDepense;



  @JsonKey(
    
    name: r'nature',
    required: false,
    includeIfNull: false,
  )


  final String? nature;



  @JsonKey(
    
    name: r'montant',
    required: false,
    includeIfNull: false,
  )


  final int? montant;



  @JsonKey(
    
    name: r'reference',
    required: false,
    includeIfNull: false,
  )


  final String? reference;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final String? statut;



  @JsonKey(
    
    name: r'motifAnnulation',
    required: false,
    includeIfNull: false,
  )


  final String? motifAnnulation;





    @override
    bool operator ==(Object other) => identical(this, other) || other is DepenseDto &&
      other.id == id &&
      other.immeubleId == immeubleId &&
      other.immeubleNom == immeubleNom &&
      other.lotId == lotId &&
      other.proprietaireId == proprietaireId &&
      other.dateDepense == dateDepense &&
      other.nature == nature &&
      other.montant == montant &&
      other.reference == reference &&
      other.statut == statut &&
      other.motifAnnulation == motifAnnulation;

    @override
    int get hashCode =>
        id.hashCode +
        immeubleId.hashCode +
        immeubleNom.hashCode +
        lotId.hashCode +
        proprietaireId.hashCode +
        dateDepense.hashCode +
        nature.hashCode +
        montant.hashCode +
        reference.hashCode +
        statut.hashCode +
        motifAnnulation.hashCode;

  factory DepenseDto.fromJson(Map<String, dynamic> json) => _$DepenseDtoFromJson(json);

  Map<String, dynamic> toJson() => _$DepenseDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

