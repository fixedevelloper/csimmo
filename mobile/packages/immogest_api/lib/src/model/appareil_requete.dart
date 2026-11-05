//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'appareil_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class AppareilRequete {
  /// Returns a new [AppareilRequete] instance.
  AppareilRequete({

    required  this.token,

    required  this.plateforme,
  });

  @JsonKey(
    
    name: r'token',
    required: true,
    includeIfNull: false,
  )


  final String token;



  @JsonKey(
    
    name: r'plateforme',
    required: true,
    includeIfNull: false,
  )


  final String plateforme;





    @override
    bool operator ==(Object other) => identical(this, other) || other is AppareilRequete &&
      other.token == token &&
      other.plateforme == plateforme;

    @override
    int get hashCode =>
        token.hashCode +
        plateforme.hashCode;

  factory AppareilRequete.fromJson(Map<String, dynamic> json) => _$AppareilRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$AppareilRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

