import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/db/app_database.dart';
import '../../core/error/app_error.dart';
import '../../core/error/error_messages.dart';
import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import '../auth/auth_repository.dart';
import '../locataire/formatage.dart';
import 'cache_terrain.dart';
import 'file_encaissements.dart';
import 'synchro_automatique.dart';
import 'synchro_encaissements.dart';

/// Saisie d'un encaissement en espèces. Fonctionne sans réseau : la saisie va dans la file locale, envoyée dès que possible.
class EncaisserScreen extends ConsumerStatefulWidget {
  const EncaisserScreen({super.key});

  @override
  ConsumerState<EncaisserScreen> createState() => _EncaisserScreenState();
}

class _EncaisserScreenState extends ConsumerState<EncaisserScreen> {
  final _recherche = TextEditingController();
  final _montant = TextEditingController();
  final _formulaire = GlobalKey<FormState>();
  LocatairesCacheData? _locataire;
  BauxCacheData? _bail;
  List<LocatairesCacheData> _resultats = const [];
  List<BauxCacheData> _baux = const [];
  bool _actualisation = false;
  bool _enregistrement = false;
  String? _erreur;

  @override
  void initState() {
    super.initState();
    _chercher('');
    // au premier affichage, on tente d'actualiser les données (silencieux si hors ligne)
    WidgetsBinding.instance.addPostFrameCallback((_) => _actualiser(silencieux: true));
  }

  @override
  void dispose() {
    _recherche.dispose();
    _montant.dispose();
    super.dispose();
  }

  Future<void> _chercher(String q) async {
    final r = await ref.read(cacheTerrainProvider).chercherLocataires(q);
    if (mounted) setState(() => _resultats = r);
  }

  Future<void> _actualiser({bool silencieux = false}) async {
    final l = AppLocalizations.of(context);
    final messenger = ScaffoldMessenger.of(context);
    setState(() => _actualisation = true);
    try {
      await ref.read(cacheTerrainProvider).actualiser();
      await _chercher(_recherche.text);
      if (!silencieux) messenger.showSnackBar(SnackBar(content: Text(l.donneesActualisees)));
    } on AppError catch (e) {
      if (!silencieux) messenger.showSnackBar(SnackBar(content: Text('${errorMessage(l, e)} ${l.donneesAnciennes}')));
    } finally {
      if (mounted) setState(() => _actualisation = false);
    }
  }

  Future<void> _choisirLocataire(LocatairesCacheData l) async {
    final baux = await ref.read(cacheTerrainProvider).bauxDuLocataire(l.id);
    if (!mounted) return;
    setState(() {
      _locataire = l;
      _baux = baux;
      _bail = baux.length == 1 ? baux.first : null; // un seul bail : choisi d'office
      _erreur = null;
    });
  }

