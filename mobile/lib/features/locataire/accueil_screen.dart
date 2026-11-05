import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/error/error_messages.dart';
import '../../l10n/app_localizations.dart';
import '../auth/auth_repository.dart';
import 'formatage.dart';
import 'locataire_repository.dart';
import 'payer_screen.dart';

/// Accueil du locataire : bienvenue, solde, bouton « Payer » (branché au paiement Mobile Money au lot 8).
class AccueilScreen extends ConsumerWidget {
  const AccueilScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l = AppLocalizations.of(context);
    final locale = Localizations.localeOf(context).toString();
    final utilisateur = ref.watch(currentUserProvider);
    final compte = ref.watch(monCompteProvider);

    return RefreshIndicator(
      onRefresh: () async {
        ref.invalidate(monCompteProvider);
        try {
          await ref.read(monCompteProvider.future);
        } catch (_) {}
      },
      child: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          utilisateur.maybeWhen(
            data: (u) => Text(l.bienvenue(u.nom ?? ''), key: const Key('bienvenue'), style: Theme.of(context).textTheme.titleLarge),
            orElse: () => const SizedBox(height: 28),
          ),
          const SizedBox(height: 16),
          compte.when(
            loading: () => const Center(child: Padding(padding: EdgeInsets.all(24), child: CircularProgressIndicator())),
            error: (e, _) => Column(children: [
              Text(errorMessage(l, e), key: const Key('erreur-compte'), textAlign: TextAlign.center),
              const SizedBox(height: 8),
              FilledButton(onPressed: () => ref.invalidate(monCompteProvider), child: Text(l.reessayer)),
            ]),
            data: (c) {
              final solde = c.solde ?? 0;
              return Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
                Card(
                  color: solde > 0 ? Theme.of(context).colorScheme.errorContainer : Theme.of(context).colorScheme.primaryContainer,
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                      Text(solde > 0 ? l.soldeDu : l.soldeAJour, style: Theme.of(context).textTheme.labelLarge),
                      const SizedBox(height: 4),
                      Text(
                        formaterFcfa(solde.abs(), locale),
                        key: const Key('solde'),
                        style: Theme.of(context).textTheme.headlineMedium,
                      ),
                    ]),
                  ),
                ),
                const SizedBox(height: 8),
                _Ligne(l.arrieres, formaterFcfa(c.arrieres, locale), const Key('arrieres')),
                _Ligne(l.aVenir, formaterFcfa(c.aVenir, locale), const Key('a-venir')),
                _Ligne(l.credit, formaterFcfa(c.credit, locale), const Key('credit')),
                const SizedBox(height: 16),
                FilledButton.icon(
                  key: const Key('bouton-payer'),
                  onPressed: () => Navigator.of(context).push(MaterialPageRoute<void>(builder: (_) => PayerScreen(montantSuggere: solde > 0 ? solde : null))),
                  icon: const Icon(Icons.payments_outlined),
                  label: Text(l.payer),
                ),
              ]);
            },
          ),
        ],
      ),
    );
  }
}

class _Ligne extends StatelessWidget {
  const _Ligne(this.libelle, this.valeur, this.cle);

  final String libelle;
  final String valeur;
  final Key cle;

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.symmetric(vertical: 4),
        child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [Text(libelle), Text(valeur, key: cle)]),
      );
}
