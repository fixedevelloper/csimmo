//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'modele_bail_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ModeleBailDto {
  /// Returns a new [ModeleBailDto] instance.
  ModeleBailDto({

     this.bailleurNom,

     this.bailleurAdresse,

     this.preambule,

     this.clauses,

     this.piedDePage,

     this.variables,
  });

  @JsonKey(
    
    name: r'bailleurNom',
    required: false,
    includeIfNull: false,
  )


  final String? bailleurNom;



  @JsonKey(
    
    name: r'bailleurAdresse',
    required: false,
    includeIfNull: false,
  )


  final String? bailleurAdresse;



  @JsonKey(
    
    name: r'preambule',
    required: false,
    includeIfNull: false,
  )


  final String? preambule;



  @JsonKey(
    
    name: r'clauses',
    required: false,
    includeIfNull: false,
  )


  final String? clauses;



  @JsonKey(
    
    name: r'piedDePage',
    required: false,
    includeIfNull: false,
  )


  final String? piedDePage;



  @JsonKey(
    
    name: r'variables',
    required: false,
    includeIfNull: false,
  )


  final List<String>? variables;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ModeleBailDto &&
      other.bailleurNom == bailleurNom &&
      other.bailleurAdresse == bailleurAdresse &&
      other.preambule == preambule &&
      other.clauses == clauses &&
      other.piedDePage == piedDePage &&
      other.variables == variables;

    @override
    int get hashCode =>
        bailleurNom.hashCode +
        bailleurAdresse.hashCode +
        preambule.hashCode +
        clauses.hashCode +
        piedDePage.hashCode +
        variables.hashCode;

  factory ModeleBailDto.fromJson(Map<String, dynamic> json) => _$ModeleBailDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ModeleBailDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

