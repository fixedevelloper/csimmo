//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'mise_en_demeure_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class MiseEnDemeureDto {
  /// Returns a new [MiseEnDemeureDto] instance.
  MiseEnDemeureDto({

     this.id,

     this.numero,

     this.bailId,

     this.bailNumero,

     this.echeanceId,

     this.locataireNom,

     this.montantArrieres,

     this.delaiJours,

     this.dateEmission,

     this.fichierId,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'numero',
    required: false,
    includeIfNull: false,
  )


  final String? numero;



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
    
    name: r'echeanceId',
    required: false,
    includeIfNull: false,
  )


  final String? echeanceId;



  @JsonKey(
    
    name: r'locataireNom',
    required: false,
    includeIfNull: false,
  )


  final String? locataireNom;



  @JsonKey(
    
    name: r'montantArrieres',
    required: false,
    includeIfNull: false,
  )


  final int? montantArrieres;



  @JsonKey(
    
    name: r'delaiJours',
    required: false,
    includeIfNull: false,
  )


  final int? delaiJours;



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





    @override
    bool operator ==(Object other) => identical(this, other) || other is MiseEnDemeureDto &&
      other.id == id &&
      other.numero == numero &&
      other.bailId == bailId &&
      other.bailNumero == bailNumero &&
      other.echeanceId == echeanceId &&
      other.locataireNom == locataireNom &&
      other.montantArrieres == montantArrieres &&
      other.delaiJours == delaiJours &&
      other.dateEmission == dateEmission &&
      other.fichierId == fichierId;

    @override
    int get hashCode =>
        id.hashCode +
        numero.hashCode +
        bailId.hashCode +
        bailNumero.hashCode +
        echeanceId.hashCode +
        locataireNom.hashCode +
        montantArrieres.hashCode +
        delaiJours.hashCode +
        dateEmission.hashCode +
        fichierId.hashCode;

  factory MiseEnDemeureDto.fromJson(Map<String, dynamic> json) => _$MiseEnDemeureDtoFromJson(json);

  Map<String, dynamic> toJson() => _$MiseEnDemeureDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

