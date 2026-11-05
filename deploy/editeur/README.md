# Déploiement de l'éditeur (serveur de licences + portail)

Séparé des installations clients : ne jamais mettre la clé privée sur un serveur client.

1. DNS : `LICENCES_HOST` et `PORTAIL_HOST` vers ce serveur (ports 80/443 ouverts).
2. Clé privée Ed25519 posée sur l'hôte, hors dépôt : `chmod 600`, lisible par l'uid 10002 (`chown 10002 …`).
3. `cp .env.example .env`, remplir les valeurs (secrets forts), puis `docker compose up -d`.
4. Les installations clients utilisent `IMMOGEST_LICENCE_SERVEUR_URL=https://<LICENCES_HOST>`.
   Seuls `/api/v1/verification` et `/api/v1/versions/derniere` sont publics ; le reste n'est atteignable que via le portail.
5. Sauvegarder le volume `pgdata` (`ls_emission` est la preuve de toutes les licences émises) et la clé privée.

Mise à jour : changer `VERSION`, `docker compose pull && docker compose up -d`.
