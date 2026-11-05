# ImmoGest — portail éditeur

Interface de gestion des clients, licences et versions (réservée à l'éditeur). Parle au `license-server` (`backend/license-server`).

```bash
pnpm install
LICENSE_SERVER_URL=http://localhost:8090 pnpm dev   # http://localhost:3000
pnpm lint && pnpm typecheck && pnpm test
```

Le compte éditeur est créé par le serveur de licences (`LICENSE_SERVER_ADMIN_EMAIL` / `LICENSE_SERVER_ADMIN_MOT_DE_PASSE`). Voir `CLAUDE.md` §5.4 et §5.5.
