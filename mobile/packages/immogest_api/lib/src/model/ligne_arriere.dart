//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'ligne_arriere.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class LigneArriere {
  /// Returns a new [LigneArriere] instance.
  LigneArriere({

     this.id,

     this.libelle,

     this.total,

     this.echeances,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'libelle',
    required: false,
    includeIfNull: false,
  )


  final String? libelle;



  @JsonKey(
    
    name: r'total',
    required: false,
    includeIfNull: false,
  )


  final int? total;



  @JsonKey(
    
    name: r'echeances',
    required: false,
    includeIfNull: false,
  )


  final int? echeances;





    @override
    bool operator ==(Object other) => identical(this, other) || other is LigneArriere &&
      other.id == id &&
      other.libelle == libelle &&
      other.total == total &&
      other.echeances == echeances;

    @override
    int get hashCode =>
        id.hashCode +
        libelle.hashCode +
        total.hashCode +
        echeances.hashCode;

  factory LigneArriere.fromJson(Map<String, dynamic> json) => _$LigneArriereFromJson(json);

  Map<String, dynamic> toJson() => _$LigneArriereToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

