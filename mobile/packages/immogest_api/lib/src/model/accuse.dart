//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'accuse.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class Accuse {
  /// Returns a new [Accuse] instance.
  Accuse({

     this.resultat,
  });

  @JsonKey(
    
    name: r'resultat',
    required: false,
    includeIfNull: false,
  )


  final AccuseResultatEnum? resultat;





    @override
    bool operator ==(Object other) => identical(this, other) || other is Accuse &&
      other.resultat == resultat;

    @override
    int get hashCode =>
        resultat.hashCode;

  factory Accuse.fromJson(Map<String, dynamic> json) => _$AccuseFromJson(json);

  Map<String, dynamic> toJson() => _$AccuseToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum AccuseResultatEnum {
@JsonValue(r'TRAITE')
TRAITE(r'TRAITE'),
@JsonValue(r'REJOUE')
REJOUE(r'REJOUE'),
@JsonValue(r'IGNORE')
IGNORE(r'IGNORE');

const AccuseResultatEnum(this.value);

final String value;

@override
String toString() => value;
}


