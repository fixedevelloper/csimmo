// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'accuse.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$AccuseCWProxy {
  Accuse resultat(AccuseResultatEnum? resultat);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Accuse(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Accuse(...).copyWith(id: 12, name: "My name")
  /// ````
  Accuse call({AccuseResultatEnum? resultat});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfAccuse.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfAccuse.copyWith.fieldName(...)`
class _$AccuseCWProxyImpl implements _$AccuseCWProxy {
  const _$AccuseCWProxyImpl(this._value);

  final Accuse _value;

  @override
  Accuse resultat(AccuseResultatEnum? resultat) => this(resultat: resultat);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Accuse(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Accuse(...).copyWith(id: 12, name: "My name")
  /// ````
  Accuse call({Object? resultat = const $CopyWithPlaceholder()}) {
    return Accuse(
      resultat: resultat == const $CopyWithPlaceholder()
          ? _value.resultat
          // ignore: cast_nullable_to_non_nullable
          : resultat as AccuseResultatEnum?,
    );
  }
}

extension $AccuseCopyWith on Accuse {
  /// Returns a callable class that can be used as follows: `instanceOfAccuse.copyWith(...)` or like so:`instanceOfAccuse.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$AccuseCWProxy get copyWith => _$AccuseCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Accuse _$AccuseFromJson(Map<String, dynamic> json) =>
    $checkedCreate('Accuse', json, ($checkedConvert) {
      final val = Accuse(
        resultat: $checkedConvert(
          'resultat',
          (v) => $enumDecodeNullable(_$AccuseResultatEnumEnumMap, v),
        ),
      );
      return val;
    });

Map<String, dynamic> _$AccuseToJson(Accuse instance) => <String, dynamic>{
  'resultat': ?_$AccuseResultatEnumEnumMap[instance.resultat],
};

const _$AccuseResultatEnumEnumMap = {
  AccuseResultatEnum.TRAITE: 'TRAITE',
  AccuseResultatEnum.REJOUE: 'REJOUE',
  AccuseResultatEnum.IGNORE: 'IGNORE',
};
