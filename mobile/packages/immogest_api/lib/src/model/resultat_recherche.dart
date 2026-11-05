//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'resultat_recherche.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ResultatRecherche {
  /// Returns a new [ResultatRecherche] instance.
  ResultatRecherche({

     this.type,

     this.id,

     this.nom,

     this.telephone,
  });

  @JsonKey(
    
    name: r'type',
    required: false,
    includeIfNull: false,
  )


  final String? type;



  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'nom',
    required: false,
    includeIfNull: false,
  )


  final String? nom;



  @JsonKey(
    
    name: r'telephone',
    required: false,
    includeIfNull: false,
  )


  final String? telephone;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ResultatRecherche &&
      other.type == type &&
      other.id == id &&
      other.nom == nom &&
      other.telephone == telephone;

    @override
    int get hashCode =>
        type.hashCode +
        id.hashCode +
        nom.hashCode +
        telephone.hashCode;

  factory ResultatRecherche.fromJson(Map<String, dynamic> json) => _$ResultatRechercheFromJson(json);

  Map<String, dynamic> toJson() => _$ResultatRechercheToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

