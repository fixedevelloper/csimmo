//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'garant_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class GarantDto {
  /// Returns a new [GarantDto] instance.
  GarantDto({

     this.id,

     this.locataireId,

     this.nom,

     this.lien,

     this.profession,

     this.telephone,

     this.adresse,

     this.numeroCni,

     this.archive,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'locataireId',
    required: false,
    includeIfNull: false,
  )


  final String? locataireId;



  @JsonKey(
    
    name: r'nom',
    required: false,
    includeIfNull: false,
  )


  final String? nom;



  @JsonKey(
    
    name: r'lien',
    required: false,
    includeIfNull: false,
  )


  final String? lien;



  @JsonKey(
    
    name: r'profession',
    required: false,
    includeIfNull: false,
  )


  final String? profession;



  @JsonKey(
    
    name: r'telephone',
    required: false,
    includeIfNull: false,
  )


  final String? telephone;



  @JsonKey(
    
    name: r'adresse',
    required: false,
    includeIfNull: false,
  )


  final String? adresse;



  @JsonKey(
    
    name: r'numeroCni',
    required: false,
    includeIfNull: false,
  )


  final String? numeroCni;



  @JsonKey(
    
    name: r'archive',
    required: false,
    includeIfNull: false,
  )


  final bool? archive;





    @override
    bool operator ==(Object other) => identical(this, other) || other is GarantDto &&
      other.id == id &&
      other.locataireId == locataireId &&
      other.nom == nom &&
      other.lien == lien &&
      other.profession == profession &&
      other.telephone == telephone &&
      other.adresse == adresse &&
      other.numeroCni == numeroCni &&
      other.archive == archive;

    @override
    int get hashCode =>
        id.hashCode +
        locataireId.hashCode +
        nom.hashCode +
        lien.hashCode +
        profession.hashCode +
        telephone.hashCode +
        adresse.hashCode +
        numeroCni.hashCode +
        archive.hashCode;

  factory GarantDto.fromJson(Map<String, dynamic> json) => _$GarantDtoFromJson(json);

  Map<String, dynamic> toJson() => _$GarantDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

