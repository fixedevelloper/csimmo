import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:immogest_api/immogest_api.dart';

import '../../core/error/app_error.dart';
import '../../core/error/error_messages.dart';
import '../../core/id/uuid.dart';
import '../../core/widgets/composants.dart';
import '../../l10n/app_localizations.dart';
import 'formatage.dart';
import 'locataire_repository.dart';

enum _Phase { formulaire, envoi, suivi, fin }

/// Paiement Mobile Money. Le téléphone ne « déclare » jamais le paiement : il demande à l'opérateur (via le serveur) et attend que le
/// serveur dise CONFIRMEE. Le suivi s'arrête à la confirmation, à l'échec, ou au bout de [delaiMax] (le serveur continue de vérifier).
class PayerScreen extends ConsumerStatefulWidget {
  const PayerScreen({super.key, this.montantSuggere, this.intervalleSuivi = const Duration(seconds: 3), this.delaiMax = const Duration(minutes: 2)});

  final int? montantSuggere;
  final Duration intervalleSuivi;
  final Duration delaiMax;

  @override
  ConsumerState<PayerScreen> createState() => _PayerScreenState();
}

class _PayerScreenState extends ConsumerState<PayerScreen> {
  final _montant = TextEditingController();
  final _telephone = TextEditingController();
  final _formulaire = GlobalKey<FormState>();
  InitiationRequeteOperateurEnum _operateur = InitiationRequeteOperateurEnum.MTN_MOMO;
  String? _bailId;
  _Phase _phase = _Phase.formulaire;
  String? _erreur;
  TransactionDto? _transaction;
  Timer? _minuteur;
  int _sondages = 0; // nombre de sondages depuis le début du suivi (durée = sondages × intervalle : indépendant de l'horloge)
  bool _delaiDepasse = false;

  // une même demande réutilise la même clé (renvoi après coupure) ; une demande différente en reçoit une nouvelle
  String? _cle;
  String? _empreinte;

  @override
  void initState() {
    super.initState();
    if (widget.montantSuggere != null && widget.montantSuggere! > 0) _montant.text = '${widget.montantSuggere}';
  }

  @override
  void dispose() {
    _minuteur?.cancel();
    _montant.dispose();
    _telephone.dispose();
    super.dispose();
  }

  Future<void> _payer(List<BailDto> baux) async {
    final l = AppLocalizations.of(context);
    final bail = _bailId ?? (baux.length == 1 ? baux.first.id : null);
    if (!(_formulaire.currentState?.validate() ?? false)) return;
    if (bail == null) {
      setState(() => _erreur = l.choisirBailErreur);
      return;
    }
    final montant = int.parse(_montant.text.replaceAll(RegExp(r'\s'), ''));
    final empreinte = '$bail|$montant|${_operateur.value}|${_telephone.text.trim()}';
    if (empreinte != _empreinte) {
      _empreinte = empreinte;
      _cle = uuidV4();
    }
    setState(() {
      _phase = _Phase.envoi;
      _erreur = null;
    });
    try {
      final t = await ref.read(locataireRepositoryProvider).initierPaiement(
            bailId: bail,
            montant: montant,
            operateur: _operateur,
            telephone: _telephone.text.trim(),
            cle: _cle!,
          );
      if (!mounted) return;
      setState(() {
        _transaction = t;
        _phase = _Phase.suivi;
        _sondages = 0;
        _delaiDepasse = false;
      });
      _minuteur = Timer.periodic(widget.intervalleSuivi, (_) => _interroger());
      unawaited(_interroger());
    } on AppError catch (e) {
      if (!mounted) return;
      setState(() {
        _phase = _Phase.formulaire;
        _erreur = errorMessage(l, e);
      });
    }
  }

