//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'initiation_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class InitiationRequete {
  /// Returns a new [InitiationRequete] instance.
  InitiationRequete({

    required  this.bailId,

    required  this.montant,

    required  this.operateur,

    required  this.telephone,
  });

  @JsonKey(
    
    name: r'bailId',
    required: true,
    includeIfNull: false,
  )


  final String bailId;



          // minimum: 1
          // maximum: 20000000
  @JsonKey(
    
    name: r'montant',
    required: true,
    includeIfNull: false,
  )


  final int montant;



  @JsonKey(
    
    name: r'operateur',
    required: true,
    includeIfNull: false,
  )


  final InitiationRequeteOperateurEnum operateur;



  @JsonKey(
    
    name: r'telephone',
    required: true,
    includeIfNull: false,
  )


  final String telephone;





    @override
    bool operator ==(Object other) => identical(this, other) || other is InitiationRequete &&
      other.bailId == bailId &&
      other.montant == montant &&
      other.operateur == operateur &&
      other.telephone == telephone;

    @override
    int get hashCode =>
        bailId.hashCode +
        montant.hashCode +
        operateur.hashCode +
        telephone.hashCode;

  factory InitiationRequete.fromJson(Map<String, dynamic> json) => _$InitiationRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$InitiationRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum InitiationRequeteOperateurEnum {
@JsonValue(r'MTN_MOMO')
MTN_MOMO(r'MTN_MOMO'),
@JsonValue(r'ORANGE_MONEY')
ORANGE_MONEY(r'ORANGE_MONEY');

const InitiationRequeteOperateurEnum(this.value);

final String value;

@override
String toString() => value;
}


