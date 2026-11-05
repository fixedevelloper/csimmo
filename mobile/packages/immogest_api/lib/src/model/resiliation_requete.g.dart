// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'resiliation_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ResiliationRequeteCWProxy {
  ResiliationRequete dateNotification(DateTime? dateNotification);

  ResiliationRequete dateSortie(DateTime? dateSortie);

  ResiliationRequete preavisReduitAccepte(bool? preavisReduitAccepte);

  ResiliationRequete motif(String? motif);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ResiliationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ResiliationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ResiliationRequete call({
    DateTime? dateNotification,
    DateTime? dateSortie,
    bool? preavisReduitAccepte,
    String? motif,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfResiliationRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfResiliationRequete.copyWith.fieldName(...)`
class _$ResiliationRequeteCWProxyImpl implements _$ResiliationRequeteCWProxy {
  const _$ResiliationRequeteCWProxyImpl(this._value);

  final ResiliationRequete _value;

  @override
  ResiliationRequete dateNotification(DateTime? dateNotification) =>
      this(dateNotification: dateNotification);

  @override
  ResiliationRequete dateSortie(DateTime? dateSortie) =>
      this(dateSortie: dateSortie);

  @override
  ResiliationRequete preavisReduitAccepte(bool? preavisReduitAccepte) =>
      this(preavisReduitAccepte: preavisReduitAccepte);

  @override
  ResiliationRequete motif(String? motif) => this(motif: motif);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ResiliationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ResiliationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ResiliationRequete call({
    Object? dateNotification = const $CopyWithPlaceholder(),
    Object? dateSortie = const $CopyWithPlaceholder(),
    Object? preavisReduitAccepte = const $CopyWithPlaceholder(),
    Object? motif = const $CopyWithPlaceholder(),
  }) {
    return ResiliationRequete(
      dateNotification: dateNotification == const $CopyWithPlaceholder()
          ? _value.dateNotification
          // ignore: cast_nullable_to_non_nullable
          : dateNotification as DateTime?,
      dateSortie: dateSortie == const $CopyWithPlaceholder()
          ? _value.dateSortie
          // ignore: cast_nullable_to_non_nullable
          : dateSortie as DateTime?,
      preavisReduitAccepte: preavisReduitAccepte == const $CopyWithPlaceholder()
          ? _value.preavisReduitAccepte
          // ignore: cast_nullable_to_non_nullable
          : preavisReduitAccepte as bool?,
      motif: motif == const $CopyWithPlaceholder()
          ? _value.motif
          // ignore: cast_nullable_to_non_nullable
          : motif as String?,
    );
  }
}

extension $ResiliationRequeteCopyWith on ResiliationRequete {
  /// Returns a callable class that can be used as follows: `instanceOfResiliationRequete.copyWith(...)` or like so:`instanceOfResiliationRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ResiliationRequeteCWProxy get copyWith =>
      _$ResiliationRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ResiliationRequete _$ResiliationRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ResiliationRequete', json, ($checkedConvert) {
      final val = ResiliationRequete(
        dateNotification: $checkedConvert(
          'dateNotification',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        dateSortie: $checkedConvert(
          'dateSortie',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        preavisReduitAccepte: $checkedConvert(
          'preavisReduitAccepte',
          (v) => v as bool?,
        ),
        motif: $checkedConvert('motif', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$ResiliationRequeteToJson(ResiliationRequete instance) =>
    <String, dynamic>{
      'dateNotification': ?instance.dateNotification?.toIso8601String(),
      'dateSortie': ?instance.dateSortie?.toIso8601String(),
      'preavisReduitAccepte': ?instance.preavisReduitAccepte,
      'motif': ?instance.motif,
    };
