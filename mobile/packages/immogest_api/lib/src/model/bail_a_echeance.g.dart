// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bail_a_echeance.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$BailAEcheanceCWProxy {
  BailAEcheance bailId(String? bailId);

  BailAEcheance numero(String? numero);

  BailAEcheance locataireNom(String? locataireNom);

  BailAEcheance logement(String? logement);

  BailAEcheance dateFin(DateTime? dateFin);

  BailAEcheance joursRestants(int? joursRestants);

  BailAEcheance statut(String? statut);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `BailAEcheance(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// BailAEcheance(...).copyWith(id: 12, name: "My name")
  /// ````
  BailAEcheance call({
    String? bailId,
    String? numero,
    String? locataireNom,
    String? logement,
    DateTime? dateFin,
    int? joursRestants,
    String? statut,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfBailAEcheance.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfBailAEcheance.copyWith.fieldName(...)`
class _$BailAEcheanceCWProxyImpl implements _$BailAEcheanceCWProxy {
  const _$BailAEcheanceCWProxyImpl(this._value);

  final BailAEcheance _value;

  @override
  BailAEcheance bailId(String? bailId) => this(bailId: bailId);

  @override
  BailAEcheance numero(String? numero) => this(numero: numero);

  @override
  BailAEcheance locataireNom(String? locataireNom) =>
      this(locataireNom: locataireNom);

  @override
  BailAEcheance logement(String? logement) => this(logement: logement);

  @override
  BailAEcheance dateFin(DateTime? dateFin) => this(dateFin: dateFin);

  @override
  BailAEcheance joursRestants(int? joursRestants) =>
      this(joursRestants: joursRestants);

  @override
  BailAEcheance statut(String? statut) => this(statut: statut);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `BailAEcheance(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// BailAEcheance(...).copyWith(id: 12, name: "My name")
  /// ````
  BailAEcheance call({
    Object? bailId = const $CopyWithPlaceholder(),
    Object? numero = const $CopyWithPlaceholder(),
    Object? locataireNom = const $CopyWithPlaceholder(),
    Object? logement = const $CopyWithPlaceholder(),
    Object? dateFin = const $CopyWithPlaceholder(),
    Object? joursRestants = const $CopyWithPlaceholder(),
    Object? statut = const $CopyWithPlaceholder(),
  }) {
    return BailAEcheance(
      bailId: bailId == const $CopyWithPlaceholder()
          ? _value.bailId
          // ignore: cast_nullable_to_non_nullable
          : bailId as String?,
      numero: numero == const $CopyWithPlaceholder()
          ? _value.numero
          // ignore: cast_nullable_to_non_nullable
          : numero as String?,
      locataireNom: locataireNom == const $CopyWithPlaceholder()
          ? _value.locataireNom
          // ignore: cast_nullable_to_non_nullable
          : locataireNom as String?,
      logement: logement == const $CopyWithPlaceholder()
          ? _value.logement
          // ignore: cast_nullable_to_non_nullable
          : logement as String?,
      dateFin: dateFin == const $CopyWithPlaceholder()
          ? _value.dateFin
          // ignore: cast_nullable_to_non_nullable
          : dateFin as DateTime?,
      joursRestants: joursRestants == const $CopyWithPlaceholder()
          ? _value.joursRestants
          // ignore: cast_nullable_to_non_nullable
          : joursRestants as int?,
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as String?,
    );
  }
}

extension $BailAEcheanceCopyWith on BailAEcheance {
  /// Returns a callable class that can be used as follows: `instanceOfBailAEcheance.copyWith(...)` or like so:`instanceOfBailAEcheance.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$BailAEcheanceCWProxy get copyWith => _$BailAEcheanceCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BailAEcheance _$BailAEcheanceFromJson(Map<String, dynamic> json) =>
    $checkedCreate('BailAEcheance', json, ($checkedConvert) {
      final val = BailAEcheance(
        bailId: $checkedConvert('bailId', (v) => v as String?),
        numero: $checkedConvert('numero', (v) => v as String?),
        locataireNom: $checkedConvert('locataireNom', (v) => v as String?),
        logement: $checkedConvert('logement', (v) => v as String?),
        dateFin: $checkedConvert(
          'dateFin',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        joursRestants: $checkedConvert(
          'joursRestants',
          (v) => (v as num?)?.toInt(),
        ),
        statut: $checkedConvert('statut', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$BailAEcheanceToJson(BailAEcheance instance) =>
    <String, dynamic>{
      'bailId': ?instance.bailId,
      'numero': ?instance.numero,
      'locataireNom': ?instance.locataireNom,
      'logement': ?instance.logement,
      'dateFin': ?instance.dateFin?.toIso8601String(),
      'joursRestants': ?instance.joursRestants,
      'statut': ?instance.statut,
    };
