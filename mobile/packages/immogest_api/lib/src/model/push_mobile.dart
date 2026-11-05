//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'push_mobile.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PushMobile {
  /// Returns a new [PushMobile] instance.
  PushMobile({

     this.projectId,

     this.apiKey,

     this.appId,

     this.messagingSenderId,
  });

  @JsonKey(
    
    name: r'projectId',
    required: false,
    includeIfNull: false,
  )


  final String? projectId;



  @JsonKey(
    
    name: r'apiKey',
    required: false,
    includeIfNull: false,
  )


  final String? apiKey;



  @JsonKey(
    
    name: r'appId',
    required: false,
    includeIfNull: false,
  )


  final String? appId;



  @JsonKey(
    
    name: r'messagingSenderId',
    required: false,
    includeIfNull: false,
  )


  final String? messagingSenderId;





    @override
    bool operator ==(Object other) => identical(this, other) || other is PushMobile &&
      other.projectId == projectId &&
      other.apiKey == apiKey &&
      other.appId == appId &&
      other.messagingSenderId == messagingSenderId;

    @override
    int get hashCode =>
        projectId.hashCode +
        apiKey.hashCode +
        appId.hashCode +
        messagingSenderId.hashCode;

  factory PushMobile.fromJson(Map<String, dynamic> json) => _$PushMobileFromJson(json);

  Map<String, dynamic> toJson() => _$PushMobileToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

