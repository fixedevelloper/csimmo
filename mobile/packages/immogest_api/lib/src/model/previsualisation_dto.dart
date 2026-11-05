//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/resume_feuille_dto.dart';
import 'package:immogest_api/src/model/erreur_import_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'previsualisation_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PrevisualisationDto {
  /// Returns a new [PrevisualisationDto] instance.
  PrevisualisationDto({

     this.empreinte,

     this.feuilles,

     this.erreurs,

     this.apercu,

     this.importable,
  });

  @JsonKey(
    
    name: r'empreinte',
    required: false,
    includeIfNull: false,
  )


  final String? empreinte;



  @JsonKey(
    
    name: r'feuilles',
    required: false,
    includeIfNull: false,
  )


  final List<ResumeFeuilleDto>? feuilles;



  @JsonKey(
    
    name: r'erreurs',
    required: false,
    includeIfNull: false,
  )


  final List<ErreurImportDto>? erreurs;



  @JsonKey(
    
    name: r'apercu',
    required: false,
    includeIfNull: false,
  )


  final Map<String, List<Map<String, String>>>? apercu;



  @JsonKey(
    
    name: r'importable',
    required: false,
    includeIfNull: false,
  )


  final bool? importable;





    @override
    bool operator ==(Object other) => identical(this, other) || other is PrevisualisationDto &&
      other.empreinte == empreinte &&
      other.feuilles == feuilles &&
      other.erreurs == erreurs &&
      other.apercu == apercu &&
      other.importable == importable;

    @override
    int get hashCode =>
        empreinte.hashCode +
        feuilles.hashCode +
        erreurs.hashCode +
        apercu.hashCode +
        importable.hashCode;

  factory PrevisualisationDto.fromJson(Map<String, dynamic> json) => _$PrevisualisationDtoFromJson(json);

  Map<String, dynamic> toJson() => _$PrevisualisationDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

