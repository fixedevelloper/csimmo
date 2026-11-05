//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'obstacle_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ObstacleDto {
  /// Returns a new [ObstacleDto] instance.
  ObstacleDto({

     this.code,

     this.message,
  });

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
    bool operator ==(Object other) => identical(this, other) || other is ObstacleDto &&
      other.code == code &&
      other.message == message;

    @override
    int get hashCode =>
        code.hashCode +
        message.hashCode;

  factory ObstacleDto.fromJson(Map<String, dynamic> json) => _$ObstacleDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ObstacleDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

