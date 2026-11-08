import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immogest_api/immogest_api.dart';

import '../../core/auth/role.dart';
import '../../core/error/error_messages.dart';
import '../../core/widgets/cle_application.dart';
import '../../l10n/app_localizations.dart';
import '../auth/auth_actions.dart';
import '../auth/auth_repository.dart';

/// Profil de l'utilisateur connecté, ouvert à tous les rôles : informations, mot de passe, double authentification.
class ProfilScreen extends ConsumerWidget {
  const ProfilScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l = AppLocalizations.of(context);
    final utilisateur = ref.watch(currentUserProvider);
    return Scaffold(
      appBar: AppBar(title: Text(l.profilTitre, key: const Key('titre-profil'))),
      body: utilisateur.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(
          child: Column(mainAxisSize: MainAxisSize.min, children: [
            Text(errorMessage(l, e), textAlign: TextAlign.center),
            const SizedBox(height: 12),
            FilledButton(onPressed: () => ref.invalidate(currentUserProvider), child: Text(l.reessayer)),
          ]),
        ),
        data: (u) => ListView(
          padding: const EdgeInsets.all(16),
          children: [
            _InfosPersonnelles(key: ValueKey('${u.id}-${u.nom}-${u.telephone}-${u.email}'), utilisateur: u),
            const SizedBox(height: 16),
            _DoubleAuthentification(utilisateur: u),
            const SizedBox(height: 16),
            const _MotDePasse(),
          ],
        ),
      ),
    );
  }
}

/// Champ mot de passe avec bouton afficher / masquer.
class _ChampMotDePasse extends StatefulWidget {
  const _ChampMotDePasse({required this.cle, required this.libelle, this.erreur, required this.controleur, this.nouveau = false});

  final String cle;
  final String libelle;
  final String? erreur;
  final TextEditingController controleur;
  final bool nouveau;

  @override
  State<_ChampMotDePasse> createState() => _ChampMotDePasseState();
}

class _ChampMotDePasseState extends State<_ChampMotDePasse> {
  bool _visible = false;

  @override
  Widget build(BuildContext context) {
    return TextField(
      key: Key(widget.cle),
      controller: widget.controleur,
      obscureText: !_visible,
      autofillHints: [widget.nouveau ? AutofillHints.newPassword : AutofillHints.password],
      decoration: InputDecoration(
        labelText: widget.libelle,
        errorText: widget.erreur,
        suffixIcon: IconButton(
          key: Key('${widget.cle}-voir'),
          icon: Icon(_visible ? Icons.visibility_off : Icons.visibility),
          onPressed: () => setState(() => _visible = !_visible),
        ),
      ),
    );
  }
}

class _InfosPersonnelles extends ConsumerStatefulWidget {
  const _InfosPersonnelles({super.key, required this.utilisateur});

  final UtilisateurDto utilisateur;

  @override
  ConsumerState<_InfosPersonnelles> createState() => _InfosPersonnellesState();
}

class _InfosPersonnellesState extends ConsumerState<_InfosPersonnelles> {
  late final _nom = TextEditingController(text: widget.utilisateur.nom ?? '');
  late final _telephone = TextEditingController(text: widget.utilisateur.telephone ?? '');
  late final _email = TextEditingController(text: widget.utilisateur.email ?? '');
  final _motDePasse = TextEditingController();
  String? _erreurNom;
  String? _erreurContact;
  String? _erreurMotDePasse;
  String? _erreur;
  bool _envoi = false;

  @override
  void dispose() {
    _nom.dispose();
    _telephone.dispose();
    _email.dispose();
    _motDePasse.dispose();
    super.dispose();
  }

  Future<void> _enregistrer(AppLocalizations l) async {
    final nom = _nom.text.trim();
    final tel = _telephone.text.trim();
    final mail = _email.text.trim();
    setState(() {
      _erreurNom = nom.isEmpty ? l.profilNomRequis : null;
      _erreurContact = tel.isEmpty && mail.isEmpty ? l.profilContactRequis : null;
      _erreurMotDePasse = _motDePasse.text.isEmpty ? l.profilMotDePasseRequis : null;
      _erreur = null;
    });
    if (_erreurNom != null || _erreurContact != null || _erreurMotDePasse != null) return;
    setState(() => _envoi = true);
    try {
      await ref.read(authRepositoryProvider).modifierProfil(
            nom: nom,
            telephone: tel.isEmpty ? null : tel,
            email: mail.isEmpty ? null : mail,
            motDePasse: _motDePasse.text,
          );
      _motDePasse.clear();
      ref.invalidate(currentUserProvider);
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(l.profilEnregistre)));
    } catch (e) {
      if (mounted) setState(() => _erreur = errorMessage(l, e));
    } finally {
      if (mounted) setState(() => _envoi = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(l.profilInfosTitre, style: Theme.of(context).textTheme.titleMedium),
          const SizedBox(height: 4),
          Text(l.profilAideInfos, style: Theme.of(context).textTheme.bodySmall),
          const SizedBox(height: 12),
          TextField(key: const Key('profil-nom'), controller: _nom, decoration: InputDecoration(labelText: l.profilNom, errorText: _erreurNom)),
          TextField(
              key: const Key('profil-telephone'),
              controller: _telephone,
              keyboardType: TextInputType.phone,
              decoration: InputDecoration(labelText: l.profilTelephone, errorText: _erreurContact)),
          TextField(
              key: const Key('profil-email'), controller: _email, keyboardType: TextInputType.emailAddress, decoration: InputDecoration(labelText: l.profilEmail)),
          _ChampMotDePasse(cle: 'profil-mdp-actuel', libelle: l.profilMotDePasseActuel, erreur: _erreurMotDePasse, controleur: _motDePasse),
          if (_erreur != null) ...[
            const SizedBox(height: 8),
            Text(_erreur!, key: const Key('profil-erreur'), style: TextStyle(color: Theme.of(context).colorScheme.error)),
          ],
          const SizedBox(height: 12),
          FilledButton(key: const Key('profil-enregistrer'), onPressed: _envoi ? null : () => _enregistrer(l), child: Text(l.profilEnregistrer)),
        ]),
      ),
    );
  }
}

