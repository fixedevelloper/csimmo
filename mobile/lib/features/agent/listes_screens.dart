import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

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
          decoration: InputDecoration(labelText: l.rechercherLot, prefixIcon: const Icon(Icons.search), border: const OutlineInputBorder()),
          onChanged: (v) => setState(() => _q = v),
        ),
      ),
      Expanded(
        child: FutureBuilder(
          future: ref.read(cacheTerrainProvider).lots(_q),
          builder: (context, snap) {
            final lots = snap.data ?? const [];
            if (snap.connectionState != ConnectionState.done) return const Center(child: CircularProgressIndicator());
            if (lots.isEmpty) return Center(child: Text(l.aucunLot, key: const Key('aucun-lot')));
            return ListView.separated(
              itemCount: lots.length,
              separatorBuilder: (_, _) => const Divider(height: 1),
              itemBuilder: (_, i) => ListTile(
                key: Key('lot-${lots[i].id}'),
                title: Text('${lots[i].immeubleNom} — ${lots[i].reference}'),
                trailing: Chip(label: Text(_statut(l, lots[i].statut))),
              ),
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
          decoration: InputDecoration(labelText: l.rechercherLocataire, prefixIcon: const Icon(Icons.search), border: const OutlineInputBorder()),
          onChanged: (v) => setState(() => _q = v),
        ),
      ),
      Expanded(
        child: FutureBuilder(
          future: ref.read(cacheTerrainProvider).chercherLocataires(_q, limite: 100),
          builder: (context, snap) {
            final liste = snap.data ?? const [];
            if (snap.connectionState != ConnectionState.done) return const Center(child: CircularProgressIndicator());
            if (liste.isEmpty) return Center(child: Text(l.aucunLocataire, key: const Key('aucun-locataire-liste')));
            return ListView.separated(
              itemCount: liste.length,
              separatorBuilder: (_, _) => const Divider(height: 1),
              itemBuilder: (_, i) => ListTile(key: Key('liste-locataire-${liste[i].id}'), title: Text(liste[i].nom), subtitle: liste[i].telephone == null ? null : Text(liste[i].telephone!)),
            );
          },
        ),
      ),
    ]);
  }
}
