# ADR 0003 — Notifications : canaux HTTP/SMTP/FCM, file en base, dépendance `spring-boot-starter-mail`

- Statut : accepté
- Date : 2026-11-04

## Contexte

L'application envoie des messages aux locataires (reçu SMS d'un encaissement en espèces, quittances, reçus) par SMS,
WhatsApp, e-mail et notification push. Le client saisit lui-même ses identifiants dans Paramètres ; pas de SaaS éditeur.
Pas de Redis ni de courtier de messages (CLAUDE.md §3).

## Décisions

- **Interface `CanalNotification`** ; une implémentation par canal. En test et en développement, `immogest.notification.simuler=true`
  remplace les canaux réels par des canaux simulés : aucun appel externe.
- **SMS** : fournisseur HTTP générique (URL, en-tête d'authentification, corps JSON modèle avec `{to}`, `{from}`, `{message}`),
  car les fournisseurs locaux (Cameroun, CEMAC) n'ont pas d'API commune. **WhatsApp** : Cloud API (messages « template »).
  **Push** : FCM HTTP v1, jeton OAuth2 obtenu avec le compte de service (JWT RS256 signé avec le JDK). Tous via `java.net.http`, sans SDK.
- **E-mail** : SMTP via **`spring-boot-starter-mail`** (Jakarta Mail). Dépendance retenue : standard, maintenue avec Spring Boot, aucune
  alternative raisonnable pour parler SMTP sans réécrire le protocole. La configuration SMTP vient de la base (saisie par le client),
  pas de `spring.mail.*`.
- **File d'envoi** dans PostgreSQL (`notif_message`), reprise par une tâche `db-scheduler` toutes les 30 s et `FOR UPDATE SKIP LOCKED`.
  1 essai + **3 réessais** (délais 1 min, 5 min, 30 min), puis statut `ECHEC` visible dans le journal et relançable à la main.
  Clé de dédoublonnage unique : la redélivrance d'un événement ne double pas un message.
- **Secrets** (jetons, mots de passe, compte de service) chiffrés AES-GCM avec la clé de l'installation, jamais renvoyés par l'API
  (seulement « renseigné »), jamais journalisés.
- Les corps de message du journal contiennent des données personnelles (nom, montant) : la lecture du journal est réservée aux
  ADMIN et GESTIONNAIRE. Les codes OTP ne passent jamais par la file ni par le journal (envoi direct).

## Conséquences

- Un canal mal configuré se voit dans le journal (`ECHEC` avec la cause) plutôt que de perdre le message en silence.
- Les volumes visés (quelques centaines de messages par jour) rendent l'envoi dans la transaction de la file acceptable ;
  au-delà il faudra sortir l'appel réseau de la transaction.
- Pas de pièce jointe PDF dans les e-mails pour l'instant : le message renvoie vers l'espace locataire.
