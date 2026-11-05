//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'bail_compte.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class BailCompte {
  /// Returns a new [BailCompte] instance.
  BailCompte({

     this.bailId,

     this.numero,

     this.statut,
  });

  @JsonKey(
    
    name: r'bailId',
    required: false,
    includeIfNull: false,
  )


  final String? bailId;



  @JsonKey(
    
    name: r'numero',
    required: false,
    includeIfNull: false,
  )


  final String? numero;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final String? statut;





    @override
    bool operator ==(Object other) => identical(this, other) || other is BailCompte &&
      other.bailId == bailId &&
      other.numero == numero &&
      other.statut == statut;

    @override
    int get hashCode =>
        bailId.hashCode +
        numero.hashCode +
        statut.hashCode;

  factory BailCompte.fromJson(Map<String, dynamic> json) => _$BailCompteFromJson(json);

  Map<String, dynamic> toJson() => _$BailCompteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

