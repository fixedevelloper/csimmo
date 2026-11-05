//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'compteur_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class CompteurRequete {
  /// Returns a new [CompteurRequete] instance.
  CompteurRequete({

    required  this.type,

    required  this.numero,
  });

  @JsonKey(
    
    name: r'type',
    required: true,
    includeIfNull: false,
  )


  final CompteurRequeteTypeEnum type;



  @JsonKey(
    
    name: r'numero',
    required: true,
    includeIfNull: false,
  )


  final String numero;





    @override
    bool operator ==(Object other) => identical(this, other) || other is CompteurRequete &&
      other.type == type &&
      other.numero == numero;

    @override
    int get hashCode =>
        type.hashCode +
        numero.hashCode;

  factory CompteurRequete.fromJson(Map<String, dynamic> json) => _$CompteurRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$CompteurRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum CompteurRequeteTypeEnum {
@JsonValue(r'EAU')
EAU(r'EAU'),
@JsonValue(r'ELECTRICITE')
ELECTRICITE(r'ELECTRICITE'),
@JsonValue(r'GAZ')
GAZ(r'GAZ');

const CompteurRequeteTypeEnum(this.value);

final String value;

@override
String toString() => value;
}


