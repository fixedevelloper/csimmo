import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/error/error_messages.dart';
import '../../core/providers.dart';
import '../../core/widgets/cle_application.dart';
import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import 'auth_actions.dart';
import 'auth_repository.dart';

enum _Etape { motDePasse, smsIdentifiant, code, codeApplication, enrolement }

/// Connexion : mot de passe (gestionnaire, propriétaire), code SMS (locataire, prestataire) ; pour les comptes qui ont la double authentification,
/// le code de l'application d'authentification en seconde étape, ou sa configuration à la première connexion (aucun SMS).
class LoginScreen extends ConsumerStatefulWidget {
  const LoginScreen({super.key});

  @override
  ConsumerState<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends ConsumerState<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _identifiant = TextEditingController();
  final _motDePasse = TextEditingController();
  final _code = TextEditingController();

  _Etape _etape = _Etape.motDePasse;
  String _defi = '';
  Enrolement? _enrolement;
  bool _busy = false;
  bool _motDePasseVisible = false;
  String? _error;
  String? _info;

  @override
  void dispose() {
    _identifiant.dispose();
    _motDePasse.dispose();
    _code.dispose();
    super.dispose();
  }

  void _aller(_Etape etape) => setState(() {
        _etape = etape;
        _error = null;
        _info = null;
        _code.clear();
      });

