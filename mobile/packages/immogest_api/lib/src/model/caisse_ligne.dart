//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'caisse_ligne.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class CaisseLigne {
  /// Returns a new [CaisseLigne] instance.
  CaisseLigne({

     this.agentId,

     this.agentNom,

     this.total,

     this.operations,
  });

  @JsonKey(
    
    name: r'agentId',
    required: false,
    includeIfNull: false,
  )


  final String? agentId;



  @JsonKey(
    
    name: r'agentNom',
    required: false,
    includeIfNull: false,
  )


  final String? agentNom;



  @JsonKey(
    
    name: r'total',
    required: false,
    includeIfNull: false,
  )


  final int? total;



  @JsonKey(
    
    name: r'operations',
    required: false,
    includeIfNull: false,
  )


  final int? operations;





    @override
    bool operator ==(Object other) => identical(this, other) || other is CaisseLigne &&
      other.agentId == agentId &&
      other.agentNom == agentNom &&
      other.total == total &&
      other.operations == operations;

    @override
    int get hashCode =>
        agentId.hashCode +
        agentNom.hashCode +
        total.hashCode +
        operations.hashCode;

  factory CaisseLigne.fromJson(Map<String, dynamic> json) => _$CaisseLigneFromJson(json);

  Map<String, dynamic> toJson() => _$CaisseLigneToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

