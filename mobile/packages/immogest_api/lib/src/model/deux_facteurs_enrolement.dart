//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'deux_facteurs_enrolement.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class DeuxFacteursEnrolement {
  /// Returns a new [DeuxFacteursEnrolement] instance.
  DeuxFacteursEnrolement({

     this.secret,

     this.uri,
  });

  @JsonKey(
    
    name: r'secret',
    required: false,
    includeIfNull: false,
  )


  final String? secret;



  @JsonKey(
    
    name: r'uri',
    required: false,
    includeIfNull: false,
  )


  final String? uri;





    @override
    bool operator ==(Object other) => identical(this, other) || other is DeuxFacteursEnrolement &&
      other.secret == secret &&
      other.uri == uri;

    @override
    int get hashCode =>
        secret.hashCode +
        uri.hashCode;

  factory DeuxFacteursEnrolement.fromJson(Map<String, dynamic> json) => _$DeuxFacteursEnrolementFromJson(json);

  Map<String, dynamic> toJson() => _$DeuxFacteursEnrolementToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

