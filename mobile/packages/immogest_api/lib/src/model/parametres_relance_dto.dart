//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'parametres_relance_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ParametresRelanceDto {
  /// Returns a new [ParametresRelanceDto] instance.
  ParametresRelanceDto({

     this.actif,

     this.niveaux,

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
    required: false,
    includeIfNull: false,
  )


  final List<int>? niveaux;



  @JsonKey(
    
    name: r'delaiMiseEnDemeureJours',
    required: false,
    includeIfNull: false,
  )


  final int? delaiMiseEnDemeureJours;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ParametresRelanceDto &&
      other.actif == actif &&
      other.niveaux == niveaux &&
      other.delaiMiseEnDemeureJours == delaiMiseEnDemeureJours;

    @override
    int get hashCode =>
        actif.hashCode +
        niveaux.hashCode +
        delaiMiseEnDemeureJours.hashCode;

  factory ParametresRelanceDto.fromJson(Map<String, dynamic> json) => _$ParametresRelanceDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ParametresRelanceDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

