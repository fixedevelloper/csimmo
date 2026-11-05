import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immogest_api/immogest_api.dart';

import '../../core/error/error_messages.dart';
import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import 'formatage.dart';
import 'locataire_repository.dart';

Ton _tonBail(BailDtoStatutEnum? s) => switch (s) {
      BailDtoStatutEnum.ACTIF => Ton.succes,
      BailDtoStatutEnum.RESILIE || BailDtoStatutEnum.TERMINE => Ton.neutre,
      _ => Ton.info,
    };

Ton _tonEcheance(EcheanceDtoStatutEnum? s) => switch (s) {
      EcheanceDtoStatutEnum.PAYEE => Ton.succes,
      EcheanceDtoStatutEnum.EN_RETARD => Ton.danger,
      EcheanceDtoStatutEnum.PARTIELLE || EcheanceDtoStatutEnum.DUE => Ton.alerte,
      _ => Ton.neutre,
    };

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
        loading: () => const ListeSquelette(),
        error: (e, _) => ListView(padding: const EdgeInsets.all(24), children: [
          Text(errorMessage(l, e), key: const Key('erreur-baux'), textAlign: TextAlign.center),
          FilledButton(onPressed: () => ref.invalidate(mesBauxProvider), child: Text(l.reessayer)),
        ]),
        data: (liste) => liste.isEmpty
            ? ListView(children: [EtatVide(icone: Icons.description_outlined, texte: l.aucunBail, cleTexte: const Key('aucun-bail'))])
            : ListView.builder(
                padding: const EdgeInsets.all(16),
                itemCount: liste.length,
                itemBuilder: (_, i) {
                  final b = liste[i];
                  return Card(
                    child: InkWell(
                      key: Key('bail-${b.id}'),
                      borderRadius: BorderRadius.circular(18),
                      onTap: () => Navigator.of(context).push(MaterialPageRoute<void>(builder: (_) => BailDetailScreen(bail: b))),
                      child: Padding(
                        padding: const EdgeInsets.all(16),
                        child: Row(children: [
                          Expanded(
                            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                              Text('${b.immeubleNom ?? ''} — ${b.lotReference ?? ''}', style: Theme.of(context).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w600)),
                              const SizedBox(height: 4),
                              Text('${b.numero ?? ''} · ${formaterFcfa((b.loyer ?? 0) + (b.charges ?? 0), locale)}', style: TextStyle(color: Theme.of(context).colorScheme.onSurfaceVariant)),
                              const SizedBox(height: 10),
                              PastilleStatut(texte: _statutBail(l, b.statut), ton: _tonBail(b.statut)),
                            ]),
                          ),
                          const Icon(Icons.chevron_right),
                        ]),
                      ),
                    ),
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
        CarteSection(titre: '${bail.immeubleNom ?? ''} — ${bail.lotReference ?? ''}', enfants: [
          LigneInfo(l.loyerMensuel, formaterFcfa(bail.loyer, locale)),
          LigneInfo(l.chargesMensuelles, formaterFcfa(bail.charges, locale)),
          LigneInfo(l.caution, formaterFcfa(bail.caution, locale)),
          LigneInfo(l.debutBail, formaterDate(bail.dateDebut, locale)),
          LigneInfo(l.finBail, bail.dateFin == null ? l.sansDateFin : formaterDate(bail.dateFin, locale)),
          if (bail.dateSortie != null) LigneInfo(l.sortiePrevue, formaterDate(bail.dateSortie, locale)),
        ]),
        const SizedBox(height: 8),
        Padding(padding: const EdgeInsets.fromLTRB(4, 8, 4, 4), child: Text(l.echeances, style: Theme.of(context).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w700))),
        echeances.when(
          loading: () => const Column(children: [Squelette(hauteur: 64, rayon: 16), SizedBox(height: 8), Squelette(hauteur: 64, rayon: 16)]),
          error: (e, _) => Column(children: [
            Text(errorMessage(l, e), key: const Key('erreur-echeances')),
            TextButton(onPressed: () => ref.invalidate(mesEcheancesProvider(bail.id ?? '')), child: Text(l.reessayer)),
          ]),
          data: (liste) => Column(children: [
            for (final e in liste)
              Card(
                key: Key('echeance-${e.id}'),
                child: Padding(
                  padding: const EdgeInsets.all(14),
                  child: Row(children: [
                    Expanded(
                      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                        Text('${formaterDate(e.periodeDebut, locale)} → ${formaterDate(e.periodeFin, locale)}', style: const TextStyle(fontWeight: FontWeight.w600)),
                        const SizedBox(height: 4),
                        Text(
                          '${l.avantLe(formaterDate(e.dateEcheance, locale))} · ${formaterFcfa(e.montant, locale)}'
                          '${(e.reste ?? 0) > 0 && (e.montantPaye ?? 0) > 0 ? ' · ${l.resteAPayer(formaterFcfa(e.reste, locale))}' : ''}',
                          style: TextStyle(color: Theme.of(context).colorScheme.onSurfaceVariant, fontSize: 13),
                        ),
                      ]),
                    ),
                    const SizedBox(width: 8),
                    KeyedSubtree(key: Key('statut-${e.id}'), child: PastilleStatut(texte: _statutEcheance(l, e.statut), ton: _tonEcheance(e.statut))),
                  ]),
                ),
              ),
          ]),
        ),
      ]),
    );
  }
}
