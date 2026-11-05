import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/db/app_database.dart';
import '../../core/error/app_error.dart';
import '../../core/error/error_messages.dart';
import '../../l10n/app_localizations.dart';
import '../locataire/formatage.dart';
import 'file_encaissements.dart';
import 'synchro_encaissements.dart';

final _suiviProvider = StreamProvider.family<List<EncaissementsEnAttenteData>, String>((ref, agent) => ref.watch(fileEncaissementsProvider).suivre(agent));

/// File d'envoi : ce qui attend le réseau, ce qui est parti, ce que le serveur a refusé.
class AEnvoyerScreen extends ConsumerStatefulWidget {
  const AEnvoyerScreen({super.key});

  @override
  ConsumerState<AEnvoyerScreen> createState() => _AEnvoyerScreenState();
}

class _AEnvoyerScreenState extends ConsumerState<AEnvoyerScreen> {
  bool _envoi = false;

  Future<void> _synchroniser() async {
    final l = AppLocalizations.of(context);
    final messenger = ScaffoldMessenger.of(context);
    setState(() => _envoi = true);
    try {
      final b = await ref.read(synchroEncaissementsProvider).synchroniser();
      final message = b.blocage != null
          ? l.synchroInterrompue(errorMessage(l, _code(b.blocage!)), b.restants)
          : l.synchroTerminee(b.envoyes, b.rejetes);
      messenger.showSnackBar(SnackBar(content: Text(message)));
    } finally {
      if (mounted) setState(() => _envoi = false);
    }
  }

  Future<void> _abandonner(EncaissementsEnAttenteData e) async {
    final l = AppLocalizations.of(context);
    final messenger = ScaffoldMessenger.of(context);
    final ok = await ref.read(fileEncaissementsProvider).abandonner(e.id);
    messenger.showSnackBar(SnackBar(content: Text(ok ? l.saisieSupprimee : l.abandonImpossible)));
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final locale = Localizations.localeOf(context).toString();
    final agent = ref.watch(agentIdProvider).value;
    final liste = agent == null ? const AsyncData<List<EncaissementsEnAttenteData>>([]) : ref.watch(_suiviProvider(agent));
    return ListView(padding: const EdgeInsets.all(16), children: [
      FilledButton.icon(
        key: const Key('synchroniser'),
        onPressed: _envoi ? null : _synchroniser,
        icon: _envoi ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth: 2)) : const Icon(Icons.sync),
        label: Text(l.synchroniserMaintenant),
      ),
      const SizedBox(height: 8),
      liste.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Text(errorMessage(l, e)),
        data: (items) => items.isEmpty
            ? Padding(padding: const EdgeInsets.all(24), child: Text(l.fileVide, key: const Key('file-vide'), textAlign: TextAlign.center))
            : Column(children: [
                for (final e in items)
                  Card(
                    key: Key('envoi-${e.id}'),
                    child: ListTile(
                      title: Text('${e.locataireNom} · ${formaterFcfa(e.montant, locale)}'),
                      subtitle: Text('${e.bailLibelle}\n${e.datePaiement}${e.statut == statutRejete ? ' · ${errorMessage(l, _code(e.derniereErreur ?? ''))}' : ''}'),
                      isThreeLine: true,
                      leading: switch (e.statut) {
                        statutEnvoye => const Icon(Icons.check_circle, color: Colors.green),
                        statutRejete => Icon(Icons.error, color: Theme.of(context).colorScheme.error),
                        _ => const Icon(Icons.schedule),
                      },
                      trailing: e.statut == statutEnvoye
                          ? Text(l.statutEnvoye, key: Key('statut-${e.id}'))
                          : Row(mainAxisSize: MainAxisSize.min, children: [
                              Text(e.statut == statutRejete ? l.statutRejete : l.statutEnAttente, key: Key('statut-${e.id}')),
                              IconButton(key: Key('abandonner-${e.id}'), tooltip: l.supprimerSaisie, icon: const Icon(Icons.delete_outline), onPressed: () => _abandonner(e)),
                            ]),
                    ),
                  ),
              ]),
      ),
    ]);
  }
}

/// Code d'erreur → objet interprétable par [errorMessage].
Object _code(String code) => AppError(code);