enum _Etape { repos, motDePasse, configuration, desactiver }

/// Double authentification par application (Google Authenticator, Microsoft Authenticator, Authy…) : on la configure en saisissant la clé puis le
/// premier code, on la désactive avec le mot de passe et un code. Obligatoire pour les employés restreints. Aucun SMS.
class _DoubleAuthentification extends ConsumerStatefulWidget {
  const _DoubleAuthentification({required this.utilisateur});

  final UtilisateurDto utilisateur;

  @override
  ConsumerState<_DoubleAuthentification> createState() => _DoubleAuthentificationState();
}

class _DoubleAuthentificationState extends ConsumerState<_DoubleAuthentification> {
  static const _roles = {Role.admin, Role.gestionnaire, Role.encaisseur, Role.gestionnaireBiens, Role.comptable};

  _Etape _etape = _Etape.repos;
  final _motDePasse = TextEditingController();
  final _code = TextEditingController();
  Enrolement? _enrolement;
  String? _erreur;
  bool _envoi = false;

  @override
  void dispose() {
    _motDePasse.dispose();
    _code.dispose();
    super.dispose();
  }

  void _annuler() => setState(() {
        _etape = _Etape.repos;
        _motDePasse.clear();
        _code.clear();
        _enrolement = null;
        _erreur = null;
      });

  Future<void> _agir(AppLocalizations l, Future<void> Function() action) async {
    setState(() {
      _erreur = null;
      _envoi = true;
    });
    try {
      await action();
    } catch (e) {
      if (mounted) setState(() => _erreur = errorMessage(l, e));
    } finally {
      if (mounted) setState(() => _envoi = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final role = Role.fromCode(widget.utilisateur.role?.value);
    final concerne = role != null && _roles.contains(role);
    // Aucune section pour un rôle sans double authentification (locataire, prestataire, propriétaire).
    if (!concerne) return const SizedBox.shrink();
    final active = widget.utilisateur.deuxFacteursActif == true;
    final configuree = active && widget.utilisateur.deuxFacteursConfigure == true;
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Row(children: [
            Expanded(child: Text(l.deuxTitre, style: Theme.of(context).textTheme.titleMedium)),
            Chip(key: const Key('deux-etat'), label: Text(!active ? l.deuxInactive : configuree ? l.deuxActive : l.deuxAConfigurer)),
          ]),
          Text(configuree ? l.deuxAideActive : active ? l.deuxAideAConfigurer : l.deuxAideInactive, style: Theme.of(context).textTheme.bodySmall),
          const SizedBox(height: 8),
          if (_etape == _Etape.repos) ...[
            if (!configuree)
              FilledButton(
                key: const Key('deux-configurer'),
                onPressed: () => setState(() => _etape = _Etape.motDePasse),
                child: Text(active ? l.deuxConfigurerMaintenant : l.deuxConfigurer),
              ),
            if (configuree)
              OutlinedButton(key: const Key('deux-desactiver'), onPressed: () => setState(() => _etape = _Etape.desactiver), child: Text(l.deuxDesactiver)),
          ],
          if (_etape == _Etape.motDePasse || _etape == _Etape.desactiver) ...[
            _ChampMotDePasse(cle: 'deux-mdp', libelle: l.profilMotDePasseActuel, controleur: _motDePasse),
            if (_etape == _Etape.desactiver)
              TextField(
                key: const Key('deux-code-desactiver'),
                controller: _code,
                keyboardType: TextInputType.number,
                maxLength: 6,
                decoration: InputDecoration(labelText: l.deuxCodeApplication),
                onChanged: (_) => setState(() {}),
              ),
            const SizedBox(height: 8),
            Row(children: [
              FilledButton(
                key: const Key('deux-valider-mdp'),
                onPressed: _envoi || (_etape == _Etape.desactiver && _code.text.length != 6)
                    ? null
                    : () => _agir(l, () async {
                          final repo = ref.read(authRepositoryProvider);
                          final messager = ScaffoldMessenger.of(context);
                          if (_etape == _Etape.motDePasse) {
                            final e = await repo.demarrerDeuxFacteurs(_motDePasse.text);
                            _motDePasse.clear();
                            if (mounted) {
                              setState(() {
                                _enrolement = e;
                                _etape = _Etape.configuration;
                              });
                            }
                          } else {
                            await repo.desactiverDeuxFacteurs(_motDePasse.text, _code.text);
                            ref.invalidate(currentUserProvider);
                            messager.showSnackBar(SnackBar(content: Text(l.deuxDesactivee)));
                            _annuler();
                          }
                        }),
                child: Text(_etape == _Etape.motDePasse ? l.deuxContinuer : l.deuxDesactiver),
              ),
              TextButton(onPressed: _annuler, child: Text(l.deuxAnnuler)),
            ]),
          ],
          if (_etape == _Etape.configuration && _enrolement != null) ...[
            CleApplication(secret: _enrolement!.secret),
            TextField(
              key: const Key('deux-code'),
              controller: _code,
              keyboardType: TextInputType.number,
              maxLength: 6,
              decoration: InputDecoration(labelText: l.deuxCodeApplication),
              onChanged: (_) => setState(() {}),
            ),
            Row(children: [
              FilledButton(
                key: const Key('deux-confirmer'),
                onPressed: _envoi || _code.text.length != 6
                    ? null
                    : () => _agir(l, () async {
                          final messager = ScaffoldMessenger.of(context);
                          await ref.read(authRepositoryProvider).activerDeuxFacteurs(_code.text);
                          ref.invalidate(currentUserProvider);
                          messager.showSnackBar(SnackBar(content: Text(l.deuxActivee)));
                          _annuler();
                        }),
                child: Text(l.deuxConfirmerActivation),
              ),
              TextButton(onPressed: _annuler, child: Text(l.deuxAnnuler)),
            ]),
          ],
          if (_erreur != null) Padding(padding: const EdgeInsets.only(top: 8), child: Text(_erreur!, key: const Key('deux-erreur'), style: TextStyle(color: Theme.of(context).colorScheme.error))),
        ]),
      ),
    );
  }
}

