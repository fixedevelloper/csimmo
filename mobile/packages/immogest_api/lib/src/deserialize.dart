import 'package:immogest_api/src/model/accuse.dart';
import 'package:immogest_api/src/model/administrateur.dart';
import 'package:immogest_api/src/model/affectations_reponse.dart';
import 'package:immogest_api/src/model/affectations_requete.dart';
import 'package:immogest_api/src/model/annulation_requete.dart';
import 'package:immogest_api/src/model/appareil_requete.dart';
import 'package:immogest_api/src/model/arriere_dto.dart';
import 'package:immogest_api/src/model/arrieres.dart';
import 'package:immogest_api/src/model/bail_a_echeance.dart';
import 'package:immogest_api/src/model/bail_compte.dart';
import 'package:immogest_api/src/model/bail_dto.dart';
import 'package:immogest_api/src/model/bail_requete.dart';
import 'package:immogest_api/src/model/bilan_generation.dart';
import 'package:immogest_api/src/model/bilan_relances.dart';
import 'package:immogest_api/src/model/caisse_ligne.dart';
import 'package:immogest_api/src/model/calcul_requete.dart';
import 'package:immogest_api/src/model/canal_parametres_dto.dart';
import 'package:immogest_api/src/model/canal_requete.dart';
import 'package:immogest_api/src/model/champ_dto.dart';
import 'package:immogest_api/src/model/changement_mot_de_passe.dart';
import 'package:immogest_api/src/model/code_requete.dart';
import 'package:immogest_api/src/model/compte_locataire_dto.dart';
import 'package:immogest_api/src/model/compteur_dto.dart';
import 'package:immogest_api/src/model/compteur_requete.dart';
import 'package:immogest_api/src/model/config_mobile_dto.dart';
import 'package:immogest_api/src/model/connexion_requete.dart';
import 'package:immogest_api/src/model/consentement_dto.dart';
import 'package:immogest_api/src/model/consentement_saisie.dart';
import 'package:immogest_api/src/model/contre_passation_requete.dart';
import 'package:immogest_api/src/model/creation_requete.dart';
import 'package:immogest_api/src/model/demande_anonymisation_dto.dart';
import 'package:immogest_api/src/model/demande_saisie.dart';
import 'package:immogest_api/src/model/depense_dto.dart';
import 'package:immogest_api/src/model/depense_requete.dart';
import 'package:immogest_api/src/model/desactivation_requete.dart';
import 'package:immogest_api/src/model/deux_facteurs_defi.dart';
import 'package:immogest_api/src/model/deux_facteurs_enrolement.dart';
import 'package:immogest_api/src/model/deux_facteurs_verification.dart';
import 'package:immogest_api/src/model/diagnostic_dto.dart';
import 'package:immogest_api/src/model/document_bail_dto.dart';
import 'package:immogest_api/src/model/document_dto.dart';
import 'package:immogest_api/src/model/echeance_dto.dart';
import 'package:immogest_api/src/model/echeance_requete.dart';
import 'package:immogest_api/src/model/entree_audit.dart';
import 'package:immogest_api/src/model/erreur_import_dto.dart';
import 'package:immogest_api/src/model/etat_installation_dto.dart';
import 'package:immogest_api/src/model/etat_licence_dto.dart';
import 'package:immogest_api/src/model/etat_sauvegardes_dto.dart';
import 'package:immogest_api/src/model/finalite_situation_dto.dart';
import 'package:immogest_api/src/model/fournisseur_dto.dart';
import 'package:immogest_api/src/model/fournisseur_requete.dart';
import 'package:immogest_api/src/model/garant_dto.dart';
import 'package:immogest_api/src/model/garant_requete.dart';
import 'package:immogest_api/src/model/garant_resume.dart';
import 'package:immogest_api/src/model/immeuble_dto.dart';
import 'package:immogest_api/src/model/immeuble_requete.dart';
import 'package:immogest_api/src/model/import_requete.dart';
import 'package:immogest_api/src/model/initiation_requete.dart';
import 'package:immogest_api/src/model/installation_requete.dart';
import 'package:immogest_api/src/model/ligne_arriere.dart';
import 'package:immogest_api/src/model/locataire_dto.dart';
import 'package:immogest_api/src/model/locataire_requete.dart';
import 'package:immogest_api/src/model/lot_dto.dart';
import 'package:immogest_api/src/model/lot_requete.dart';
import 'package:immogest_api/src/model/mandat_dto.dart';
import 'package:immogest_api/src/model/mandat_requete.dart';
import 'package:immogest_api/src/model/message_dto.dart';
import 'package:immogest_api/src/model/mise_en_demeure_dto.dart';
import 'package:immogest_api/src/model/modele_bail_dto.dart';
import 'package:immogest_api/src/model/modele_consentement_dto.dart';
import 'package:immogest_api/src/model/modele_requete.dart';
import 'package:immogest_api/src/model/modification_requete.dart';
import 'package:immogest_api/src/model/mois.dart';
import 'package:immogest_api/src/model/mot_de_passe_requete.dart';
import 'package:immogest_api/src/model/obstacle_dto.dart';
import 'package:immogest_api/src/model/occupation.dart';
import 'package:immogest_api/src/model/operation_sauvegarde_dto.dart';
import 'package:immogest_api/src/model/otp_demande.dart';
import 'package:immogest_api/src/model/otp_verification.dart';
import 'package:immogest_api/src/model/page_reponse_arriere_dto.dart';
import 'package:immogest_api/src/model/page_reponse_bail_dto.dart';
import 'package:immogest_api/src/model/page_reponse_depense_dto.dart';
import 'package:immogest_api/src/model/page_reponse_document_dto.dart';
import 'package:immogest_api/src/model/page_reponse_echeance_dto.dart';
import 'package:immogest_api/src/model/page_reponse_entree_audit.dart';
import 'package:immogest_api/src/model/page_reponse_immeuble_dto.dart';
import 'package:immogest_api/src/model/page_reponse_locataire_dto.dart';
import 'package:immogest_api/src/model/page_reponse_lot_dto.dart';
import 'package:immogest_api/src/model/page_reponse_message_dto.dart';
import 'package:immogest_api/src/model/page_reponse_mise_en_demeure_dto.dart';
import 'package:immogest_api/src/model/page_reponse_paiement_dto.dart';
import 'package:immogest_api/src/model/page_reponse_prestataire_dto.dart';
import 'package:immogest_api/src/model/page_reponse_proprietaire_dto.dart';
import 'package:immogest_api/src/model/page_reponse_relance_dto.dart';
import 'package:immogest_api/src/model/page_reponse_reversement_dto.dart';
import 'package:immogest_api/src/model/page_reponse_signalement_dto.dart';
import 'package:immogest_api/src/model/page_reponse_transaction_dto.dart';
import 'package:immogest_api/src/model/page_reponse_utilisateur_dto.dart';
import 'package:immogest_api/src/model/paiement_dto.dart';
import 'package:immogest_api/src/model/paiement_requete.dart';
import 'package:immogest_api/src/model/paiement_reversement_requete.dart';
import 'package:immogest_api/src/model/parametres_relance_dto.dart';
import 'package:immogest_api/src/model/parametres_requete.dart';
import 'package:immogest_api/src/model/photo_dto.dart';
import 'package:immogest_api/src/model/piece_dto.dart';
import 'package:immogest_api/src/model/prestataire_dto.dart';
import 'package:immogest_api/src/model/prestataire_requete.dart';
import 'package:immogest_api/src/model/previsualisation_dto.dart';
import 'package:immogest_api/src/model/profil_requete.dart';
import 'package:immogest_api/src/model/proprietaire_dto.dart';
import 'package:immogest_api/src/model/proprietaire_requete.dart';
import 'package:immogest_api/src/model/push_mobile.dart';
import 'package:immogest_api/src/model/refresh_requete.dart';
import 'package:immogest_api/src/model/refus_requete.dart';
import 'package:immogest_api/src/model/registre_dto.dart';
import 'package:immogest_api/src/model/reinitialisation_requete.dart';
import 'package:immogest_api/src/model/relance_dto.dart';
import 'package:immogest_api/src/model/renouvellement_requete.dart';
import 'package:immogest_api/src/model/reponse_connexion.dart';
import 'package:immogest_api/src/model/resiliation_requete.dart';
import 'package:immogest_api/src/model/restauration_requete.dart';
import 'package:immogest_api/src/model/resultat_anonymisation_dto.dart';
import 'package:immogest_api/src/model/resultat_import_dto.dart';
import 'package:immogest_api/src/model/resultat_recherche.dart';
import 'package:immogest_api/src/model/resultat_test.dart';
import 'package:immogest_api/src/model/resume_feuille_dto.dart';
import 'package:immogest_api/src/model/reversement_dto.dart';
import 'package:immogest_api/src/model/sauvegarde_dto.dart';
import 'package:immogest_api/src/model/signalement_dto.dart';
import 'package:immogest_api/src/model/situation_consentement_dto.dart';
import 'package:immogest_api/src/model/statut_requete.dart';
import 'package:immogest_api/src/model/statut_signalement_requete.dart';
import 'package:immogest_api/src/model/synthese.dart';
import 'package:immogest_api/src/model/test_requete.dart';
import 'package:immogest_api/src/model/traitement_dto.dart';
import 'package:immogest_api/src/model/transaction_dto.dart';
import 'package:immogest_api/src/model/utilisateur_dto.dart';

