import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/error/error_messages.dart';
import '../../core/providers.dart';
import '../../l10n/app_localizations.dart';
import 'auth_actions.dart';

enum _Etape { motDePasse, smsIdentifiant, code }

/// Connexion : mot de passe (gestionnaire, propriétaire), code SMS (locataire, prestataire),
/// et code SMS en seconde étape quand la double authentification est activée.
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
  bool _busy = false;
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
        final ouverte = await actions.login(_identifiant.text, _motDePasse.text);
        if (!ouverte && mounted) _aller(_Etape.code); // 2FA : le serveur a envoyé un code
      });

  Future<void> _demanderCode() => _executer(() async {
        await ref.read(authActionsProvider).demanderOtp(_identifiant.text);
        if (mounted) {
          _aller(_Etape.code);
          setState(() => _info = AppLocalizations.of(context).codeEnvoye);
        }
      });

  Future<void> _verifier() => _executer(() => ref.read(authActionsProvider).verifierOtp(_identifiant.text, _code.text));

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
      appBar: AppBar(title: Text(l.connexionTitre)),
      body: SafeArea(
        child: Form(
          key: _formKey,
          child: ListView(
            padding: const EdgeInsets.all(24),
            children: [
              Text(l.connexionServeur(serveur), style: Theme.of(context).textTheme.bodySmall),
              const SizedBox(height: 16),
              if (_etape == _Etape.code) ...[
                Text(_info ?? l.deuxFacteursInfo),
                const SizedBox(height: 16),
                TextFormField(
                  key: const Key('champ-code'),
                  controller: _code,
                  keyboardType: TextInputType.number,
                  autofillHints: const [AutofillHints.oneTimeCode],
                  decoration: InputDecoration(labelText: l.codeLabel, border: const OutlineInputBorder()),
                  validator: (v) => RegExp(r'^\d{6}$').hasMatch((v ?? '').trim()) ? null : l.codeSixChiffres,
                  onFieldSubmitted: (_) => _verifier(),
                ),
              ] else ...[
                TextFormField(
                  key: const Key('champ-identifiant'),
                  controller: _identifiant,
                  keyboardType: TextInputType.emailAddress,
                  autocorrect: false,
                  autofillHints: const [AutofillHints.username],
                  decoration: InputDecoration(labelText: l.identifiantLabel, border: const OutlineInputBorder()),
                  validator: (v) => _requis(v, l),
                ),
                if (_etape == _Etape.motDePasse) ...[
                  const SizedBox(height: 16),
                  TextFormField(
                    key: const Key('champ-mot-de-passe'),
                    controller: _motDePasse,
                    obscureText: true,
                    autofillHints: const [AutofillHints.password],
                    decoration: InputDecoration(labelText: l.motDePasseLabel, border: const OutlineInputBorder()),
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
                      },
                child: Text(switch (_etape) {
                  _Etape.motDePasse => _busy ? l.connexionEnCours : l.seConnecter,
                  _Etape.smsIdentifiant => l.recevoirCode,
                  _Etape.code => l.verifier,
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
                  onPressed: _busy ? null : () => _aller(_Etape.motDePasse),
                  child: Text(_etape == _Etape.code ? l.retour : l.connexionParMotDePasse),
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
