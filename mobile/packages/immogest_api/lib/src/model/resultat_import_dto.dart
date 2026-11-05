//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/erreur_import_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'resultat_import_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ResultatImportDto {
  /// Returns a new [ResultatImportDto] instance.
  ResultatImportDto({

     this.importe,

     this.crees,

     this.echeancesReprises,

     this.erreurs,
  });

  @JsonKey(
    
    name: r'importe',
    required: false,
    includeIfNull: false,
  )


  final bool? importe;



  @JsonKey(
    
    name: r'crees',
    required: false,
    includeIfNull: false,
  )


  final Map<String, int>? crees;



  @JsonKey(
    
    name: r'echeancesReprises',
    required: false,
    includeIfNull: false,
  )


  final int? echeancesReprises;



  @JsonKey(
    
    name: r'erreurs',
    required: false,
    includeIfNull: false,
  )


  final List<ErreurImportDto>? erreurs;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ResultatImportDto &&
      other.importe == importe &&
      other.crees == crees &&
      other.echeancesReprises == echeancesReprises &&
      other.erreurs == erreurs;

    @override
    int get hashCode =>
        importe.hashCode +
        crees.hashCode +
        echeancesReprises.hashCode +
        erreurs.hashCode;

  factory ResultatImportDto.fromJson(Map<String, dynamic> json) => _$ResultatImportDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ResultatImportDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

