import 'package:immogest_api/immogest_api.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/error/app_error.dart';
import '../../core/providers.dart';

part 'gestion_repository.g.dart';

/// Appels de l'espace gestion (ADMIN, GESTIONNAIRE, GESTIONNAIRE_BIENS) : synthèse, arriérés, signalements.
/// Le serveur limite déjà ce que voit chaque rôle (périmètre, droits) ; on ne les appelle qu'ici.
class GestionRepository {
  GestionRepository(this._api);

  final ImmogestApi _api;

  /// Indicateurs du tableau de bord (ADMIN et GESTIONNAIRE seulement).
  Future<Synthese> synthese() => guard(() async {
        final d = (await _api.getDashboardControllerApi().syntheseTableauDeBord()).data;
        if (d == null) throw const AppError(AppError.inconnue);
        return d;
      });

  /// Échéances en retard, les plus anciennes d'abord, filtrées par nom de locataire.
  Future<List<ArriereDto>> arrieres({String? q}) => guard(() async {
        final page = (await _api.getRelanceControllerApi().listerArrieres(q: (q ?? '').trim().isEmpty ? null : q!.trim(), size: 50, sort: 'dateEcheance')).data;
        return page?.content?.toList() ?? const <ArriereDto>[];
      });

  /// Relance manuelle d'une échéance (un message par le canal disponible).
  Future<void> relancer(String echeanceId) => guard(() async {
        await _api.getRelanceControllerApi().relancerEcheance(echeanceRequete: EcheanceRequete(echeanceId: echeanceId));
      });

  /// Signalements des locataires, les plus récents d'abord ; [statut] filtre (NOUVEAU, EN_COURS…).
  Future<List<SignalementDto>> signalements({String? statut}) => guard(() async {
        final page = (await _api.getSignalementControllerApi().listerSignalements(statut: statut, size: 50, sort: 'createdAt,desc')).data;
        return page?.content?.toList() ?? const <SignalementDto>[];
      });

  Future<void> changerStatut(String id, StatutSignalementRequeteStatutEnum statut, {String? note}) => guard(() async {
        await _api.getSignalementControllerApi().changerStatutSignalement(
              id: id,
              statutSignalementRequete: StatutSignalementRequete(statut: statut, note: (note ?? '').trim().isEmpty ? null : note!.trim()),
            );
      });
}

@riverpod
GestionRepository gestionRepository(Ref ref) => GestionRepository(ref.watch(apiProvider));

@riverpod
Future<Synthese> syntheseGestion(Ref ref) => ref.watch(gestionRepositoryProvider).synthese();

@riverpod
Future<List<ArriereDto>> arrieresGestion(Ref ref, String q) => ref.watch(gestionRepositoryProvider).arrieres(q: q);

@riverpod
Future<List<SignalementDto>> signalementsGestion(Ref ref, String? statut) => ref.watch(gestionRepositoryProvider).signalements(statut: statut);
