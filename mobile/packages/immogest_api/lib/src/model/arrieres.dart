//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/ligne_arriere.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'arrieres.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class Arrieres {
  /// Returns a new [Arrieres] instance.
  Arrieres({

     this.total,

     this.parBien,

     this.parLocataire,
  });

  @JsonKey(
    
    name: r'total',
    required: false,
    includeIfNull: false,
  )


  final int? total;



  @JsonKey(
    
    name: r'parBien',
    required: false,
    includeIfNull: false,
  )


  final List<LigneArriere>? parBien;



  @JsonKey(
    
    name: r'parLocataire',
    required: false,
    includeIfNull: false,
  )


  final List<LigneArriere>? parLocataire;





    @override
    bool operator ==(Object other) => identical(this, other) || other is Arrieres &&
      other.total == total &&
      other.parBien == parBien &&
      other.parLocataire == parLocataire;

    @override
    int get hashCode =>
        total.hashCode +
        parBien.hashCode +
        parLocataire.hashCode;

  factory Arrieres.fromJson(Map<String, dynamic> json) => _$ArrieresFromJson(json);

  Map<String, dynamic> toJson() => _$ArrieresToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

