//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'code_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class CodeRequete {
  /// Returns a new [CodeRequete] instance.
  CodeRequete({

    required  this.code,
  });

  @JsonKey(
    
    name: r'code',
    required: true,
    includeIfNull: false,
  )


  final String code;





    @override
    bool operator ==(Object other) => identical(this, other) || other is CodeRequete &&
      other.code == code;

    @override
    int get hashCode =>
        code.hashCode;

  factory CodeRequete.fromJson(Map<String, dynamic> json) => _$CodeRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$CodeRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

