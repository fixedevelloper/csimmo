//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'statut_signalement_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class StatutSignalementRequete {
  /// Returns a new [StatutSignalementRequete] instance.
  StatutSignalementRequete({

    required  this.statut,

     this.note,
  });

  @JsonKey(
    
    name: r'statut',
    required: true,
    includeIfNull: false,
  )


  final StatutSignalementRequeteStatutEnum statut;



  @JsonKey(
    
    name: r'note',
    required: false,
    includeIfNull: false,
  )


  final String? note;





    @override
    bool operator ==(Object other) => identical(this, other) || other is StatutSignalementRequete &&
      other.statut == statut &&
      other.note == note;

    @override
    int get hashCode =>
        statut.hashCode +
        note.hashCode;

  factory StatutSignalementRequete.fromJson(Map<String, dynamic> json) => _$StatutSignalementRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$StatutSignalementRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum StatutSignalementRequeteStatutEnum {
@JsonValue(r'NOUVEAU')
NOUVEAU(r'NOUVEAU'),
@JsonValue(r'EN_COURS')
EN_COURS(r'EN_COURS'),
@JsonValue(r'RESOLU')
RESOLU(r'RESOLU'),
@JsonValue(r'REJETE')
REJETE(r'REJETE');

const StatutSignalementRequeteStatutEnum(this.value);

final String value;

@override
String toString() => value;
}


