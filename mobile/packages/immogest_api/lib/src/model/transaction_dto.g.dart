// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'transaction_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$TransactionDtoCWProxy {
  TransactionDto id(String? id);

  TransactionDto bailId(String? bailId);

  TransactionDto bailNumero(String? bailNumero);

  TransactionDto locataireId(String? locataireId);

  TransactionDto locataireNom(String? locataireNom);

  TransactionDto operateur(TransactionDtoOperateurEnum? operateur);

  TransactionDto telephone(String? telephone);

  TransactionDto montant(int? montant);

  TransactionDto statut(TransactionDtoStatutEnum? statut);

  TransactionDto instructions(String? instructions);

  TransactionDto erreur(String? erreur);

  TransactionDto paiementId(String? paiementId);

  TransactionDto creeLe(DateTime? creeLe);

  TransactionDto confirmeeLe(DateTime? confirmeeLe);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `TransactionDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// TransactionDto(...).copyWith(id: 12, name: "My name")
  /// ````
  TransactionDto call({
    String? id,
    String? bailId,
    String? bailNumero,
    String? locataireId,
    String? locataireNom,
    TransactionDtoOperateurEnum? operateur,
    String? telephone,
    int? montant,
    TransactionDtoStatutEnum? statut,
    String? instructions,
    String? erreur,
    String? paiementId,
    DateTime? creeLe,
    DateTime? confirmeeLe,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfTransactionDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfTransactionDto.copyWith.fieldName(...)`
class _$TransactionDtoCWProxyImpl implements _$TransactionDtoCWProxy {
  const _$TransactionDtoCWProxyImpl(this._value);

  final TransactionDto _value;

  @override
  TransactionDto id(String? id) => this(id: id);

  @override
  TransactionDto bailId(String? bailId) => this(bailId: bailId);

  @override
  TransactionDto bailNumero(String? bailNumero) => this(bailNumero: bailNumero);

  @override
  TransactionDto locataireId(String? locataireId) =>
      this(locataireId: locataireId);

  @override
  TransactionDto locataireNom(String? locataireNom) =>
      this(locataireNom: locataireNom);

  @override
  TransactionDto operateur(TransactionDtoOperateurEnum? operateur) =>
      this(operateur: operateur);

  @override
  TransactionDto telephone(String? telephone) => this(telephone: telephone);

  @override
  TransactionDto montant(int? montant) => this(montant: montant);

  @override
  TransactionDto statut(TransactionDtoStatutEnum? statut) =>
      this(statut: statut);

  @override
  TransactionDto instructions(String? instructions) =>
      this(instructions: instructions);

  @override
  TransactionDto erreur(String? erreur) => this(erreur: erreur);

  @override
  TransactionDto paiementId(String? paiementId) => this(paiementId: paiementId);

  @override
  TransactionDto creeLe(DateTime? creeLe) => this(creeLe: creeLe);

