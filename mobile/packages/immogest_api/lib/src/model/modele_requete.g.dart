// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'modele_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ModeleRequeteCWProxy {
  ModeleRequete bailleurNom(String? bailleurNom);

  ModeleRequete bailleurAdresse(String? bailleurAdresse);

  ModeleRequete preambule(String? preambule);

  ModeleRequete clauses(String? clauses);

  ModeleRequete piedDePage(String? piedDePage);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ModeleRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ModeleRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ModeleRequete call({
    String? bailleurNom,
    String? bailleurAdresse,
    String? preambule,
    String? clauses,
    String? piedDePage,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfModeleRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfModeleRequete.copyWith.fieldName(...)`
class _$ModeleRequeteCWProxyImpl implements _$ModeleRequeteCWProxy {
  const _$ModeleRequeteCWProxyImpl(this._value);

  final ModeleRequete _value;

  @override
  ModeleRequete bailleurNom(String? bailleurNom) =>
      this(bailleurNom: bailleurNom);

  @override
  ModeleRequete bailleurAdresse(String? bailleurAdresse) =>
      this(bailleurAdresse: bailleurAdresse);

  @override
  ModeleRequete preambule(String? preambule) => this(preambule: preambule);

  @override
  ModeleRequete clauses(String? clauses) => this(clauses: clauses);

  @override
  ModeleRequete piedDePage(String? piedDePage) => this(piedDePage: piedDePage);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ModeleRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ModeleRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ModeleRequete call({
    Object? bailleurNom = const $CopyWithPlaceholder(),
    Object? bailleurAdresse = const $CopyWithPlaceholder(),
    Object? preambule = const $CopyWithPlaceholder(),
    Object? clauses = const $CopyWithPlaceholder(),
    Object? piedDePage = const $CopyWithPlaceholder(),
  }) {
    return ModeleRequete(
      bailleurNom: bailleurNom == const $CopyWithPlaceholder()
          ? _value.bailleurNom
          // ignore: cast_nullable_to_non_nullable
          : bailleurNom as String?,
      bailleurAdresse: bailleurAdresse == const $CopyWithPlaceholder()
          ? _value.bailleurAdresse
          // ignore: cast_nullable_to_non_nullable
          : bailleurAdresse as String?,
      preambule: preambule == const $CopyWithPlaceholder()
          ? _value.preambule
          // ignore: cast_nullable_to_non_nullable
          : preambule as String?,
      clauses: clauses == const $CopyWithPlaceholder()
          ? _value.clauses
          // ignore: cast_nullable_to_non_nullable
          : clauses as String?,
      piedDePage: piedDePage == const $CopyWithPlaceholder()
          ? _value.piedDePage
          // ignore: cast_nullable_to_non_nullable
          : piedDePage as String?,
    );
  }
}

extension $ModeleRequeteCopyWith on ModeleRequete {
  /// Returns a callable class that can be used as follows: `instanceOfModeleRequete.copyWith(...)` or like so:`instanceOfModeleRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ModeleRequeteCWProxy get copyWith => _$ModeleRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ModeleRequete _$ModeleRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ModeleRequete', json, ($checkedConvert) {
      final val = ModeleRequete(
        bailleurNom: $checkedConvert('bailleurNom', (v) => v as String?),
        bailleurAdresse: $checkedConvert(
          'bailleurAdresse',
          (v) => v as String?,
        ),
        preambule: $checkedConvert('preambule', (v) => v as String?),
        clauses: $checkedConvert('clauses', (v) => v as String?),
        piedDePage: $checkedConvert('piedDePage', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$ModeleRequeteToJson(ModeleRequete instance) =>
    <String, dynamic>{
      'bailleurNom': ?instance.bailleurNom,
      'bailleurAdresse': ?instance.bailleurAdresse,
      'preambule': ?instance.preambule,
      'clauses': ?instance.clauses,
      'piedDePage': ?instance.piedDePage,
    };
