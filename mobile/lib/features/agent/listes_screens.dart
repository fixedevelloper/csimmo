import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import 'cache_terrain.dart';

/// Lots du patrimoine (copie locale, consultable sans réseau). Filtre par référence ou immeuble.
class LotsScreen extends ConsumerStatefulWidget {
  const LotsScreen({super.key});

  @override
  ConsumerState<LotsScreen> createState() => _LotsScreenState();
}

class _LotsScreenState extends ConsumerState<LotsScreen> {
  String _q = '';

  String _statut(AppLocalizations l, String s) => switch (s) {
        'OCCUPE' => l.lotOccupe,
        'EN_TRAVAUX' => l.lotEnTravaux,
        _ => l.lotLibre,
      };

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    return Column(children: [
      Padding(
        padding: const EdgeInsets.all(12),
        child: TextField(
          key: const Key('recherche-lots'),
          decoration: InputDecoration(labelText: l.rechercherLot, prefixIcon: const Icon(Icons.search),),
          onChanged: (v) => setState(() => _q = v),
        ),
      ),
      Expanded(
        child: FutureBuilder(
          future: ref.read(cacheTerrainProvider).lots(_q),
          builder: (context, snap) {
            final lots = snap.data ?? const [];
            if (snap.connectionState != ConnectionState.done) return const ListeSquelette();
            if (lots.isEmpty) return EtatVide(icone: Icons.apartment_outlined, texte: l.aucunLot, cleTexte: const Key('aucun-lot'));
            return ListView.builder(
              padding: const EdgeInsets.symmetric(horizontal: 12),
              itemCount: lots.length,
              itemBuilder: (_, i) => Card(child: ListTile(
                key: Key('lot-${lots[i].id}'),
                title: Text('${lots[i].immeubleNom} — ${lots[i].reference}'),
                trailing: PastilleStatut(texte: _statut(l, lots[i].statut), ton: switch (lots[i].statut) { 'OCCUPE' => Ton.succes, 'EN_TRAVAUX' => Ton.info, _ => Ton.alerte }),
              )),
            );
          },
        ),
      ),
    ]);
  }
}

/// Locataires (copie locale, consultable sans réseau). Filtre par nom ou téléphone.
class LocatairesScreen extends ConsumerStatefulWidget {
  const LocatairesScreen({super.key});

  @override
  ConsumerState<LocatairesScreen> createState() => _LocatairesScreenState();
}

class _LocatairesScreenState extends ConsumerState<LocatairesScreen> {
  String _q = '';

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    return Column(children: [
      Padding(
        padding: const EdgeInsets.all(12),
        child: TextField(
          key: const Key('recherche-locataires'),
          decoration: InputDecoration(labelText: l.rechercherLocataire, prefixIcon: const Icon(Icons.search),),
          onChanged: (v) => setState(() => _q = v),
        ),
      ),
      Expanded(
        child: FutureBuilder(
          future: ref.read(cacheTerrainProvider).chercherLocataires(_q, limite: 100),
          builder: (context, snap) {
            final liste = snap.data ?? const [];
            if (snap.connectionState != ConnectionState.done) return const ListeSquelette();
            if (liste.isEmpty) return EtatVide(icone: Icons.people_outline, texte: l.aucunLocataire, cleTexte: const Key('aucun-locataire-liste'));
            return ListView.builder(
              padding: const EdgeInsets.symmetric(horizontal: 12),
              itemCount: liste.length,
              itemBuilder: (_, i) => Card(child: ListTile(key: Key('liste-locataire-${liste[i].id}'), title: Text(liste[i].nom), subtitle: liste[i].telephone == null ? null : Text(liste[i].telephone!))),
            );
          },
        ),
      ),
    ]);
  }
}
