//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'resultat_test.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ResultatTest {
  /// Returns a new [ResultatTest] instance.
  ResultatTest({

     this.succes,

     this.erreur,
  });

  @JsonKey(
    
    name: r'succes',
    required: false,
    includeIfNull: false,
  )


  final bool? succes;



  @JsonKey(
    
    name: r'erreur',
    required: false,
    includeIfNull: false,
  )


  final String? erreur;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ResultatTest &&
      other.succes == succes &&
      other.erreur == erreur;

    @override
    int get hashCode =>
        succes.hashCode +
        erreur.hashCode;

  factory ResultatTest.fromJson(Map<String, dynamic> json) => _$ResultatTestFromJson(json);

  Map<String, dynamic> toJson() => _$ResultatTestToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

