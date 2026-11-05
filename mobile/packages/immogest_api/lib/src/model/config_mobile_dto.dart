//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/push_mobile.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'config_mobile_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ConfigMobileDto {
  /// Returns a new [ConfigMobileDto] instance.
  ConfigMobileDto({

     this.push,
  });

  @JsonKey(
    
    name: r'push',
    required: false,
    includeIfNull: false,
  )


  final PushMobile? push;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ConfigMobileDto &&
      other.push == push;

    @override
    int get hashCode =>
        push.hashCode;

  factory ConfigMobileDto.fromJson(Map<String, dynamic> json) => _$ConfigMobileDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ConfigMobileDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

