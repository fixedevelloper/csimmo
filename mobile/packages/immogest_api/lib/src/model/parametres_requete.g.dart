// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'parametres_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ParametresRequeteCWProxy {
  ParametresRequete actif(bool? actif);

  ParametresRequete niveaux(List<int> niveaux);

  ParametresRequete delaiMiseEnDemeureJours(int? delaiMiseEnDemeureJours);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ParametresRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ParametresRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ParametresRequete call({
    bool? actif,
    List<int> niveaux,
    int? delaiMiseEnDemeureJours,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfParametresRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfParametresRequete.copyWith.fieldName(...)`
class _$ParametresRequeteCWProxyImpl implements _$ParametresRequeteCWProxy {
  const _$ParametresRequeteCWProxyImpl(this._value);

  final ParametresRequete _value;

  @override
  ParametresRequete actif(bool? actif) => this(actif: actif);

  @override
  ParametresRequete niveaux(List<int> niveaux) => this(niveaux: niveaux);

  @override
  ParametresRequete delaiMiseEnDemeureJours(int? delaiMiseEnDemeureJours) =>
      this(delaiMiseEnDemeureJours: delaiMiseEnDemeureJours);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ParametresRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ParametresRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ParametresRequete call({
    Object? actif = const $CopyWithPlaceholder(),
    Object? niveaux = const $CopyWithPlaceholder(),
    Object? delaiMiseEnDemeureJours = const $CopyWithPlaceholder(),
  }) {
    return ParametresRequete(
      actif: actif == const $CopyWithPlaceholder()
          ? _value.actif
          // ignore: cast_nullable_to_non_nullable
          : actif as bool?,
      niveaux: niveaux == const $CopyWithPlaceholder()
          ? _value.niveaux
          // ignore: cast_nullable_to_non_nullable
          : niveaux as List<int>,
      delaiMiseEnDemeureJours:
          delaiMiseEnDemeureJours == const $CopyWithPlaceholder()
          ? _value.delaiMiseEnDemeureJours
          // ignore: cast_nullable_to_non_nullable
          : delaiMiseEnDemeureJours as int?,
    );
  }
}

extension $ParametresRequeteCopyWith on ParametresRequete {
  /// Returns a callable class that can be used as follows: `instanceOfParametresRequete.copyWith(...)` or like so:`instanceOfParametresRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ParametresRequeteCWProxy get copyWith =>
      _$ParametresRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ParametresRequete _$ParametresRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ParametresRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['niveaux']);
      final val = ParametresRequete(
        actif: $checkedConvert('actif', (v) => v as bool?),
        niveaux: $checkedConvert(
          'niveaux',
          (v) => (v as List<dynamic>).map((e) => (e as num).toInt()).toList(),
        ),
        delaiMiseEnDemeureJours: $checkedConvert(
          'delaiMiseEnDemeureJours',
          (v) => (v as num?)?.toInt(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$ParametresRequeteToJson(ParametresRequete instance) =>
    <String, dynamic>{
      'actif': ?instance.actif,
      'niveaux': instance.niveaux,
      'delaiMiseEnDemeureJours': ?instance.delaiMiseEnDemeureJours,
    };