final _regList = RegExp(r'^List<(.*)>$');
final _regSet = RegExp(r'^Set<(.*)>$');
final _regMap = RegExp(r'^Map<String,(.*)>$');

  ReturnType deserialize<ReturnType, BaseType>(dynamic value, String targetType, {bool growable= true}) {
      switch (targetType) {
        case 'String':
          return '$value' as ReturnType;
        case 'int':
          return (value is int ? value : int.parse('$value')) as ReturnType;
        case 'bool':
          if (value is bool) {
            return value as ReturnType;
          }
          final valueString = '$value'.toLowerCase();
          return (valueString == 'true' || valueString == '1') as ReturnType;
        case 'double':
          return (value is double ? value : double.parse('$value')) as ReturnType;
        case 'Accuse':
          return Accuse.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'Administrateur':
          return Administrateur.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'AffectationsReponse':
          return AffectationsReponse.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'AffectationsRequete':
          return AffectationsRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'AnnulationRequete':
          return AnnulationRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'AppareilRequete':
          return AppareilRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ArriereDto':
          return ArriereDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'Arrieres':
          return Arrieres.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'BailAEcheance':
          return BailAEcheance.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'BailCompte':
          return BailCompte.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'BailDto':
          return BailDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'BailRequete':
          return BailRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'BilanGeneration':
          return BilanGeneration.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'BilanRelances':
          return BilanRelances.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'CaisseLigne':
          return CaisseLigne.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'CalculRequete':
          return CalculRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'CanalParametresDto':
          return CanalParametresDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'CanalRequete':
          return CanalRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ChampDto':
          return ChampDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ChangementMotDePasse':
          return ChangementMotDePasse.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'CodeRequete':
          return CodeRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'CompteLocataireDto':
          return CompteLocataireDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'CompteurDto':
          return CompteurDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'CompteurRequete':
          return CompteurRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ConfigMobileDto':
          return ConfigMobileDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ConnexionRequete':
          return ConnexionRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ConsentementDto':
          return ConsentementDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ConsentementSaisie':
          return ConsentementSaisie.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ContrePassationRequete':
          return ContrePassationRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'CreationRequete':
          return CreationRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'DemandeAnonymisationDto':
          return DemandeAnonymisationDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'DemandeSaisie':
          return DemandeSaisie.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'DepenseDto':
          return DepenseDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'DepenseRequete':
          return DepenseRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'DesactivationRequete':
          return DesactivationRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'DeuxFacteursDefi':
          return DeuxFacteursDefi.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'DeuxFacteursEnrolement':
          return DeuxFacteursEnrolement.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'DeuxFacteursVerification':
          return DeuxFacteursVerification.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'DiagnosticDto':
          return DiagnosticDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'DocumentBailDto':
          return DocumentBailDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'DocumentDto':
          return DocumentDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'EcheanceDto':
          return EcheanceDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'EcheanceRequete':
          return EcheanceRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'EntreeAudit':
          return EntreeAudit.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ErreurImportDto':
          return ErreurImportDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'EtatInstallationDto':
          return EtatInstallationDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'EtatLicenceDto':
          return EtatLicenceDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'EtatSauvegardesDto':
          return EtatSauvegardesDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'FinaliteSituationDto':
          return FinaliteSituationDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'FournisseurDto':
          return FournisseurDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'FournisseurRequete':
          return FournisseurRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'GarantDto':
          return GarantDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'GarantRequete':
          return GarantRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'GarantResume':
          return GarantResume.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ImmeubleDto':
          return ImmeubleDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ImmeubleRequete':
          return ImmeubleRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ImportRequete':
          return ImportRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'InitiationRequete':
          return InitiationRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'InstallationRequete':
          return InstallationRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'LigneArriere':
          return LigneArriere.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'LocataireDto':
          return LocataireDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'LocataireRequete':
          return LocataireRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'LotDto':
          return LotDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'LotRequete':
          return LotRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'MandatDto':
          return MandatDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'MandatRequete':
          return MandatRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'MessageDto':
          return MessageDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'MiseEnDemeureDto':
          return MiseEnDemeureDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ModeleBailDto':
          return ModeleBailDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ModeleConsentementDto':
          return ModeleConsentementDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ModeleRequete':
          return ModeleRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ModificationRequete':
          return ModificationRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'Mois':
          return Mois.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'MotDePasseRequete':
          return MotDePasseRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ObstacleDto':
          return ObstacleDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'Occupation':
          return Occupation.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'OperationSauvegardeDto':
          return OperationSauvegardeDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'OtpDemande':
          return OtpDemande.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'OtpVerification':
          return OtpVerification.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseArriereDto':
          return PageReponseArriereDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseBailDto':
          return PageReponseBailDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseDepenseDto':
          return PageReponseDepenseDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseDocumentDto':
          return PageReponseDocumentDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseEcheanceDto':
          return PageReponseEcheanceDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseEntreeAudit':
          return PageReponseEntreeAudit.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseImmeubleDto':
          return PageReponseImmeubleDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseLocataireDto':
          return PageReponseLocataireDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseLotDto':
          return PageReponseLotDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseMessageDto':
          return PageReponseMessageDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseMiseEnDemeureDto':
          return PageReponseMiseEnDemeureDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponsePaiementDto':
          return PageReponsePaiementDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponsePrestataireDto':
          return PageReponsePrestataireDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseProprietaireDto':
          return PageReponseProprietaireDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseRelanceDto':
          return PageReponseRelanceDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseReversementDto':
          return PageReponseReversementDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseSignalementDto':
          return PageReponseSignalementDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseTransactionDto':
          return PageReponseTransactionDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PageReponseUtilisateurDto':
          return PageReponseUtilisateurDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PaiementDto':
          return PaiementDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PaiementRequete':
          return PaiementRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PaiementReversementRequete':
          return PaiementReversementRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ParametresRelanceDto':
          return ParametresRelanceDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ParametresRequete':
          return ParametresRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PhotoDto':
          return PhotoDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PieceDto':
          return PieceDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PrestataireDto':
          return PrestataireDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PrestataireRequete':
          return PrestataireRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PrevisualisationDto':
          return PrevisualisationDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ProfilRequete':
          return ProfilRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ProprietaireDto':
          return ProprietaireDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ProprietaireRequete':
          return ProprietaireRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'PushMobile':
          return PushMobile.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'RefreshRequete':
          return RefreshRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'RefusRequete':
          return RefusRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'RegistreDto':
          return RegistreDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ReinitialisationRequete':
          return ReinitialisationRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'RelanceDto':
          return RelanceDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'RenouvellementRequete':
          return RenouvellementRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ReponseConnexion':
          return ReponseConnexion.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ResiliationRequete':
          return ResiliationRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'RestaurationRequete':
          return RestaurationRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ResultatAnonymisationDto':
          return ResultatAnonymisationDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ResultatImportDto':
          return ResultatImportDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ResultatRecherche':
          return ResultatRecherche.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ResultatTest':
          return ResultatTest.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ResumeFeuilleDto':
          return ResumeFeuilleDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'ReversementDto':
          return ReversementDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'SauvegardeDto':
          return SauvegardeDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'SignalementDto':
          return SignalementDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'SituationConsentementDto':
          return SituationConsentementDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'StatutRequete':
          return StatutRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'StatutSignalementRequete':
          return StatutSignalementRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'Synthese':
          return Synthese.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'TestRequete':
          return TestRequete.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'TraitementDto':
          return TraitementDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'TransactionDto':
          return TransactionDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        case 'UtilisateurDto':
          return UtilisateurDto.fromJson(value as Map<String, dynamic>) as ReturnType;
        default:
          RegExpMatch? match;

          if (value is List && (match = _regList.firstMatch(targetType)) != null) {
            targetType = match![1]!; // ignore: parameter_assignments
            return value
              .map<BaseType>((dynamic v) => deserialize<BaseType, BaseType>(v, targetType, growable: growable))
              .toList(growable: growable) as ReturnType;
          }
          if (value is Set && (match = _regSet.firstMatch(targetType)) != null) {
            targetType = match![1]!; // ignore: parameter_assignments
            return value
              .map<BaseType>((dynamic v) => deserialize<BaseType, BaseType>(v, targetType, growable: growable))
              .toSet() as ReturnType;
          }
          if (value is Map && (match = _regMap.firstMatch(targetType)) != null) {
            targetType = match![1]!.trim(); // ignore: parameter_assignments
            return Map<String, BaseType>.fromIterables(
              value.keys as Iterable<String>,
              value.values.map((dynamic v) => deserialize<BaseType, BaseType>(v, targetType, growable: growable)),
            ) as ReturnType;
          }
          break;
    }
    throw Exception('Cannot deserialize');
  }