class _MotDePasse extends ConsumerStatefulWidget {
  const _MotDePasse();

  @override
  ConsumerState<_MotDePasse> createState() => _MotDePasseState();
}

class _MotDePasseState extends ConsumerState<_MotDePasse> {
  final _ancien = TextEditingController();
  final _nouveau = TextEditingController();
  final _confirmation = TextEditingController();
  String? _erreurAncien;
  String? _erreurNouveau;
  String? _erreurConfirmation;
  String? _erreur;
  bool _envoi = false;

  @override
  void dispose() {
    _ancien.dispose();
    _nouveau.dispose();
    _confirmation.dispose();
    super.dispose();
  }

  Future<void> _changer(AppLocalizations l) async {
    setState(() {
      _erreurAncien = _ancien.text.isEmpty ? l.profilMotDePasseRequis : null;
      _erreurNouveau = _nouveau.text.length < 12
          ? l.mdpCourt
          : _nouveau.text == _ancien.text
              ? l.mdpIdentique
              : null;
      _erreurConfirmation = _confirmation.text != _nouveau.text ? l.mdpDifferent : null;
      _erreur = null;
    });
    if (_erreurAncien != null || _erreurNouveau != null || _erreurConfirmation != null) return;
    setState(() => _envoi = true);
    try {
      await ref.read(authRepositoryProvider).changerMotDePasse(_ancien.text, _nouveau.text);
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(l.mdpChange)));
      // Le serveur a fermé toutes les sessions : on referme aussi la session locale, ce qui ramène à la connexion.
      await ref.read(authActionsProvider).logout();
    } catch (e) {
      if (mounted) setState(() => _erreur = errorMessage(l, e));
    } finally {
      if (mounted) setState(() => _envoi = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(l.mdpTitre, style: Theme.of(context).textTheme.titleMedium),
          Text(l.mdpAide, style: Theme.of(context).textTheme.bodySmall),
          _ChampMotDePasse(cle: 'mdp-ancien', libelle: l.profilMotDePasseActuel, erreur: _erreurAncien, controleur: _ancien),
          _ChampMotDePasse(cle: 'mdp-nouveau', libelle: l.mdpNouveau, erreur: _erreurNouveau, controleur: _nouveau, nouveau: true),
          _ChampMotDePasse(cle: 'mdp-confirmation', libelle: l.mdpConfirmation, erreur: _erreurConfirmation, controleur: _confirmation, nouveau: true),
          if (_erreur != null) ...[
            const SizedBox(height: 8),
            Text(_erreur!, key: const Key('mdp-erreur'), style: TextStyle(color: Theme.of(context).colorScheme.error)),
          ],
          const SizedBox(height: 12),
          FilledButton(key: const Key('mdp-changer'), onPressed: _envoi ? null : () => _changer(l), child: Text(l.mdpChanger)),
        ]),
      ),
    );
  }
}
