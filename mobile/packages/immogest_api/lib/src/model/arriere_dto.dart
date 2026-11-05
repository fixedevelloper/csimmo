//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/relance_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'arriere_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ArriereDto {
  /// Returns a new [ArriereDto] instance.
  ArriereDto({

     this.echeanceId,

     this.bailId,

     this.bailNumero,

     this.locataireId,

     this.locataireNom,

     this.logement,

     this.periodeDebut,

     this.periodeFin,

     this.dateEcheance,

     this.montant,

     this.reste,

     this.joursRetard,

     this.derniereRelance,

     this.peutMiseEnDemeure,

     this.miseEnDemeureId,
  });

  @JsonKey(
    
    name: r'echeanceId',
    required: false,
    includeIfNull: false,
  )


  final String? echeanceId;



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
    
    name: r'logement',
    required: false,
    includeIfNull: false,
  )


  final String? logement;



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
    
    name: r'dateEcheance',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateEcheance;



  @JsonKey(
    
    name: r'montant',
    required: false,
    includeIfNull: false,
  )


  final int? montant;



  @JsonKey(
    
    name: r'reste',
    required: false,
    includeIfNull: false,
  )


  final int? reste;



  @JsonKey(
    
    name: r'joursRetard',
    required: false,
    includeIfNull: false,
  )


  final int? joursRetard;



  @JsonKey(
    
    name: r'derniereRelance',
    required: false,
    includeIfNull: false,
  )


  final RelanceDto? derniereRelance;



  @JsonKey(
    
    name: r'peutMiseEnDemeure',
    required: false,
    includeIfNull: false,
  )


  final bool? peutMiseEnDemeure;



  @JsonKey(
    
    name: r'miseEnDemeureId',
    required: false,
    includeIfNull: false,
  )


  final String? miseEnDemeureId;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ArriereDto &&
      other.echeanceId == echeanceId &&
      other.bailId == bailId &&
      other.bailNumero == bailNumero &&
      other.locataireId == locataireId &&
      other.locataireNom == locataireNom &&
      other.logement == logement &&
      other.periodeDebut == periodeDebut &&
      other.periodeFin == periodeFin &&
      other.dateEcheance == dateEcheance &&
      other.montant == montant &&
      other.reste == reste &&
      other.joursRetard == joursRetard &&
      other.derniereRelance == derniereRelance &&
      other.peutMiseEnDemeure == peutMiseEnDemeure &&
      other.miseEnDemeureId == miseEnDemeureId;

    @override
    int get hashCode =>
        echeanceId.hashCode +
        bailId.hashCode +
        bailNumero.hashCode +
        locataireId.hashCode +
        locataireNom.hashCode +
        logement.hashCode +
        periodeDebut.hashCode +
        periodeFin.hashCode +
        dateEcheance.hashCode +
        montant.hashCode +
        reste.hashCode +
        joursRetard.hashCode +
        derniereRelance.hashCode +
        peutMiseEnDemeure.hashCode +
        miseEnDemeureId.hashCode;

  factory ArriereDto.fromJson(Map<String, dynamic> json) => _$ArriereDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ArriereDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

