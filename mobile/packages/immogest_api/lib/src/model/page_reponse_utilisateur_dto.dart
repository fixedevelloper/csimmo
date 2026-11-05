//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/utilisateur_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'page_reponse_utilisateur_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PageReponseUtilisateurDto {
  /// Returns a new [PageReponseUtilisateurDto] instance.
  PageReponseUtilisateurDto({

     this.content,

     this.page,

     this.size,

     this.totalElements,
  });

  @JsonKey(
    
    name: r'content',
    required: false,
    includeIfNull: false,
  )


  final List<UtilisateurDto>? content;



  @JsonKey(
    
    name: r'page',
    required: false,
    includeIfNull: false,
  )


  final int? page;



  @JsonKey(
    
    name: r'size',
    required: false,
    includeIfNull: false,
  )


  final int? size;



  @JsonKey(
    
    name: r'totalElements',
    required: false,
    includeIfNull: false,
  )


  final int? totalElements;





    @override
    bool operator ==(Object other) => identical(this, other) || other is PageReponseUtilisateurDto &&
      other.content == content &&
      other.page == page &&
      other.size == size &&
      other.totalElements == totalElements;

    @override
    int get hashCode =>
        content.hashCode +
        page.hashCode +
        size.hashCode +
        totalElements.hashCode;

  factory PageReponseUtilisateurDto.fromJson(Map<String, dynamic> json) => _$PageReponseUtilisateurDtoFromJson(json);

  Map<String, dynamic> toJson() => _$PageReponseUtilisateurDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

