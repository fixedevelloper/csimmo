//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'otp_demande.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class OtpDemande {
  /// Returns a new [OtpDemande] instance.
  OtpDemande({

    required  this.identifiant,
  });

  @JsonKey(
    
    name: r'identifiant',
    required: true,
    includeIfNull: false,
  )


  final String identifiant;





    @override
    bool operator ==(Object other) => identical(this, other) || other is OtpDemande &&
      other.identifiant == identifiant;

    @override
    int get hashCode =>
        identifiant.hashCode;

  factory OtpDemande.fromJson(Map<String, dynamic> json) => _$OtpDemandeFromJson(json);

  Map<String, dynamic> toJson() => _$OtpDemandeToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

