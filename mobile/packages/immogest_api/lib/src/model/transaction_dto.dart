//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'transaction_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class TransactionDto {
  /// Returns a new [TransactionDto] instance.
  TransactionDto({

     this.id,

     this.bailId,

     this.bailNumero,

     this.locataireId,

     this.locataireNom,

     this.operateur,

     this.telephone,

     this.montant,

     this.statut,

     this.instructions,

     this.erreur,

     this.paiementId,

     this.creeLe,

     this.confirmeeLe,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'bailId',
    required: false,
    includeIfNull: false,
  )


  final String? bailId;



  @JsonKey(
    
    name: r'bailNumero',
    required: false,
    includeIfNull: false,
  )


  final String? bailNumero;



  @JsonKey(
    
    name: r'locataireId',
    required: false,
    includeIfNull: false,
  )


  final String? locataireId;



  @JsonKey(
    
    name: r'locataireNom',
    required: false,
    includeIfNull: false,
  )


  final String? locataireNom;



  @JsonKey(
    
    name: r'operateur',
    required: false,
    includeIfNull: false,
  )


  final TransactionDtoOperateurEnum? operateur;



  @JsonKey(
    
    name: r'telephone',
    required: false,
    includeIfNull: false,
  )


  final String? telephone;



  @JsonKey(
    
    name: r'montant',
    required: false,
    includeIfNull: false,
  )


  final int? montant;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final TransactionDtoStatutEnum? statut;



  @JsonKey(
    
    name: r'instructions',
    required: false,
    includeIfNull: false,
  )


  final String? instructions;



  @JsonKey(
    
    name: r'erreur',
    required: false,
    includeIfNull: false,
  )


  final String? erreur;



  @JsonKey(
    
    name: r'paiementId',
    required: false,
    includeIfNull: false,
  )


  final String? paiementId;



  @JsonKey(
    
    name: r'creeLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? creeLe;



  @JsonKey(
    
    name: r'confirmeeLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? confirmeeLe;





    @override
    bool operator ==(Object other) => identical(this, other) || other is TransactionDto &&
      other.id == id &&
      other.bailId == bailId &&
      other.bailNumero == bailNumero &&
      other.locataireId == locataireId &&
      other.locataireNom == locataireNom &&
      other.operateur == operateur &&
      other.telephone == telephone &&
      other.montant == montant &&
      other.statut == statut &&
      other.instructions == instructions &&
      other.erreur == erreur &&
      other.paiementId == paiementId &&
      other.creeLe == creeLe &&
      other.confirmeeLe == confirmeeLe;

    @override
    int get hashCode =>
        id.hashCode +
        bailId.hashCode +
        bailNumero.hashCode +
        locataireId.hashCode +
        locataireNom.hashCode +
        operateur.hashCode +
        telephone.hashCode +
        montant.hashCode +
        statut.hashCode +
        instructions.hashCode +
        erreur.hashCode +
        paiementId.hashCode +
        creeLe.hashCode +
        confirmeeLe.hashCode;

  factory TransactionDto.fromJson(Map<String, dynamic> json) => _$TransactionDtoFromJson(json);

  Map<String, dynamic> toJson() => _$TransactionDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum TransactionDtoOperateurEnum {
@JsonValue(r'MTN_MOMO')
MTN_MOMO(r'MTN_MOMO'),
@JsonValue(r'ORANGE_MONEY')
ORANGE_MONEY(r'ORANGE_MONEY');

const TransactionDtoOperateurEnum(this.value);

final String value;

@override
String toString() => value;
}


enum TransactionDtoStatutEnum {
@JsonValue(r'EN_ATTENTE')
EN_ATTENTE(r'EN_ATTENTE'),
@JsonValue(r'CONFIRMEE')
CONFIRMEE(r'CONFIRMEE'),
@JsonValue(r'ECHOUEE')
ECHOUEE(r'ECHOUEE'),
@JsonValue(r'EXPIREE')
EXPIREE(r'EXPIREE'),
@JsonValue(r'ANOMALIE')
ANOMALIE(r'ANOMALIE');

const TransactionDtoStatutEnum(this.value);

final String value;

@override
String toString() => value;
}


