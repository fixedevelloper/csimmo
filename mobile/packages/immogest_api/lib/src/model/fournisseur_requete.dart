//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'fournisseur_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class FournisseurRequete {
  /// Returns a new [FournisseurRequete] instance.
  FournisseurRequete({

     this.actif,

     this.champs,
  });

  @JsonKey(
    
    name: r'actif',
    required: false,
    includeIfNull: false,
  )


  final bool? actif;



  @JsonKey(
    
    name: r'champs',
    required: false,
    includeIfNull: false,
  )


  final Map<String, String>? champs;





    @override
    bool operator ==(Object other) => identical(this, other) || other is FournisseurRequete &&
      other.actif == actif &&
      other.champs == champs;

    @override
    int get hashCode =>
        actif.hashCode +
        champs.hashCode;

  factory FournisseurRequete.fromJson(Map<String, dynamic> json) => _$FournisseurRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$FournisseurRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

