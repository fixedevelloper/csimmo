//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'operation_sauvegarde_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class OperationSauvegardeDto {
  /// Returns a new [OperationSauvegardeDto] instance.
  OperationSauvegardeDto({

     this.operation,

     this.statut,

     this.id,

     this.debut,

     this.fin,

     this.message,
  });

  @JsonKey(
    
    name: r'operation',
    required: false,
    includeIfNull: false,
  )


  final String? operation;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final String? statut;



  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'debut',
    required: false,
    includeIfNull: false,
  )


  final DateTime? debut;



  @JsonKey(
    
    name: r'fin',
    required: false,
    includeIfNull: false,
  )


  final DateTime? fin;



  @JsonKey(
    
    name: r'message',
    required: false,
    includeIfNull: false,
  )


  final String? message;





    @override
    bool operator ==(Object other) => identical(this, other) || other is OperationSauvegardeDto &&
      other.operation == operation &&
      other.statut == statut &&
      other.id == id &&
      other.debut == debut &&
      other.fin == fin &&
      other.message == message;

    @override
    int get hashCode =>
        operation.hashCode +
        statut.hashCode +
        id.hashCode +
        debut.hashCode +
        fin.hashCode +
        message.hashCode;

  factory OperationSauvegardeDto.fromJson(Map<String, dynamic> json) => _$OperationSauvegardeDtoFromJson(json);

  Map<String, dynamic> toJson() => _$OperationSauvegardeDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