  @override
  TransactionDto confirmeeLe(DateTime? confirmeeLe) =>
      this(confirmeeLe: confirmeeLe);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `TransactionDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// TransactionDto(...).copyWith(id: 12, name: "My name")
  /// ````
  TransactionDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? bailId = const $CopyWithPlaceholder(),
    Object? bailNumero = const $CopyWithPlaceholder(),
    Object? locataireId = const $CopyWithPlaceholder(),
    Object? locataireNom = const $CopyWithPlaceholder(),
    Object? operateur = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? montant = const $CopyWithPlaceholder(),
    Object? statut = const $CopyWithPlaceholder(),
    Object? instructions = const $CopyWithPlaceholder(),
    Object? erreur = const $CopyWithPlaceholder(),
    Object? paiementId = const $CopyWithPlaceholder(),
    Object? creeLe = const $CopyWithPlaceholder(),
    Object? confirmeeLe = const $CopyWithPlaceholder(),
  }) {
    return TransactionDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      bailId: bailId == const $CopyWithPlaceholder()
          ? _value.bailId
          // ignore: cast_nullable_to_non_nullable
          : bailId as String?,
      bailNumero: bailNumero == const $CopyWithPlaceholder()
          ? _value.bailNumero
          // ignore: cast_nullable_to_non_nullable
          : bailNumero as String?,
      locataireId: locataireId == const $CopyWithPlaceholder()
          ? _value.locataireId
          // ignore: cast_nullable_to_non_nullable
          : locataireId as String?,
      locataireNom: locataireNom == const $CopyWithPlaceholder()
          ? _value.locataireNom
          // ignore: cast_nullable_to_non_nullable
          : locataireNom as String?,
      operateur: operateur == const $CopyWithPlaceholder()
          ? _value.operateur
          // ignore: cast_nullable_to_non_nullable
          : operateur as TransactionDtoOperateurEnum?,
      telephone: telephone == const $CopyWithPlaceholder()
          ? _value.telephone
          // ignore: cast_nullable_to_non_nullable
          : telephone as String?,
      montant: montant == const $CopyWithPlaceholder()
          ? _value.montant
          // ignore: cast_nullable_to_non_nullable
          : montant as int?,
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as TransactionDtoStatutEnum?,
      instructions: instructions == const $CopyWithPlaceholder()
          ? _value.instructions
          // ignore: cast_nullable_to_non_nullable
          : instructions as String?,
      erreur: erreur == const $CopyWithPlaceholder()
          ? _value.erreur
          // ignore: cast_nullable_to_non_nullable
          : erreur as String?,
      paiementId: paiementId == const $CopyWithPlaceholder()
          ? _value.paiementId
          // ignore: cast_nullable_to_non_nullable
          : paiementId as String?,
      creeLe: creeLe == const $CopyWithPlaceholder()
          ? _value.creeLe
          // ignore: cast_nullable_to_non_nullable
          : creeLe as DateTime?,
      confirmeeLe: confirmeeLe == const $CopyWithPlaceholder()
          ? _value.confirmeeLe
          // ignore: cast_nullable_to_non_nullable
          : confirmeeLe as DateTime?,
    );
  }
}

extension $TransactionDtoCopyWith on TransactionDto {
  /// Returns a callable class that can be used as follows: `instanceOfTransactionDto.copyWith(...)` or like so:`instanceOfTransactionDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$TransactionDtoCWProxy get copyWith => _$TransactionDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

TransactionDto _$TransactionDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('TransactionDto', json, ($checkedConvert) {
      final val = TransactionDto(
        id: $checkedConvert('id', (v) => v as String?),
        bailId: $checkedConvert('bailId', (v) => v as String?),
        bailNumero: $checkedConvert('bailNumero', (v) => v as String?),
        locataireId: $checkedConvert('locataireId', (v) => v as String?),
        locataireNom: $checkedConvert('locataireNom', (v) => v as String?),
        operateur: $checkedConvert(
          'operateur',
          (v) => $enumDecodeNullable(_$TransactionDtoOperateurEnumEnumMap, v),
        ),
        telephone: $checkedConvert('telephone', (v) => v as String?),
        montant: $checkedConvert('montant', (v) => (v as num?)?.toInt()),
        statut: $checkedConvert(
          'statut',
          (v) => $enumDecodeNullable(_$TransactionDtoStatutEnumEnumMap, v),
        ),
        instructions: $checkedConvert('instructions', (v) => v as String?),
        erreur: $checkedConvert('erreur', (v) => v as String?),
        paiementId: $checkedConvert('paiementId', (v) => v as String?),
        creeLe: $checkedConvert(
          'creeLe',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        confirmeeLe: $checkedConvert(
          'confirmeeLe',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
      );
      return val;
    });

Map<String, dynamic> _$TransactionDtoToJson(TransactionDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'bailId': ?instance.bailId,
      'bailNumero': ?instance.bailNumero,
      'locataireId': ?instance.locataireId,
      'locataireNom': ?instance.locataireNom,
      'operateur': ?_$TransactionDtoOperateurEnumEnumMap[instance.operateur],
      'telephone': ?instance.telephone,
      'montant': ?instance.montant,
      'statut': ?_$TransactionDtoStatutEnumEnumMap[instance.statut],
      'instructions': ?instance.instructions,
      'erreur': ?instance.erreur,
      'paiementId': ?instance.paiementId,
      'creeLe': ?instance.creeLe?.toIso8601String(),
      'confirmeeLe': ?instance.confirmeeLe?.toIso8601String(),
    };

const _$TransactionDtoOperateurEnumEnumMap = {
  TransactionDtoOperateurEnum.MTN_MOMO: 'MTN_MOMO',
  TransactionDtoOperateurEnum.ORANGE_MONEY: 'ORANGE_MONEY',
};

const _$TransactionDtoStatutEnumEnumMap = {
  TransactionDtoStatutEnum.EN_ATTENTE: 'EN_ATTENTE',
  TransactionDtoStatutEnum.CONFIRMEE: 'CONFIRMEE',
  TransactionDtoStatutEnum.ECHOUEE: 'ECHOUEE',
  TransactionDtoStatutEnum.EXPIREE: 'EXPIREE',
  TransactionDtoStatutEnum.ANOMALIE: 'ANOMALIE',
};
