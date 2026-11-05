//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'etat_installation_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class EtatInstallationDto {
  /// Returns a new [EtatInstallationDto] instance.
  EtatInstallationDto({

     this.installee,

     this.empreinteServeur,
  });

  @JsonKey(
    
    name: r'installee',
    required: false,
    includeIfNull: false,
  )


  final bool? installee;



  @JsonKey(
    
    name: r'empreinteServeur',
    required: false,
    includeIfNull: false,
  )


  final String? empreinteServeur;





    @override
    bool operator ==(Object other) => identical(this, other) || other is EtatInstallationDto &&
      other.installee == installee &&
      other.empreinteServeur == empreinteServeur;

    @override
    int get hashCode =>
        installee.hashCode +
        empreinteServeur.hashCode;

  factory EtatInstallationDto.fromJson(Map<String, dynamic> json) => _$EtatInstallationDtoFromJson(json);

  Map<String, dynamic> toJson() => _$EtatInstallationDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

