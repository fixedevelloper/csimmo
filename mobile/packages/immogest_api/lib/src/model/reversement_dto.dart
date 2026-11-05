//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'reversement_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ReversementDto {
  /// Returns a new [ReversementDto] instance.
  ReversementDto({

     this.id,

     this.proprietaireId,

     this.proprietaireNom,

     this.periode,

     this.statut,

     this.numero,

     this.loyersEncaisses,

     this.chargesEncaissees,

     this.brut,

     this.honoraires,

     this.tauxMoyenBp,

     this.depenses,

     this.net,

     this.sansMandat,

     this.fichierId,

     this.calculeLe,

     this.valideLe,

     this.payeLe,

     this.modePaiement,

     this.referencePaiement,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'proprietaireId',
    required: false,
    includeIfNull: false,
  )


  final String? proprietaireId;



  @JsonKey(
    
    name: r'proprietaireNom',
    required: false,
    includeIfNull: false,
  )


  final String? proprietaireNom;



  @JsonKey(
    
    name: r'periode',
    required: false,
    includeIfNull: false,
  )


  final DateTime? periode;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final String? statut;



  @JsonKey(
    
    name: r'numero',
    required: false,
    includeIfNull: false,
  )


  final String? numero;



  @JsonKey(
    
    name: r'loyersEncaisses',
    required: false,
    includeIfNull: false,
  )


  final int? loyersEncaisses;



  @JsonKey(
    
    name: r'chargesEncaissees',
    required: false,
    includeIfNull: false,
  )


  final int? chargesEncaissees;



  @JsonKey(
    
    name: r'brut',
    required: false,
    includeIfNull: false,
  )


  final int? brut;



  @JsonKey(
    
    name: r'honoraires',
    required: false,
    includeIfNull: false,
  )


  final int? honoraires;



  @JsonKey(
    
    name: r'tauxMoyenBp',
    required: false,
    includeIfNull: false,
  )


  final int? tauxMoyenBp;



  @JsonKey(
    
    name: r'depenses',
    required: false,
    includeIfNull: false,
  )


  final int? depenses;



  @JsonKey(
    
    name: r'net',
    required: false,
    includeIfNull: false,
  )


  final int? net;



  @JsonKey(
    
    name: r'sansMandat',
    required: false,
    includeIfNull: false,
  )


  final bool? sansMandat;



  @JsonKey(
    
    name: r'fichierId',
    required: false,
    includeIfNull: false,
  )


  final String? fichierId;



  @JsonKey(
    
    name: r'calculeLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? calculeLe;



  @JsonKey(
    
    name: r'valideLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? valideLe;



  @JsonKey(
    
    name: r'payeLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? payeLe;



  @JsonKey(
    
    name: r'modePaiement',
    required: false,
    includeIfNull: false,
  )


  final String? modePaiement;



  @JsonKey(
    
    name: r'referencePaiement',
    required: false,
    includeIfNull: false,
  )


  final String? referencePaiement;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ReversementDto &&
      other.id == id &&
      other.proprietaireId == proprietaireId &&
      other.proprietaireNom == proprietaireNom &&
      other.periode == periode &&
      other.statut == statut &&
      other.numero == numero &&
      other.loyersEncaisses == loyersEncaisses &&
      other.chargesEncaissees == chargesEncaissees &&
      other.brut == brut &&
      other.honoraires == honoraires &&
      other.tauxMoyenBp == tauxMoyenBp &&
      other.depenses == depenses &&
      other.net == net &&
      other.sansMandat == sansMandat &&
      other.fichierId == fichierId &&
      other.calculeLe == calculeLe &&
      other.valideLe == valideLe &&
      other.payeLe == payeLe &&
      other.modePaiement == modePaiement &&
      other.referencePaiement == referencePaiement;

    @override
    int get hashCode =>
        id.hashCode +
        proprietaireId.hashCode +
        proprietaireNom.hashCode +
        periode.hashCode +
        statut.hashCode +
        numero.hashCode +
        loyersEncaisses.hashCode +
        chargesEncaissees.hashCode +
        brut.hashCode +
        honoraires.hashCode +
        tauxMoyenBp.hashCode +
        depenses.hashCode +
        net.hashCode +
        sansMandat.hashCode +
        fichierId.hashCode +
        calculeLe.hashCode +
        valideLe.hashCode +
        payeLe.hashCode +
        modePaiement.hashCode +
        referencePaiement.hashCode;

  factory ReversementDto.fromJson(Map<String, dynamic> json) => _$ReversementDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ReversementDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

