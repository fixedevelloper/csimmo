import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immogest_api/immogest_api.dart';

import '../../core/error/app_error.dart';
import '../../core/error/error_messages.dart';
import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import '../locataire/formatage.dart';
import 'gestion_repository.dart';

String _libelle(AppLocalizations l, SignalementDtoStatutEnum? s) => switch (s) {
      SignalementDtoStatutEnum.EN_COURS => l.signalementEnCours,
      SignalementDtoStatutEnum.RESOLU => l.signalementResolu,
      SignalementDtoStatutEnum.REJETE => l.signalementRejete,
      _ => l.signalementNouveau,
    };

Ton _ton(SignalementDtoStatutEnum? s) => switch (s) {
      SignalementDtoStatutEnum.EN_COURS => Ton.info,
      SignalementDtoStatutEnum.RESOLU => Ton.succes,
      SignalementDtoStatutEnum.REJETE => Ton.neutre,
      _ => Ton.danger,
    };

/// Signalements de pannes des locataires : filtre par statut, prise en charge, résolution ou rejet avec note.
class SignalementsGestionScreen extends ConsumerStatefulWidget {
  const SignalementsGestionScreen({super.key});

  @override
  ConsumerState<SignalementsGestionScreen> createState() => _SignalementsGestionScreenState();
}

class _SignalementsGestionScreenState extends ConsumerState<SignalementsGestionScreen> {
  String? _statut;

  Future<void> _ouvrir(SignalementDto s) async {
    final l = AppLocalizations.of(context);
    final messenger = ScaffoldMessenger.of(context);
    final choix = await showModalBottomSheet<(StatutSignalementRequeteStatutEnum, String)>(
      context: context,
      isScrollControlled: true,
      showDragHandle: true,
      builder: (_) => _Detail(signalement: s),
    );
    if (choix == null || s.id == null) return;
    try {
      await ref.read(gestionRepositoryProvider).changerStatut(s.id!, choix.$1, note: choix.$2);
      ref.invalidate(signalementsGestionProvider);
      messenger.showSnackBar(SnackBar(content: Text(l.signalementMisAJour)));
    } on AppError catch (e) {
      messenger.showSnackBar(SnackBar(content: Text(errorMessage(l, e))));
    }
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final locale = Localizations.localeOf(context).toString();
    final signalements = ref.watch(signalementsGestionProvider(_statut));
    final filtres = <(String?, String)>[
      (null, l.signalementsFiltreTous),
      ('NOUVEAU', l.signalementNouveau),
      ('EN_COURS', l.signalementEnCours),
      ('RESOLU', l.signalementResolu),
      ('REJETE', l.signalementRejete),
    ];
    return Scaffold(
      appBar: AppBar(title: Text(l.signalementsGestionTitre)),
      body: Column(children: [
        SizedBox(
          height: 56,
          child: ListView(scrollDirection: Axis.horizontal, padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10), children: [
            for (final f in filtres)
              Padding(
                padding: const EdgeInsets.only(right: 8),
                child: ChoiceChip(
                  key: Key('filtre-${f.$1 ?? 'tous'}'),
                  label: Text(f.$2),
                  selected: _statut == f.$1,
                  onSelected: (_) => setState(() => _statut = f.$1),
                ),
              ),
          ]),
        ),
        Expanded(
          child: RefreshIndicator(
            onRefresh: () async {
              ref.invalidate(signalementsGestionProvider);
              await ref.read(signalementsGestionProvider(_statut).future).catchError((_) => const <SignalementDto>[]);
            },
            child: signalements.when(
              loading: () => const ListeSquelette(),
              error: (e, _) => ListView(children: [
                ListTile(
                  key: const Key('erreur-signalements-gestion'),
                  leading: const Icon(Icons.error_outline),
                  title: Text(errorMessage(l, e)),
                  trailing: TextButton(onPressed: () => ref.invalidate(signalementsGestionProvider), child: Text(l.reessayer)),
                ),
              ]),
              data: (liste) => liste.isEmpty
                  ? ListView(children: [EtatVide(icone: Icons.build_circle_outlined, texte: l.aucunSignalementGestion, cleTexte: const Key('aucun-signalement-gestion'))])
                  : ListView.builder(
                      padding: const EdgeInsets.symmetric(horizontal: 12),
                      itemCount: liste.length,
                      itemBuilder: (_, i) {
                        final s = liste[i];
                        return Card(
                          child: ListTile(
                            key: Key('signalement-gestion-${s.id}'),
                            title: Text(s.description ?? '', maxLines: 2, overflow: TextOverflow.ellipsis),
                            subtitle: Text('${s.logement ?? ''} · ${formaterDate(s.creeLe, locale)}'),
                            trailing: PastilleStatut(texte: _libelle(l, s.statut), ton: _ton(s.statut)),
                            onTap: () => _ouvrir(s),
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

/// Fiche d'un signalement avec les actions possibles ; renvoie le nouveau statut et la note.
class _Detail extends StatefulWidget {
  const _Detail({required this.signalement});

  final SignalementDto signalement;

  @override
  State<_Detail> createState() => _DetailState();
}

class _DetailState extends State<_Detail> {
  final _note = TextEditingController();

  @override
  void dispose() {
    _note.dispose();
    super.dispose();
  }

  void _choisir(StatutSignalementRequeteStatutEnum statut) => Navigator.pop(context, (statut, _note.text));

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final s = widget.signalement;
    final termine = s.statut == SignalementDtoStatutEnum.RESOLU || s.statut == SignalementDtoStatutEnum.REJETE;
    return Padding(
      padding: EdgeInsets.fromLTRB(20, 0, 20, MediaQuery.of(context).viewInsets.bottom + 20),
      child: SingleChildScrollView(
        child: Column(mainAxisSize: MainAxisSize.min, crossAxisAlignment: CrossAxisAlignment.start, children: [
          Row(children: [
            Expanded(child: Text(s.logement ?? '', style: Theme.of(context).textTheme.titleMedium)),
            PastilleStatut(texte: _libelle(l, s.statut), ton: _ton(s.statut)),
          ]),
          const SizedBox(height: 4),
          Text(l.signalementPar(s.locataireNom ?? '—'), style: Theme.of(context).textTheme.bodySmall),
          const SizedBox(height: 12),
          Text(s.description ?? ''),
          if ((s.noteGestion ?? '').isNotEmpty) ...[
            const SizedBox(height: 8),
            Text(s.noteGestion!, style: Theme.of(context).textTheme.bodySmall),
          ],
          if (!termine) ...[
            const SizedBox(height: 12),
            TextField(key: const Key('note-signalement'), controller: _note, maxLength: 500, decoration: InputDecoration(labelText: l.signalementNote)),
            const SizedBox(height: 8),
            Wrap(spacing: 8, runSpacing: 8, children: [
              if (s.statut == SignalementDtoStatutEnum.NOUVEAU)
                FilledButton(key: const Key('signalement-prendre'), onPressed: () => _choisir(StatutSignalementRequeteStatutEnum.EN_COURS), child: Text(l.signalementPrendre)),
              FilledButton.tonal(key: const Key('signalement-resoudre'), onPressed: () => _choisir(StatutSignalementRequeteStatutEnum.RESOLU), child: Text(l.signalementResoudre)),
              OutlinedButton(key: const Key('signalement-rejeter'), onPressed: () => _choisir(StatutSignalementRequeteStatutEnum.REJETE), child: Text(l.signalementRejeter)),
            ]),
          ],
        ]),
      ),
    );
  }
}
