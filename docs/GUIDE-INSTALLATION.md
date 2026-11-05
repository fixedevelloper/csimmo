# Guide d'installation d'ImmoGest

Public : la personne qui installe ImmoGest chez le client (technicien, prestataire informatique ou propriétaire à l'aise avec un terminal).
Durée : 20 à 30 minutes, hors construction des images.

## 1. Avant de commencer

| Il vous faut | Détail |
| --- | --- |
| Un serveur Linux | VPS ou PC dédié, Ubuntu 22.04+ ou Debian 12+ recommandés. 4 Go de mémoire, 10 Go de disque libres au minimum (davantage pour les photos et les sauvegardes). |
| Docker | Moteur Docker et plugin Compose v2 (`docker compose version`). Votre utilisateur doit pouvoir lancer `docker`. |
| `openssl` | Pour générer les secrets. |
| Votre licence | Le fichier `.lic` remis par l'éditeur, **lié à l'empreinte de CE serveur** (l'installation l'affiche au premier lancement ; communiquez-la à l'éditeur pour obtenir le fichier). |
| La clé publique de l'éditeur | Déjà dans le jar livré ; sinon `--cle-publique <base64>`. Sans elle, toute licence est refusée. |
| Un accès réseau | Selon le mode choisi ci-dessous. |

## 2. Choisir le mode d'accès

| Mode | Quand | Ce qu'il faut |
| --- | --- | --- |
| **domaine** | Serveur sur Internet (VPS) avec un nom de domaine | DNS du domaine vers le serveur, ports 80 et 443 ouverts. Certificat HTTPS automatique. |
| **tunnel** | Serveur derrière un routeur, sans port ouvert | Un tunnel Cloudflare et son jeton. HTTPS assuré par Cloudflare. |
| **local** | Réseau interne sans Internet | Une adresse IP fixe. Certificat local : installez le certificat racine de Caddy sur les postes, sinon avertissement du navigateur. **Paiement en ligne et vérification en ligne de la licence désactivés.** |

Stockage des fichiers : par défaut un serveur S3 livré (SeaweedFS), **jamais joignable depuis Internet**. Vous pouvez à la place brancher un S3 externe (AWS, Backblaze B2, MinIO existant) avec `--stockage externe`.

## 3. Installer

```bash
git clone <dépôt> immogest && cd immogest/deploy        # ou décompressez l'archive reçue
./install.sh                                              # assistant : mode, adresse, stockage
# ou, sans questions :
./install.sh --mode domaine --site immo.exemple.cm --yes
./install.sh --mode local   --site 192.168.1.20 --yes
./install.sh --mode tunnel  --site immo.exemple.cm --tunnel-token <jeton> --yes
```

Options utiles : `--port-http N --port-https N` (si 80/443 sont occupés), `--licence-serveur-url URL`, `--cle-publique <base64>`, `--images-prebuilt` (images déjà construites par l'éditeur).

Le script : vérifie Docker, l'espace disque et la mémoire, génère les secrets, construit puis démarre les services, attend que l'API soit prête (les migrations de base s'exécutent à ce moment) et affiche l'adresse.

> **Sauvegardez `deploy/.env` hors du serveur, tout de suite.** Il contient `IMMOGEST_CLE_CHIFFREMENT` : sans elle, les pièces d'identité chiffrées sont irrécupérables, même avec une sauvegarde de la base. `install.sh` ne le réécrit jamais.

## 4. Premier lancement

1. Ouvrez l'adresse affichée. L'**assistant** crée le compte administrateur et vous demande le fichier de licence.
2. Paramètres > **Licence** : vérifiez la date d'expiration et le palier de lots.
3. Paramètres > **Notifications** : renseignez le fournisseur SMS (et WhatsApp, e-mail) du client.
4. Paramètres > **Sauvegardes** : vérifiez l'alerte « copie externe ». Configurez-la (voir § 6).
5. Créez les comptes des gestionnaires (Paramètres > Utilisateurs). Activez la double authentification pour l'administrateur.
6. Reprise de données existantes : Paramètres > **Import de données** (modèle Excel à remplir).
7. Application mobile : saisissez l'adresse du serveur à son premier lancement.

## 5. Vérifier que tout va bien

```bash
./diagnostic.sh        # versions, services, disque, licence, sauvegardes (aucun secret affiché)
docker compose ps      # tous les services « healthy » ou « running »
```

## 6. Sauvegardes (à faire le jour même)

Une sauvegarde complète est faite chaque jour à 02:30 (base, fichiers, clé de chiffrement chiffrée à part), conservée 30 jours. Elle reste **sur le même serveur** : configurez une **copie externe** dans `deploy/.env`, puis `docker compose up -d sauvegarde` :

- autre S3 : `SAUVEGARDE_EXTERNE_S3_ENDPOINT`, `_BUCKET`, `_ACCESS_KEY`, `_SECRET_KEY` ;
- ou dossier de l'hôte (disque USB, NAS monté) : `SAUVEGARDE_DISQUE_EXTERNE=/chemin`, et créez le fichier `.actif` dans ce dossier.

**Essayez une restauration une fois** sur une installation de test avant d'en avoir besoin (`./restore.sh <id>`).

## 7. Mettre à jour

```bash
./update.sh                                            # dernière version publiée
./update.sh --archive immogest-1.4.0.tar.gz --sha256 <somme> --yes    # hors ligne
```
Le script sauvegarde, vérifie la somme de contrôle, migre, redémarre et **revient automatiquement à la version précédente en cas d'échec**. Voir `deploy/README.md`.

## 8. Dépannage

| Symptôme | Piste |
| --- | --- |
| `install.sh` : « le port 80 est déjà utilisé » | Un autre serveur web tourne : arrêtez-le, ou utilisez `--port-http`/`--port-https`. |
| L'API reste « starting » | `docker compose logs app` : base inaccessible, clé manquante, migration en échec. |
| « Licence refusée » | L'empreinte du serveur a changé (machine différente) : demandez un transfert à l'éditeur. Sans clé publique dans le jar, toute licence est refusée. |
| Application en lecture seule | Licence expirée ou invalide (bandeau en haut). Les sauvegardes et exports restent possibles. |
| Avertissement de certificat (mode local) | Installez le certificat racine de Caddy sur les postes. |
| Plus de place | `./diagnostic.sh` ; réduisez la rétention (`SAUVEGARDE_RETENTION_JOURS`) ou ajoutez du disque. |

## 9. Sécurité de l'installation

- Seul Caddy publie des ports (80/443). La base, le stockage S3 et l'API ne sont pas exposés directement.
- Les secrets sont dans `deploy/.env` (droits 600). Ne le commitez pas, ne le partagez pas.
- Tenez le système et Docker à jour. Limitez l'accès SSH au serveur.
- La limitation de débit protège la connexion (par adresse IP) ; 5 échecs verrouillent un compte 15 minutes.