  Future<void> _enregistrer() async {
    final l = AppLocalizations.of(context);
    if (!(_formulaire.currentState?.validate() ?? false) || _locataire == null || _bail == null) {
      setState(() => _erreur = _bail == null ? l.choisirBailErreur : null);
      return;
    }
    final messenger = ScaffoldMessenger.of(context);
    setState(() => _enregistrement = true);
    try {
      final agent = await ref.read(agentIdProvider.future);
      if (agent == null) throw const AppError('NON_AUTHENTIFIE');
      await ref.read(fileEncaissementsProvider).ajouter(
            utilisateurId: agent,
            bailId: _bail!.id,
            bailLibelle: _bail!.libelle,
            locataireNom: _locataire!.nom,
            montant: int.parse(_montant.text.replaceAll(RegExp(r'\s'), '')),
          );
      messenger.showSnackBar(SnackBar(content: Text(l.encaissementEnregistre)));
      setState(() {
        _locataire = null;
        _bail = null;
        _baux = const [];
        _recherche.clear();
        _montant.clear();
      });
      await _chercher('');
      ref.read(synchroAutomatiqueProvider).declencher(); // envoi immédiat si le réseau est là
    } on AppError catch (e) {
      if (mounted) setState(() => _erreur = errorMessage(l, e));
    } finally {
      if (mounted) setState(() => _enregistrement = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final locale = Localizations.localeOf(context).toString();
    final maj = ref.watch(derniereMajProvider).value;
    final utilisateur = ref.watch(currentUserProvider);
    return ListView(padding: const EdgeInsets.all(16), children: [
      utilisateur.maybeWhen(
        data: (u) => Padding(padding: const EdgeInsets.only(bottom: 8), child: Text(l.bienvenue(u.nom ?? ''), key: const Key('bienvenue'), style: Theme.of(context).textTheme.titleMedium)),
        orElse: () => const SizedBox.shrink(),
      ),
      Row(children: [
        Expanded(
          child: Text(maj == null ? l.donneesAbsentes : l.donneesDu(formaterDate(maj, locale)), key: const Key('donnees-du'), style: Theme.of(context).textTheme.bodySmall),
        ),
        TextButton.icon(
          key: const Key('actualiser-donnees'),
          onPressed: _actualisation ? null : () => _actualiser(),
          icon: _actualisation ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth: 2)) : const Icon(Icons.refresh),
          label: Text(l.actualiserDonnees),
        ),
      ]),
      if (_locataire == null) ...[
        TextField(
          key: const Key('recherche-locataire'),
          controller: _recherche,
          decoration: InputDecoration(labelText: l.rechercherLocataire, prefixIcon: const Icon(Icons.search),),
          onChanged: _chercher,
        ),
        const SizedBox(height: 8),
        if (_resultats.isEmpty) EtatVide(icone: Icons.person_search_outlined, texte: maj == null ? l.donneesAbsentesAide : l.aucunLocataire, cleTexte: const Key('aucun-locataire')),
        for (final r in _resultats)
          Card(child: ListTile(key: Key('locataire-${r.id}'), title: Text(r.nom), subtitle: r.telephone == null ? null : Text(r.telephone!), trailing: const Icon(Icons.chevron_right), onTap: () => _choisirLocataire(r))),
      ] else ...[
        Card(
          child: ListTile(
            key: const Key('locataire-choisi'),
            leading: CircleAvatar(backgroundColor: Theme.of(context).colorScheme.primaryContainer, child: Icon(Icons.person, color: Theme.of(context).colorScheme.primary)),
            title: Text(_locataire!.nom, style: Theme.of(context).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w600)),
            trailing: TextButton(onPressed: () => setState(() => _locataire = null), child: Text(l.changer)),
          ),
        ),
        if (_baux.isEmpty)
          Text(l.aucunBailLocataire, key: const Key('aucun-bail-locataire'))
        else
          RadioGroup<String>(
            groupValue: _bail?.id,
            onChanged: (id) => setState(() => _bail = _baux.firstWhere((b) => b.id == id)),
            child: Column(children: [
              for (final b in _baux)
                Card(child: RadioListTile<String>(
                  key: Key('bail-${b.id}'),
                  value: b.id,
                  title: Text(b.libelle),
                  subtitle: Text('${b.numero} · ${l.mensuel(formaterFcfa(b.montantMensuel, locale))}'),
                )),
            ]),
          ),
        Form(
          key: _formulaire,
          child: TextFormField(
            key: const Key('champ-montant'),
            controller: _montant,
            keyboardType: TextInputType.number,
            decoration: InputDecoration(labelText: l.montantFcfa,),
            validator: (v) {
              final n = int.tryParse((v ?? '').replaceAll(RegExp(r'\s'), ''));
              return n == null || n <= 0 ? l.montantInvalide : null;
            },
          ),
        ),
        if (_erreur != null) Padding(padding: const EdgeInsets.only(top: 8), child: Text(_erreur!, key: const Key('erreur-encaissement'), style: TextStyle(color: Theme.of(context).colorScheme.error))),
        const SizedBox(height: 12),
        FilledButton.icon(
          key: const Key('enregistrer-encaissement'),
          onPressed: _enregistrement || _baux.isEmpty ? null : () {
            HapticFeedback.mediumImpact();
            _enregistrer();
          },
          icon: const Icon(Icons.save),
          label: Text(l.enregistrerEncaissement),
        ),
        const SizedBox(height: 8),
        Text(l.recuSmsInfo, key: const Key('info-recu-sms'), style: Theme.of(context).textTheme.bodySmall),
      ],
    ]);
  }
}


/// Pour les tests : relit la file sans passer par l'écran.
Future<BilanSynchro> synchroniserMaintenant(WidgetRef ref) => ref.read(synchroEncaissementsProvider).synchroniser();
