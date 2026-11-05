//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'mandat_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class MandatRequete {
  /// Returns a new [MandatRequete] instance.
  MandatRequete({

    required  this.tauxHonorairesBp,

    required  this.dateDebut,

     this.dateFin,

     this.notes,
  });

          // minimum: 0
          // maximum: 10000
  @JsonKey(
    
    name: r'tauxHonorairesBp',
    required: true,
    includeIfNull: false,
  )


  final int tauxHonorairesBp;



  @JsonKey(
    
    name: r'dateDebut',
    required: true,
    includeIfNull: false,
  )


  final DateTime dateDebut;



  @JsonKey(
    
    name: r'dateFin',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateFin;



  @JsonKey(
    
    name: r'notes',
    required: false,
    includeIfNull: false,
  )


  final String? notes;





    @override
    bool operator ==(Object other) => identical(this, other) || other is MandatRequete &&
      other.tauxHonorairesBp == tauxHonorairesBp &&
      other.dateDebut == dateDebut &&
      other.dateFin == dateFin &&
      other.notes == notes;

    @override
    int get hashCode =>
        tauxHonorairesBp.hashCode +
        dateDebut.hashCode +
        dateFin.hashCode +
        notes.hashCode;

  factory MandatRequete.fromJson(Map<String, dynamic> json) => _$MandatRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$MandatRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

