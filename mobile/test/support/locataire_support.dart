import 'dart:async';
import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/misc.dart' show Override;
import 'package:immogest_api/immogest_api.dart';
import 'package:immogest_mobile/core/platform/plateforme.dart';
import 'package:immogest_mobile/core/providers.dart';
import 'package:immogest_mobile/l10n/app_localizations.dart';

import 'fake_adapter.dart';

class FauxFichiers implements FichiersLocaux {
  final List<(String, int)> ecrits = [];
  final List<String> ouverts = [];
  bool echec = false;

  @override
  Future<String> ecrire(String nom, Uint8List octets) async {
    ecrits.add((nom, octets.length));
    return '/tmp/immogest/${nomSur(nom)}';
  }

  @override
  Future<void> ouvrir(String chemin) async {
    ouverts.add(chemin);
  }
}

class FauxSelecteur implements SelecteurPhoto {
  FauxSelecteur(this.photo);

  PhotoChoisie? photo;
  SourcePhoto? derniereSource;

  @override
  Future<PhotoChoisie?> choisir(SourcePhoto source) async {
    derniereSource = source;
    return photo;
  }
}

Map<String, Object?> page(List<Map<String, Object?>> contenu) => {'content': contenu, 'page': 0, 'size': 50, 'totalElements': contenu.length};

/// API branchée sur un serveur scripté (aucun réseau).
(ImmogestApi, FakeAdapter) apiFactice(FutureOr<Reponse> Function(RequestOptions) handler) {
  final adaptateur = FakeAdapter(handler);
  final dio = Dio(BaseOptions(baseUrl: 'http://serveur.test'))..httpClientAdapter = adaptateur;
  return (ImmogestApi(dio: dio, interceptors: const []), adaptateur);
}

/// Écran seul dans une application de test française.
Widget appDeTest(Widget ecran, {required ImmogestApi api, FauxFichiers? fichiers, FauxSelecteur? selecteur, List<Override> extra = const []}) {
  return ProviderScope(
    retry: (_, _) => null,
    overrides: [
      apiProvider.overrideWithValue(api),
      if (fichiers != null) fichiersLocauxProvider.overrideWithValue(fichiers),
      if (selecteur != null) selecteurPhotoProvider.overrideWithValue(selecteur),
      ...extra,
    ],
    child: MaterialApp(
      locale: const Locale('fr'),
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      home: Scaffold(body: ecran),
    ),
  );
}
