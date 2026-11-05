//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'otp_verification.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class OtpVerification {
  /// Returns a new [OtpVerification] instance.
  OtpVerification({

    required  this.identifiant,

    required  this.code,
  });

  @JsonKey(
    
    name: r'identifiant',
    required: true,
    includeIfNull: false,
  )


  final String identifiant;



  @JsonKey(
    
    name: r'code',
    required: true,
    includeIfNull: false,
  )


  final String code;





    @override
    bool operator ==(Object other) => identical(this, other) || other is OtpVerification &&
      other.identifiant == identifiant &&
      other.code == code;

    @override
    int get hashCode =>
        identifiant.hashCode +
        code.hashCode;

  factory OtpVerification.fromJson(Map<String, dynamic> json) => _$OtpVerificationFromJson(json);

  Map<String, dynamic> toJson() => _$OtpVerificationToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

