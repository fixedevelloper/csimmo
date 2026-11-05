// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'contre_passation_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ContrePassationRequeteCWProxy {
  ContrePassationRequete motif(String motif);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ContrePassationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ContrePassationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ContrePassationRequete call({String motif});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfContrePassationRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfContrePassationRequete.copyWith.fieldName(...)`
class _$ContrePassationRequeteCWProxyImpl
    implements _$ContrePassationRequeteCWProxy {
  const _$ContrePassationRequeteCWProxyImpl(this._value);

  final ContrePassationRequete _value;

  @override
  ContrePassationRequete motif(String motif) => this(motif: motif);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ContrePassationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ContrePassationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ContrePassationRequete call({Object? motif = const $CopyWithPlaceholder()}) {
    return ContrePassationRequete(
      motif: motif == const $CopyWithPlaceholder()
          ? _value.motif
          // ignore: cast_nullable_to_non_nullable
          : motif as String,
    );
  }
}

extension $ContrePassationRequeteCopyWith on ContrePassationRequete {
  /// Returns a callable class that can be used as follows: `instanceOfContrePassationRequete.copyWith(...)` or like so:`instanceOfContrePassationRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ContrePassationRequeteCWProxy get copyWith =>
      _$ContrePassationRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ContrePassationRequete _$ContrePassationRequeteFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('ContrePassationRequete', json, ($checkedConvert) {
  $checkKeys(json, requiredKeys: const ['motif']);
  final val = ContrePassationRequete(
    motif: $checkedConvert('motif', (v) => v as String),
  );
  return val;
});

Map<String, dynamic> _$ContrePassationRequeteToJson(
  ContrePassationRequete instance,
) => <String, dynamic>{'motif': instance.motif};
