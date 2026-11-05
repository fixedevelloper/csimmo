//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'mois.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class Mois {
  /// Returns a new [Mois] instance.
  Mois({

     this.mois,

     this.attendu,

     this.encaisse,

     this.tauxBp,
  });

  @JsonKey(
    
    name: r'mois',
    required: false,
    includeIfNull: false,
  )


  final DateTime? mois;



  @JsonKey(
    
    name: r'attendu',
    required: false,
    includeIfNull: false,
  )


  final int? attendu;



  @JsonKey(
    
    name: r'encaisse',
    required: false,
    includeIfNull: false,
  )


  final int? encaisse;



  @JsonKey(
    
    name: r'tauxBp',
    required: false,
    includeIfNull: false,
  )


  final int? tauxBp;





    @override
    bool operator ==(Object other) => identical(this, other) || other is Mois &&
      other.mois == mois &&
      other.attendu == attendu &&
      other.encaisse == encaisse &&
      other.tauxBp == tauxBp;

    @override
    int get hashCode =>
        mois.hashCode +
        attendu.hashCode +
        encaisse.hashCode +
        tauxBp.hashCode;

  factory Mois.fromJson(Map<String, dynamic> json) => _$MoisFromJson(json);

  Map<String, dynamic> toJson() => _$MoisToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

