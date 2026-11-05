//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'paiement_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PaiementRequete {
  /// Returns a new [PaiementRequete] instance.
  PaiementRequete({

    required  this.bailId,

    required  this.montant,

    required  this.mode,

     this.reference,

     this.datePaiement,
  });

  @JsonKey(
    
    name: r'bailId',
    required: true,
    includeIfNull: false,
  )


  final String bailId;



          // minimum: 1
  @JsonKey(
    
    name: r'montant',
    required: true,
    includeIfNull: false,
  )


  final int montant;



  @JsonKey(
    
    name: r'mode',
    required: true,
    includeIfNull: false,
  )


  final PaiementRequeteModeEnum mode;



  @JsonKey(
    
    name: r'reference',
    required: false,
    includeIfNull: false,
  )


  final String? reference;



  @JsonKey(
    
    name: r'datePaiement',
    required: false,
    includeIfNull: false,
  )


  final DateTime? datePaiement;





    @override
    bool operator ==(Object other) => identical(this, other) || other is PaiementRequete &&
      other.bailId == bailId &&
      other.montant == montant &&
      other.mode == mode &&
      other.reference == reference &&
      other.datePaiement == datePaiement;

    @override
    int get hashCode =>
        bailId.hashCode +
        montant.hashCode +
        mode.hashCode +
        reference.hashCode +
        datePaiement.hashCode;

  factory PaiementRequete.fromJson(Map<String, dynamic> json) => _$PaiementRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$PaiementRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum PaiementRequeteModeEnum {
@JsonValue(r'ESPECES')
ESPECES(r'ESPECES'),
@JsonValue(r'VIREMENT')
VIREMENT(r'VIREMENT'),
@JsonValue(r'MTN_MOMO')
MTN_MOMO(r'MTN_MOMO'),
@JsonValue(r'ORANGE_MONEY')
ORANGE_MONEY(r'ORANGE_MONEY');

const PaiementRequeteModeEnum(this.value);

final String value;

@override
String toString() => value;
}


