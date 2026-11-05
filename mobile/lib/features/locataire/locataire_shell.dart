import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

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
        title: Text(l.espaceLocataire, key: const Key('titre-LOCATAIRE')),
        actions: [
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
        onDestinationSelected: (i) => setState(() => _onglet = i),
        destinations: [
          NavigationDestination(key: const Key('onglet-accueil'), icon: const Icon(Icons.home_outlined), label: l.ongletAccueil),
          NavigationDestination(key: const Key('onglet-baux'), icon: const Icon(Icons.description_outlined), label: l.ongletBaux),
          NavigationDestination(key: const Key('onglet-documents'), icon: const Icon(Icons.receipt_long_outlined), label: l.ongletDocuments),
          NavigationDestination(key: const Key('onglet-signaler'), icon: const Icon(Icons.build_outlined), label: l.ongletSignaler),
        ],
      ),
    );
  }
}
