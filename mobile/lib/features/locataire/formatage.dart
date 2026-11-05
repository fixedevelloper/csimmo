import 'package:intl/intl.dart';

/// « 150 000 FCFA » (aucune décimale : le FCFA n'en a pas).
String formaterFcfa(int? montant, String locale) =>
    montant == null ? '—' : '${NumberFormat.decimalPattern(locale).format(montant)} FCFA';

String formaterDate(DateTime? date, String locale) => date == null ? '—' : DateFormat.yMMMd(locale).format(date);
