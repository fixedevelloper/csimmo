// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'etat_installation_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$EtatInstallationDtoCWProxy {
  EtatInstallationDto installee(bool? installee);

  EtatInstallationDto empreinteServeur(String? empreinteServeur);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `EtatInstallationDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// EtatInstallationDto(...).copyWith(id: 12, name: "My name")
  /// ````
  EtatInstallationDto call({bool? installee, String? empreinteServeur});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfEtatInstallationDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfEtatInstallationDto.copyWith.fieldName(...)`
class _$EtatInstallationDtoCWProxyImpl implements _$EtatInstallationDtoCWProxy {
  const _$EtatInstallationDtoCWProxyImpl(this._value);

  final EtatInstallationDto _value;

  @override
  EtatInstallationDto installee(bool? installee) => this(installee: installee);

  @override
  EtatInstallationDto empreinteServeur(String? empreinteServeur) =>
      this(empreinteServeur: empreinteServeur);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `EtatInstallationDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// EtatInstallationDto(...).copyWith(id: 12, name: "My name")
  /// ````
  EtatInstallationDto call({
    Object? installee = const $CopyWithPlaceholder(),
    Object? empreinteServeur = const $CopyWithPlaceholder(),
  }) {
    return EtatInstallationDto(
      installee: installee == const $CopyWithPlaceholder()
          ? _value.installee
          // ignore: cast_nullable_to_non_nullable
          : installee as bool?,
      empreinteServeur: empreinteServeur == const $CopyWithPlaceholder()
          ? _value.empreinteServeur
          // ignore: cast_nullable_to_non_nullable
          : empreinteServeur as String?,
    );
  }
}

extension $EtatInstallationDtoCopyWith on EtatInstallationDto {
  /// Returns a callable class that can be used as follows: `instanceOfEtatInstallationDto.copyWith(...)` or like so:`instanceOfEtatInstallationDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$EtatInstallationDtoCWProxy get copyWith =>
      _$EtatInstallationDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

EtatInstallationDto _$EtatInstallationDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('EtatInstallationDto', json, ($checkedConvert) {
      final val = EtatInstallationDto(
        installee: $checkedConvert('installee', (v) => v as bool?),
        empreinteServeur: $checkedConvert(
          'empreinteServeur',
          (v) => v as String?,
        ),
      );
      return val;
    });

Map<String, dynamic> _$EtatInstallationDtoToJson(
  EtatInstallationDto instance,
) => <String, dynamic>{
  'installee': ?instance.installee,
  'empreinteServeur': ?instance.empreinteServeur,
};
