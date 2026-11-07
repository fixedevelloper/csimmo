//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'entree_audit.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class EntreeAudit {
  /// Returns a new [EntreeAudit] instance.
  EntreeAudit({

     this.id,

     this.date,

     this.action,

     this.entite,

     this.entiteId,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'date',
    required: false,
    includeIfNull: false,
  )


  final DateTime? date;



  @JsonKey(
    
    name: r'action',
    required: false,
    includeIfNull: false,
  )


  final String? action;



  @JsonKey(
    
    name: r'entite',
    required: false,
    includeIfNull: false,
  )


  final String? entite;



  @JsonKey(
    
    name: r'entiteId',
    required: false,
    includeIfNull: false,
  )


  final String? entiteId;





    @override
    bool operator ==(Object other) => identical(this, other) || other is EntreeAudit &&
      other.id == id &&
      other.date == date &&
      other.action == action &&
      other.entite == entite &&
      other.entiteId == entiteId;

    @override
    int get hashCode =>
        id.hashCode +
        date.hashCode +
        action.hashCode +
        entite.hashCode +
        entiteId.hashCode;

  factory EntreeAudit.fromJson(Map<String, dynamic> json) => _$EntreeAuditFromJson(json);

  Map<String, dynamic> toJson() => _$EntreeAuditToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

