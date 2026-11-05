//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'erreur_import_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ErreurImportDto {
  /// Returns a new [ErreurImportDto] instance.
  ErreurImportDto({

     this.feuille,

     this.ligne,

     this.colonne,

     this.code,

     this.message,
  });

  @JsonKey(
    
    name: r'feuille',
    required: false,
    includeIfNull: false,
  )


  final String? feuille;



  @JsonKey(
    
    name: r'ligne',
    required: false,
    includeIfNull: false,
  )


  final int? ligne;



  @JsonKey(
    
    name: r'colonne',
    required: false,
    includeIfNull: false,
  )


  final String? colonne;



  @JsonKey(
    
    name: r'code',
    required: false,
    includeIfNull: false,
  )


  final String? code;



  @JsonKey(
    
    name: r'message',
    required: false,
    includeIfNull: false,
  )


  final String? message;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ErreurImportDto &&
      other.feuille == feuille &&
      other.ligne == ligne &&
      other.colonne == colonne &&
      other.code == code &&
      other.message == message;

    @override
    int get hashCode =>
        feuille.hashCode +
        ligne.hashCode +
        colonne.hashCode +
        code.hashCode +
        message.hashCode;

  factory ErreurImportDto.fromJson(Map<String, dynamic> json) => _$ErreurImportDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ErreurImportDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

