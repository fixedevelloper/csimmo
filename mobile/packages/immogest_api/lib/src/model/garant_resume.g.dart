// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'garant_resume.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$GarantResumeCWProxy {
  GarantResume id(String? id);

  GarantResume nom(String? nom);

  GarantResume telephone(String? telephone);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `GarantResume(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// GarantResume(...).copyWith(id: 12, name: "My name")
  /// ````
  GarantResume call({String? id, String? nom, String? telephone});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfGarantResume.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfGarantResume.copyWith.fieldName(...)`
class _$GarantResumeCWProxyImpl implements _$GarantResumeCWProxy {
  const _$GarantResumeCWProxyImpl(this._value);

  final GarantResume _value;

  @override
  GarantResume id(String? id) => this(id: id);

  @override
  GarantResume nom(String? nom) => this(nom: nom);

  @override
  GarantResume telephone(String? telephone) => this(telephone: telephone);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `GarantResume(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// GarantResume(...).copyWith(id: 12, name: "My name")
  /// ````
  GarantResume call({
    Object? id = const $CopyWithPlaceholder(),
    Object? nom = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
  }) {
    return GarantResume(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      nom: nom == const $CopyWithPlaceholder()
          ? _value.nom
          // ignore: cast_nullable_to_non_nullable
          : nom as String?,
      telephone: telephone == const $CopyWithPlaceholder()
          ? _value.telephone
          // ignore: cast_nullable_to_non_nullable
          : telephone as String?,
    );
  }
}

extension $GarantResumeCopyWith on GarantResume {
  /// Returns a callable class that can be used as follows: `instanceOfGarantResume.copyWith(...)` or like so:`instanceOfGarantResume.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$GarantResumeCWProxy get copyWith => _$GarantResumeCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

GarantResume _$GarantResumeFromJson(Map<String, dynamic> json) =>
    $checkedCreate('GarantResume', json, ($checkedConvert) {
      final val = GarantResume(
        id: $checkedConvert('id', (v) => v as String?),
        nom: $checkedConvert('nom', (v) => v as String?),
        telephone: $checkedConvert('telephone', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$GarantResumeToJson(GarantResume instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'nom': ?instance.nom,
      'telephone': ?instance.telephone,
    };
