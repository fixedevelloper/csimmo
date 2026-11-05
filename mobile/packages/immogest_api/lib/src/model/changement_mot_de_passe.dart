//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'changement_mot_de_passe.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ChangementMotDePasse {
  /// Returns a new [ChangementMotDePasse] instance.
  ChangementMotDePasse({

    required  this.ancienMotDePasse,

    required  this.nouveauMotDePasse,
  });

  @JsonKey(
    
    name: r'ancienMotDePasse',
    required: true,
    includeIfNull: false,
  )


  final String ancienMotDePasse;



  @JsonKey(
    
    name: r'nouveauMotDePasse',
    required: true,
    includeIfNull: false,
  )


  final String nouveauMotDePasse;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ChangementMotDePasse &&
      other.ancienMotDePasse == ancienMotDePasse &&
      other.nouveauMotDePasse == nouveauMotDePasse;

    @override
    int get hashCode =>
        ancienMotDePasse.hashCode +
        nouveauMotDePasse.hashCode;

  factory ChangementMotDePasse.fromJson(Map<String, dynamic> json) => _$ChangementMotDePasseFromJson(json);

  Map<String, dynamic> toJson() => _$ChangementMotDePasseToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

