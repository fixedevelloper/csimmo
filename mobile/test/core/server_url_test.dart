import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/core/config/server_url.dart';

void main() {
  group('ServerUrl.parse', () {
    test('normalise une adresse HTTPS (retire le / final, garde le chemin)', () {
      expect(ServerUrl.parse('https://immo.exemple.cm/'), 'https://immo.exemple.cm');
      expect(ServerUrl.parse('  https://immo.exemple.cm:8443/immogest/ '), 'https://immo.exemple.cm:8443/immogest');
    });

    test('ajoute https:// à un nom de domaine sans schéma', () {
      expect(ServerUrl.parse('immo.exemple.cm'), 'https://immo.exemple.cm');
    });

    test('suppose http:// pour une adresse de réseau local sans schéma', () {
      expect(ServerUrl.parse('192.168.1.20:8080'), 'http://192.168.1.20:8080');
      expect(ServerUrl.parse('serveur.local'), 'http://serveur.local');
    });

    test('accepte http:// pour le réseau local et l\'émulateur', () {
      expect(ServerUrl.parse('http://10.0.2.2:8080'), 'http://10.0.2.2:8080');
      expect(ServerUrl.parse('http://localhost:8080'), 'http://localhost:8080');
      expect(ServerUrl.parse('http://172.20.0.5'), 'http://172.20.0.5');
    });

    test('refuse http:// vers une adresse publique', () {
      for (final adresse in ['http://immo.exemple.cm', 'http://8.8.8.8', 'http://172.32.0.1', 'http://192.169.1.1']) {
        expect(
          () => ServerUrl.parse(adresse),
          throwsA(isA<ServerUrlException>().having((e) => e.error, 'error', ServerUrlError.httpNonLocal)),
          reason: adresse,
        );
      }
    });

    test('refuse les adresses vides ou illisibles', () {
      for (final adresse in ['', '   ', 'ftp://serveur', 'https://', '://x']) {
        expect(
          () => ServerUrl.parse(adresse),
          throwsA(isA<ServerUrlException>().having((e) => e.error, 'error', ServerUrlError.invalide)),
          reason: adresse,
        );
      }
    });

    test('lit le contenu d\'un QR code immogest://serveur?url=…', () {
      final qr = 'immogest://serveur?url=${Uri.encodeComponent('https://immo.exemple.cm')}';
      expect(ServerUrl.parse(qr), 'https://immo.exemple.cm');
    });

    test('un QR code sans adresse ou vers un serveur http public est refusé', () {
      expect(() => ServerUrl.parse('immogest://serveur'), throwsA(isA<ServerUrlException>()));
      expect(
        () => ServerUrl.parse('immogest://serveur?url=${Uri.encodeComponent('http://immo.exemple.cm')}'),
        throwsA(isA<ServerUrlException>()),
      );
    });
  });
}
