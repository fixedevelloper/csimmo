//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'mot_de_passe_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class MotDePasseRequete {
  /// Returns a new [MotDePasseRequete] instance.
  MotDePasseRequete({

    required  this.motDePasse,
  });

  @JsonKey(
    
    name: r'motDePasse',
    required: true,
    includeIfNull: false,
  )


  final String motDePasse;





    @override
    bool operator ==(Object other) => identical(this, other) || other is MotDePasseRequete &&
      other.motDePasse == motDePasse;

    @override
    int get hashCode =>
        motDePasse.hashCode;

  factory MotDePasseRequete.fromJson(Map<String, dynamic> json) => _$MotDePasseRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$MotDePasseRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

