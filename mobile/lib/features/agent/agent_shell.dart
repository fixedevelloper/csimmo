import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../l10n/app_localizations.dart';
import '../auth/auth_actions.dart';
import 'a_envoyer_screen.dart';
import 'encaisser_screen.dart';
import 'file_encaissements.dart';
import 'listes_screens.dart';
import 'synchro_encaissements.dart';

/// Espace de l'agent de terrain (ADMIN et GESTIONNAIRE) : encaisser en espèces même sans réseau, suivre la file d'envoi, consulter lots et locataires.
class AgentShell extends ConsumerStatefulWidget {
  const AgentShell({super.key});

  @override
  ConsumerState<AgentShell> createState() => _AgentShellState();
}

class _AgentShellState extends ConsumerState<AgentShell> {
  int _onglet = 0;

  Future<void> _deconnecter() async {
    final l = AppLocalizations.of(context);
    final agent = await ref.read(agentIdProvider.future);
    final enAttente = agent == null ? 0 : (await ref.read(fileEncaissementsProvider).aEnvoyer(agent)).length;
    if (!mounted) return;
    if (enAttente > 0) {
      final ok = await showDialog<bool>(
        context: context,
        builder: (c) => AlertDialog(
          key: const Key('dialogue-deconnexion'),
          title: Text(l.deconnexion),
          content: Text(l.deconnexionAvecFile(enAttente)),
          actions: [
            TextButton(onPressed: () => Navigator.pop(c, false), child: Text(l.annuler)),
            FilledButton(key: const Key('confirmer-deconnexion'), onPressed: () => Navigator.pop(c, true), child: Text(l.deconnexion)),
          ],
        ),
      );
      if (ok != true) return;
    }
    await ref.read(authActionsProvider).logout();
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final agent = ref.watch(agentIdProvider).value;
    final enAttente = agent == null ? const AsyncData<int>(0) : ref.watch(nombreEnAttenteProvider(agent));
    const pages = [EncaisserScreen(), AEnvoyerScreen(), LotsScreen(), LocatairesScreen()];
    return Scaffold(
      appBar: AppBar(
        title: Text(l.espaceAgent, key: const Key('titre-GESTIONNAIRE')),
        actions: [
          IconButton(key: const Key('bouton-deconnexion'), tooltip: l.deconnexion, icon: const Icon(Icons.logout), onPressed: _deconnecter),
        ],
      ),
      body: IndexedStack(index: _onglet, children: pages),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _onglet,
        onDestinationSelected: (i) => setState(() => _onglet = i),
        destinations: [
          NavigationDestination(key: const Key('onglet-encaisser'), icon: const Icon(Icons.payments_outlined), label: l.ongletEncaisser),
          NavigationDestination(
            key: const Key('onglet-a-envoyer'),
            icon: Badge(isLabelVisible: (enAttente.value ?? 0) > 0, label: Text('${enAttente.value ?? 0}', key: const Key('badge-a-envoyer')), child: const Icon(Icons.cloud_upload_outlined)),
            label: l.ongletAEnvoyer,
          ),
          NavigationDestination(key: const Key('onglet-lots'), icon: const Icon(Icons.apartment_outlined), label: l.ongletLots),
          NavigationDestination(key: const Key('onglet-locataires'), icon: const Icon(Icons.people_outline), label: l.ongletLocataires),
        ],
      ),
    );
  }
}

/// Nombre d'encaissements de l'agent encore à envoyer (badge de l'onglet).
final nombreEnAttenteProvider = StreamProvider.family<int, String>((ref, agent) => ref.watch(fileEncaissementsProvider).suivreNombreEnAttente(agent));
