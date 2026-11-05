//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'mandat_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class MandatDto {
  /// Returns a new [MandatDto] instance.
  MandatDto({

     this.id,

     this.proprietaireId,

     this.tauxHonorairesBp,

     this.dateDebut,

     this.dateFin,

     this.notes,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'proprietaireId',
    required: false,
    includeIfNull: false,
  )


  final String? proprietaireId;



  @JsonKey(
    
    name: r'tauxHonorairesBp',
    required: false,
    includeIfNull: false,
  )


  final int? tauxHonorairesBp;



  @JsonKey(
    
    name: r'dateDebut',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateDebut;



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
    bool operator ==(Object other) => identical(this, other) || other is MandatDto &&
      other.id == id &&
      other.proprietaireId == proprietaireId &&
      other.tauxHonorairesBp == tauxHonorairesBp &&
      other.dateDebut == dateDebut &&
      other.dateFin == dateFin &&
      other.notes == notes;

    @override
    int get hashCode =>
        id.hashCode +
        proprietaireId.hashCode +
        tauxHonorairesBp.hashCode +
        dateDebut.hashCode +
        dateFin.hashCode +
        notes.hashCode;

  factory MandatDto.fromJson(Map<String, dynamic> json) => _$MandatDtoFromJson(json);

  Map<String, dynamic> toJson() => _$MandatDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

