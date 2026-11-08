import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immogest_api/immogest_api.dart';

import '../../core/error/app_error.dart';
import '../../core/error/error_messages.dart';
import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import '../locataire/formatage.dart';
import 'gestion_repository.dart';

/// Échéances en retard, avec relance manuelle. Recherche par nom de locataire.
class ArrieresScreen extends ConsumerStatefulWidget {
  const ArrieresScreen({super.key});

  @override
  ConsumerState<ArrieresScreen> createState() => _ArrieresScreenState();
}

class _ArrieresScreenState extends ConsumerState<ArrieresScreen> {
  String _q = '';
  final _enCours = <String>{};

  Future<void> _relancer(ArriereDto a) async {
    final id = a.echeanceId;
    if (id == null || _enCours.contains(id)) return;
    final l = AppLocalizations.of(context);
    final messenger = ScaffoldMessenger.of(context);
    setState(() => _enCours.add(id));
    try {
      await ref.read(gestionRepositoryProvider).relancer(id);
      ref.invalidate(arrieresGestionProvider);
      messenger.showSnackBar(SnackBar(content: Text(l.relanceEnvoyee(a.locataireNom ?? ''))));
    } on AppError catch (e) {
      messenger.showSnackBar(SnackBar(content: Text(errorMessage(l, e))));
    } finally {
      if (mounted) setState(() => _enCours.remove(id));
    }
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final locale = Localizations.localeOf(context).toString();
    final arrieres = ref.watch(arrieresGestionProvider(_q));
    return Scaffold(
      appBar: AppBar(title: Text(l.arrieresTitre)),
      body: Column(children: [
        Padding(
          padding: const EdgeInsets.all(12),
          child: TextField(
            key: const Key('recherche-arrieres'),
            decoration: InputDecoration(labelText: l.rechercherArriere, prefixIcon: const Icon(Icons.search)),
            onChanged: (v) => setState(() => _q = v),
          ),
        ),
        Expanded(
          child: RefreshIndicator(
            onRefresh: () async {
              ref.invalidate(arrieresGestionProvider);
              await ref.read(arrieresGestionProvider(_q).future).catchError((_) => const <ArriereDto>[]);
            },
            child: arrieres.when(
              loading: () => const ListeSquelette(),
              error: (e, _) => ListView(children: [
                ListTile(
                  key: const Key('erreur-arrieres'),
                  leading: const Icon(Icons.error_outline),
                  title: Text(errorMessage(l, e)),
                  trailing: TextButton(onPressed: () => ref.invalidate(arrieresGestionProvider), child: Text(l.reessayer)),
                ),
              ]),
              data: (liste) => liste.isEmpty
                  ? ListView(children: [EtatVide(icone: Icons.check_circle_outline, texte: l.aucunArriere, cleTexte: const Key('aucun-arriere'))])
                  : ListView.builder(
                      padding: const EdgeInsets.symmetric(horizontal: 12),
                      itemCount: liste.length,
                      itemBuilder: (_, i) {
                        final a = liste[i];
                        final retard = a.joursRetard ?? 0;
                        return Card(
                          key: Key('arriere-${a.echeanceId}'),
                          child: Padding(
                            padding: const EdgeInsets.all(14),
                            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                              Row(children: [
                                Expanded(child: Text(a.locataireNom ?? '—', style: const TextStyle(fontWeight: FontWeight.w700))),
                                PastilleStatut(texte: l.arriereRetard(retard), ton: retard > 30 ? Ton.danger : Ton.alerte),
                              ]),
                              const SizedBox(height: 2),
                              Text(a.logement ?? '', style: Theme.of(context).textTheme.bodySmall),
                              const SizedBox(height: 8),
                              Text(l.arriereReste(formaterFcfa(a.reste, locale)), style: Theme.of(context).textTheme.titleMedium),
                              if (a.derniereRelance?.creeLe != null)
                                Text(l.relanceDerniere(formaterDate(a.derniereRelance!.creeLe, locale)), style: Theme.of(context).textTheme.bodySmall),
                              const SizedBox(height: 8),
                              Align(
                                alignment: Alignment.centerRight,
                                child: FilledButton.tonalIcon(
                                  key: Key('relancer-${a.echeanceId}'),
                                  onPressed: _enCours.contains(a.echeanceId) ? null : () => _relancer(a),
                                  icon: const Icon(Icons.notifications_active_outlined),
                                  label: Text(l.relancer),
                                ),
                              ),
                            ]),
                          ),
                        );
                      },
                    ),
            ),
          ),
        ),
      ]),
    );
  }
}
