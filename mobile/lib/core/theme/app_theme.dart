import 'package:flutter/cupertino.dart' show CupertinoPageTransitionsBuilder;
import 'package:flutter/material.dart';

/// Couleurs de la charte CSImmo (bleu principal, orange d'accent).
const bleuMarque = Color(0xFF0B4DB0);
const orangeMarque = Color(0xFFF97316);

/// Thème clair ou sombre de l'application : Material 3, formes arrondies, champs remplis, transitions douces.
ThemeData construireTheme(Brightness luminosite) {
  final sombre = luminosite == Brightness.dark;
  final base = ColorScheme.fromSeed(seedColor: bleuMarque, brightness: luminosite);
  final schema = base.copyWith(
    primary: sombre ? const Color(0xFF8AB4FF) : bleuMarque,
    onPrimary: sombre ? const Color(0xFF002B6B) : Colors.white,
    secondary: sombre ? const Color(0xFFFFA05A) : orangeMarque,
    onSecondary: sombre ? const Color(0xFF3A1A00) : Colors.white,
    secondaryContainer: sombre ? const Color(0xFF5A2E0B) : const Color(0xFFFFE2CC),
    onSecondaryContainer: sombre ? const Color(0xFFFFDCC4) : const Color(0xFF4A2000),
    surface: sombre ? const Color(0xFF111418) : const Color(0xFFF8F9FC),
  );
  final rayon = BorderRadius.circular(14);

  return ThemeData(
    useMaterial3: true,
    colorScheme: schema,
    scaffoldBackgroundColor: schema.surface,
    visualDensity: VisualDensity.standard,
    pageTransitionsTheme: const PageTransitionsTheme(builders: {
      TargetPlatform.android: FadeForwardsPageTransitionsBuilder(),
      TargetPlatform.iOS: CupertinoPageTransitionsBuilder(),
    }),
    appBarTheme: AppBarTheme(
      centerTitle: false,
      elevation: 0,
      scrolledUnderElevation: 0,
      backgroundColor: schema.surface,
      foregroundColor: schema.onSurface,
      titleTextStyle: TextStyle(fontSize: 20, fontWeight: FontWeight.w600, color: schema.onSurface),
    ),
    cardTheme: CardThemeData(
      elevation: 0,
      margin: const EdgeInsets.symmetric(vertical: 6),
      color: sombre ? const Color(0xFF1A1F26) : Colors.white,
      surfaceTintColor: Colors.transparent,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18), side: BorderSide(color: schema.outlineVariant.withValues(alpha: 0.6))),
    ),
    inputDecorationTheme: InputDecorationTheme(
      filled: true,
      fillColor: sombre ? const Color(0xFF1A1F26) : Colors.white,
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
      border: OutlineInputBorder(borderRadius: rayon, borderSide: BorderSide(color: schema.outlineVariant)),
      enabledBorder: OutlineInputBorder(borderRadius: rayon, borderSide: BorderSide(color: schema.outlineVariant)),
      focusedBorder: OutlineInputBorder(borderRadius: rayon, borderSide: BorderSide(color: schema.primary, width: 2)),
    ),
    filledButtonTheme: FilledButtonThemeData(
      style: FilledButton.styleFrom(minimumSize: const Size(64, 50), shape: RoundedRectangleBorder(borderRadius: rayon), textStyle: const TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
    ),
    outlinedButtonTheme: OutlinedButtonThemeData(
      style: OutlinedButton.styleFrom(minimumSize: const Size(64, 50), shape: RoundedRectangleBorder(borderRadius: rayon)),
    ),
    textButtonTheme: TextButtonThemeData(style: TextButton.styleFrom(shape: RoundedRectangleBorder(borderRadius: rayon))),
    navigationBarTheme: NavigationBarThemeData(
      height: 68,
      elevation: 0,
      backgroundColor: sombre ? const Color(0xFF161A20) : Colors.white,
      indicatorColor: schema.primaryContainer,
      labelTextStyle: WidgetStateProperty.resolveWith(
        (etats) => TextStyle(fontSize: 12, fontWeight: etats.contains(WidgetState.selected) ? FontWeight.w600 : FontWeight.w500),
      ),
    ),
    snackBarTheme: SnackBarThemeData(behavior: SnackBarBehavior.floating, shape: RoundedRectangleBorder(borderRadius: rayon)),
    dialogTheme: DialogThemeData(shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(22))),
    dividerTheme: DividerThemeData(color: schema.outlineVariant.withValues(alpha: 0.6), space: 1),
    listTileTheme: const ListTileThemeData(contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 2)),
  );
}
