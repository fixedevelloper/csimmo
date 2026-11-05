//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/traitement_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'registre_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class RegistreDto {
  /// Returns a new [RegistreDto] instance.
  RegistreDto({

     this.version,

     this.responsable,

     this.note,

     this.traitements,
  });

  @JsonKey(
    
    name: r'version',
    required: false,
    includeIfNull: false,
  )


  final String? version;



  @JsonKey(
    
    name: r'responsable',
    required: false,
    includeIfNull: false,
  )


  final String? responsable;



  @JsonKey(
    
    name: r'note',
    required: false,
    includeIfNull: false,
  )


  final String? note;



  @JsonKey(
    
    name: r'traitements',
    required: false,
    includeIfNull: false,
  )


  final List<TraitementDto>? traitements;





    @override
    bool operator ==(Object other) => identical(this, other) || other is RegistreDto &&
      other.version == version &&
      other.responsable == responsable &&
      other.note == note &&
      other.traitements == traitements;

    @override
    int get hashCode =>
        version.hashCode +
        responsable.hashCode +
        note.hashCode +
        traitements.hashCode;

  factory RegistreDto.fromJson(Map<String, dynamic> json) => _$RegistreDtoFromJson(json);

  Map<String, dynamic> toJson() => _$RegistreDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

