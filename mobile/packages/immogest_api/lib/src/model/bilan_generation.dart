//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'bilan_generation.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class BilanGeneration {
  /// Returns a new [BilanGeneration] instance.
  BilanGeneration({

     this.baux,

     this.echeancesCreees,

     this.echeancesAjustees,

     this.avancesImputees,

     this.statutsMisAJour,
  });

  @JsonKey(
    
    name: r'baux',
    required: false,
    includeIfNull: false,
  )


  final int? baux;



  @JsonKey(
    
    name: r'echeancesCreees',
    required: false,
    includeIfNull: false,
  )


  final int? echeancesCreees;



  @JsonKey(
    
    name: r'echeancesAjustees',
    required: false,
    includeIfNull: false,
  )


  final int? echeancesAjustees;



  @JsonKey(
    
    name: r'avancesImputees',
    required: false,
    includeIfNull: false,
  )


  final int? avancesImputees;



  @JsonKey(
    
    name: r'statutsMisAJour',
    required: false,
    includeIfNull: false,
  )


  final int? statutsMisAJour;





    @override
    bool operator ==(Object other) => identical(this, other) || other is BilanGeneration &&
      other.baux == baux &&
      other.echeancesCreees == echeancesCreees &&
      other.echeancesAjustees == echeancesAjustees &&
      other.avancesImputees == avancesImputees &&
      other.statutsMisAJour == statutsMisAJour;

    @override
    int get hashCode =>
        baux.hashCode +
        echeancesCreees.hashCode +
        echeancesAjustees.hashCode +
        avancesImputees.hashCode +
        statutsMisAJour.hashCode;

  factory BilanGeneration.fromJson(Map<String, dynamic> json) => _$BilanGenerationFromJson(json);

  Map<String, dynamic> toJson() => _$BilanGenerationToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

