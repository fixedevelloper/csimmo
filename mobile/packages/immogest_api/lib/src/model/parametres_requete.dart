//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'parametres_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ParametresRequete {
  /// Returns a new [ParametresRequete] instance.
  ParametresRequete({

     this.actif,

    required  this.niveaux,

     this.delaiMiseEnDemeureJours,
  });

  @JsonKey(
    
    name: r'actif',
    required: false,
    includeIfNull: false,
  )


  final bool? actif;



  @JsonKey(
    
    name: r'niveaux',
    required: true,
    includeIfNull: false,
  )


  final List<int> niveaux;



  @JsonKey(
    
    name: r'delaiMiseEnDemeureJours',
    required: false,
    includeIfNull: false,
  )


  final int? delaiMiseEnDemeureJours;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ParametresRequete &&
      other.actif == actif &&
      other.niveaux == niveaux &&
      other.delaiMiseEnDemeureJours == delaiMiseEnDemeureJours;

    @override
    int get hashCode =>
        actif.hashCode +
        niveaux.hashCode +
        delaiMiseEnDemeureJours.hashCode;

  factory ParametresRequete.fromJson(Map<String, dynamic> json) => _$ParametresRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$ParametresRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

