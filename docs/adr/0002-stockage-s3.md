# ADR 0002 — Stockage de fichiers compatible S3, SeaweedFS livré par défaut

- Statut : accepté
- Date : 2026-11-03

## Contexte

L'application stocke des photos de biens, des pièces d'identité, des baux, quittances, reçus et relevés en PDF.
Elle est installée chez chaque client (licence annuelle, pas de SaaS) via Docker Compose.
On veut un stockage objet de type S3, comme MinIO ou équivalent.

État de MinIO à la date de cette décision :
- mai 2025 : console d'administration retirée de l'édition communautaire ;
- 23 octobre 2025 : fin de publication des images Docker et des binaires communautaires (source seulement) ;
- 3 décembre 2025 : dépôt communautaire en « maintenance mode » ;
- avril 2026 : dépôt GitHub archivé (lecture seule).
- Licence AGPLv3 : à valider juridiquement pour un produit commercial redistribué.

Livrer MinIO dans chaque installation imposerait de compiler et maintenir nous-mêmes ses images, sans correctifs de sécurité garantis.

## Décision

1. Le code applicatif ne dépend que de l'**API S3**, via AWS SDK for Java v2 (`endpointOverride`, `forcePathStyle(true)`), derrière l'interface `FileStorage`.
2. Le serveur S3 livré par défaut dans `docker-compose.yml` est **SeaweedFS** (Apache 2.0, maintenu, mature) en mode `weed server -s3`, accessible seulement sur le réseau Docker interne.
3. Le client peut brancher à la place un S3 existant (AWS S3, Backblaze B2, Wasabi, MinIO déjà en place, RustFS, Garage) par configuration (`immogest.stockage.*`), sans changement de code.
4. Les tests d'intégration utilisent un conteneur SeaweedFS (Testcontainers `GenericContainer`).
5. Les sauvegardes copient le bucket avec `rclone` (indépendant du serveur S3).

## Conséquences

- Positives : pas de dépendance à un projet abandonné ; licence permissive pour la redistribution ; changement de serveur S3 possible à tout moment.
- Négatives : SeaweedFS a une console moins soignée que l'ancienne console MinIO (sans impact : les utilisateurs n'y accèdent pas) ; à surveiller : n'utiliser aucune fonctionnalité S3 avancée non supportée (object lock, réplication) sans test sur SeaweedFS.
- À réévaluer : RustFS (Apache 2.0, API proche de MinIO) quand une version stable 1.0 sera publiée.
