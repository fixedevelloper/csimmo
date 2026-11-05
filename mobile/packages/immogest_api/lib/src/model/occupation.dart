//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'occupation.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class Occupation {
  /// Returns a new [Occupation] instance.
  Occupation({

     this.libres,

     this.occupes,

     this.enTravaux,

     this.tauxBp,
  });

  @JsonKey(
    
    name: r'libres',
    required: false,
    includeIfNull: false,
  )


  final int? libres;



  @JsonKey(
    
    name: r'occupes',
    required: false,
    includeIfNull: false,
  )


  final int? occupes;



  @JsonKey(
    
    name: r'enTravaux',
    required: false,
    includeIfNull: false,
  )


  final int? enTravaux;



  @JsonKey(
    
    name: r'tauxBp',
    required: false,
    includeIfNull: false,
  )


  final int? tauxBp;





    @override
    bool operator ==(Object other) => identical(this, other) || other is Occupation &&
      other.libres == libres &&
      other.occupes == occupes &&
      other.enTravaux == enTravaux &&
      other.tauxBp == tauxBp;

    @override
    int get hashCode =>
        libres.hashCode +
        occupes.hashCode +
        enTravaux.hashCode +
        tauxBp.hashCode;

  factory Occupation.fromJson(Map<String, dynamic> json) => _$OccupationFromJson(json);

  Map<String, dynamic> toJson() => _$OccupationToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

