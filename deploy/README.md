# Déploiement d'ImmoGest

Tout passe par Docker Compose (`docker-compose.yml`) : postgres, seaweedfs (stockage S3 interne), app (API), web, caddy (HTTPS), sauvegarde.
Seul Caddy publie des ports. Voir `docs/adr/0005-packaging-production.md`.

## Installer

```bash
cd deploy
./install.sh                                   # assistant
./install.sh --mode domaine --site immo.exemple.cm --yes
./install.sh --mode local   --site 192.168.1.20 --yes          # sans Internet
./install.sh --mode tunnel  --site immo.exemple.cm --tunnel-token <jeton> --yes
```

**À partir des images publiées par la CI** (GHCR, publiques : aucune connexion requise, rien n'est construit) :

```bash
./install.sh --mode domaine --site immo.exemple.cm --version 1.4.0 --images-prebuilt --yes
```
Les noms des images (`ghcr.io/fixedevelloper/immogest-app|web|sauvegarde`, autre préfixe avec `--registre`) sont écrits dans `.env`.

Options utiles : `--stockage externe --s3-endpoint … --s3-bucket … --s3-access-key … --s3-secret-key …` (désactive SeaweedFS) ;
`--port-http N --port-https N` (si 80/443 sont pris) ; `--cle-publique <base64>` ; `--licence-serveur-url URL`.

**Avant la première livraison** : la clé publique de l'éditeur doit être dans le jar (`backend/app/src/main/resources/licence/cle-publique-editeur.txt`)
ou passée par `--cle-publique`. Sans elle, toute licence est refusée.

`install.sh` ne réécrit jamais `deploy/.env`. **Copiez ce fichier hors du serveur** : sans `IMMOGEST_CLE_CHIFFREMENT`, les pièces d'identité
chiffrées sont irrécupérables.

## Sauvegardes

Quotidienne à 02:30 (rattrapage si le serveur était éteint), rétention 30 jours (3 sauvegardes minimum conservées). Contenu : `pg_dump`, copie du bucket,
clé de chiffrement **chiffrée dans un fichier séparé** (`/sauvegardes/cles`, passphrase `SAUVEGARDE_PASSPHRASE` du `.env`).
`./backup.sh [étiquette]` la déclenche à la main ; Paramètres > Sauvegardes (ADMIN) aussi.

**Copie externe** (indispensable : un disque ou un vol du serveur emporte aussi les sauvegardes locales) :
- autre S3 : `SAUVEGARDE_EXTERNE_S3_ENDPOINT`, `_BUCKET`, `_PREFIXE`, `_ACCESS_KEY`, `_SECRET_KEY`, `_REGION` dans `.env` ;
- ou dossier de l'hôte (disque USB, NAS monté) : `SAUVEGARDE_DISQUE_EXTERNE=/chemin`, et créer le fichier `.actif` dans ce dossier (évite de copier dans
  un dossier non monté). Puis `docker compose up -d sauvegarde`.

Un échec de copie externe ne perd pas la sauvegarde locale : il est signalé (alerte dans l'écran, code de sortie 3).

## Restaurer

- Écran Paramètres > Sauvegardes (sauvegarde de la **même version** que l'application installée) : l'application redémarre seule.
- Ligne de commande : `./restore.sh <id>` (application arrêtée). Autre version : remettre l'image correspondante (`IMMOGEST_VERSION`), puis
  `FORCER_VERSION=1 ./restore.sh <id>`.
Une sauvegarde « avant-restauration » est toujours faite d'abord.

## Mettre à jour

```bash
./update.sh                                    # dernière version publiée par l'éditeur
./update.sh --archive immogest-1.4.0.tar.gz --sha256 <somme> --yes   # hors ligne
```
Changer de version par tag : `./update.sh --version 1.4.0` tire les trois images du registre (`--registre PREFIX` pour un autre préfixe ; sans sources sur le serveur, GHCR de l'éditeur est utilisé et les noms d'images sont écrits dans `.env`, même si l'installation n'en venait pas) (échec = rien n'est modifié ;
`--verifier-signature` contrôle d'abord la signature cosign, cosign requis), puis le reste de l'ordre ci-dessous s'applique (même retour arrière).
Les scripts et le compose de `deploy/` ne sont pas mis à jour par ce mode : récupérez-les avec la version (dépôt ou archive) si elle les a modifiés.

Ordre : téléchargement → somme de contrôle (avant toute modification) → sauvegarde → chargement des images → redémarrage (migrations Flyway) → contrôle de
santé et d'intégrité des données. **En cas d'échec : retour arrière automatique** (restauration de la sauvegarde d'avant + ancienne image). `./package-release.sh X.Y.Z --prefixe ghcr.io/fixedevelloper/immogest`
construit l'archive à publier (portail éditeur > Versions).

## Diagnostic

`./diagnostic.sh` (versions, services, disque, licence, sauvegardes ; aucun secret) ou Paramètres > Sauvegardes > Diagnostic.
