//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'import_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ImportRequete {
  /// Returns a new [ImportRequete] instance.
  ImportRequete({

    required  this.contenu,
  });

  @JsonKey(
    
    name: r'contenu',
    required: true,
    includeIfNull: false,
  )


  final String contenu;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ImportRequete &&
      other.contenu == contenu;

    @override
    int get hashCode =>
        contenu.hashCode;

  factory ImportRequete.fromJson(Map<String, dynamic> json) => _$ImportRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$ImportRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

