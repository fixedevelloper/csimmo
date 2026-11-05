//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/sauvegarde_dto.dart';
import 'package:immogest_api/src/model/operation_sauvegarde_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'etat_sauvegardes_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class EtatSauvegardesDto {
  /// Returns a new [EtatSauvegardesDto] instance.
  EtatSauvegardesDto({

     this.sauvegardes,

     this.operation,

     this.heureQuotidienne,

     this.versionInstallee,
  });

  @JsonKey(
    
    name: r'sauvegardes',
    required: false,
    includeIfNull: false,
  )


  final List<SauvegardeDto>? sauvegardes;



  @JsonKey(
    
    name: r'operation',
    required: false,
    includeIfNull: false,
  )


  final OperationSauvegardeDto? operation;



  @JsonKey(
    
    name: r'heureQuotidienne',
    required: false,
    includeIfNull: false,
  )


  final String? heureQuotidienne;



  @JsonKey(
    
    name: r'versionInstallee',
    required: false,
    includeIfNull: false,
  )


  final String? versionInstallee;





    @override
    bool operator ==(Object other) => identical(this, other) || other is EtatSauvegardesDto &&
      other.sauvegardes == sauvegardes &&
      other.operation == operation &&
      other.heureQuotidienne == heureQuotidienne &&
      other.versionInstallee == versionInstallee;

    @override
    int get hashCode =>
        sauvegardes.hashCode +
        operation.hashCode +
        heureQuotidienne.hashCode +
        versionInstallee.hashCode;

  factory EtatSauvegardesDto.fromJson(Map<String, dynamic> json) => _$EtatSauvegardesDtoFromJson(json);

  Map<String, dynamic> toJson() => _$EtatSauvegardesDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