  /// Exécute [action] en gérant l'état d'attente et l'affichage de l'erreur traduite.
  Future<void> _executer(Future<void> Function() action) async {
    if (!_formKey.currentState!.validate()) return;
    final l = AppLocalizations.of(context);
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      await action();
    } catch (e) {
      if (mounted) setState(() => _error = errorMessage(l, e));
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  Future<void> _connecter() => _executer(() async {
        final actions = ref.read(authActionsProvider);
        final resultat = await actions.login(_identifiant.text, _motDePasse.text);
        if (!mounted) return;
        switch (resultat) {
          case SessionOuverte():
            break; // la redirection par rôle se fait via la session
          case DeuxFacteursRequis(:final defi):
            _defi = defi;
            _aller(_Etape.codeApplication);
          case EnrolementRequis(:final defi):
            // première connexion d'un compte qui doit avoir une application : on la configure avant d'ouvrir la session
            final e = await actions.demarrerEnrolement(defi);
            if (!mounted) return;
            _defi = defi;
            _enrolement = e;
            _aller(_Etape.enrolement);
        }
      });

  Future<void> _demanderCode() => _executer(() async {
        await ref.read(authActionsProvider).demanderOtp(_identifiant.text);
        if (mounted) {
          _aller(_Etape.code);
          setState(() => _info = AppLocalizations.of(context).codeEnvoye);
        }
      });

  Future<void> _verifier() => _executer(() => ref.read(authActionsProvider).verifierOtp(_identifiant.text, _code.text));

  Future<void> _verifierApplication() => _executer(() => ref.read(authActionsProvider).verifierDeuxFacteurs(_defi, _code.text));

  Future<void> _confirmerEnrolement() => _executer(() => ref.read(authActionsProvider).confirmerEnrolement(_defi, _code.text));

  void _retourMotDePasse() {
    _defi = '';
    _enrolement = null;
    _aller(_Etape.motDePasse);
  }

  Future<void> _changerServeur() async {
    await ref.read(serverSettingsProvider.notifier).forget();
    if (mounted) context.go('/serveur');
  }

  String? _requis(String? v, AppLocalizations l) => (v == null || v.trim().isEmpty) ? l.champRequis : null;

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final serveur = ref.watch(serverSettingsProvider) ?? '';
    return Scaffold(
      body: SafeArea(
        child: Form(
          key: _formKey,
          child: ListView(
            padding: const EdgeInsets.all(24),
            children: [
              EnteteMarque(titre: l.connexionTitre, sousTitre: l.connexionServeur(serveur)),
              if (_etape == _Etape.enrolement && _enrolement != null) ...[
                Text(l.enrolementTitre, style: Theme.of(context).textTheme.titleMedium),
                const SizedBox(height: 8),
                CleApplication(secret: _enrolement!.secret),
                const SizedBox(height: 16),
              ],
              if (_etape == _Etape.code || _etape == _Etape.codeApplication || _etape == _Etape.enrolement) ...[
                if (_etape != _Etape.enrolement) Text(_info ?? l.deuxFacteursInfo),
                const SizedBox(height: 16),
                TextFormField(
                  key: const Key('champ-code'),
                  controller: _code,
                  keyboardType: TextInputType.number,
                  autofillHints: const [AutofillHints.oneTimeCode],
                  decoration: InputDecoration(labelText: _etape == _Etape.code ? l.codeLabel : l.codeApplicationLabel),
                  validator: (v) => RegExp(r'^\d{6}$').hasMatch((v ?? '').trim()) ? null : l.codeSixChiffres,
                  onFieldSubmitted: (_) => switch (_etape) {
                    _Etape.codeApplication => _verifierApplication(),
                    _Etape.enrolement => _confirmerEnrolement(),
                    _ => _verifier(),
                  },
                ),
              ] else ...[
                TextFormField(
                  key: const Key('champ-identifiant'),
                  controller: _identifiant,
                  keyboardType: TextInputType.emailAddress,
                  autocorrect: false,
                  autofillHints: const [AutofillHints.username],
                  decoration: InputDecoration(labelText: l.identifiantLabel,),
                  validator: (v) => _requis(v, l),
                ),
                if (_etape == _Etape.motDePasse) ...[
                  const SizedBox(height: 16),
                  TextFormField(
                    key: const Key('champ-mot-de-passe'),
                    controller: _motDePasse,
                    obscureText: !_motDePasseVisible,
                    autofillHints: const [AutofillHints.password],
                    decoration: InputDecoration(
                      labelText: l.motDePasseLabel,
                      suffixIcon: IconButton(
                        key: const Key('bouton-voir-mot-de-passe'),
                        tooltip: _motDePasseVisible ? l.masquerMotDePasse : l.afficherMotDePasse,
                        icon: Icon(_motDePasseVisible ? Icons.visibility_off : Icons.visibility),
                        onPressed: () => setState(() => _motDePasseVisible = !_motDePasseVisible),
                      ),
                    ),
                    validator: (v) => (v == null || v.isEmpty) ? l.champRequis : null,
                    onFieldSubmitted: (_) => _connecter(),
                  ),
                ],
              ],
              if (_error != null) ...[
                const SizedBox(height: 12),
                Text(_error!, key: const Key('erreur'), style: TextStyle(color: Theme.of(context).colorScheme.error)),
              ],
              const SizedBox(height: 16),
              FilledButton(
                key: const Key('bouton-principal'),
                onPressed: _busy
                    ? null
                    : switch (_etape) {
                        _Etape.motDePasse => _connecter,
                        _Etape.smsIdentifiant => _demanderCode,
                        _Etape.code => _verifier,
                        _Etape.codeApplication => _verifierApplication,
                        _Etape.enrolement => _confirmerEnrolement,
                      },
                child: Text(switch (_etape) {
                  _Etape.motDePasse => _busy ? l.connexionEnCours : l.seConnecter,
                  _Etape.smsIdentifiant => l.recevoirCode,
                  _Etape.code || _Etape.codeApplication => l.verifier,
                  _Etape.enrolement => l.enrolementConfirmer,
                }),
              ),
              const SizedBox(height: 8),
              if (_etape == _Etape.motDePasse)
                TextButton(
                  key: const Key('bouton-mode-sms'),
                  onPressed: _busy ? null : () => _aller(_Etape.smsIdentifiant),
                  child: Text(l.connexionParSms),
                )
              else
                TextButton(
                  key: const Key('bouton-retour'),
                  onPressed: _busy ? null : _retourMotDePasse,
                  child: Text(_etape == _Etape.smsIdentifiant ? l.connexionParMotDePasse : l.retour),
                ),
              TextButton(
                key: const Key('bouton-changer-serveur'),
                onPressed: _busy ? null : _changerServeur,
                child: Text(l.changerServeur),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
