//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'reinitialisation_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ReinitialisationRequete {
  /// Returns a new [ReinitialisationRequete] instance.
  ReinitialisationRequete({

    required  this.motDePasse,
  });

  @JsonKey(
    
    name: r'motDePasse',
    required: true,
    includeIfNull: false,
  )


  final String motDePasse;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ReinitialisationRequete &&
      other.motDePasse == motDePasse;

    @override
    int get hashCode =>
        motDePasse.hashCode;

  factory ReinitialisationRequete.fromJson(Map<String, dynamic> json) => _$ReinitialisationRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$ReinitialisationRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

