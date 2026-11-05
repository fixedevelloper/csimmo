//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'document_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class DocumentDto {
  /// Returns a new [DocumentDto] instance.
  DocumentDto({

     this.id,

     this.type,

     this.numero,

     this.statut,

     this.bailId,

     this.bailNumero,

     this.locataireId,

     this.locataireNom,

     this.echeanceId,

     this.paiementId,

     this.periodeDebut,

     this.periodeFin,

     this.montant,

     this.dateEmission,

     this.fichierId,

     this.motifAnnulation,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'type',
    required: false,
    includeIfNull: false,
  )


  final String? type;



  @JsonKey(
    
    name: r'numero',
    required: false,
    includeIfNull: false,
  )


  final String? numero;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final String? statut;



  @JsonKey(
    
    name: r'bailId',
    required: false,
    includeIfNull: false,
  )


  final String? bailId;



  @JsonKey(
    
    name: r'bailNumero',
    required: false,
    includeIfNull: false,
  )


  final String? bailNumero;



  @JsonKey(
    
    name: r'locataireId',
    required: false,
    includeIfNull: false,
  )


  final String? locataireId;



  @JsonKey(
    
    name: r'locataireNom',
    required: false,
    includeIfNull: false,
  )


  final String? locataireNom;



  @JsonKey(
    
    name: r'echeanceId',
    required: false,
    includeIfNull: false,
  )


  final String? echeanceId;



  @JsonKey(
    
    name: r'paiementId',
    required: false,
    includeIfNull: false,
  )


  final String? paiementId;



  @JsonKey(
    
    name: r'periodeDebut',
    required: false,
    includeIfNull: false,
  )


  final DateTime? periodeDebut;



  @JsonKey(
    
    name: r'periodeFin',
    required: false,
    includeIfNull: false,
  )


  final DateTime? periodeFin;



  @JsonKey(
    
    name: r'montant',
    required: false,
    includeIfNull: false,
  )


  final int? montant;



  @JsonKey(
    
    name: r'dateEmission',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateEmission;



  @JsonKey(
    
    name: r'fichierId',
    required: false,
    includeIfNull: false,
  )


  final String? fichierId;



  @JsonKey(
    
    name: r'motifAnnulation',
    required: false,
    includeIfNull: false,
  )


  final String? motifAnnulation;





    @override
    bool operator ==(Object other) => identical(this, other) || other is DocumentDto &&
      other.id == id &&
      other.type == type &&
      other.numero == numero &&
      other.statut == statut &&
      other.bailId == bailId &&
      other.bailNumero == bailNumero &&
      other.locataireId == locataireId &&
      other.locataireNom == locataireNom &&
      other.echeanceId == echeanceId &&
      other.paiementId == paiementId &&
      other.periodeDebut == periodeDebut &&
      other.periodeFin == periodeFin &&
      other.montant == montant &&
      other.dateEmission == dateEmission &&
      other.fichierId == fichierId &&
      other.motifAnnulation == motifAnnulation;

    @override
    int get hashCode =>
        id.hashCode +
        type.hashCode +
        numero.hashCode +
        statut.hashCode +
        bailId.hashCode +
        bailNumero.hashCode +
        locataireId.hashCode +
        locataireNom.hashCode +
        echeanceId.hashCode +
        paiementId.hashCode +
        periodeDebut.hashCode +
        periodeFin.hashCode +
        montant.hashCode +
        dateEmission.hashCode +
        fichierId.hashCode +
        motifAnnulation.hashCode;

  factory DocumentDto.fromJson(Map<String, dynamic> json) => _$DocumentDtoFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

