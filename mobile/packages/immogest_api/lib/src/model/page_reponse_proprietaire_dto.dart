//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/proprietaire_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'page_reponse_proprietaire_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PageReponseProprietaireDto {
  /// Returns a new [PageReponseProprietaireDto] instance.
  PageReponseProprietaireDto({

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


  final List<ProprietaireDto>? content;



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
    bool operator ==(Object other) => identical(this, other) || other is PageReponseProprietaireDto &&
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

  factory PageReponseProprietaireDto.fromJson(Map<String, dynamic> json) => _$PageReponseProprietaireDtoFromJson(json);

  Map<String, dynamic> toJson() => _$PageReponseProprietaireDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

