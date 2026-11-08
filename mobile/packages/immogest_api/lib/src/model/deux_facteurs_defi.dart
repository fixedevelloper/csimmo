//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'deux_facteurs_defi.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class DeuxFacteursDefi {
  /// Returns a new [DeuxFacteursDefi] instance.
  DeuxFacteursDefi({

    required  this.defi,
  });

  @JsonKey(
    
    name: r'defi',
    required: true,
    includeIfNull: false,
  )


  final String defi;





    @override
    bool operator ==(Object other) => identical(this, other) || other is DeuxFacteursDefi &&
      other.defi == defi;

    @override
    int get hashCode =>
        defi.hashCode;

  factory DeuxFacteursDefi.fromJson(Map<String, dynamic> json) => _$DeuxFacteursDefiFromJson(json);

  Map<String, dynamic> toJson() => _$DeuxFacteursDefiToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

