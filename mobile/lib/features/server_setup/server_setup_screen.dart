import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/error/error_messages.dart';
import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import 'server_setup_actions.dart';

/// Premier lancement : l'utilisateur indique le serveur de son agence (saisie ou QR code).
class ServerSetupScreen extends ConsumerStatefulWidget {
  const ServerSetupScreen({super.key});

  @override
  ConsumerState<ServerSetupScreen> createState() => _ServerSetupScreenState();
}

class _ServerSetupScreenState extends ConsumerState<ServerSetupScreen> {
  final _controller = TextEditingController();
  bool _busy = false;
  String? _error;

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  Future<void> _valider() async {
    final l = AppLocalizations.of(context);
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      await ref.read(serverSetupActionsProvider).valider(_controller.text);
      if (mounted) context.go('/connexion');
    } catch (e) {
      if (mounted) setState(() => _error = errorMessage(l, e));
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  Future<void> _scanner() async {
    final lu = await context.push<String>('/serveur/scan');
    if (lu == null || !mounted) return;
    _controller.text = lu;
    await _valider();
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    return Scaffold(
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(24),
          children: [
            EnteteMarque(titre: l.serveurTitre, sousTitre: l.serveurExplication),
            TextField(
              key: const Key('champ-serveur'),
              controller: _controller,
              keyboardType: TextInputType.url,
              autocorrect: false,
              decoration: InputDecoration(
                labelText: l.serveurUrlLabel,
                hintText: l.serveurUrlHint,
              ),
              onSubmitted: (_) => _valider(),
            ),
            if (_error != null) ...[
              const SizedBox(height: 12),
              Text(_error!, key: const Key('erreur'), style: TextStyle(color: Theme.of(context).colorScheme.error)),
            ],
            const SizedBox(height: 16),
            FilledButton(
              key: const Key('bouton-continuer'),
              onPressed: _busy ? null : _valider,
              child: Text(_busy ? l.serveurVerification : l.serveurContinuer),
            ),
            const SizedBox(height: 8),
            OutlinedButton.icon(
              key: const Key('bouton-scanner'),
              onPressed: _busy ? null : _scanner,
              icon: const Icon(Icons.qr_code_scanner),
              label: Text(l.serveurScanner),
            ),
          ],
        ),
      ),
    );
  }
}
