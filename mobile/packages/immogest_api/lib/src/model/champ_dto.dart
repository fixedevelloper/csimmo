//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'champ_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ChampDto {
  /// Returns a new [ChampDto] instance.
  ChampDto({

     this.nom,

     this.secret,

     this.requis,

     this.valeur,

     this.renseigne,
  });

  @JsonKey(
    
    name: r'nom',
    required: false,
    includeIfNull: false,
  )


  final String? nom;



  @JsonKey(
    
    name: r'secret',
    required: false,
    includeIfNull: false,
  )


  final bool? secret;



  @JsonKey(
    
    name: r'requis',
    required: false,
    includeIfNull: false,
  )


  final bool? requis;



  @JsonKey(
    
    name: r'valeur',
    required: false,
    includeIfNull: false,
  )


  final String? valeur;



  @JsonKey(
    
    name: r'renseigne',
    required: false,
    includeIfNull: false,
  )


  final bool? renseigne;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ChampDto &&
      other.nom == nom &&
      other.secret == secret &&
      other.requis == requis &&
      other.valeur == valeur &&
      other.renseigne == renseigne;

    @override
    int get hashCode =>
        nom.hashCode +
        secret.hashCode +
        requis.hashCode +
        valeur.hashCode +
        renseigne.hashCode;

  factory ChampDto.fromJson(Map<String, dynamic> json) => _$ChampDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ChampDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

