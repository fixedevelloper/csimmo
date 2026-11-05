//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'resiliation_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ResiliationRequete {
  /// Returns a new [ResiliationRequete] instance.
  ResiliationRequete({

     this.dateNotification,

     this.dateSortie,

     this.preavisReduitAccepte,

     this.motif,
  });

  @JsonKey(
    
    name: r'dateNotification',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateNotification;



  @JsonKey(
    
    name: r'dateSortie',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateSortie;



  @JsonKey(
    
    name: r'preavisReduitAccepte',
    required: false,
    includeIfNull: false,
  )


  final bool? preavisReduitAccepte;



  @JsonKey(
    
    name: r'motif',
    required: false,
    includeIfNull: false,
  )


  final String? motif;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ResiliationRequete &&
      other.dateNotification == dateNotification &&
      other.dateSortie == dateSortie &&
      other.preavisReduitAccepte == preavisReduitAccepte &&
      other.motif == motif;

    @override
    int get hashCode =>
        dateNotification.hashCode +
        dateSortie.hashCode +
        preavisReduitAccepte.hashCode +
        motif.hashCode;

  factory ResiliationRequete.fromJson(Map<String, dynamic> json) => _$ResiliationRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$ResiliationRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

