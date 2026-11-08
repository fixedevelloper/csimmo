//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'deux_facteurs_verification.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class DeuxFacteursVerification {
  /// Returns a new [DeuxFacteursVerification] instance.
  DeuxFacteursVerification({

    required  this.defi,

    required  this.code,
  });

  @JsonKey(
    
    name: r'defi',
    required: true,
    includeIfNull: false,
  )


  final String defi;



  @JsonKey(
    
    name: r'code',
    required: true,
    includeIfNull: false,
  )


  final String code;





    @override
    bool operator ==(Object other) => identical(this, other) || other is DeuxFacteursVerification &&
      other.defi == defi &&
      other.code == code;

    @override
    int get hashCode =>
        defi.hashCode +
        code.hashCode;

  factory DeuxFacteursVerification.fromJson(Map<String, dynamic> json) => _$DeuxFacteursVerificationFromJson(json);

  Map<String, dynamic> toJson() => _$DeuxFacteursVerificationToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

