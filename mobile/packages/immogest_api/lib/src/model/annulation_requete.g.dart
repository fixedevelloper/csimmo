// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'annulation_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$AnnulationRequeteCWProxy {
  AnnulationRequete motif(String motif);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `AnnulationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// AnnulationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  AnnulationRequete call({String motif});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfAnnulationRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfAnnulationRequete.copyWith.fieldName(...)`
class _$AnnulationRequeteCWProxyImpl implements _$AnnulationRequeteCWProxy {
  const _$AnnulationRequeteCWProxyImpl(this._value);

  final AnnulationRequete _value;

  @override
  AnnulationRequete motif(String motif) => this(motif: motif);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `AnnulationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// AnnulationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  AnnulationRequete call({Object? motif = const $CopyWithPlaceholder()}) {
    return AnnulationRequete(
      motif: motif == const $CopyWithPlaceholder()
          ? _value.motif
          // ignore: cast_nullable_to_non_nullable
          : motif as String,
    );
  }
}

extension $AnnulationRequeteCopyWith on AnnulationRequete {
  /// Returns a callable class that can be used as follows: `instanceOfAnnulationRequete.copyWith(...)` or like so:`instanceOfAnnulationRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$AnnulationRequeteCWProxy get copyWith =>
      _$AnnulationRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

AnnulationRequete _$AnnulationRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('AnnulationRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['motif']);
      final val = AnnulationRequete(
        motif: $checkedConvert('motif', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$AnnulationRequeteToJson(AnnulationRequete instance) =>
    <String, dynamic>{'motif': instance.motif};
