import 'dart:convert';

import 'package:dio/dio.dart';

import 'fake_adapter.dart';

enum Reseau { ok, coupe, perteReponse }

/// Faux serveur ImmoGest pour les paiements : applique l'idempotence comme le vrai (même clé + même requête = même paiement, rejeu = 200).
class ServeurPaiements {
  Reseau reseau = Reseau.ok;

  /// Paiements créés, par clé d'idempotence.
  final Map<String, Map<String, Object?>> crees = {};
  final List<RequestOptions> requetes = [];
  int get nombrePaiements => crees.length;

  /// Bails connus ; un autre bail donne 404 BAIL_INTROUVABLE.
  final Set<String> bailsConnus = {'b-1', 'b-2'};

  Reponse traiter(RequestOptions o) {
    requetes.add(o);
    if (reseau == Reseau.coupe) return (status: -1, corps: null);
    if (o.path == '/api/v1/paiements' && o.method == 'POST') {
      final cle = o.headers['Idempotency-Key'] as String?;
      final corps = jsonDecode(o.data as String) as Map<String, dynamic>;
      Reponse reponse;
      if (!bailsConnus.contains(corps['bailId'])) {
        reponse = (status: 404, corps: {'code': 'BAIL_INTROUVABLE'});
      } else if (cle != null && crees.containsKey(cle)) {
        reponse = (status: 200, corps: crees[cle]); // rejeu : le paiement d'origine, rien de recréé
      } else {
        final paiement = <String, Object?>{'id': 'p-${crees.length + 1}', 'bailId': corps['bailId'], 'montant': corps['montant'], 'mode': corps['mode']};
        if (cle != null) crees[cle] = paiement;
        reponse = (status: 201, corps: paiement);
      }
      // la réponse se perd en route : le serveur a bien travaillé, mais le téléphone ne le sait pas
      return reseau == Reseau.perteReponse ? (status: -1, corps: null) : reponse;
    }
    return (status: 404, corps: {'code': 'INTROUVABLE'});
  }
}
