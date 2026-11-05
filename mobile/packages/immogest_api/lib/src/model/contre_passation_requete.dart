//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'contre_passation_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ContrePassationRequete {
  /// Returns a new [ContrePassationRequete] instance.
  ContrePassationRequete({

    required  this.motif,
  });

  @JsonKey(
    
    name: r'motif',
    required: true,
    includeIfNull: false,
  )


  final String motif;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ContrePassationRequete &&
      other.motif == motif;

    @override
    int get hashCode =>
        motif.hashCode;

  factory ContrePassationRequete.fromJson(Map<String, dynamic> json) => _$ContrePassationRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$ContrePassationRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

