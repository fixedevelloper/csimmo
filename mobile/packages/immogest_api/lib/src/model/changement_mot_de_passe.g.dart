// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'changement_mot_de_passe.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ChangementMotDePasseCWProxy {
  ChangementMotDePasse ancienMotDePasse(String ancienMotDePasse);

  ChangementMotDePasse nouveauMotDePasse(String nouveauMotDePasse);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ChangementMotDePasse(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ChangementMotDePasse(...).copyWith(id: 12, name: "My name")
  /// ````
  ChangementMotDePasse call({
    String ancienMotDePasse,
    String nouveauMotDePasse,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfChangementMotDePasse.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfChangementMotDePasse.copyWith.fieldName(...)`
class _$ChangementMotDePasseCWProxyImpl
    implements _$ChangementMotDePasseCWProxy {
  const _$ChangementMotDePasseCWProxyImpl(this._value);

  final ChangementMotDePasse _value;

  @override
  ChangementMotDePasse ancienMotDePasse(String ancienMotDePasse) =>
      this(ancienMotDePasse: ancienMotDePasse);

  @override
  ChangementMotDePasse nouveauMotDePasse(String nouveauMotDePasse) =>
      this(nouveauMotDePasse: nouveauMotDePasse);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ChangementMotDePasse(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ChangementMotDePasse(...).copyWith(id: 12, name: "My name")
  /// ````
  ChangementMotDePasse call({
    Object? ancienMotDePasse = const $CopyWithPlaceholder(),
    Object? nouveauMotDePasse = const $CopyWithPlaceholder(),
  }) {
    return ChangementMotDePasse(
      ancienMotDePasse: ancienMotDePasse == const $CopyWithPlaceholder()
          ? _value.ancienMotDePasse
          // ignore: cast_nullable_to_non_nullable
          : ancienMotDePasse as String,
      nouveauMotDePasse: nouveauMotDePasse == const $CopyWithPlaceholder()
          ? _value.nouveauMotDePasse
          // ignore: cast_nullable_to_non_nullable
          : nouveauMotDePasse as String,
    );
  }
}

extension $ChangementMotDePasseCopyWith on ChangementMotDePasse {
  /// Returns a callable class that can be used as follows: `instanceOfChangementMotDePasse.copyWith(...)` or like so:`instanceOfChangementMotDePasse.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ChangementMotDePasseCWProxy get copyWith =>
      _$ChangementMotDePasseCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ChangementMotDePasse _$ChangementMotDePasseFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('ChangementMotDePasse', json, ($checkedConvert) {
  $checkKeys(
    json,
    requiredKeys: const ['ancienMotDePasse', 'nouveauMotDePasse'],
  );
  final val = ChangementMotDePasse(
    ancienMotDePasse: $checkedConvert('ancienMotDePasse', (v) => v as String),
    nouveauMotDePasse: $checkedConvert('nouveauMotDePasse', (v) => v as String),
  );
  return val;
});

Map<String, dynamic> _$ChangementMotDePasseToJson(
  ChangementMotDePasse instance,
) => <String, dynamic>{
  'ancienMotDePasse': instance.ancienMotDePasse,
  'nouveauMotDePasse': instance.nouveauMotDePasse,
};
