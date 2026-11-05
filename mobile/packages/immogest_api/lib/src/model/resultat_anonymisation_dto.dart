//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'resultat_anonymisation_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ResultatAnonymisationDto {
  /// Returns a new [ResultatAnonymisationDto] instance.
  ResultatAnonymisationDto({

     this.demandeId,

     this.piecesSupprimees,

     this.conserve,
  });

  @JsonKey(
    
    name: r'demandeId',
    required: false,
    includeIfNull: false,
  )


  final String? demandeId;



  @JsonKey(
    
    name: r'piecesSupprimees',
    required: false,
    includeIfNull: false,
  )


  final int? piecesSupprimees;



  @JsonKey(
    
    name: r'conserve',
    required: false,
    includeIfNull: false,
  )


  final List<String>? conserve;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ResultatAnonymisationDto &&
      other.demandeId == demandeId &&
      other.piecesSupprimees == piecesSupprimees &&
      other.conserve == conserve;

    @override
    int get hashCode =>
        demandeId.hashCode +
        piecesSupprimees.hashCode +
        conserve.hashCode;

  factory ResultatAnonymisationDto.fromJson(Map<String, dynamic> json) => _$ResultatAnonymisationDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ResultatAnonymisationDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

