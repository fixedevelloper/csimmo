import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../l10n/app_localizations.dart';

/// Clé à saisir dans l'application d'authentification (Google Authenticator, Microsoft Authenticator, Authy…), affichée par groupes de 4
/// avec un bouton pour la copier. Sur le téléphone même, on ne peut pas scanner son propre écran : la saisie (ou le collage) de la clé est le chemin.
class CleApplication extends StatelessWidget {
  const CleApplication({super.key, required this.secret});

  final String secret;

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final groupes = RegExp('.{1,4}').allMatches(secret).map((m) => m.group(0)).join(' ');
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(l.enrolementEtapes, key: const Key('enrolement-etapes')),
          const SizedBox(height: 12),
          Text(l.enrolementCle, style: Theme.of(context).textTheme.bodySmall),
          const SizedBox(height: 4),
          SelectableText(groupes, key: const Key('cle-application'), style: const TextStyle(fontFamily: 'monospace', fontSize: 18, letterSpacing: 1.5)),
          const SizedBox(height: 8),
          OutlinedButton.icon(
            key: const Key('copier-cle'),
            icon: const Icon(Icons.copy, size: 18),
            label: Text(l.enrolementCopier),
            onPressed: () async {
              final messager = ScaffoldMessenger.of(context);
              await Clipboard.setData(ClipboardData(text: secret));
              messager.showSnackBar(SnackBar(content: Text(l.enrolementCopiee)));
            },
          ),
        ]),
      ),
    );
  }
}
