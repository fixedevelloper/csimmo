//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'restauration_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class RestaurationRequete {
  /// Returns a new [RestaurationRequete] instance.
  RestaurationRequete({

    required  this.confirmation,
  });

  @JsonKey(
    
    name: r'confirmation',
    required: true,
    includeIfNull: false,
  )


  final String confirmation;





    @override
    bool operator ==(Object other) => identical(this, other) || other is RestaurationRequete &&
      other.confirmation == confirmation;

    @override
    int get hashCode =>
        confirmation.hashCode;

  factory RestaurationRequete.fromJson(Map<String, dynamic> json) => _$RestaurationRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$RestaurationRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

