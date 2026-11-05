import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'core/theme/app_theme.dart';
import 'features/agent/synchro_automatique.dart';
import 'features/push/push_registrar.dart';
import 'l10n/app_localizations.dart';
import 'router.dart';

class ImmoGestApp extends ConsumerStatefulWidget {
  const ImmoGestApp({super.key});

  @override
  ConsumerState<ImmoGestApp> createState() => _ImmoGestAppState();
}

class _ImmoGestAppState extends ConsumerState<ImmoGestApp> {
  final _messagerie = GlobalKey<ScaffoldMessengerState>();
  StreamSubscription<dynamic>? _push;

  @override
  void initState() {
    super.initState();
    ref.read(synchroAutomatiqueProvider); // envoi de la file d'encaissements de l'agent (minuteur, retour au premier plan)
    // Crée l'enregistreur de notifications push (il suit la session) et affiche les messages reçus au premier plan.
    _push = ref.read(pushRegistrarProvider).messages.listen((m) {
      final texte = [m.titre, m.corps].whereType<String>().where((s) => s.isNotEmpty).join(' — ');
      if (texte.isNotEmpty) _messagerie.currentState?.showSnackBar(SnackBar(content: Text(texte)));
    });
  }

  @override
  void dispose() {
    unawaited(_push?.cancel());
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      scaffoldMessengerKey: _messagerie,
      routerConfig: ref.watch(routerProvider),
      localizationsDelegates: const [
        AppLocalizations.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      supportedLocales: AppLocalizations.supportedLocales,
      onGenerateTitle: (context) => AppLocalizations.of(context).appTitle,
      theme: construireTheme(Brightness.light),
      darkTheme: construireTheme(Brightness.dark),
      themeMode: ThemeMode.system,
    );
  }
}
