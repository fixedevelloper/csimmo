import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import '../../core/auth/role.dart';
import '../../core/providers.dart';
import '../auth/auth_actions.dart';
import 'a_envoyer_screen.dart';
import 'encaisser_screen.dart';
import '../gestion/accueil_gestion_screen.dart';
import 'file_encaissements.dart';
import 'listes_screens.dart';
import 'synchro_encaissements.dart';

/// Espace de l'agent de terrain (ADMIN, GESTIONNAIRE, ENCAISSEUR et GESTIONNAIRE_BIENS, ces deux derniers limités à leur périmètre par le serveur) : encaisser en espèces même sans réseau, suivre la file d'envoi, consulter lots et locataires.
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
    final role = ref.watch(sessionProvider);
    // suivi (indicateurs, arriérés, signalements) : réservé aux rôles de gestion ; les indicateurs seulement à ADMIN et GESTIONNAIRE
    final suivi = role == Role.admin || role == Role.gestionnaire || role == Role.gestionnaireBiens;
    final pages = [
      const EncaisserScreen(),
      const AEnvoyerScreen(),
      const LotsScreen(),
      const LocatairesScreen(),
      if (suivi) AccueilGestionScreen(avecStats: role != Role.gestionnaireBiens),
    ];
    return Scaffold(
      appBar: AppBar(
        title: TitreMarque(l.espaceAgent, cleTexte: const Key('titre-GESTIONNAIRE')),
        actions: [
          IconButton(
            key: const Key('bouton-profil'),
            tooltip: l.profilBouton,
            icon: const Icon(Icons.account_circle_outlined),
            onPressed: () => context.push('/profil'),
          ),
          IconButton(key: const Key('bouton-deconnexion'), tooltip: l.deconnexion, icon: const Icon(Icons.logout), onPressed: _deconnecter),
        ],
      ),
      body: Column(children: [
        if ((enAttente.value ?? 0) > 0 && _onglet != 1)
          _BandeauFile(nombre: enAttente.value ?? 0, onTap: () => setState(() => _onglet = 1)),
        Expanded(child: IndexedStack(index: _onglet, children: pages)),
      ]),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _onglet,
        onDestinationSelected: (i) {
          HapticFeedback.selectionClick();
          setState(() => _onglet = i);
        },
        destinations: [
          NavigationDestination(key: const Key('onglet-encaisser'), icon: const Icon(Icons.payments_outlined), selectedIcon: const Icon(Icons.payments), label: l.ongletEncaisser),
          NavigationDestination(
            key: const Key('onglet-a-envoyer'),
            icon: Badge(isLabelVisible: (enAttente.value ?? 0) > 0, label: Text('${enAttente.value ?? 0}', key: const Key('badge-a-envoyer')), child: const Icon(Icons.cloud_upload_outlined)),
            label: l.ongletAEnvoyer,
          ),
          NavigationDestination(key: const Key('onglet-lots'), icon: const Icon(Icons.apartment_outlined), selectedIcon: const Icon(Icons.apartment), label: l.ongletLots),
          NavigationDestination(key: const Key('onglet-locataires'), icon: const Icon(Icons.people_outline), selectedIcon: const Icon(Icons.people), label: l.ongletLocataires),
          if (suivi) NavigationDestination(key: const Key('onglet-suivi'), icon: const Icon(Icons.insights_outlined), selectedIcon: const Icon(Icons.insights), label: l.ongletSuivi),
        ],
      ),
    );
  }
}

/// Nombre d'encaissements de l'agent encore à envoyer (badge de l'onglet).
final nombreEnAttenteProvider = StreamProvider.family<int, String>((ref, agent) => ref.watch(fileEncaissementsProvider).suivreNombreEnAttente(agent));

/// Bandeau discret : des encaissements attendent le réseau ; un appui ouvre la file d'envoi.
class _BandeauFile extends StatelessWidget {
  const _BandeauFile({required this.nombre, required this.onTap});

  final int nombre;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final c = Theme.of(context).colorScheme;
    return Material(
      color: c.secondaryContainer,
      child: InkWell(
        key: const Key('bandeau-file'),
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
          child: Row(children: [
            Icon(Icons.cloud_off_outlined, size: 18, color: c.onSecondaryContainer),
            const SizedBox(width: 10),
            Expanded(child: Text(l.bandeauFile(nombre), style: TextStyle(color: c.onSecondaryContainer, fontWeight: FontWeight.w600))),
            Icon(Icons.chevron_right, color: c.onSecondaryContainer),
          ]),
        ),
      ),
    );
  }
}
