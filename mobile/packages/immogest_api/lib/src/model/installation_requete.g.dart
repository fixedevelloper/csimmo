// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'installation_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$InstallationRequeteCWProxy {
  InstallationRequete admin(Administrateur admin);

  InstallationRequete licence(String licence);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `InstallationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// InstallationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  InstallationRequete call({Administrateur admin, String licence});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfInstallationRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfInstallationRequete.copyWith.fieldName(...)`
class _$InstallationRequeteCWProxyImpl implements _$InstallationRequeteCWProxy {
  const _$InstallationRequeteCWProxyImpl(this._value);

  final InstallationRequete _value;

  @override
  InstallationRequete admin(Administrateur admin) => this(admin: admin);

  @override
  InstallationRequete licence(String licence) => this(licence: licence);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `InstallationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// InstallationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  InstallationRequete call({
    Object? admin = const $CopyWithPlaceholder(),
    Object? licence = const $CopyWithPlaceholder(),
  }) {
    return InstallationRequete(
      admin: admin == const $CopyWithPlaceholder()
          ? _value.admin
          // ignore: cast_nullable_to_non_nullable
          : admin as Administrateur,
      licence: licence == const $CopyWithPlaceholder()
          ? _value.licence
          // ignore: cast_nullable_to_non_nullable
          : licence as String,
    );
  }
}

extension $InstallationRequeteCopyWith on InstallationRequete {
  /// Returns a callable class that can be used as follows: `instanceOfInstallationRequete.copyWith(...)` or like so:`instanceOfInstallationRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$InstallationRequeteCWProxy get copyWith =>
      _$InstallationRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

InstallationRequete _$InstallationRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('InstallationRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['admin', 'licence']);
      final val = InstallationRequete(
        admin: $checkedConvert(
          'admin',
          (v) => Administrateur.fromJson(v as Map<String, dynamic>),
        ),
        licence: $checkedConvert('licence', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$InstallationRequeteToJson(
  InstallationRequete instance,
) => <String, dynamic>{
  'admin': instance.admin.toJson(),
  'licence': instance.licence,
};
