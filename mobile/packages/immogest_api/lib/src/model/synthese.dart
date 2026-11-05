//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/occupation.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'synthese.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class Synthese {
  /// Returns a new [Synthese] instance.
  Synthese({

     this.occupation,

     this.attenduMois,

     this.encaisseMois,

     this.tauxRecouvrementMoisBp,

     this.arrieresTotal,

     this.echeancesEnRetard,

     this.bauxAEcheance,
  });

  @JsonKey(
    
    name: r'occupation',
    required: false,
    includeIfNull: false,
  )


  final Occupation? occupation;



  @JsonKey(
    
    name: r'attenduMois',
    required: false,
    includeIfNull: false,
  )


  final int? attenduMois;



  @JsonKey(
    
    name: r'encaisseMois',
    required: false,
    includeIfNull: false,
  )


  final int? encaisseMois;



  @JsonKey(
    
    name: r'tauxRecouvrementMoisBp',
    required: false,
    includeIfNull: false,
  )


  final int? tauxRecouvrementMoisBp;



  @JsonKey(
    
    name: r'arrieresTotal',
    required: false,
    includeIfNull: false,
  )


  final int? arrieresTotal;



  @JsonKey(
    
    name: r'echeancesEnRetard',
    required: false,
    includeIfNull: false,
  )


  final int? echeancesEnRetard;



  @JsonKey(
    
    name: r'bauxAEcheance',
    required: false,
    includeIfNull: false,
  )


  final int? bauxAEcheance;





    @override
    bool operator ==(Object other) => identical(this, other) || other is Synthese &&
      other.occupation == occupation &&
      other.attenduMois == attenduMois &&
      other.encaisseMois == encaisseMois &&
      other.tauxRecouvrementMoisBp == tauxRecouvrementMoisBp &&
      other.arrieresTotal == arrieresTotal &&
      other.echeancesEnRetard == echeancesEnRetard &&
      other.bauxAEcheance == bauxAEcheance;

    @override
    int get hashCode =>
        occupation.hashCode +
        attenduMois.hashCode +
        encaisseMois.hashCode +
        tauxRecouvrementMoisBp.hashCode +
        arrieresTotal.hashCode +
        echeancesEnRetard.hashCode +
        bauxAEcheance.hashCode;

  factory Synthese.fromJson(Map<String, dynamic> json) => _$SyntheseFromJson(json);

  Map<String, dynamic> toJson() => _$SyntheseToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

