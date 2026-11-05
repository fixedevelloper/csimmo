//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'test_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class TestRequete {
  /// Returns a new [TestRequete] instance.
  TestRequete({

    required  this.destinataire,
  });

  @JsonKey(
    
    name: r'destinataire',
    required: true,
    includeIfNull: false,
  )


  final String destinataire;





    @override
    bool operator ==(Object other) => identical(this, other) || other is TestRequete &&
      other.destinataire == destinataire;

    @override
    int get hashCode =>
        destinataire.hashCode;

  factory TestRequete.fromJson(Map<String, dynamic> json) => _$TestRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$TestRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

