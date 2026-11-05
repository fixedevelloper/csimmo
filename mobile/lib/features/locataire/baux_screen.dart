import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immogest_api/immogest_api.dart';

import '../../core/error/error_messages.dart';
import '../../l10n/app_localizations.dart';
import 'formatage.dart';
import 'locataire_repository.dart';

String _statutBail(AppLocalizations l, BailDtoStatutEnum? s) => switch (s) {
      BailDtoStatutEnum.ACTIF => l.bailActif,
      BailDtoStatutEnum.RESILIE => l.bailResilie,
      BailDtoStatutEnum.TERMINE => l.bailTermine,
      _ => l.bailBrouillon,
    };

/// Liste des baux du locataire ; un bail ouvre ses conditions et ses échéances.
class BauxScreen extends ConsumerWidget {
  const BauxScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l = AppLocalizations.of(context);
    final locale = Localizations.localeOf(context).toString();
    final baux = ref.watch(mesBauxProvider);
    return RefreshIndicator(
      onRefresh: () async {
        ref.invalidate(mesBauxProvider);
        try {
          await ref.read(mesBauxProvider.future);
        } catch (_) {}
      },
      child: baux.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => ListView(padding: const EdgeInsets.all(24), children: [
          Text(errorMessage(l, e), key: const Key('erreur-baux'), textAlign: TextAlign.center),
          FilledButton(onPressed: () => ref.invalidate(mesBauxProvider), child: Text(l.reessayer)),
        ]),
        data: (liste) => liste.isEmpty
            ? ListView(padding: const EdgeInsets.all(24), children: [Text(l.aucunBail, key: const Key('aucun-bail'), textAlign: TextAlign.center)])
            : ListView.separated(
                padding: const EdgeInsets.all(8),
                itemCount: liste.length,
                separatorBuilder: (_, _) => const Divider(height: 1),
                itemBuilder: (_, i) {
                  final b = liste[i];
                  return ListTile(
                    key: Key('bail-${b.id}'),
                    title: Text('${b.immeubleNom ?? ''} — ${b.lotReference ?? ''}'),
                    subtitle: Text('${b.numero ?? ''} · ${_statutBail(l, b.statut)} · ${formaterFcfa((b.loyer ?? 0) + (b.charges ?? 0), locale)}'),
                    trailing: const Icon(Icons.chevron_right),
                    onTap: () => Navigator.of(context).push(MaterialPageRoute<void>(builder: (_) => BailDetailScreen(bail: b))),
                  );
                },
              ),
      ),
    );
  }
}

String _statutEcheance(AppLocalizations l, EcheanceDtoStatutEnum? s) => switch (s) {
      EcheanceDtoStatutEnum.PAYEE => l.echeancePayee,
      EcheanceDtoStatutEnum.EN_RETARD => l.echeanceEnRetard,
      EcheanceDtoStatutEnum.PARTIELLE => l.echeancePartielle,
      EcheanceDtoStatutEnum.DUE => l.echeanceDue,
      _ => l.echeanceAVenir,
    };

/// Conditions d'un bail et ses échéances (période, montant, reste, statut).
class BailDetailScreen extends ConsumerWidget {
  const BailDetailScreen({super.key, required this.bail});

  final BailDto bail;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l = AppLocalizations.of(context);
    final locale = Localizations.localeOf(context).toString();
    final echeances = ref.watch(mesEcheancesProvider(bail.id ?? ''));
    return Scaffold(
      appBar: AppBar(title: Text(bail.numero ?? '', key: const Key('titre-bail'))),
      body: ListView(padding: const EdgeInsets.all(16), children: [
        Text('${bail.immeubleNom ?? ''} — ${bail.lotReference ?? ''}', style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: 8),
        _Info(l.loyerMensuel, formaterFcfa(bail.loyer, locale)),
        _Info(l.chargesMensuelles, formaterFcfa(bail.charges, locale)),
        _Info(l.caution, formaterFcfa(bail.caution, locale)),
        _Info(l.debutBail, formaterDate(bail.dateDebut, locale)),
        _Info(l.finBail, bail.dateFin == null ? l.sansDateFin : formaterDate(bail.dateFin, locale)),
        if (bail.dateSortie != null) _Info(l.sortiePrevue, formaterDate(bail.dateSortie, locale)),
        const Divider(height: 32),
        Text(l.echeances, style: Theme.of(context).textTheme.titleMedium),
        echeances.when(
          loading: () => const Padding(padding: EdgeInsets.all(24), child: Center(child: CircularProgressIndicator())),
          error: (e, _) => Column(children: [
            Text(errorMessage(l, e), key: const Key('erreur-echeances')),
            TextButton(onPressed: () => ref.invalidate(mesEcheancesProvider(bail.id ?? '')), child: Text(l.reessayer)),
          ]),
          data: (liste) => Column(children: [
            for (final e in liste)
              ListTile(
                key: Key('echeance-${e.id}'),
                dense: true,
                contentPadding: EdgeInsets.zero,
                title: Text('${formaterDate(e.periodeDebut, locale)} → ${formaterDate(e.periodeFin, locale)}'),
                subtitle: Text('${l.avantLe(formaterDate(e.dateEcheance, locale))} · ${formaterFcfa(e.montant, locale)}'
                    '${(e.reste ?? 0) > 0 && (e.montantPaye ?? 0) > 0 ? ' · ${l.resteAPayer(formaterFcfa(e.reste, locale))}' : ''}'),
                trailing: Chip(
                  key: Key('statut-${e.id}'),
                  label: Text(_statutEcheance(l, e.statut)),
                  backgroundColor: e.statut == EcheanceDtoStatutEnum.EN_RETARD ? Theme.of(context).colorScheme.errorContainer : null,
                ),
              ),
          ]),
        ),
      ]),
    );
  }
}

class _Info extends StatelessWidget {
  const _Info(this.libelle, this.valeur);

  final String libelle;
  final String valeur;

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.symmetric(vertical: 3),
        child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [Text(libelle), Flexible(child: Text(valeur, textAlign: TextAlign.end))]),
      );
}
