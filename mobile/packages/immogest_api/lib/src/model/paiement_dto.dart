//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'paiement_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PaiementDto {
  /// Returns a new [PaiementDto] instance.
  PaiementDto({

     this.id,

     this.bailId,

     this.bailNumero,

     this.locataireId,

     this.locataireNom,

     this.type,

     this.mode,

     this.montant,

     this.datePaiement,

     this.reference,

     this.saisiPar,

     this.saisiParNom,

     this.motif,

     this.contrePasse,

     this.contrePassationDe,

     this.creeLe,
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
    
    name: r'type',
    required: false,
    includeIfNull: false,
  )


  final String? type;



  @JsonKey(
    
    name: r'mode',
    required: false,
    includeIfNull: false,
  )


  final PaiementDtoModeEnum? mode;



  @JsonKey(
    
    name: r'montant',
    required: false,
    includeIfNull: false,
  )


  final int? montant;



  @JsonKey(
    
    name: r'datePaiement',
    required: false,
    includeIfNull: false,
  )


  final DateTime? datePaiement;



  @JsonKey(
    
    name: r'reference',
    required: false,
    includeIfNull: false,
  )


  final String? reference;



  @JsonKey(
    
    name: r'saisiPar',
    required: false,
    includeIfNull: false,
  )


  final String? saisiPar;



  @JsonKey(
    
    name: r'saisiParNom',
    required: false,
    includeIfNull: false,
  )


  final String? saisiParNom;



  @JsonKey(
    
    name: r'motif',
    required: false,
    includeIfNull: false,
  )


  final String? motif;



  @JsonKey(
    
    name: r'contrePasse',
    required: false,
    includeIfNull: false,
  )


  final bool? contrePasse;



  @JsonKey(
    
    name: r'contrePassationDe',
    required: false,
    includeIfNull: false,
  )


  final String? contrePassationDe;



  @JsonKey(
    
    name: r'creeLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? creeLe;





    @override
    bool operator ==(Object other) => identical(this, other) || other is PaiementDto &&
      other.id == id &&
      other.bailId == bailId &&
      other.bailNumero == bailNumero &&
      other.locataireId == locataireId &&
      other.locataireNom == locataireNom &&
      other.type == type &&
      other.mode == mode &&
      other.montant == montant &&
      other.datePaiement == datePaiement &&
      other.reference == reference &&
      other.saisiPar == saisiPar &&
      other.saisiParNom == saisiParNom &&
      other.motif == motif &&
      other.contrePasse == contrePasse &&
      other.contrePassationDe == contrePassationDe &&
      other.creeLe == creeLe;

    @override
    int get hashCode =>
        id.hashCode +
        bailId.hashCode +
        bailNumero.hashCode +
        locataireId.hashCode +
        locataireNom.hashCode +
        type.hashCode +
        mode.hashCode +
        montant.hashCode +
        datePaiement.hashCode +
        reference.hashCode +
        saisiPar.hashCode +
        saisiParNom.hashCode +
        motif.hashCode +
        contrePasse.hashCode +
        contrePassationDe.hashCode +
        creeLe.hashCode;

  factory PaiementDto.fromJson(Map<String, dynamic> json) => _$PaiementDtoFromJson(json);

  Map<String, dynamic> toJson() => _$PaiementDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum PaiementDtoModeEnum {
@JsonValue(r'ESPECES')
ESPECES(r'ESPECES'),
@JsonValue(r'VIREMENT')
VIREMENT(r'VIREMENT'),
@JsonValue(r'MTN_MOMO')
MTN_MOMO(r'MTN_MOMO'),
@JsonValue(r'ORANGE_MONEY')
ORANGE_MONEY(r'ORANGE_MONEY');

const PaiementDtoModeEnum(this.value);

final String value;

@override
String toString() => value;
}


