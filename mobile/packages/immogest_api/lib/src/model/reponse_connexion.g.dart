// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reponse_connexion.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ReponseConnexionCWProxy {
  ReponseConnexion accessToken(String? accessToken);

  ReponseConnexion refreshToken(String? refreshToken);

  ReponseConnexion expiresIn(int? expiresIn);

  ReponseConnexion tokenType(String? tokenType);

  ReponseConnexion role(ReponseConnexionRoleEnum? role);

  ReponseConnexion deuxFacteursRequis(bool? deuxFacteursRequis);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ReponseConnexion(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ReponseConnexion(...).copyWith(id: 12, name: "My name")
  /// ````
  ReponseConnexion call({
    String? accessToken,
    String? refreshToken,
    int? expiresIn,
    String? tokenType,
    ReponseConnexionRoleEnum? role,
    bool? deuxFacteursRequis,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfReponseConnexion.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfReponseConnexion.copyWith.fieldName(...)`
class _$ReponseConnexionCWProxyImpl implements _$ReponseConnexionCWProxy {
  const _$ReponseConnexionCWProxyImpl(this._value);

  final ReponseConnexion _value;

  @override
  ReponseConnexion accessToken(String? accessToken) =>
      this(accessToken: accessToken);

  @override
  ReponseConnexion refreshToken(String? refreshToken) =>
      this(refreshToken: refreshToken);

  @override
  ReponseConnexion expiresIn(int? expiresIn) => this(expiresIn: expiresIn);

  @override
  ReponseConnexion tokenType(String? tokenType) => this(tokenType: tokenType);

  @override
  ReponseConnexion role(ReponseConnexionRoleEnum? role) => this(role: role);

  @override
  ReponseConnexion deuxFacteursRequis(bool? deuxFacteursRequis) =>
      this(deuxFacteursRequis: deuxFacteursRequis);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ReponseConnexion(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ReponseConnexion(...).copyWith(id: 12, name: "My name")
  /// ````
  ReponseConnexion call({
    Object? accessToken = const $CopyWithPlaceholder(),
    Object? refreshToken = const $CopyWithPlaceholder(),
    Object? expiresIn = const $CopyWithPlaceholder(),
    Object? tokenType = const $CopyWithPlaceholder(),
    Object? role = const $CopyWithPlaceholder(),
    Object? deuxFacteursRequis = const $CopyWithPlaceholder(),
  }) {
    return ReponseConnexion(
      accessToken: accessToken == const $CopyWithPlaceholder()
          ? _value.accessToken
          // ignore: cast_nullable_to_non_nullable
          : accessToken as String?,
      refreshToken: refreshToken == const $CopyWithPlaceholder()
          ? _value.refreshToken
          // ignore: cast_nullable_to_non_nullable
          : refreshToken as String?,
      expiresIn: expiresIn == const $CopyWithPlaceholder()
          ? _value.expiresIn
          // ignore: cast_nullable_to_non_nullable
          : expiresIn as int?,
      tokenType: tokenType == const $CopyWithPlaceholder()
          ? _value.tokenType
          // ignore: cast_nullable_to_non_nullable
          : tokenType as String?,
      role: role == const $CopyWithPlaceholder()
          ? _value.role
          // ignore: cast_nullable_to_non_nullable
          : role as ReponseConnexionRoleEnum?,
      deuxFacteursRequis: deuxFacteursRequis == const $CopyWithPlaceholder()
          ? _value.deuxFacteursRequis
          // ignore: cast_nullable_to_non_nullable
          : deuxFacteursRequis as bool?,
    );
  }
}

extension $ReponseConnexionCopyWith on ReponseConnexion {
  /// Returns a callable class that can be used as follows: `instanceOfReponseConnexion.copyWith(...)` or like so:`instanceOfReponseConnexion.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ReponseConnexionCWProxy get copyWith => _$ReponseConnexionCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ReponseConnexion _$ReponseConnexionFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ReponseConnexion', json, ($checkedConvert) {
      final val = ReponseConnexion(
        accessToken: $checkedConvert('accessToken', (v) => v as String?),
        refreshToken: $checkedConvert('refreshToken', (v) => v as String?),
        expiresIn: $checkedConvert('expiresIn', (v) => (v as num?)?.toInt()),
        tokenType: $checkedConvert('tokenType', (v) => v as String?),
        role: $checkedConvert(
          'role',
          (v) => $enumDecodeNullable(_$ReponseConnexionRoleEnumEnumMap, v),
        ),
        deuxFacteursRequis: $checkedConvert(
          'deuxFacteursRequis',
          (v) => v as bool?,
        ),
      );
      return val;
    });

Map<String, dynamic> _$ReponseConnexionToJson(ReponseConnexion instance) =>
    <String, dynamic>{
      'accessToken': ?instance.accessToken,
      'refreshToken': ?instance.refreshToken,
      'expiresIn': ?instance.expiresIn,
      'tokenType': ?instance.tokenType,
      'role': ?_$ReponseConnexionRoleEnumEnumMap[instance.role],
      'deuxFacteursRequis': ?instance.deuxFacteursRequis,
    };

const _$ReponseConnexionRoleEnumEnumMap = {
  ReponseConnexionRoleEnum.ADMIN: 'ADMIN',
  ReponseConnexionRoleEnum.GESTIONNAIRE: 'GESTIONNAIRE',
  ReponseConnexionRoleEnum.PROPRIETAIRE_TIERS: 'PROPRIETAIRE_TIERS',
  ReponseConnexionRoleEnum.LOCATAIRE: 'LOCATAIRE',
  ReponseConnexionRoleEnum.PRESTATAIRE: 'PRESTATAIRE',
  ReponseConnexionRoleEnum.ENCAISSEUR: 'ENCAISSEUR',
  ReponseConnexionRoleEnum.GESTIONNAIRE_BIENS: 'GESTIONNAIRE_BIENS',
};
