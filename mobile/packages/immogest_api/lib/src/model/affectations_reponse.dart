//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'affectations_reponse.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class AffectationsReponse {
  /// Returns a new [AffectationsReponse] instance.
  AffectationsReponse({

     this.immeubleIds,
  });

  @JsonKey(
    
    name: r'immeubleIds',
    required: false,
    includeIfNull: false,
  )


  final List<String>? immeubleIds;





    @override
    bool operator ==(Object other) => identical(this, other) || other is AffectationsReponse &&
      other.immeubleIds == immeubleIds;

    @override
    int get hashCode =>
        immeubleIds.hashCode;

  factory AffectationsReponse.fromJson(Map<String, dynamic> json) => _$AffectationsReponseFromJson(json);

  Map<String, dynamic> toJson() => _$AffectationsReponseToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

