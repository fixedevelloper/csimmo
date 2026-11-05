# ADR 0001 — Choix de la stack

- Statut : accepté
- Date : 2026-11-03

## Contexte

Application de gestion locative installée chez chaque client (licence annuelle, pas de SaaS), 10 à 500 lots, parfois 5 000.
Serveur modeste (2 cœurs, 4 Go RAM), clients non informaticiens, réseau mobile instable (Cameroun). Voir `docs/ANALYSE.md` §6 et §7.

## Décision

| Couche | Choix | Raison |
| --- | --- | --- |
| Backend | Java 21, Spring Boot 3.x, Spring Modulith | Monolithe facile à installer, frontières de modules vérifiées en CI, jar compilé/obfusqué |
| Base | PostgreSQL 16 + Flyway | Intégrité des montants, contraintes d'exclusion (un bail actif par lot), migrations maîtrisées |
| Tâches | db-scheduler ou Quartz JDBC | Persistant en base, pas de Redis à installer |
| Fichiers | S3 (voir ADR 0002) | Portable, indépendant du serveur livré |
| Web | Next.js 15, TypeScript strict, Tailwind, shadcn/ui | Tableaux de bord et formulaires riches |
| Mobile | Flutter, Riverpod, drift | Une base de code Android, mode hors ligne |
| Contrat d'API | OpenAPI (springdoc) → clients TS et Dart générés | Web et mobile alignés sur le backend |
| Packaging | Docker Compose + Caddy | Installation et mise à jour reproductibles, HTTPS automatique |
| Licence | Ed25519 (JDK natif), vérification hors ligne | Pas de dépendance réseau permanente |

Nom du produit : **ImmoGest**, package Java `com.immogest`.

## Conséquences

- Toute dépendance lourde ou nouveau framework exige un nouvel ADR.
- L'obfuscation (ProGuard) ralentit mais n'empêche pas le contournement de la licence : la protection repose aussi sur le contrat et la vérification en ligne périodique.
- Le retour arrière d'une mise à jour passe par la restauration de sauvegarde (Flyway n'est pas réversible).