  Future<void> _interroger() async {
    final id = _transaction?.id;
    if (id == null) return;
    try {
      final t = await ref.read(locataireRepositoryProvider).statutPaiement(id);
      if (!mounted) return;
      _sondages++;
      setState(() => _transaction = t);
      if (t.statut != TransactionDtoStatutEnum.EN_ATTENTE) {
        _minuteur?.cancel();
        setState(() => _phase = _Phase.fin);
        if (t.statut == TransactionDtoStatutEnum.CONFIRMEE) {
          // le solde, les échéances et les quittances ont changé
          ref.invalidate(monCompteProvider);
          ref.invalidate(mesDocumentsProvider);
        }
      } else if (widget.intervalleSuivi * _sondages >= widget.delaiMax) {
        _minuteur?.cancel();
        setState(() => _delaiDepasse = true);
      }
    } on AppError {
      // coupure pendant le suivi : on réessaiera au prochain passage, la transaction existe côté serveur
    }
  }

  void _continuerSuivi() {
    setState(() {
      _delaiDepasse = false;
      _sondages = 0;
    });
    _minuteur = Timer.periodic(widget.intervalleSuivi, (_) => _interroger());
  }

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final locale = Localizations.localeOf(context).toString();
    final baux = ref.watch(mesBauxProvider);
    return Scaffold(
      appBar: AppBar(title: Text(l.payerTitre, key: const Key('titre-payer'))),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: switch (_phase) {
          _Phase.formulaire || _Phase.envoi => baux.when(
              loading: () => const Column(children: [Squelette(hauteur: 56, rayon: 14), SizedBox(height: 12), Squelette(hauteur: 56, rayon: 14), SizedBox(height: 12), Squelette(hauteur: 56, rayon: 14)]),
              error: (e, _) => Center(child: Text(errorMessage(l, e))),
              data: (liste) {
                final payables = liste.where((b) => b.statut != BailDtoStatutEnum.TERMINE || liste.length == 1).toList();
                return ListView(children: [
                  Form(
                    key: _formulaire,
                    child: Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
                      if (payables.length > 1)
                        DropdownButtonFormField<String>(
                          key: const Key('champ-bail'),
                          initialValue: _bailId,
                          decoration: InputDecoration(labelText: l.payerBail,),
                          items: [for (final b in payables) DropdownMenuItem(value: b.id, child: Text('${b.immeubleNom ?? ''} — ${b.lotReference ?? ''}'))],
                          onChanged: (v) => setState(() => _bailId = v),
                        ),
                      const SizedBox(height: 12),
                      TextFormField(
                        key: const Key('champ-montant-paiement'),
                        controller: _montant,
                        keyboardType: TextInputType.number,
                        inputFormatters: [FilteringTextInputFormatter.allow(RegExp(r'[0-9 ]'))],
                        decoration: InputDecoration(labelText: l.montantFcfa,),
                        validator: (v) {
                          final n = int.tryParse((v ?? '').replaceAll(RegExp(r'\s'), ''));
                          return n == null || n <= 0 || n > 20000000 ? l.montantInvalide : null;
                        },
                      ),
                      const SizedBox(height: 12),
                      SegmentedButton<InitiationRequeteOperateurEnum>(
                        key: const Key('choix-operateur'),
                        segments: [
                          ButtonSegment(value: InitiationRequeteOperateurEnum.MTN_MOMO, label: Text(l.operateurMtn)),
                          ButtonSegment(value: InitiationRequeteOperateurEnum.ORANGE_MONEY, label: Text(l.operateurOrange)),
                        ],
                        selected: {_operateur},
                        onSelectionChanged: (s) => setState(() => _operateur = s.first),
                      ),
                      const SizedBox(height: 12),
                      TextFormField(
                        key: const Key('champ-telephone-paiement'),
                        controller: _telephone,
                        keyboardType: TextInputType.phone,
                        decoration: InputDecoration(labelText: l.payerTelephone, hintText: '+237 6XX XX XX XX',),
                        validator: (v) => RegExp(r'^\+?[0-9 .\-()]{9,20}$').hasMatch((v ?? '').trim()) ? null : l.payerTelephoneInvalide,
                      ),
                    ]),
                  ),
                  if (_erreur != null) Padding(padding: const EdgeInsets.only(top: 12), child: Text(_erreur!, key: const Key('erreur-paiement'), style: TextStyle(color: Theme.of(context).colorScheme.error))),
                  const SizedBox(height: 16),
                  FilledButton(key: const Key('confirmer-paiement'), onPressed: _phase == _Phase.envoi ? null : () {
                    HapticFeedback.mediumImpact();
                    _payer(payables);
                  }, child: Text(_phase == _Phase.envoi ? l.envoiEnCours : l.payerConfirmer)),
                ]);
              },
            ),
          _Phase.suivi => _Suivi(transaction: _transaction!, locale: locale, delaiDepasse: _delaiDepasse, onContinuer: _continuerSuivi),
          _Phase.fin => _Fin(transaction: _transaction!, locale: locale),
        },
      ),
    );
  }
}

