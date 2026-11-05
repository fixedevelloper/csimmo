//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/champ_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'fournisseur_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class FournisseurDto {
  /// Returns a new [FournisseurDto] instance.
  FournisseurDto({

     this.code,

     this.actif,

     this.configure,

     this.champs,
  });

  @JsonKey(
    
    name: r'code',
    required: false,
    includeIfNull: false,
  )


  final String? code;



  @JsonKey(
    
    name: r'actif',
    required: false,
    includeIfNull: false,
  )


  final bool? actif;



  @JsonKey(
    
    name: r'configure',
    required: false,
    includeIfNull: false,
  )


  final bool? configure;



  @JsonKey(
    
    name: r'champs',
    required: false,
    includeIfNull: false,
  )


  final List<ChampDto>? champs;





    @override
    bool operator ==(Object other) => identical(this, other) || other is FournisseurDto &&
      other.code == code &&
      other.actif == actif &&
      other.configure == configure &&
      other.champs == champs;

    @override
    int get hashCode =>
        code.hashCode +
        actif.hashCode +
        configure.hashCode +
        champs.hashCode;

  factory FournisseurDto.fromJson(Map<String, dynamic> json) => _$FournisseurDtoFromJson(json);

  Map<String, dynamic> toJson() => _$FournisseurDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

