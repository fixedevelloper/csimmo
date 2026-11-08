import 'dart:convert';
import 'dart:io';

/// Compte du faux backend.
class FakeUser {
  FakeUser({required this.nom, required this.role, this.telephone, this.email, this.motDePasse, this.deuxFacteurs = false});

  final String nom;
  final String role;
  final String? telephone;
  final String? email;
  final String? motDePasse;
  final bool deuxFacteurs;
}

/// Faux serveur ImmoGest (vraie socket locale, vrai Dio côté app) : health, auth, OTP, refresh avec rotation.
class FakeBackend {
  FakeBackend._(this._server);

  static const codeOtp = '123456';
  final _defis = <String, dynamic>{};

  final HttpServer _server;
  final Map<String, FakeUser> _utilisateurs = {};
  final Map<String, FakeUser> _acces = {};
  final Map<String, FakeUser> _refreshActifs = {};
  final Set<String> _refreshRevoques = {};
  final Set<String> _otpDemandes = {};
  int _compteur = 0;

  int refreshAppeles = 0;
  int loginsAppeles = 0;

  String get url => 'http://127.0.0.1:${_server.port}';

  static Future<FakeBackend> demarrer() async {
    final serveur = await HttpServer.bind(InternetAddress.loopbackIPv4, 0);
    final backend = FakeBackend._(serveur);
    serveur.listen(backend._traiter);
    return backend;
  }

  Future<void> arreter() => _server.close(force: true);

  void ajouter(FakeUser u) {
    for (final id in [u.telephone, u.email]) {
      if (id != null) _utilisateurs[id.toLowerCase()] = u;
    }
  }

  /// Invalide tous les jetons d'accès émis (le prochain appel authentifié répond 401 TOKEN_EXPIRE).
  void expirerLesJetonsDAcces() => _acces.clear();

  /// Émet une session déjà ouverte (pour simuler une application relancée) : (access, refresh).
  (String, String) ouvrirSession(FakeUser u, {bool accesExpire = false}) {
    final access = 'acces-${++_compteur}';
    final refresh = 'refresh-${++_compteur}';
    if (!accesExpire) _acces[access] = u;
    _refreshActifs[refresh] = u;
    return (access, refresh);
  }

  Future<void> _traiter(HttpRequest req) async {
    final corps = req.method == 'POST' ? await utf8.decoder.bind(req).join() : '';
    final json = corps.isEmpty ? <String, dynamic>{} : jsonDecode(corps) as Map<String, dynamic>;
    final chemin = req.uri.path;
    try {
      switch ((req.method, chemin)) {
        case ('GET', '/api/v1/health'):
          return await _json(req, 200, {'status': 'UP'});
        case ('POST', '/api/v1/auth/login'):
          loginsAppeles++;
          final u = _utilisateurs[(json['identifiant'] as String? ?? '').toLowerCase()];
          if (u == null || u.motDePasse != json['motDePasse']) {
            return await _probleme(req, 401, 'IDENTIFIANTS_INVALIDES');
          }
          if (u.deuxFacteurs) {
            _defis['defi-${u.motDePasse}'] = u;
            return await _json(req, 200, {'deuxFacteursRequis': true, 'defi': 'defi-${u.motDePasse}'});
          }
          return await _json(req, 200, _session(u));
        case ('POST', '/api/v1/auth/deux-facteurs/verifier'):
          final u = _defis[json['defi']];
          if (u == null || json['code'] != codeOtp) return await _probleme(req, 401, 'OTP_INVALIDE');
          _defis.remove(json['defi']);
          return await _json(req, 200, _session(u));
        case ('POST', '/api/v1/auth/otp/demander'):
          final id = (json['identifiant'] as String? ?? '').toLowerCase();
          if (_utilisateurs[id] != null && !(_utilisateurs[id]!.motDePasse != null && _utilisateurs[id]!.role == 'GESTIONNAIRE')) {
            _otpDemandes.add(id);
          }
          return await _json(req, 202, null); // ne révèle jamais si le compte existe
        case ('POST', '/api/v1/auth/otp/verifier'):
          final id = (json['identifiant'] as String? ?? '').toLowerCase();
          final u = _utilisateurs[id];
          if (u == null || !_otpDemandes.contains(id) || json['code'] != codeOtp) {
            return await _probleme(req, 401, 'OTP_INVALIDE');
          }
          _otpDemandes.remove(id);
          return await _json(req, 200, _session(u));
        case ('POST', '/api/v1/auth/refresh'):
          refreshAppeles++;
          final jeton = json['refreshToken'] as String? ?? '';
          if (_refreshRevoques.contains(jeton)) {
            _refreshActifs.clear(); // réutilisation : toute la session est révoquée
            return await _probleme(req, 401, 'REFRESH_TOKEN_REUTILISE');
          }
          final u = _refreshActifs.remove(jeton);
          if (u == null) return await _probleme(req, 401, 'REFRESH_TOKEN_INVALIDE');
          _refreshRevoques.add(jeton);
          return await _json(req, 200, _session(u));
        case ('POST', '/api/v1/auth/logout'):
          final jeton = json['refreshToken'] as String? ?? '';
          if (_refreshActifs.remove(jeton) != null) _refreshRevoques.add(jeton);
          return await _json(req, 204, null);
        case ('GET', '/api/v1/auth/moi'):
          final jeton = (req.headers.value('authorization') ?? '').replaceFirst('Bearer ', '');
          final u = _acces[jeton];
          if (u == null) return await _probleme(req, 401, 'TOKEN_EXPIRE');
          return await _json(req, 200, {
            'id': '00000000-0000-0000-0000-000000000001',
            'nom': u.nom,
            'telephone': u.telephone,
            'email': u.email,
            'role': u.role,
            'actif': true,
            'deuxFacteursActif': u.deuxFacteurs,
            'verrouille': false,
          });
      }
      return await _probleme(req, 404, 'HTTP_404');
    } catch (_) {
      await _probleme(req, 500, 'ERREUR_INTERNE');
    }
  }

  Map<String, dynamic> _session(FakeUser u) {
    final (access, refresh) = ouvrirSession(u);
    return {
      'accessToken': access,
      'refreshToken': refresh,
      'expiresIn': 900,
      'tokenType': 'Bearer',
      'role': u.role,
      'deuxFacteursRequis': false,
    };
  }

  Future<void> _probleme(HttpRequest req, int status, String code) =>
      _json(req, status, {'type': 'about:blank', 'status': status, 'code': code, 'detail': code}, probleme: true);

  Future<void> _json(HttpRequest req, int status, Object? corps, {bool probleme = false}) async {
    req.response.statusCode = status;
    if (corps != null) {
      req.response.headers.contentType = ContentType('application', probleme ? 'problem+json' : 'json', charset: 'utf-8');
      req.response.write(jsonEncode(corps));
    }
    await req.response.close();
  }
}
