//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'modele_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ModeleRequete {
  /// Returns a new [ModeleRequete] instance.
  ModeleRequete({

     this.bailleurNom,

     this.bailleurAdresse,

     this.preambule,

     this.clauses,

     this.piedDePage,
  });

  @JsonKey(
    
    name: r'bailleurNom',
    required: false,
    includeIfNull: false,
  )


  final String? bailleurNom;



  @JsonKey(
    
    name: r'bailleurAdresse',
    required: false,
    includeIfNull: false,
  )


  final String? bailleurAdresse;



  @JsonKey(
    
    name: r'preambule',
    required: false,
    includeIfNull: false,
  )


  final String? preambule;



  @JsonKey(
    
    name: r'clauses',
    required: false,
    includeIfNull: false,
  )


  final String? clauses;



  @JsonKey(
    
    name: r'piedDePage',
    required: false,
    includeIfNull: false,
  )


  final String? piedDePage;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ModeleRequete &&
      other.bailleurNom == bailleurNom &&
      other.bailleurAdresse == bailleurAdresse &&
      other.preambule == preambule &&
      other.clauses == clauses &&
      other.piedDePage == piedDePage;

    @override
    int get hashCode =>
        bailleurNom.hashCode +
        bailleurAdresse.hashCode +
        preambule.hashCode +
        clauses.hashCode +
        piedDePage.hashCode;

  factory ModeleRequete.fromJson(Map<String, dynamic> json) => _$ModeleRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$ModeleRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

