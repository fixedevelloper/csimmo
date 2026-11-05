import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immogest_api/immogest_api.dart';

import '../../core/error/app_error.dart';
import '../../core/error/error_messages.dart';
import '../../core/platform/plateforme.dart';
import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import 'formatage.dart';
import 'locataire_repository.dart';

String _statutSignalement(AppLocalizations l, SignalementDtoStatutEnum? s) => switch (s) {
      SignalementDtoStatutEnum.EN_COURS => l.signalementEnCours,
      SignalementDtoStatutEnum.RESOLU => l.signalementResolu,
      SignalementDtoStatutEnum.REJETE => l.signalementRejete,
      _ => l.signalementNouveau,
    };

Ton _tonSignalement(SignalementDtoStatutEnum? s) => switch (s) {
      SignalementDtoStatutEnum.EN_COURS => Ton.info,
      SignalementDtoStatutEnum.RESOLU => Ton.succes,
      SignalementDtoStatutEnum.REJETE => Ton.neutre,
      _ => Ton.danger,
    };

/// Signalement d'une panne (description + photo facultative) et suivi des signalements déjà faits.
class SignalerScreen extends ConsumerStatefulWidget {
  const SignalerScreen({super.key});

  @override
  ConsumerState<SignalerScreen> createState() => _SignalerScreenState();
}

class _SignalerScreenState extends ConsumerState<SignalerScreen> {
  final _description = TextEditingController();
  final _formulaire = GlobalKey<FormState>();
  PhotoChoisie? _photo;
  bool _envoi = false;
  String? _erreur;

  @override
  void dispose() {
    _description.dispose();
    super.dispose();
  }

  Future<void> _choisir(SourcePhoto source) async {
    final photo = await ref.read(selecteurPhotoProvider).choisir(source);
    if (photo != null) setState(() => _photo = photo);
  }

  Future<void> _envoyer() async {
    final l = AppLocalizations.of(context);
    if (!(_formulaire.currentState?.validate() ?? false)) return;
    final messenger = ScaffoldMessenger.of(context);
    setState(() {
      _envoi = true;
      _erreur = null;
    });
    try {
      await ref.read(locataireRepositoryProvider).signalerPanne(description: _description.text, photo: _photo);
      _description.clear();
      ref.invalidate(mesSignalementsProvider);
      if (!mounted) return;
      setState(() => _photo = null);
      messenger.showSnackBar(SnackBar(content: Text(l.signalementEnvoye)));
    } on AppError catch (e) {
      if (mounted) setState(() => _erreur = errorMessage(l, e));
    } on Exception catch (e) {
      // photo devenue illisible (fichier supprimé, stockage plein…) : message générique, le texte saisi est conservé
      if (mounted) setState(() => _erreur = errorMessage(l, e));
    } finally {
      if (mounted) setState(() => _envoi = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final locale = Localizations.localeOf(context).toString();
    final precedents = ref.watch(mesSignalementsProvider);
    return ListView(padding: const EdgeInsets.all(16), children: [
      Text(l.signalerTitre, style: Theme.of(context).textTheme.titleMedium),
      const SizedBox(height: 8),
      Form(
        key: _formulaire,
        child: TextFormField(
          key: const Key('champ-description'),
          controller: _description,
          minLines: 3,
          maxLines: 6,
          maxLength: 2000,
          decoration: InputDecoration(labelText: l.signalerDescription,),
          validator: (v) => (v ?? '').trim().length < 5 ? l.signalerDescriptionCourte : null,
        ),
      ),
      Row(children: [
        OutlinedButton.icon(key: const Key('photo-camera'), onPressed: _envoi ? null : () => _choisir(SourcePhoto.camera), icon: const Icon(Icons.photo_camera), label: Text(l.photoCamera)),
        const SizedBox(width: 8),
        OutlinedButton.icon(key: const Key('photo-galerie'), onPressed: _envoi ? null : () => _choisir(SourcePhoto.galerie), icon: const Icon(Icons.photo_library), label: Text(l.photoGalerie)),
      ]),
      if (_photo != null)
        ListTile(
          key: const Key('photo-choisie'),
          contentPadding: EdgeInsets.zero,
          leading: const Icon(Icons.image),
          title: Text(_photo!.nom, overflow: TextOverflow.ellipsis),
          trailing: IconButton(tooltip: l.retirerPhoto, icon: const Icon(Icons.close), onPressed: () => setState(() => _photo = null)),
        ),
      if (_erreur != null) Padding(padding: const EdgeInsets.only(top: 8), child: Text(_erreur!, key: const Key('erreur-signalement'), style: TextStyle(color: Theme.of(context).colorScheme.error))),
      const SizedBox(height: 12),
      FilledButton(key: const Key('envoyer-signalement'), onPressed: _envoi ? null : _envoyer, child: Text(_envoi ? l.envoiEnCours : l.signalerEnvoyer)),
      const SizedBox(height: 28),
      Text(l.mesSignalements, style: Theme.of(context).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w700)),
      const SizedBox(height: 8),
      precedents.when(
        loading: () => const Padding(padding: EdgeInsets.symmetric(vertical: 12), child: Squelette(hauteur: 64, rayon: 16)),
        error: (e, _) => Text(errorMessage(l, e), key: const Key('erreur-signalements')),
        data: (liste) => liste.isEmpty
            ? Padding(padding: const EdgeInsets.all(8), child: Text(l.aucunSignalement, key: const Key('aucun-signalement')))
            : Column(children: [
                for (final s in liste)
                  Card(child: ListTile(
                    key: Key('signalement-${s.id}'),
                    title: Text(s.description ?? '', maxLines: 2, overflow: TextOverflow.ellipsis),
                    subtitle: Text('${formaterDate(s.creeLe, locale)}${s.noteGestion == null ? '' : ' · ${s.noteGestion}'}'),
                    trailing: PastilleStatut(texte: _statutSignalement(l, s.statut), ton: _tonSignalement(s.statut)),
                  )),
              ]),
      ),
    ]);
  }
}
