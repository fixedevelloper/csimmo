//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'statut_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class StatutRequete {
  /// Returns a new [StatutRequete] instance.
  StatutRequete({

    required  this.statut,
  });

  @JsonKey(
    
    name: r'statut',
    required: true,
    includeIfNull: false,
  )


  final StatutRequeteStatutEnum statut;





    @override
    bool operator ==(Object other) => identical(this, other) || other is StatutRequete &&
      other.statut == statut;

    @override
    int get hashCode =>
        statut.hashCode;

  factory StatutRequete.fromJson(Map<String, dynamic> json) => _$StatutRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$StatutRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum StatutRequeteStatutEnum {
@JsonValue(r'LIBRE')
LIBRE(r'LIBRE'),
@JsonValue(r'OCCUPE')
OCCUPE(r'OCCUPE'),
@JsonValue(r'EN_TRAVAUX')
EN_TRAVAUX(r'EN_TRAVAUX');

const StatutRequeteStatutEnum(this.value);

final String value;

@override
String toString() => value;
}


