// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'statut_signalement_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$StatutSignalementRequeteCWProxy {
  StatutSignalementRequete statut(StatutSignalementRequeteStatutEnum statut);

  StatutSignalementRequete note(String? note);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `StatutSignalementRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// StatutSignalementRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  StatutSignalementRequete call({
    StatutSignalementRequeteStatutEnum statut,
    String? note,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfStatutSignalementRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfStatutSignalementRequete.copyWith.fieldName(...)`
class _$StatutSignalementRequeteCWProxyImpl
    implements _$StatutSignalementRequeteCWProxy {
  const _$StatutSignalementRequeteCWProxyImpl(this._value);

  final StatutSignalementRequete _value;

  @override
  StatutSignalementRequete statut(StatutSignalementRequeteStatutEnum statut) =>
      this(statut: statut);

  @override
  StatutSignalementRequete note(String? note) => this(note: note);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `StatutSignalementRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// StatutSignalementRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  StatutSignalementRequete call({
    Object? statut = const $CopyWithPlaceholder(),
    Object? note = const $CopyWithPlaceholder(),
  }) {
    return StatutSignalementRequete(
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as StatutSignalementRequeteStatutEnum,
      note: note == const $CopyWithPlaceholder()
          ? _value.note
          // ignore: cast_nullable_to_non_nullable
          : note as String?,
    );
  }
}

extension $StatutSignalementRequeteCopyWith on StatutSignalementRequete {
  /// Returns a callable class that can be used as follows: `instanceOfStatutSignalementRequete.copyWith(...)` or like so:`instanceOfStatutSignalementRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$StatutSignalementRequeteCWProxy get copyWith =>
      _$StatutSignalementRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

StatutSignalementRequete _$StatutSignalementRequeteFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('StatutSignalementRequete', json, ($checkedConvert) {
  $checkKeys(json, requiredKeys: const ['statut']);
  final val = StatutSignalementRequete(
    statut: $checkedConvert(
      'statut',
      (v) => $enumDecode(_$StatutSignalementRequeteStatutEnumEnumMap, v),
    ),
    note: $checkedConvert('note', (v) => v as String?),
  );
  return val;
});

Map<String, dynamic> _$StatutSignalementRequeteToJson(
  StatutSignalementRequete instance,
) => <String, dynamic>{
  'statut': _$StatutSignalementRequeteStatutEnumEnumMap[instance.statut]!,
  'note': ?instance.note,
};

const _$StatutSignalementRequeteStatutEnumEnumMap = {
  StatutSignalementRequeteStatutEnum.NOUVEAU: 'NOUVEAU',
  StatutSignalementRequeteStatutEnum.EN_COURS: 'EN_COURS',
  StatutSignalementRequeteStatutEnum.RESOLU: 'RESOLU',
  StatutSignalementRequeteStatutEnum.REJETE: 'REJETE',
};
