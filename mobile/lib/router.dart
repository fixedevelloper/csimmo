import 'package:flutter/foundation.dart';
import 'package:go_router/go_router.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import 'core/auth/role.dart';
import 'core/providers.dart';
import 'features/auth/login_screen.dart';
import 'features/home/role_home_screen.dart';
import 'features/agent/agent_shell.dart';
import 'features/locataire/locataire_shell.dart';
import 'features/server_setup/qr_scan_screen.dart';
import 'features/server_setup/server_setup_screen.dart';

part 'router.g.dart';

/// Redirections : pas de serveur → configuration ; pas de session → connexion ; session → accueil du rôle.
/// Un utilisateur ne peut jamais afficher l'accueil d'un autre rôle.
@visibleForTesting
String? redirection({required String? serveur, required Role? role, required String chemin}) {
  if (serveur == null) return chemin.startsWith('/serveur') ? null : '/serveur';
  if (role == null) return (chemin == '/connexion' || chemin.startsWith('/serveur')) ? null : '/connexion';
  return chemin == role.homeRoute ? null : role.homeRoute;
}

@Riverpod(keepAlive: true)
GoRouter router(Ref ref) {
  final changement = ValueNotifier<int>(0);
  ref.listen(serverSettingsProvider, (_, _) => changement.value++);
  ref.listen(sessionProvider, (_, _) => changement.value++);
  ref.onDispose(changement.dispose);

  return GoRouter(
    initialLocation: '/',
    refreshListenable: changement,
    redirect: (context, state) => redirection(
      serveur: ref.read(serverSettingsProvider),
      role: ref.read(sessionProvider),
      chemin: state.matchedLocation,
    ),
    routes: [
      GoRoute(path: '/', builder: (_, _) => const ServerSetupScreen()),
      GoRoute(
        path: '/serveur',
        builder: (_, _) => const ServerSetupScreen(),
        routes: [GoRoute(path: 'scan', builder: (_, _) => const QrScanScreen())],
      ),
      GoRoute(path: '/connexion', builder: (_, _) => const LoginScreen()),
      GoRoute(path: Role.locataire.homeRoute, builder: (_, _) => const LocataireShell()),
      GoRoute(path: Role.gestionnaire.homeRoute, builder: (_, _) => const AgentShell()),
      for (final role in [Role.prestataire, Role.proprietaireTiers, Role.comptable])
        GoRoute(path: role.homeRoute, builder: (_, _) => RoleHomeScreen(role)),
    ],
  );
}
