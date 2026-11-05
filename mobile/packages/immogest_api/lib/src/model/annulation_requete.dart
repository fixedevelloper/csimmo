//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'annulation_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class AnnulationRequete {
  /// Returns a new [AnnulationRequete] instance.
  AnnulationRequete({

    required  this.motif,
  });

  @JsonKey(
    
    name: r'motif',
    required: true,
    includeIfNull: false,
  )


  final String motif;





    @override
    bool operator ==(Object other) => identical(this, other) || other is AnnulationRequete &&
      other.motif == motif;

    @override
    int get hashCode =>
        motif.hashCode;

  factory AnnulationRequete.fromJson(Map<String, dynamic> json) => _$AnnulationRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$AnnulationRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

