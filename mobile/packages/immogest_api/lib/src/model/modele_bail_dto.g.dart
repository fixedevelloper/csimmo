// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'modele_bail_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ModeleBailDtoCWProxy {
  ModeleBailDto bailleurNom(String? bailleurNom);

  ModeleBailDto bailleurAdresse(String? bailleurAdresse);

  ModeleBailDto preambule(String? preambule);

  ModeleBailDto clauses(String? clauses);

  ModeleBailDto piedDePage(String? piedDePage);

  ModeleBailDto variables(List<String>? variables);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ModeleBailDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ModeleBailDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ModeleBailDto call({
    String? bailleurNom,
    String? bailleurAdresse,
    String? preambule,
    String? clauses,
    String? piedDePage,
    List<String>? variables,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfModeleBailDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfModeleBailDto.copyWith.fieldName(...)`
class _$ModeleBailDtoCWProxyImpl implements _$ModeleBailDtoCWProxy {
  const _$ModeleBailDtoCWProxyImpl(this._value);

  final ModeleBailDto _value;

  @override
  ModeleBailDto bailleurNom(String? bailleurNom) =>
      this(bailleurNom: bailleurNom);

  @override
  ModeleBailDto bailleurAdresse(String? bailleurAdresse) =>
      this(bailleurAdresse: bailleurAdresse);

  @override
  ModeleBailDto preambule(String? preambule) => this(preambule: preambule);

  @override
  ModeleBailDto clauses(String? clauses) => this(clauses: clauses);

  @override
  ModeleBailDto piedDePage(String? piedDePage) => this(piedDePage: piedDePage);

  @override
  ModeleBailDto variables(List<String>? variables) =>
      this(variables: variables);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ModeleBailDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ModeleBailDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ModeleBailDto call({
    Object? bailleurNom = const $CopyWithPlaceholder(),
    Object? bailleurAdresse = const $CopyWithPlaceholder(),
    Object? preambule = const $CopyWithPlaceholder(),
    Object? clauses = const $CopyWithPlaceholder(),
    Object? piedDePage = const $CopyWithPlaceholder(),
    Object? variables = const $CopyWithPlaceholder(),
  }) {
    return ModeleBailDto(
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
      variables: variables == const $CopyWithPlaceholder()
          ? _value.variables
          // ignore: cast_nullable_to_non_nullable
          : variables as List<String>?,
    );
  }
}

extension $ModeleBailDtoCopyWith on ModeleBailDto {
  /// Returns a callable class that can be used as follows: `instanceOfModeleBailDto.copyWith(...)` or like so:`instanceOfModeleBailDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ModeleBailDtoCWProxy get copyWith => _$ModeleBailDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ModeleBailDto _$ModeleBailDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ModeleBailDto', json, ($checkedConvert) {
      final val = ModeleBailDto(
        bailleurNom: $checkedConvert('bailleurNom', (v) => v as String?),
        bailleurAdresse: $checkedConvert(
          'bailleurAdresse',
          (v) => v as String?,
        ),
        preambule: $checkedConvert('preambule', (v) => v as String?),
        clauses: $checkedConvert('clauses', (v) => v as String?),
        piedDePage: $checkedConvert('piedDePage', (v) => v as String?),
        variables: $checkedConvert(
          'variables',
          (v) => (v as List<dynamic>?)?.map((e) => e as String).toList(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$ModeleBailDtoToJson(ModeleBailDto instance) =>
    <String, dynamic>{
      'bailleurNom': ?instance.bailleurNom,
      'bailleurAdresse': ?instance.bailleurAdresse,
      'preambule': ?instance.preambule,
      'clauses': ?instance.clauses,
      'piedDePage': ?instance.piedDePage,
      'variables': ?instance.variables,
    };
