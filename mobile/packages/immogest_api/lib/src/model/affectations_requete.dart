//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'affectations_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class AffectationsRequete {
  /// Returns a new [AffectationsRequete] instance.
  AffectationsRequete({

    required  this.immeubleIds,
  });

  @JsonKey(
    
    name: r'immeubleIds',
    required: true,
    includeIfNull: false,
  )


  final List<String> immeubleIds;





    @override
    bool operator ==(Object other) => identical(this, other) || other is AffectationsRequete &&
      other.immeubleIds == immeubleIds;

    @override
    int get hashCode =>
        immeubleIds.hashCode;

  factory AffectationsRequete.fromJson(Map<String, dynamic> json) => _$AffectationsRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$AffectationsRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

