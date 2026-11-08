import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immogest_api/immogest_api.dart';

import '../../core/error/error_messages.dart';
import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import '../locataire/formatage.dart';
import 'arrieres_screen.dart';
import 'gestion_repository.dart';
import 'signalements_gestion_screen.dart';

/// Accueil de la gestion : indicateurs du mois (ADMIN et GESTIONNAIRE, [avecStats]) et accès aux arriérés et aux signalements.
class AccueilGestionScreen extends ConsumerWidget {
  const AccueilGestionScreen({super.key, required this.avecStats});

  final bool avecStats;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l = AppLocalizations.of(context);
    final locale = Localizations.localeOf(context).toString();
    final synthese = avecStats ? ref.watch(syntheseGestionProvider) : null;
    return RefreshIndicator(
      onRefresh: () async {
        ref.invalidate(syntheseGestionProvider);
        ref.invalidate(arrieresGestionProvider);
        ref.invalidate(signalementsGestionProvider);
        if (avecStats) await ref.read(syntheseGestionProvider.future).catchError((_) => Synthese());
      },
      child: ListView(padding: const EdgeInsets.all(16), children: [
        if (synthese != null)
          synthese.when(
            loading: () => const Padding(padding: EdgeInsets.only(bottom: 12), child: Squelette(hauteur: 140, rayon: 16)),
            error: (e, _) => Card(
              child: ListTile(
                key: const Key('erreur-synthese'),
                leading: const Icon(Icons.error_outline),
                title: Text(errorMessage(l, e)),
                trailing: TextButton(onPressed: () => ref.invalidate(syntheseGestionProvider), child: Text(l.reessayer)),
              ),
            ),
            data: (s) => _Indicateurs(synthese: s, locale: locale),
          ),
        _Acces(
          cle: const Key('acces-arrieres'),
          icone: Icons.warning_amber_rounded,
          titre: l.gestionArrieres,
          detail: synthese?.value == null ? null : l.gestionArrieresDetail(synthese!.value!.echeancesEnRetard ?? 0),
          montant: synthese?.value?.arrieresTotal == null ? null : formaterFcfa(synthese!.value!.arrieresTotal, locale),
          onTap: () => Navigator.of(context).push(MaterialPageRoute<void>(builder: (_) => const ArrieresScreen())),
        ),
        _Acces(
          cle: const Key('acces-signalements'),
          icone: Icons.build_circle_outlined,
          titre: l.gestionSignalements,
          detail: l.gestionSignalementsAide,
          onTap: () => Navigator.of(context).push(MaterialPageRoute<void>(builder: (_) => const SignalementsGestionScreen())),
        ),
      ]),
    );
  }
}

class _Indicateurs extends StatelessWidget {
  const _Indicateurs({required this.synthese, required this.locale});

  final Synthese synthese;
  final String locale;

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final t = Theme.of(context).textTheme;
    final o = synthese.occupation;
    final taux = ((o?.tauxBp ?? 0) / 100).round();
    final recouvrement = ((synthese.tauxRecouvrementMoisBp ?? 0) / 100).round();
    return Column(children: [
      Card(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(l.gestionOccupation, style: t.titleSmall),
            const SizedBox(height: 6),
            Text('$taux %', key: const Key('taux-occupation'), style: t.headlineMedium?.copyWith(fontWeight: FontWeight.w800)),
            const SizedBox(height: 8),
            LinearProgressIndicator(value: (taux / 100).clamp(0.0, 1.0), minHeight: 8, borderRadius: BorderRadius.circular(8)),
            const SizedBox(height: 8),
            Text(l.gestionOccupationDetail(o?.occupes ?? 0, o?.libres ?? 0, o?.enTravaux ?? 0), style: t.bodySmall),
          ]),
        ),
      ),
      Card(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(l.gestionMois, style: t.titleSmall),
            const SizedBox(height: 10),
            Row(children: [
              Expanded(child: _Chiffre(libelle: l.gestionAttendu, valeur: formaterFcfa(synthese.attenduMois, locale), cle: const Key('attendu-mois'))),
              Expanded(child: _Chiffre(libelle: l.gestionEncaisse, valeur: formaterFcfa(synthese.encaisseMois, locale), cle: const Key('encaisse-mois'))),
            ]),
            const SizedBox(height: 10),
            LinearProgressIndicator(value: (recouvrement / 100).clamp(0.0, 1.0), minHeight: 8, borderRadius: BorderRadius.circular(8)),
            const SizedBox(height: 6),
            Text(l.gestionRecouvrement(recouvrement), style: t.bodySmall),
          ]),
        ),
      ),
    ]);
  }
}

class _Chiffre extends StatelessWidget {
  const _Chiffre({required this.libelle, required this.valeur, required this.cle});

  final String libelle;
  final String valeur;
  final Key cle;

  @override
  Widget build(BuildContext context) {
    final t = Theme.of(context).textTheme;
    return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Text(libelle, style: t.bodySmall),
      Text(valeur, key: cle, style: t.titleMedium?.copyWith(fontWeight: FontWeight.w700)),
    ]);
  }
}

class _Acces extends StatelessWidget {
  const _Acces({required this.cle, required this.icone, required this.titre, required this.onTap, this.detail, this.montant});

  final Key cle;
  final IconData icone;
  final String titre;
  final String? detail;
  final String? montant;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) => Card(
        child: ListTile(
          key: cle,
          leading: Icon(icone),
          title: Text(titre),
          subtitle: detail == null ? null : Text(detail!),
          trailing: Row(mainAxisSize: MainAxisSize.min, children: [
            if (montant != null) Text(montant!, style: const TextStyle(fontWeight: FontWeight.w700)),
            const Icon(Icons.chevron_right),
          ]),
          onTap: onTap,
        ),
      );
}
