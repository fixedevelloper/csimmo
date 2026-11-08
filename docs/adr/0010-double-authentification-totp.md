# ADR 0010 — Double authentification par application (TOTP), sans SMS

Statut : accepté. Remplace la double authentification par code SMS (étape 11.2 / profil).

## Contexte

Le second facteur était un code envoyé par SMS. Le SMS coûte, dépend d'un opérateur et d'une passerelle configurée, et il est le maillon faible (échange de SIM). Un employé sans téléphone renseigné ne pouvait pas avoir la double authentification.

## Décisions

- **Second facteur = TOTP** (RFC 6238 : HMAC-SHA1, pas de 30 s, 6 chiffres, fenêtre ±1 pas), compatible Google Authenticator, Microsoft Authenticator, Authy, FreeOTP. Aucune dépendance ajoutée côté serveur (`securite.infra.Totp`).
- **Secret chiffré au repos** (AES-GCM, `ChiffrementDonnees`), jamais renvoyé après l'enrôlement ni journalisé. Un code déjà accepté (`totp_dernier_pas`) ne rejoue pas.
- **Défi de connexion** : après un mot de passe correct, le serveur ne délivre **aucune session** mais un défi opaque (seul son SHA-256 est stocké, 10 min, tentatives plafonnées, `sec_defi_2fa`). Le code seul n'ouvre jamais de session. Un code faux compte dans le verrouillage du compte.
- **Enrôlement forcé** : un compte qui doit avoir la double authentification (`ENCAISSEUR`, `GESTIONNAIRE_BIENS`, `COMPTABLE`, ou activée par choix) mais sans secret reçoit un défi d'enrôlement : il scanne le QR (web) ou saisit la clé (mobile), confirme avec un premier code, et la session s'ouvre ensuite.
- **Profil** : activer = mot de passe → clé/QR → premier code ; désactiver = mot de passe + code (interdit aux rôles restreints).
- **Perte du téléphone** : l'ADMIN réinitialise (`POST /api/v1/utilisateurs/{id}/reinitialiser-deux-facteurs`) : secret effacé, sessions révoquées, nouvel enrôlement à la connexion suivante.
- Le téléphone n'est plus exigé pour la double authentification ; un contact (téléphone **ou** e-mail) suffit.

## Inchangé

La connexion **par SMS sans mot de passe** des locataires et prestataires (`/api/v1/auth/otp/*`) reste : c'est leur seul moyen de connexion, pas un second facteur. ADMIN, GESTIONNAIRE et les employés restreints ne peuvent jamais se connecter par ce biais.

## Risques

- Pas de codes de secours : la réinitialisation par l'ADMIN est le seul recours. Si l'ADMIN lui-même perd son application, il faut un autre ADMIN (ou intervention sur la base) ; à documenter dans l'exploitation.
- L'horloge du serveur doit être juste (NTP) ; la fenêtre ±1 pas tolère une petite dérive.
