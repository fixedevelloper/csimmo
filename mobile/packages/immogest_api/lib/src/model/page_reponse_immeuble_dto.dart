//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/immeuble_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'page_reponse_immeuble_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PageReponseImmeubleDto {
  /// Returns a new [PageReponseImmeubleDto] instance.
  PageReponseImmeubleDto({

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


  final List<ImmeubleDto>? content;



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
    bool operator ==(Object other) => identical(this, other) || other is PageReponseImmeubleDto &&
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

  factory PageReponseImmeubleDto.fromJson(Map<String, dynamic> json) => _$PageReponseImmeubleDtoFromJson(json);

  Map<String, dynamic> toJson() => _$PageReponseImmeubleDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

