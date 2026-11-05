//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'bilan_relances.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class BilanRelances {
  /// Returns a new [BilanRelances] instance.
  BilanRelances({

     this.examinees,

     this.envoyees,

     this.sansCanal,

     this.ignorees,
  });

  @JsonKey(
    
    name: r'examinees',
    required: false,
    includeIfNull: false,
  )


  final int? examinees;



  @JsonKey(
    
    name: r'envoyees',
    required: false,
    includeIfNull: false,
  )


  final int? envoyees;



  @JsonKey(
    
    name: r'sansCanal',
    required: false,
    includeIfNull: false,
  )


  final int? sansCanal;



  @JsonKey(
    
    name: r'ignorees',
    required: false,
    includeIfNull: false,
  )


  final int? ignorees;





    @override
    bool operator ==(Object other) => identical(this, other) || other is BilanRelances &&
      other.examinees == examinees &&
      other.envoyees == envoyees &&
      other.sansCanal == sansCanal &&
      other.ignorees == ignorees;

    @override
    int get hashCode =>
        examinees.hashCode +
        envoyees.hashCode +
        sansCanal.hashCode +
        ignorees.hashCode;

  factory BilanRelances.fromJson(Map<String, dynamic> json) => _$BilanRelancesFromJson(json);

  Map<String, dynamic> toJson() => _$BilanRelancesToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

