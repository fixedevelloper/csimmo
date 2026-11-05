import 'package:flutter/material.dart';

/// Rectangle gris qui pulse pendant un chargement (remplace le cercle de progression).
class Squelette extends StatefulWidget {
  const Squelette({super.key, this.largeur, this.hauteur = 16, this.rayon = 8});

  final double? largeur;
  final double hauteur;
  final double rayon;

  @override
  State<Squelette> createState() => _SqueletteState();
}

class _SqueletteState extends State<Squelette> with SingleTickerProviderStateMixin {
  late final AnimationController _anim = AnimationController(vsync: this, duration: const Duration(milliseconds: 1100))..repeat(reverse: true);

  @override
  void dispose() {
    _anim.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final base = Theme.of(context).colorScheme.onSurface;
    return AnimatedBuilder(
      animation: _anim,
      builder: (_, _) => Container(
        width: widget.largeur,
        height: widget.hauteur,
        decoration: BoxDecoration(color: base.withValues(alpha: 0.06 + 0.07 * _anim.value), borderRadius: BorderRadius.circular(widget.rayon)),
      ),
    );
  }
}

/// Liste de cartes factices pendant le chargement d'une liste.
class ListeSquelette extends StatelessWidget {
  const ListeSquelette({super.key, this.nombre = 4});

  final int nombre;

  @override
  Widget build(BuildContext context) => ListView(
        physics: const NeverScrollableScrollPhysics(),
        padding: const EdgeInsets.all(16),
        children: [
          for (var i = 0; i < nombre; i++)
            const Card(
              child: Padding(
                padding: EdgeInsets.all(16),
                child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Squelette(largeur: 180, hauteur: 16),
                  SizedBox(height: 10),
                  Squelette(hauteur: 12),
                ]),
              ),
            ),
        ],
      );
}

/// Message centré quand il n'y a rien à afficher.
class EtatVide extends StatelessWidget {
  const EtatVide({super.key, required this.icone, required this.texte, this.cleTexte});

  final IconData icone;
  final String texte;
  final Key? cleTexte;

  @override
  Widget build(BuildContext context) {
    final c = Theme.of(context).colorScheme;
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 48, horizontal: 24),
      child: Column(mainAxisSize: MainAxisSize.min, children: [
        Container(
          padding: const EdgeInsets.all(18),
          decoration: BoxDecoration(color: c.primaryContainer.withValues(alpha: 0.6), shape: BoxShape.circle),
          child: Icon(icone, size: 32, color: c.primary),
        ),
        const SizedBox(height: 14),
        Text(texte, key: cleTexte, textAlign: TextAlign.center, style: Theme.of(context).textTheme.bodyLarge?.copyWith(color: c.onSurfaceVariant)),
      ]),
    );
  }
}

enum Ton { succes, alerte, danger, info, neutre }

/// Pastille de statut : point coloré et libellé, couleur selon le ton.
class PastilleStatut extends StatelessWidget {
  const PastilleStatut({super.key, required this.texte, required this.ton, this.cleTexte});

  final String texte;
  final Ton ton;
  final Key? cleTexte;

  @override
  Widget build(BuildContext context) {
    final sombre = Theme.of(context).brightness == Brightness.dark;
    final c = Theme.of(context).colorScheme;
    final (Color fond, Color encre) = switch (ton) {
      Ton.succes => sombre ? (const Color(0x3334D399), const Color(0xFF6EE7B7)) : (const Color(0xFFD9F5E8), const Color(0xFF0B6B45)),
      Ton.alerte => sombre ? (const Color(0x33FBBF24), const Color(0xFFFCD34D)) : (const Color(0xFFFFF0C9), const Color(0xFF8A5A00)),
      Ton.danger => sombre ? (const Color(0x33F87171), const Color(0xFFFCA5A5)) : (const Color(0xFFFDE0E0), const Color(0xFFB42318)),
      Ton.info => (c.primaryContainer, c.onPrimaryContainer),
      Ton.neutre => (c.surfaceContainerHighest, c.onSurfaceVariant),
    };
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(color: fond, borderRadius: BorderRadius.circular(20)),
      child: Row(mainAxisSize: MainAxisSize.min, children: [
        Container(width: 6, height: 6, decoration: BoxDecoration(color: encre, shape: BoxShape.circle)),
        const SizedBox(width: 6),
        Text(texte, key: cleTexte, style: TextStyle(color: encre, fontSize: 12, fontWeight: FontWeight.w600)),
      ]),
    );
  }
}

/// Carte avec un titre de section.
class CarteSection extends StatelessWidget {
  const CarteSection({super.key, this.titre, required this.enfants});

  final String? titre;
  final List<Widget> enfants;

  @override
  Widget build(BuildContext context) => Card(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
            if (titre != null) ...[
              Text(titre!, style: Theme.of(context).textTheme.titleSmall?.copyWith(fontWeight: FontWeight.w700)),
              const SizedBox(height: 10),
            ],
            ...enfants,
          ]),
        ),
      );
}

/// Ligne « libellé / valeur » d'une carte d'informations.
class LigneInfo extends StatelessWidget {
  const LigneInfo(this.libelle, this.valeur, {super.key});

  final String libelle;
  final String valeur;

  @override
  Widget build(BuildContext context) {
    final c = Theme.of(context).colorScheme;
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 5),
      child: Row(crossAxisAlignment: CrossAxisAlignment.start, mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
        Text(libelle, style: TextStyle(color: c.onSurfaceVariant)),
        const SizedBox(width: 16),
        Flexible(child: Text(valeur, textAlign: TextAlign.end, style: const TextStyle(fontWeight: FontWeight.w600))),
      ]),
    );
  }
}

/// Titre de barre d'application avec l'icône de la marque (le texte garde la clé fournie).
class TitreMarque extends StatelessWidget {
  const TitreMarque(this.texte, {super.key, this.cleTexte});

  final String texte;
  final Key? cleTexte;

  @override
  Widget build(BuildContext context) => Row(mainAxisSize: MainAxisSize.min, children: [
        ClipRRect(
          borderRadius: BorderRadius.circular(8),
          child: Image.asset('assets/brand/icone.png', width: 30, height: 30, errorBuilder: (_, _, _) => const SizedBox(width: 30, height: 30)),
        ),
        const SizedBox(width: 10),
        Flexible(child: Text(texte, key: cleTexte, overflow: TextOverflow.ellipsis)),
      ]);
}
