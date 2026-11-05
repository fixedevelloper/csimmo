import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:mobile_scanner/mobile_scanner.dart';

import '../../core/config/server_url.dart';
import '../../l10n/app_localizations.dart';

/// Scanne le QR code affiché par l'installation (`immogest://serveur?url=…` ou une adresse simple).
class QrScanScreen extends StatefulWidget {
  const QrScanScreen({super.key});

  @override
  State<QrScanScreen> createState() => _QrScanScreenState();
}

class _QrScanScreenState extends State<QrScanScreen> {
  bool _termine = false;
  bool _invalide = false;

  void _onDetect(BarcodeCapture capture) {
    if (_termine) return;
    for (final code in capture.barcodes) {
      final brut = code.rawValue;
      if (brut == null) continue;
      try {
        ServerUrl.parse(brut);
      } on ServerUrlException {
        if (!_invalide) setState(() => _invalide = true);
        continue;
      }
      _termine = true;
      context.pop(brut);
      return;
    }
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    return Scaffold(
      appBar: AppBar(title: Text(l.serveurScanTitre)),
      body: Stack(
        children: [
          MobileScanner(
            onDetect: _onDetect,
            errorBuilder: (context, erreur) => Center(
              child: Padding(padding: const EdgeInsets.all(24), child: Text(l.serveurCameraRefusee)),
            ),
          ),
          Positioned(
            left: 16,
            right: 16,
            bottom: 32,
            child: Card(
              child: Padding(
                padding: const EdgeInsets.all(12),
                child: Text(_invalide ? l.serveurQrInvalide : l.serveurScanAide, textAlign: TextAlign.center),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
