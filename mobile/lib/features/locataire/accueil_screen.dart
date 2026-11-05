import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/error/error_messages.dart';
import '../../core/widgets/composants.dart';
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
            loading: () => const Column(children: [
              Squelette(hauteur: 120, rayon: 20),
              SizedBox(height: 12),
              Squelette(hauteur: 90, rayon: 18),
            ]),
            error: (e, _) => Column(children: [
              Text(errorMessage(l, e), key: const Key('erreur-compte'), textAlign: TextAlign.center),
              const SizedBox(height: 8),
              FilledButton(onPressed: () => ref.invalidate(monCompteProvider), child: Text(l.reessayer)),
            ]),
            data: (c) {
              final solde = c.solde ?? 0;
              return Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
                _CarteSolde(solde: solde, locale: locale),
                const SizedBox(height: 8),
                CarteSection(enfants: [
                  _Ligne(l.arrieres, formaterFcfa(c.arrieres, locale), const Key('arrieres')),
                  _Ligne(l.aVenir, formaterFcfa(c.aVenir, locale), const Key('a-venir')),
                  _Ligne(l.credit, formaterFcfa(c.credit, locale), const Key('credit')),
                ]),
                const SizedBox(height: 16),
                FilledButton.icon(
                  key: const Key('bouton-payer'),
                  onPressed: () {
                    HapticFeedback.lightImpact();
                    Navigator.of(context).push(MaterialPageRoute<void>(builder: (_) => PayerScreen(montantSuggere: solde > 0 ? solde : null)));
                  },
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

/// Carte principale : le solde, en grand, sur un fond qui change selon qu'il y a des arriérés ou non.
class _CarteSolde extends StatelessWidget {
  const _CarteSolde({required this.solde, required this.locale});

  final int solde;
  final String locale;

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final du = solde > 0;
    final couleurs = du ? [const Color(0xFFB42318), const Color(0xFFE5484D)] : [const Color(0xFF0B4DB0), const Color(0xFF2F7BE8)];
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: LinearGradient(begin: Alignment.topLeft, end: Alignment.bottomRight, colors: couleurs),
        borderRadius: BorderRadius.circular(22),
        boxShadow: [BoxShadow(color: couleurs.first.withValues(alpha: 0.28), blurRadius: 18, offset: const Offset(0, 8))],
      ),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(children: [
          Icon(du ? Icons.warning_amber_rounded : Icons.check_circle_outline, color: Colors.white.withValues(alpha: 0.9), size: 20),
          const SizedBox(width: 8),
          Text(du ? l.soldeDu : l.soldeAJour, style: TextStyle(color: Colors.white.withValues(alpha: 0.9), fontWeight: FontWeight.w600)),
        ]),
        const SizedBox(height: 10),
        Text(formaterFcfa(solde.abs(), locale), key: const Key('solde'), style: TextStyle(color: Colors.white, fontSize: 34, fontWeight: FontWeight.w800, letterSpacing: -0.5)),
      ]),
    );
  }
}
