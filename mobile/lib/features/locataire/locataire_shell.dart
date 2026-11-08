import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import '../auth/auth_actions.dart';
import 'accueil_screen.dart';
import 'baux_screen.dart';
import 'documents_screen.dart';
import 'signaler_screen.dart';

/// Espace du locataire : accueil (solde), baux et échéances, quittances, signalement de panne.
class LocataireShell extends ConsumerStatefulWidget {
  const LocataireShell({super.key});

  @override
  ConsumerState<LocataireShell> createState() => _LocataireShellState();
}

class _LocataireShellState extends ConsumerState<LocataireShell> {
  int _onglet = 0;

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    const pages = [AccueilScreen(), BauxScreen(), DocumentsScreen(), SignalerScreen()];
    return Scaffold(
      appBar: AppBar(
        title: TitreMarque(l.espaceLocataire, cleTexte: const Key('titre-LOCATAIRE')),
        actions: [
          IconButton(
            key: const Key('bouton-profil'),
            tooltip: l.profilBouton,
            icon: const Icon(Icons.account_circle_outlined),
            onPressed: () => context.push('/profil'),
          ),
          IconButton(
            key: const Key('bouton-deconnexion'),
            tooltip: l.deconnexion,
            icon: const Icon(Icons.logout),
            onPressed: () => ref.read(authActionsProvider).logout(),
          ),
        ],
      ),
      body: IndexedStack(index: _onglet, children: pages),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _onglet,
        onDestinationSelected: (i) {
          HapticFeedback.selectionClick();
          setState(() => _onglet = i);
        },
        destinations: [
          NavigationDestination(key: const Key('onglet-accueil'), icon: const Icon(Icons.home_outlined), selectedIcon: const Icon(Icons.home), label: l.ongletAccueil),
          NavigationDestination(key: const Key('onglet-baux'), icon: const Icon(Icons.description_outlined), selectedIcon: const Icon(Icons.description), label: l.ongletBaux),
          NavigationDestination(key: const Key('onglet-documents'), icon: const Icon(Icons.receipt_long_outlined), selectedIcon: const Icon(Icons.receipt_long), label: l.ongletDocuments),
          NavigationDestination(key: const Key('onglet-signaler'), icon: const Icon(Icons.build_outlined), selectedIcon: const Icon(Icons.build), label: l.ongletSignaler),
        ],
      ),
    );
  }
}
