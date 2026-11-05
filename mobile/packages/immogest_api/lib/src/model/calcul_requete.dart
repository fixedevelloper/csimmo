//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'calcul_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class CalculRequete {
  /// Returns a new [CalculRequete] instance.
  CalculRequete({

    required  this.mois,
  });

  @JsonKey(
    
    name: r'mois',
    required: true,
    includeIfNull: false,
  )


  final String mois;





    @override
    bool operator ==(Object other) => identical(this, other) || other is CalculRequete &&
      other.mois == mois;

    @override
    int get hashCode =>
        mois.hashCode;

  factory CalculRequete.fromJson(Map<String, dynamic> json) => _$CalculRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$CalculRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

