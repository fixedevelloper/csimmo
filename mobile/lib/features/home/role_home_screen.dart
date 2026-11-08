import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/auth/role.dart';
import '../../core/error/error_messages.dart';
import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import '../auth/auth_actions.dart';
import '../auth/auth_repository.dart';

/// Accueil d'un rôle : locataire, agent (gestionnaire/admin), prestataire ou propriétaire.
/// Les fonctionnalités de chaque espace arrivent avec les lots suivants (7.x).
class RoleHomeScreen extends ConsumerWidget {
  const RoleHomeScreen(this.role, {super.key});

  final Role role;

  String _titre(AppLocalizations l) => switch (role) {
        Role.locataire => l.espaceLocataire,
        Role.gestionnaire || Role.admin || Role.encaisseur || Role.gestionnaireBiens => l.espaceAgent,
        Role.prestataire => l.espacePrestataire,
        Role.proprietaireTiers => l.espaceProprietaire,
        Role.comptable => l.espaceComptable,
      };

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l = AppLocalizations.of(context);
    final utilisateur = ref.watch(currentUserProvider);
    return Scaffold(
      appBar: AppBar(
        title: TitreMarque(_titre(l), cleTexte: Key('titre-${role.code}')),
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
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: utilisateur.when(
            loading: () => const CircularProgressIndicator(),
            error: (e, _) => Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(errorMessage(l, e), textAlign: TextAlign.center),
                const SizedBox(height: 12),
                FilledButton(onPressed: () => ref.invalidate(currentUserProvider), child: Text(l.reessayer)),
              ],
            ),
            data: (u) => Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(l.bienvenue(u.nom ?? ''), key: const Key('bienvenue'), style: Theme.of(context).textTheme.titleLarge),
                const SizedBox(height: 8),
                Text(l.bientotDisponible, textAlign: TextAlign.center),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
