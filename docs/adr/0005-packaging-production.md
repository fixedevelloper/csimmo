# ADR 0005 — Packaging de production (étape 10.1)

Statut : accepté.

## Décisions

- **Docker Compose** (`deploy/docker-compose.yml`) : postgres, seaweedfs (profil `stockage-local`), app, web, caddy, cloudflared (profil `tunnel`). Deux réseaux : `interne` (`internal: true`, sans sortie ni port publié : postgres et seaweedfs) et `exposee` (app, web, caddy). Seul Caddy publie des ports (80, 443). Le serveur S3 n'est donc jamais joignable depuis Internet (CLAUDE.md §5.2).
- **Trois modes d'accès**, un `Caddyfile.*` chacun : `domaine` (Let's Encrypt automatique), `local` (`tls internal`, webhooks bloqués, `IMMOGEST_RESEAU_LOCAL=true` : le paiement en ligne répond 409 `PAIEMENT_EN_LIGNE_INDISPONIBLE`, pas de vérification en ligne de la licence), `tunnel` (Cloudflare Tunnel, Caddy en HTTP seulement).
- **Stockage externe** : ne pas activer le profil `stockage-local` et renseigner `IMMOGEST_S3_*`.
- **`install.sh` ne réécrit jamais `deploy/.env`** : la clé AES-GCM n'est pas régénérable sans rendre les pièces d'identité illisibles. Les identifiants S3 du seaweedfs livré sont générés par installation (`deploy/generated/s3.json`), jamais ceux de développement.
- **Obfuscation ProGuard conservatrice** (profil Maven `obfuscation`, `deploy/proguard/app.pro`) : noms de classes et de packages conservés (Spring Modulith, JPA, Jackson, Thymeleaf, Spring Data lisent par réflexion), membres privés renommés, fichiers source et numéros de ligne retirés. C'est une barrière contre la lecture casuelle du code, **pas** une protection forte : un jar qui ne démarre plus chez un client coûte plus cher qu'un code lisible. Durcir davantage (renommer les classes) exige une campagne de tests sur le jar obfusqué.
- **Application web** : `output: "standalone"`, image `node:22-alpine`, utilisateur non root.

## Limites connues

- L'application « locataire » n'est pas désactivée par un interrupteur : en mode `local` elle n'est tout simplement pas joignable hors du réseau.
- Le code QR de `install.sh` encode l'adresse du serveur ; l'application mobile ne sait pas encore le scanner (saisie manuelle de l'adresse).
- Sauvegardes, mises à jour et restauration : étape 10.2.
