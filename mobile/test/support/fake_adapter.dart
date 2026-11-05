import 'dart:async';
import 'dart:convert';
import 'dart:typed_data';

import 'package:dio/dio.dart';

typedef Reponse = ({int status, Object? corps});

/// Adaptateur Dio scripté : pas de réseau, chaque requête est traitée par [handler].
class FakeAdapter implements HttpClientAdapter {
  FakeAdapter(this.handler);

  final FutureOr<Reponse> Function(RequestOptions options) handler;
  final List<RequestOptions> requetes = [];

  @override
  Future<ResponseBody> fetch(RequestOptions options, Stream<Uint8List>? requestStream, Future<void>? cancelFuture) async {
    requetes.add(options);
    final r = await handler(options);
    if (r.status == -1) {
      throw DioException.connectionError(requestOptions: options, reason: 'réseau coupé');
    }
    if (r.corps is Uint8List) {
      return ResponseBody.fromBytes(r.corps! as Uint8List, r.status, headers: {
        Headers.contentTypeHeader: ['application/pdf'],
      });
    }
    return ResponseBody.fromString(
      jsonEncode(r.corps),
      r.status,
      headers: {
        Headers.contentTypeHeader: ['application/json'],
      },
    );
  }

  int appels(String chemin) => requetes.where((o) => o.path == chemin).length;

  @override
  void close({bool force = false}) {}
}
