//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'garant_resume.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class GarantResume {
  /// Returns a new [GarantResume] instance.
  GarantResume({

     this.id,

     this.nom,

     this.telephone,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'nom',
    required: false,
    includeIfNull: false,
  )


  final String? nom;



  @JsonKey(
    
    name: r'telephone',
    required: false,
    includeIfNull: false,
  )


  final String? telephone;





    @override
    bool operator ==(Object other) => identical(this, other) || other is GarantResume &&
      other.id == id &&
      other.nom == nom &&
      other.telephone == telephone;

    @override
    int get hashCode =>
        id.hashCode +
        nom.hashCode +
        telephone.hashCode;

  factory GarantResume.fromJson(Map<String, dynamic> json) => _$GarantResumeFromJson(json);

  Map<String, dynamic> toJson() => _$GarantResumeToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

