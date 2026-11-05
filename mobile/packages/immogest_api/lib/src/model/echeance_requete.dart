//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'echeance_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class EcheanceRequete {
  /// Returns a new [EcheanceRequete] instance.
  EcheanceRequete({

    required  this.echeanceId,
  });

  @JsonKey(
    
    name: r'echeanceId',
    required: true,
    includeIfNull: false,
  )


  final String echeanceId;





    @override
    bool operator ==(Object other) => identical(this, other) || other is EcheanceRequete &&
      other.echeanceId == echeanceId;

    @override
    int get hashCode =>
        echeanceId.hashCode;

  factory EcheanceRequete.fromJson(Map<String, dynamic> json) => _$EcheanceRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$EcheanceRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

