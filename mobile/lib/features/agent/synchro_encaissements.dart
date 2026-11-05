// ignore_for_file: prefer_initializing_formals
import 'package:dio/dio.dart';
import 'package:immogest_api/immogest_api.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/auth/jwt.dart';
import '../../core/error/app_error.dart';
import '../../core/providers.dart';
import 'file_encaissements.dart';

part 'synchro_encaissements.g.dart';

/// Résultat d'un passage de synchronisation.
class BilanSynchro {
  const BilanSynchro({this.envoyes = 0, this.rejetes = 0, this.restants = 0, this.blocage});

  final int envoyes;
  final int rejetes;

  /// Encaissements encore en attente après le passage.
  final int restants;

  /// Code de ce qui a interrompu le passage (réseau, licence, session…), `null` si tout a été traité.
  final String? blocage;
}

/// Envoie la file d'encaissements au serveur, dans l'ordre de saisie. Chaque envoi porte la clé d'idempotence de la ligne : si une
/// réponse se perd (coupure après création côté serveur), le renvoi ne crée jamais un second paiement.
class SynchroEncaissements {
  SynchroEncaissements({required ImmogestApi api, required FileEncaissements file, required Future<String?> Function() utilisateurId})
      : _api = api,
        _file = file,
        _utilisateurId = utilisateurId;

  final ImmogestApi _api;
  final FileEncaissements _file;
  final Future<String?> Function() _utilisateurId;
  Future<BilanSynchro>? _enCours;

  /// Un seul passage à la fois : un appel pendant un passage en cours attend et renvoie le même bilan.
  Future<BilanSynchro> synchroniser() => _enCours ??= _passage().whenComplete(() => _enCours = null);

  Future<BilanSynchro> _passage() async {
    final agent = await _utilisateurId();
    if (agent == null) return const BilanSynchro();
    var envoyes = 0;
    var rejetes = 0;
    String? blocage;
    final liste = await _file.aEnvoyer(agent);
    for (final e in liste) {
      try {
        await _api.getPaiementControllerApi().enregistrer(
              idempotencyKey: e.id,
              paiementRequete: PaiementRequete(
                bailId: e.bailId,
                montant: e.montant,
                mode: PaiementRequeteModeEnum.ESPECES,
                // minuit UTC du jour de la saisie : le serveur lit la date, sans décalage de fuseau
                datePaiement: _dateUtc(e.datePaiement),
              ),
            );
        await _file.marquerEnvoye(e.id);
        envoyes++;
      } on DioException catch (ex) {
        final erreur = AppError.fromDio(ex);
        final decision = classer(erreur);
        if (decision == Decision.rejeter) {
          await _file.marquerRejete(e.id, erreur.code);
          rejetes++;
          continue;
        }
        await _file.noterEchecTemporaire(e.id, erreur.code);
        blocage = erreur.code; // réseau, serveur, licence ou session : inutile d'insister sur les suivants
        break;
      }
    }
    final restants = (await _file.aEnvoyer(agent)).length;
    return BilanSynchro(envoyes: envoyes, rejetes: rejetes, restants: restants, blocage: blocage);
  }

  static DateTime _dateUtc(String jour) {
    final p = jour.split('-').map(int.parse).toList();
    return DateTime.utc(p[0], p[1], p[2]);
  }
}

enum Decision { reessayer, rejeter }

/// Faut-il réessayer plus tard (problème temporaire ou hors de portée de l'agent) ou rejeter définitivement (le serveur a dit non) ?
Decision classer(AppError e) {
  final status = e.status;
  if (status == null || status >= 500 || status == 408 || status == 429) return Decision.reessayer; // réseau, serveur en panne
  if (status == 401) return Decision.reessayer; // session à renouveler : la saisie reste valable
  if (e.code == 'LICENCE_EXPIREE') return Decision.reessayer; // se règle côté éditeur/administrateur, pas par une nouvelle saisie
  return Decision.rejeter; // 400, 403, 404, 409, 422… : le serveur refuse cette saisie telle quelle
}

@Riverpod(keepAlive: true)
SynchroEncaissements synchroEncaissements(Ref ref) => SynchroEncaissements(
      api: ref.watch(apiProvider),
      file: ref.watch(fileEncaissementsProvider),
      utilisateurId: () => utilisateurCourant(ref),
    );

/// Identifiant du compte connecté, lu dans le jeton mémorisé (disponible hors ligne).
Future<String?> utilisateurCourant(Ref ref) async {
  final tokens = await ref.read(tokenStoreProvider).read();
  return tokens == null ? null : sujetDuJwt(tokens.access);
}

@riverpod
Future<String?> agentId(Ref ref) {
  ref.watch(sessionProvider); // se recalcule à l'ouverture/fermeture de session
  return utilisateurCourant(ref);
}
