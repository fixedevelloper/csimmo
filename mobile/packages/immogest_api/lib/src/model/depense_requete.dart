//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'depense_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class DepenseRequete {
  /// Returns a new [DepenseRequete] instance.
  DepenseRequete({

    required  this.immeubleId,

     this.lotId,

    required  this.dateDepense,

    required  this.nature,

    required  this.montant,

     this.reference,
  });

  @JsonKey(
    
    name: r'immeubleId',
    required: true,
    includeIfNull: false,
  )


  final String immeubleId;



  @JsonKey(
    
    name: r'lotId',
    required: false,
    includeIfNull: false,
  )


  final String? lotId;



  @JsonKey(
    
    name: r'dateDepense',
    required: true,
    includeIfNull: false,
  )


  final DateTime dateDepense;



  @JsonKey(
    
    name: r'nature',
    required: true,
    includeIfNull: false,
  )


  final String nature;



          // minimum: 1
  @JsonKey(
    
    name: r'montant',
    required: true,
    includeIfNull: false,
  )


  final int montant;



  @JsonKey(
    
    name: r'reference',
    required: false,
    includeIfNull: false,
  )


  final String? reference;





    @override
    bool operator ==(Object other) => identical(this, other) || other is DepenseRequete &&
      other.immeubleId == immeubleId &&
      other.lotId == lotId &&
      other.dateDepense == dateDepense &&
      other.nature == nature &&
      other.montant == montant &&
      other.reference == reference;

    @override
    int get hashCode =>
        immeubleId.hashCode +
        lotId.hashCode +
        dateDepense.hashCode +
        nature.hashCode +
        montant.hashCode +
        reference.hashCode;

  factory DepenseRequete.fromJson(Map<String, dynamic> json) => _$DepenseRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$DepenseRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

