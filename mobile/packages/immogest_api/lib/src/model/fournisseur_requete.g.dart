// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'fournisseur_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$FournisseurRequeteCWProxy {
  FournisseurRequete actif(bool? actif);

  FournisseurRequete champs(Map<String, String>? champs);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `FournisseurRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// FournisseurRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  FournisseurRequete call({bool? actif, Map<String, String>? champs});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfFournisseurRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfFournisseurRequete.copyWith.fieldName(...)`
class _$FournisseurRequeteCWProxyImpl implements _$FournisseurRequeteCWProxy {
  const _$FournisseurRequeteCWProxyImpl(this._value);

  final FournisseurRequete _value;

  @override
  FournisseurRequete actif(bool? actif) => this(actif: actif);

  @override
  FournisseurRequete champs(Map<String, String>? champs) =>
      this(champs: champs);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `FournisseurRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// FournisseurRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  FournisseurRequete call({
    Object? actif = const $CopyWithPlaceholder(),
    Object? champs = const $CopyWithPlaceholder(),
  }) {
    return FournisseurRequete(
      actif: actif == const $CopyWithPlaceholder()
          ? _value.actif
          // ignore: cast_nullable_to_non_nullable
          : actif as bool?,
      champs: champs == const $CopyWithPlaceholder()
          ? _value.champs
          // ignore: cast_nullable_to_non_nullable
          : champs as Map<String, String>?,
    );
  }
}

extension $FournisseurRequeteCopyWith on FournisseurRequete {
  /// Returns a callable class that can be used as follows: `instanceOfFournisseurRequete.copyWith(...)` or like so:`instanceOfFournisseurRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$FournisseurRequeteCWProxy get copyWith =>
      _$FournisseurRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FournisseurRequete _$FournisseurRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('FournisseurRequete', json, ($checkedConvert) {
      final val = FournisseurRequete(
        actif: $checkedConvert('actif', (v) => v as bool?),
        champs: $checkedConvert(
          'champs',
          (v) => (v as Map<String, dynamic>?)?.map(
            (k, e) => MapEntry(k, e as String),
          ),
        ),
      );
      return val;
    });

Map<String, dynamic> _$FournisseurRequeteToJson(FournisseurRequete instance) =>
    <String, dynamic>{'actif': ?instance.actif, 'champs': ?instance.champs};
