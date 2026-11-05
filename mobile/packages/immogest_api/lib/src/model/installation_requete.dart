//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/administrateur.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'installation_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class InstallationRequete {
  /// Returns a new [InstallationRequete] instance.
  InstallationRequete({

    required  this.admin,

    required  this.licence,
  });

  @JsonKey(
    
    name: r'admin',
    required: true,
    includeIfNull: false,
  )


  final Administrateur admin;



  @JsonKey(
    
    name: r'licence',
    required: true,
    includeIfNull: false,
  )


  final String licence;





    @override
    bool operator ==(Object other) => identical(this, other) || other is InstallationRequete &&
      other.admin == admin &&
      other.licence == licence;

    @override
    int get hashCode =>
        admin.hashCode +
        licence.hashCode;

  factory InstallationRequete.fromJson(Map<String, dynamic> json) => _$InstallationRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$InstallationRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

