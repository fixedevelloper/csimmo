//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'refresh_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class RefreshRequete {
  /// Returns a new [RefreshRequete] instance.
  RefreshRequete({

    required  this.refreshToken,
  });

  @JsonKey(
    
    name: r'refreshToken',
    required: true,
    includeIfNull: false,
  )


  final String refreshToken;





    @override
    bool operator ==(Object other) => identical(this, other) || other is RefreshRequete &&
      other.refreshToken == refreshToken;

    @override
    int get hashCode =>
        refreshToken.hashCode;

  factory RefreshRequete.fromJson(Map<String, dynamic> json) => _$RefreshRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$RefreshRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

