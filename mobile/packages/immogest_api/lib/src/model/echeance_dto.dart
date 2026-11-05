//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'echeance_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class EcheanceDto {
  /// Returns a new [EcheanceDto] instance.
  EcheanceDto({

     this.id,

     this.bailId,

     this.periodeDebut,

     this.periodeFin,

     this.dateEcheance,

     this.montant,

     this.montantPaye,

     this.reste,

     this.statut,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'bailId',
    required: false,
    includeIfNull: false,
  )


  final String? bailId;



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
    
    name: r'montantPaye',
    required: false,
    includeIfNull: false,
  )


  final int? montantPaye;



  @JsonKey(
    
    name: r'reste',
    required: false,
    includeIfNull: false,
  )


  final int? reste;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final EcheanceDtoStatutEnum? statut;





    @override
    bool operator ==(Object other) => identical(this, other) || other is EcheanceDto &&
      other.id == id &&
      other.bailId == bailId &&
      other.periodeDebut == periodeDebut &&
      other.periodeFin == periodeFin &&
      other.dateEcheance == dateEcheance &&
      other.montant == montant &&
      other.montantPaye == montantPaye &&
      other.reste == reste &&
      other.statut == statut;

    @override
    int get hashCode =>
        id.hashCode +
        bailId.hashCode +
        periodeDebut.hashCode +
        periodeFin.hashCode +
        dateEcheance.hashCode +
        montant.hashCode +
        montantPaye.hashCode +
        reste.hashCode +
        statut.hashCode;

  factory EcheanceDto.fromJson(Map<String, dynamic> json) => _$EcheanceDtoFromJson(json);

  Map<String, dynamic> toJson() => _$EcheanceDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum EcheanceDtoStatutEnum {
@JsonValue(r'A_VENIR')
A_VENIR(r'A_VENIR'),
@JsonValue(r'DUE')
DUE(r'DUE'),
@JsonValue(r'PARTIELLE')
PARTIELLE(r'PARTIELLE'),
@JsonValue(r'PAYEE')
PAYEE(r'PAYEE'),
@JsonValue(r'EN_RETARD')
EN_RETARD(r'EN_RETARD'),
@JsonValue(r'ANNULEE')
ANNULEE(r'ANNULEE');

const EcheanceDtoStatutEnum(this.value);

final String value;

@override
String toString() => value;
}


