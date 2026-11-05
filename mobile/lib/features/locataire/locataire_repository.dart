import 'package:dio/dio.dart';
import 'package:immogest_api/immogest_api.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/error/app_error.dart';
import '../../core/platform/plateforme.dart';
import '../../core/providers.dart';

part 'locataire_repository.g.dart';

/// Appels de l'espace locataire. Les numéros des méthodes générées (lister9, creer1…) changent quand l'API s'enrichit :
/// on ne les appelle qu'ici.
class LocataireRepository {
  LocataireRepository(this._api, this._fichiers);

  final ImmogestApi _api;
  final FichiersLocaux _fichiers;

  /// Arriérés, montant à venir, crédit et solde du locataire connecté.
  Future<CompteLocataireDto> monCompte() => guard(() async {
        final d = (await _api.getPaiementControllerApi().monCompte()).data;
        if (d == null) throw const AppError(AppError.inconnue);
        return d;
      });

  /// Baux du locataire (le serveur ne renvoie que les siens), du plus récent au plus ancien.
  Future<List<BailDto>> baux() => guard(() async {
        final page = (await _api.getBailControllerApi().listerBaux(size: 50, sort: 'dateDebut,desc')).data;
        return (page?.content ?? const <BailDto>[]).where((b) => b.statut != BailDtoStatutEnum.BROUILLON).toList();
      });

  Future<List<EcheanceDto>> echeances(String bailId) => guard(() async {
        final page = (await _api.getEcheanceControllerApi().listerEcheances(bailId: bailId, size: 100, sort: 'periodeDebut')).data;
        return (page?.content ?? const <EcheanceDto>[]).where((e) => e.statut != EcheanceDtoStatutEnum.ANNULEE).toList();
      });

  /// Quittances et reçus non annulés (le serveur n'envoie pas les documents annulés au locataire).
  Future<List<DocumentDto>> documents() => guard(() async {
        final page = (await _api.getDocumentControllerApi().listerDocumentsPaiement(size: 50, sort: 'createdAt,desc')).data;
        return page?.content ?? const <DocumentDto>[];
      });

  Future<List<SignalementDto>> signalements() => guard(() async {
        final page = (await _api.getSignalementControllerApi().listerSignalements(size: 30, sort: 'createdAt,desc')).data;
        return page?.content ?? const <SignalementDto>[];
      });

  /// Signale une panne ; [photo] est facultative (chemin local). [bailId] seulement si le locataire a plusieurs baux.
  Future<SignalementDto> signalerPanne({required String description, String? bailId, PhotoChoisie? photo}) => guard(() async {
        final d = (await _api.getSignalementControllerApi().creerSignalement(
          description: description.trim(),
          bailId: bailId,
          photo: photo == null ? null : await MultipartFile.fromFile(photo.chemin, filename: photo.nom),
        ))
            .data;
        if (d == null) throw const AppError(AppError.inconnue);
        return d;
      });

  /// Lance un paiement Mobile Money : le locataire le valide ensuite sur son téléphone. [cle] (Idempotency-Key) est la même pour toutes les
  /// tentatives d'une même demande : un double appui ou un renvoi après coupure ne crée jamais deux transactions.
  Future<TransactionDto> initierPaiement({
    required String bailId,
    required int montant,
    required InitiationRequeteOperateurEnum operateur,
    required String telephone,
    required String cle,
  }) =>
      guard(() async {
        final d = (await _api.getPaiementEnLigneControllerApi().initierPaiementEnLigne(
          idempotencyKey: cle,
          initiationRequete: InitiationRequete(bailId: bailId, montant: montant, operateur: operateur, telephone: telephone),
        ))
            .data;
        if (d == null) throw const AppError(AppError.inconnue);
        return d;
      });

  /// État d'une transaction (le serveur ne confirme qu'après l'opérateur : jamais sur la déclaration du téléphone).
  Future<TransactionDto> statutPaiement(String id) => guard(() async {
        final d = (await _api.getPaiementEnLigneControllerApi().obtenirPaiementEnLigne(id: id)).data;
        if (d == null) throw const AppError(AppError.inconnue);
        return d;
      });

  /// Télécharge un PDF (accès contrôlé par le serveur) et le range dans le dossier temporaire ; renvoie le chemin local.
  Future<String> telechargerDocument(String fichierId, String nom) => guard(() async {
        final octets = (await _api.getFichierControllerApi().telecharger(id: fichierId)).data;
        if (octets == null || octets.isEmpty) throw const AppError(AppError.inconnue);
        return _fichiers.ecrire(nom, octets);
      });

  Future<void> ouvrir(String chemin) => _fichiers.ouvrir(chemin);
}

@Riverpod(keepAlive: true)
LocataireRepository locataireRepository(Ref ref) => LocataireRepository(ref.watch(apiProvider), ref.watch(fichiersLocauxProvider));

@riverpod
Future<CompteLocataireDto> monCompte(Ref ref) => ref.watch(locataireRepositoryProvider).monCompte();

@riverpod
Future<List<BailDto>> mesBaux(Ref ref) => ref.watch(locataireRepositoryProvider).baux();

@riverpod
Future<List<EcheanceDto>> mesEcheances(Ref ref, String bailId) => ref.watch(locataireRepositoryProvider).echeances(bailId);

@riverpod
Future<List<DocumentDto>> mesDocuments(Ref ref) => ref.watch(locataireRepositoryProvider).documents();

@riverpod
Future<List<SignalementDto>> mesSignalements(Ref ref) => ref.watch(locataireRepositoryProvider).signalements();
