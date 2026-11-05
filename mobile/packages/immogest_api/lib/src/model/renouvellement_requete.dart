//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'renouvellement_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class RenouvellementRequete {
  /// Returns a new [RenouvellementRequete] instance.
  RenouvellementRequete({

     this.dateFin,

     this.loyer,

     this.charges,

     this.caution,

     this.moisAvance,
  });

  @JsonKey(
    
    name: r'dateFin',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateFin;



          // minimum: 1
  @JsonKey(
    
    name: r'loyer',
    required: false,
    includeIfNull: false,
  )


  final int? loyer;



          // minimum: 0
  @JsonKey(
    
    name: r'charges',
    required: false,
    includeIfNull: false,
  )


  final int? charges;



          // minimum: 0
  @JsonKey(
    
    name: r'caution',
    required: false,
    includeIfNull: false,
  )


  final int? caution;



          // minimum: 0
          // maximum: 24
  @JsonKey(
    
    name: r'moisAvance',
    required: false,
    includeIfNull: false,
  )


  final int? moisAvance;





    @override
    bool operator ==(Object other) => identical(this, other) || other is RenouvellementRequete &&
      other.dateFin == dateFin &&
      other.loyer == loyer &&
      other.charges == charges &&
      other.caution == caution &&
      other.moisAvance == moisAvance;

    @override
    int get hashCode =>
        dateFin.hashCode +
        loyer.hashCode +
        charges.hashCode +
        caution.hashCode +
        moisAvance.hashCode;

  factory RenouvellementRequete.fromJson(Map<String, dynamic> json) => _$RenouvellementRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$RenouvellementRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

