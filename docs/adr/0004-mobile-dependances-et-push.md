# ADR 0004 — Application mobile : dépendances natives et notifications push

- Statut : accepté (avec une décision ouverte, voir « Limite connue »)
- Date : 2026-11-04

## Contexte

L'espace locataire (étape 7.1) doit télécharger des quittances PDF, permettre de photographier une panne et recevoir des notifications
push. Chaque installation d'ImmoGest est indépendante : le client saisit ses propres identifiants dans Paramètres (ADR 0003).

## Décisions

- **`image_picker`** : appareil photo et galerie, avec réduction à 1600 px / qualité 85 avant envoi (réseau mobile). Standard de l'écosystème.
- **`open_filex`** : ouvre le PDF téléchargé dans le lecteur du téléphone. Le fichier est téléchargé par l'API (jeton du locataire, contrôle
  d'accès serveur), écrit dans le dossier temporaire sous un nom assaini (`nomSur`), jamais sous un nom fourni par le serveur tel quel.
- **`firebase_core` + `firebase_messaging`** : push FCM. L'application **n'embarque aucun `google-services.json`** : chaque installation a son
  propre projet Firebase, dont le serveur communique à l'application les identifiants publics (`apiKey`, `appId`, `messagingSenderId`,
  `projectId`) par `GET /api/v1/notifications/mobile-config`. L'application initialise Firebase à l'exécution avec ces valeurs, obtient son
  jeton d'appareil et l'enregistre (`POST /api/v1/notifications/appareils`). Le compte de service (secret) ne quitte jamais le serveur.
- Tout est **facultatif et silencieux** : canal push non configuré, autorisation refusée ou erreur réseau n'affichent rien et ne bloquent
  rien (SMS, WhatsApp et e-mail restent disponibles). À la déconnexion, l'appareil est retiré côté serveur avant la fermeture de session.
- Le jeton enregistré est le jeton FCM (`getToken`), seul accepté par l'API HTTP v1 utilisée par le serveur. Les nouvelles méthodes
  `register()` / `onRegistered` du plugin fournissent un identifiant d'installation (FID), pas ce jeton : l'ancienne API est donc conservée
  (`ignore: deprecated_member_use`) jusqu'à vérification du support FID par le serveur.

## Limite connue (décision à prendre avant la diffusion)

Sans `google-services.json`, Firebase n'est initialisé qu'une fois l'application lancée. Un message reçu **alors que l'application est
complètement fermée** peut ne pas être affiché : le service Android d'FCM démarre sans projet par défaut. La réception fonctionne
application ouverte ou en arrière-plan récent. Deux issues, à trancher avec la stratégie de distribution :

1. **Une application par client** (marque blanche) construite avec le `google-services.json` du client : réception fiable partout ;
2. **Une application commune** : on garde l'initialisation à l'exécution, et le push reste un complément des SMS/WhatsApp.

Tant que ce choix n'est pas fait, SMS, WhatsApp et e-mail sont les canaux fiables ; le push est un bonus.
