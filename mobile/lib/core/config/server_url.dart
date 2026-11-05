/// Raisons pour lesquelles une adresse de serveur est refusée.
enum ServerUrlError {
  /// Texte vide ou adresse illisible.
  invalide,

  /// `http://` vers une adresse publique : les données et les jetons circuleraient en clair sur Internet.
  httpNonLocal,
}

class ServerUrlException implements Exception {
  const ServerUrlException(this.error);

  final ServerUrlError error;

  @override
  String toString() => 'ServerUrlException($error)';
}

/// Adresse du serveur ImmoGest du client (une installation = un serveur).
///
/// Accepte : une adresse saisie (`https://immo.exemple.cm`, `192.168.1.20:8080`, avec ou sans schéma) ou le
/// contenu d'un QR code `immogest://serveur?url=<adresse encodée>` (format produit par `install.sh`).
/// Le résultat est normalisé : schéma + hôte (+ port, + chemin), sans `/` final.
abstract final class ServerUrl {
  static const _qrScheme = 'immogest';

  /// Normalise [saisie] ou lève [ServerUrlException].
  static String parse(String saisie) {
    var texte = saisie.trim();
    if (texte.isEmpty) throw const ServerUrlException(ServerUrlError.invalide);

    if (texte.toLowerCase().startsWith('$_qrScheme://')) {
      final cible = Uri.tryParse(texte)?.queryParameters['url'];
      if (cible == null) throw const ServerUrlException(ServerUrlError.invalide);
      texte = cible.trim();
    }
    if (!texte.contains('://')) {
      // Sans schéma : HTTPS, sauf pour une adresse de réseau local où l'on suppose HTTP (installation sur PC/LAN).
      final hote = Uri.tryParse('//$texte')?.host ?? '';
      texte = '${estPrive(hote) ? 'http' : 'https'}://$texte';
    }

    final uri = Uri.tryParse(texte);
    if (uri == null || !uri.hasAuthority || uri.host.isEmpty || !(uri.scheme == 'http' || uri.scheme == 'https')) {
      throw const ServerUrlException(ServerUrlError.invalide);
    }
    if (uri.scheme == 'http' && !estPrive(uri.host)) {
      throw const ServerUrlException(ServerUrlError.httpNonLocal);
    }
    final chemin = uri.path.endsWith('/') ? uri.path.substring(0, uri.path.length - 1) : uri.path;
    return Uri(
      scheme: uri.scheme,
      host: uri.host,
      port: uri.hasPort ? uri.port : null,
      path: chemin,
    ).toString();
  }

  /// Hôte joignable seulement depuis le réseau local (HTTP en clair toléré).
  static bool estPrive(String hote) {
    final h = hote.toLowerCase();
    if (h == 'localhost' || h.endsWith('.local') || h.endsWith('.lan') || h.endsWith('.home')) return true;
    final octets = h.split('.');
    if (octets.length != 4) return false;
    final n = octets.map(int.tryParse).toList();
    if (n.any((o) => o == null || o < 0 || o > 255)) return false;
    final [a, b, ...] = n.cast<int>();
    return a == 10 || a == 127 || (a == 172 && b >= 16 && b <= 31) || (a == 192 && b == 168);
  }
}
