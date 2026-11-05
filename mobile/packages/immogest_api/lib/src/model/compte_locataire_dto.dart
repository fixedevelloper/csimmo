//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/bail_compte.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'compte_locataire_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class CompteLocataireDto {
  /// Returns a new [CompteLocataireDto] instance.
  CompteLocataireDto({

     this.locataireId,

     this.locataireNom,

     this.arrieres,

     this.aVenir,

     this.credit,

     this.solde,

     this.baux,
  });

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
    
    name: r'arrieres',
    required: false,
    includeIfNull: false,
  )


  final int? arrieres;



  @JsonKey(
    
    name: r'aVenir',
    required: false,
    includeIfNull: false,
  )


  final int? aVenir;



  @JsonKey(
    
    name: r'credit',
    required: false,
    includeIfNull: false,
  )


  final int? credit;



  @JsonKey(
    
    name: r'solde',
    required: false,
    includeIfNull: false,
  )


  final int? solde;



  @JsonKey(
    
    name: r'baux',
    required: false,
    includeIfNull: false,
  )


  final List<BailCompte>? baux;





    @override
    bool operator ==(Object other) => identical(this, other) || other is CompteLocataireDto &&
      other.locataireId == locataireId &&
      other.locataireNom == locataireNom &&
      other.arrieres == arrieres &&
      other.aVenir == aVenir &&
      other.credit == credit &&
      other.solde == solde &&
      other.baux == baux;

    @override
    int get hashCode =>
        locataireId.hashCode +
        locataireNom.hashCode +
        arrieres.hashCode +
        aVenir.hashCode +
        credit.hashCode +
        solde.hashCode +
        baux.hashCode;

  factory CompteLocataireDto.fromJson(Map<String, dynamic> json) => _$CompteLocataireDtoFromJson(json);

  Map<String, dynamic> toJson() => _$CompteLocataireDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

