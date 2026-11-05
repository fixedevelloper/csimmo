# ADR 0008 — Graphiques du web : Recharts

Statut : accepté.

## Contexte

Le tableau de bord (web) dessinait ses barres « attendu / encaissé » en SVG à la main : échelle écrasée par les grands montants, pas d'infobulle, pas de graphique d'occupation. La refonte visuelle (phase 2) demande des graphiques propres et interactifs, et d'autres suivront (arriérés, reversements).

## Décision

- **Recharts** (MIT) est ajouté au projet `web` uniquement : composants React déclaratifs, SVG (donc stylable avec les variables CSS du thème clair/sombre), compatible React 19. Pas de canvas, pas de dépendance côté mobile ni `license-web`.
- Les couleurs viennent des variables du thème (`--primary`, `--marque-orange`), jamais de valeurs en dur.
- **Accessibilité conservée** : chaque graphique garde un conteneur `role="img"` avec libellé et un tableau masqué (`sr-only`) portant les mêmes chiffres.
- Les tests unitaires vérifient le tableau accessible et les libellés ; le dessin lui-même (jsdom n'a pas de mise en page) est vérifié dans le navigateur par Playwright.

## Conséquences

- Taille : Recharts ajoute son poids au bundle des seules pages qui l'importent ; le composant du graphique est chargé dynamiquement (`next/dynamic`, sans rendu serveur) pour ne pas alourdir les autres écrans.
- Alternatives écartées : garder le SVG à la main (coût de maintenance, pas d'interactions) ; Chart.js (canvas, moins adapté au thème CSS) ; Nivo/visx (plus lourds ou plus bas niveau).