class _Suivi extends StatelessWidget {
  const _Suivi({required this.transaction, required this.locale, required this.delaiDepasse, required this.onContinuer});

  final TransactionDto transaction;
  final String locale;
  final bool delaiDepasse;
  final VoidCallback onContinuer;

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    return Center(
      child: Column(mainAxisSize: MainAxisSize.min, children: [
        if (!delaiDepasse) const SizedBox(width: 56, height: 56, child: CircularProgressIndicator(strokeWidth: 5)),
        const SizedBox(height: 16),
        Text(l.payerAttenteTitre, key: const Key('paiement-en-attente'), style: Theme.of(context).textTheme.titleMedium, textAlign: TextAlign.center),
        const SizedBox(height: 8),
        Text(transaction.instructions ?? l.payerAttenteAide, textAlign: TextAlign.center),
        const SizedBox(height: 8),
        Text(formaterFcfa(transaction.montant, locale), style: Theme.of(context).textTheme.headlineSmall),
        if (delaiDepasse) ...[
          const SizedBox(height: 16),
          Text(l.payerDelai, key: const Key('paiement-delai'), textAlign: TextAlign.center),
          const SizedBox(height: 8),
          FilledButton(key: const Key('continuer-suivi'), onPressed: onContinuer, child: Text(l.payerContinuerSuivi)),
          TextButton(onPressed: () => Navigator.of(context).pop(), child: Text(l.payerFermer)),
        ],
      ]),
    );
  }
}

class _Fin extends StatelessWidget {
  const _Fin({required this.transaction, required this.locale});

  final TransactionDto transaction;
  final String locale;

  @override
  Widget build(BuildContext context) {
    final l = AppLocalizations.of(context);
    final ok = transaction.statut == TransactionDtoStatutEnum.CONFIRMEE;
    final message = switch (transaction.statut) {
      TransactionDtoStatutEnum.CONFIRMEE => l.payerSucces(formaterFcfa(transaction.montant, locale)),
      TransactionDtoStatutEnum.ANOMALIE => l.payerAnomalie,
      TransactionDtoStatutEnum.EXPIREE => l.payerExpiree,
      _ => l.payerRefusee,
    };
    return Center(
      child: Column(mainAxisSize: MainAxisSize.min, children: [
        Container(
          padding: const EdgeInsets.all(20),
          decoration: BoxDecoration(shape: BoxShape.circle, color: (ok ? const Color(0xFF16A34A) : Theme.of(context).colorScheme.error).withValues(alpha: 0.12)),
          child: Icon(ok ? Icons.check_circle : Icons.error, size: 64, color: ok ? const Color(0xFF16A34A) : Theme.of(context).colorScheme.error),
        ),
        const SizedBox(height: 12),
        Text(message, key: Key(ok ? 'paiement-confirme' : 'paiement-echoue'), textAlign: TextAlign.center, style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: 16),
        FilledButton(key: const Key('fermer-paiement'), onPressed: () => Navigator.of(context).pop(), child: Text(l.payerFermer)),
      ]),
    );
  }
}
