import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immogest_api/immogest_api.dart';

import '../../core/error/app_error.dart';
import '../../core/error/error_messages.dart';
import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import 'formatage.dart';
import 'locataire_repository.dart';

/// Quittances et reçus : un appui télécharge le PDF (avec le jeton du locataire) puis l'ouvre.
class DocumentsScreen extends ConsumerStatefulWidget {
  const DocumentsScreen({super.key});

  @override
  ConsumerState<DocumentsScreen> createState() => _DocumentsScreenState();
}

class _DocumentsScreenState extends ConsumerState<DocumentsScreen> {
  String? _enCours;

  Future<void> _ouvrir(DocumentDto d) async {
    final l = AppLocalizations.of(context);
    final messenger = ScaffoldMessenger.of(context);
    setState(() => _enCours = d.id);
    try {
      final repo = ref.read(locataireRepositoryProvider);
      final chemin = await repo.telechargerDocument(d.fichierId ?? '', '${d.numero ?? 'document'}.pdf');
      await repo.ouvrir(chemin);
    } on AppError catch (e) {
      messenger.showSnackBar(SnackBar(content: Text(errorMessage(l, e))));
    } finally {
      if (mounted) setState(() => _enCours = null);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final locale = Localizations.localeOf(context).toString();
    final documents = ref.watch(mesDocumentsProvider);
    return RefreshIndicator(
      onRefresh: () async {
        ref.invalidate(mesDocumentsProvider);
        try {
          await ref.read(mesDocumentsProvider.future);
        } catch (_) {}
      },
      child: documents.when(
        loading: () => const ListeSquelette(),
        error: (e, _) => ListView(padding: const EdgeInsets.all(24), children: [
          Text(errorMessage(l, e), key: const Key('erreur-documents'), textAlign: TextAlign.center),
          FilledButton(onPressed: () => ref.invalidate(mesDocumentsProvider), child: Text(l.reessayer)),
        ]),
        data: (liste) => liste.isEmpty
            ? ListView(children: [EtatVide(icone: Icons.receipt_long_outlined, texte: l.aucunDocument, cleTexte: const Key('aucun-document'))])
            : ListView.builder(
                padding: const EdgeInsets.all(16),
                itemCount: liste.length,
                itemBuilder: (_, i) {
                  final d = liste[i];
                  final quittance = d.type == 'QUITTANCE';
                  return Card(child: ListTile(
                    key: Key('document-${d.id}'),
                    leading: Icon(quittance ? Icons.receipt_long : Icons.receipt_outlined),
                    title: Text('${quittance ? l.quittance : l.recu} ${d.numero ?? ''}'),
                    subtitle: Text(quittance && d.periodeDebut != null
                        ? '${formaterDate(d.periodeDebut, locale)} → ${formaterDate(d.periodeFin, locale)} · ${formaterFcfa(d.montant, locale)}'
                        : '${formaterDate(d.dateEmission, locale)} · ${formaterFcfa(d.montant, locale)}'),
                    trailing: _enCours == d.id
                        ? const SizedBox(width: 24, height: 24, child: CircularProgressIndicator(strokeWidth: 2))
                        : const Icon(Icons.download),
                    onTap: _enCours == null ? () => _ouvrir(d) : null,
                  ));
                },
              ),
      ),
    );
  }
}
