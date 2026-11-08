//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'desactivation_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class DesactivationRequete {
  /// Returns a new [DesactivationRequete] instance.
  DesactivationRequete({

    required  this.motDePasse,

     this.code,
  });

  @JsonKey(
    
    name: r'motDePasse',
    required: true,
    includeIfNull: false,
  )


  final String motDePasse;



  @JsonKey(
    
    name: r'code',
    required: false,
    includeIfNull: false,
  )


  final String? code;





    @override
    bool operator ==(Object other) => identical(this, other) || other is DesactivationRequete &&
      other.motDePasse == motDePasse &&
      other.code == code;

    @override
    int get hashCode =>
        motDePasse.hashCode +
        code.hashCode;

  factory DesactivationRequete.fromJson(Map<String, dynamic> json) => _$DesactivationRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$DesactivationRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

