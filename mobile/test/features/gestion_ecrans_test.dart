import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_api/immogest_api.dart';
import 'package:immogest_mobile/features/gestion/accueil_gestion_screen.dart';
import 'package:immogest_mobile/features/gestion/arrieres_screen.dart';
import 'package:immogest_mobile/features/gestion/gestion_repository.dart';
import 'package:immogest_mobile/features/gestion/signalements_gestion_screen.dart';
import 'package:immogest_mobile/l10n/app_localizations.dart';

class _FauxDepot implements GestionRepository {
  final relances = <String>[];
  final statuts = <(String, StatutSignalementRequeteStatutEnum, String?)>[];

  @override
  Future<Synthese> synthese() async => Synthese(
        occupation: Occupation(libres: 2, occupes: 6, enTravaux: 1, tauxBp: 7500),
        attenduMois: 1200000,
        encaisseMois: 900000,
        tauxRecouvrementMoisBp: 7500,
        arrieresTotal: 300000,
        echeancesEnRetard: 3,
      );

  @override
  Future<List<ArriereDto>> arrieres({String? q}) async => [
        ArriereDto(echeanceId: 'e-1', locataireNom: 'Paul Mbarga', logement: 'Palmiers — A1', reste: 160000, joursRetard: 12),
      ];

  @override
  Future<void> relancer(String echeanceId) async => relances.add(echeanceId);

  @override
  Future<List<SignalementDto>> signalements({String? statut}) async => [
        SignalementDto(id: 's-1', description: 'Fuite dans la cuisine', logement: 'Palmiers — A1', locataireNom: 'Paul Mbarga', statut: SignalementDtoStatutEnum.NOUVEAU),
      ];

  @override
  Future<void> changerStatut(String id, StatutSignalementRequeteStatutEnum statut, {String? note}) async => statuts.add((id, statut, note));
}

void main() {
  late _FauxDepot depot;

  Widget app(Widget ecran) => ProviderScope(
        overrides: [gestionRepositoryProvider.overrideWithValue(depot)],
        child: MaterialApp(
          locale: const Locale('fr'),
          localizationsDelegates: const [AppLocalizations.delegate, GlobalMaterialLocalizations.delegate, GlobalWidgetsLocalizations.delegate, GlobalCupertinoLocalizations.delegate],
          supportedLocales: AppLocalizations.supportedLocales,
          home: Scaffold(body: ecran),
        ),
      );

  setUp(() => depot = _FauxDepot());

  testWidgets("l'accueil affiche occupation, encaissé et arriérés", (tester) async {
    await tester.pumpWidget(app(const AccueilGestionScreen(avecStats: true)));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('taux-occupation')), findsOneWidget);
    expect(find.text('75 %'), findsOneWidget);
    expect(find.byKey(const Key('acces-arrieres')), findsOneWidget);
    expect(find.textContaining('3 échéances en retard'), findsOneWidget);
  });

  testWidgets("sans droit aux indicateurs, l'accueil ne montre que les accès", (tester) async {
    await tester.pumpWidget(app(const AccueilGestionScreen(avecStats: false)));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('taux-occupation')), findsNothing);
    expect(find.byKey(const Key('acces-signalements')), findsOneWidget);
  });

  testWidgets('relancer un arriéré appelle le serveur', (tester) async {
    await tester.pumpWidget(app(const ArrieresScreen()));
    await tester.pumpAndSettle();
    expect(find.text('Paul Mbarga'), findsOneWidget);
    await tester.tap(find.byKey(const Key('relancer-e-1')));
    await tester.pumpAndSettle();
    expect(depot.relances, ['e-1']);
    expect(find.textContaining('Relance envoyée à Paul Mbarga'), findsOneWidget);
  });

  testWidgets('prendre un signalement en charge avec une note', (tester) async {
    await tester.pumpWidget(app(const SignalementsGestionScreen()));
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const Key('signalement-gestion-s-1')));
    await tester.pumpAndSettle();
    await tester.enterText(find.byKey(const Key('note-signalement')), 'Plombier jeudi');
    await tester.tap(find.byKey(const Key('signalement-prendre')));
    await tester.pumpAndSettle();
    expect(depot.statuts, [('s-1', StatutSignalementRequeteStatutEnum.EN_COURS, 'Plombier jeudi')]);
  });
}
