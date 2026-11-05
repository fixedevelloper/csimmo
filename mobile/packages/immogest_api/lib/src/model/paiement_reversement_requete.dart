//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'paiement_reversement_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PaiementReversementRequete {
  /// Returns a new [PaiementReversementRequete] instance.
  PaiementReversementRequete({

    required  this.mode,

     this.reference,

     this.date,
  });

  @JsonKey(
    
    name: r'mode',
    required: true,
    includeIfNull: false,
  )


  final String mode;



  @JsonKey(
    
    name: r'reference',
    required: false,
    includeIfNull: false,
  )


  final String? reference;



  @JsonKey(
    
    name: r'date',
    required: false,
    includeIfNull: false,
  )


  final DateTime? date;





    @override
    bool operator ==(Object other) => identical(this, other) || other is PaiementReversementRequete &&
      other.mode == mode &&
      other.reference == reference &&
      other.date == date;

    @override
    int get hashCode =>
        mode.hashCode +
        reference.hashCode +
        date.hashCode;

  factory PaiementReversementRequete.fromJson(Map<String, dynamic> json) => _$PaiementReversementRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$PaiementReversementRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

