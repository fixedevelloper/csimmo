# ADR 0009 — Équipe (employés) et périmètres d'accès

Statut : accepté ; phases A (`ENCAISSEUR`, étapes 12.1-12.2) et B (`GESTIONNAIRE_BIENS`, étape 12.3) livrées. Étape du plan : 12 (`docs/PLAN.md`).

## Contexte

Un client « propriétaire direct » veut confier l'encaissement (ou la gestion) d'une partie de son patrimoine à un employé, sans que celui-ci voie le reste ni les honoraires, reversements et paramètres.

Aujourd'hui :

- le seul rôle d'employé est `GESTIONNAIRE`, qui a **les mêmes droits que l'ADMIN** sur toutes les routes de gestion (`hasAnyRole('ADMIN','GESTIONNAIRE')`) et voit **tout** le patrimoine ; les six `ControleAcces*` répondent « oui » à ADMIN et GESTIONNAIRE ;
- seul `PROPRIETAIRE_TIERS` a un périmètre (ses immeubles), appliqué dans `PatrimoineService` (404 hors périmètre) ;
- chaque encaissement porte déjà l'`agentId`, la caisse du jour est calculée par agent, tout est dans `audit_log` ;
- le web n'a pas de page pour gérer le personnel (seul existe Paramètres > Utilisateurs, ajouté récemment, sans notion de périmètre).

## Décisions

### D1 — De nouveaux rôles, refus par défaut

Les profils d'employé sont de **nouveaux rôles** (`ENCAISSEUR` d'abord ; `GESTIONNAIRE_BIENS` et `COMPTABLE` ensuite), pas un champ « profil » sur `GESTIONNAIRE`.

- Les contrôleurs existants déclarent `hasAnyRole('ADMIN','GESTIONNAIRE')` : un nouveau rôle n'y figure pas, donc **tout est refusé par défaut** et chaque route est ouverte au cas par cas. Un champ « profil » ferait l'inverse (tout accordé, restrictions à ajouter) : un oubli deviendrait une fuite.
- `GESTIONNAIRE` ne change pas : il reste l'employé de confiance **à accès total** (comportement actuel, aucune migration de comptes). L'écran l'appelle « Gestionnaire (accès total) ».
- Conséquences : `Role` (Java), l'enum OpenAPI, les clients TS et Dart sont régénérés ; le mobile affiche l'espace agent pour `ENCAISSEUR`.

### D2 — Affectations

Table `sec_affectation (utilisateur_id, immeuble_id, cree_par, created_at)`, clé unique `(utilisateur_id, immeuble_id)`, dans le module `securite` (là où vivent les comptes). **Pas de clé étrangère vers `patrimoine`** (règle Modulith : un module ne référence pas les tables d'un autre) ; un immeuble archivé ou supprimé reste sans effet. Un employé peut être affecté à **plusieurs immeubles**, y compris de propriétaires différents.

### D3 — Un employé sans affectation ne voit rien

Pour les rôles restreints, aucune affectation = aucune donnée (listes vides, 404 sur les fiches). `ADMIN` et `GESTIONNAIRE` ne sont jamais restreints.

### D4 — Périmètre exprimé en immeubles, appliqué côté serveur

Interface dans `shared` (même schéma que `ActeurCourant`) :

```java
public interface PerimetreAcces {
    /** Vide : aucune restriction (ADMIN, GESTIONNAIRE). Sinon, les immeubles visibles (ensemble vide = rien). */
    Optional<Set<UUID>> immeublesVisibles();
}
```

Implémentée par `securite`. Chaque module en déduit sa visibilité, sans dépendre d'un autre module :

| Donnée | Visible si |
| --- | --- |
| immeuble | affecté |
| lot | son immeuble est affecté |
| bail, échéance | le lot du bail est dans un immeuble affecté (`bail` lit les lots via `patrimoine::api`) |
| paiement, quittance, reçu, compte locataire | le bail est visible |
| locataire | il a (ou a eu) un bail visible ; interface `shared.LocatairesDuPerimetre` **implémentée par `bail`** (évite le cycle `tiers → bail → tiers`) |
| caisse | uniquement les encaissements saisis par l'employé lui-même |

Hors périmètre : **404**, jamais 403 (on ne révèle pas l'existence). Le filtrage est dans les requêtes (pas après chargement) et dans les `ControleAcces*` pour les fichiers.

### D5 — Droits de `ENCAISSEUR` (phase A)

Autorisé : lire immeubles, lots, baux, échéances, locataires de son périmètre (loyers et montants dus inclus : indispensables pour encaisser) ; **créer un paiement** sur un bail visible (avec `Idempotency-Key`) ; lire ses propres paiements et sa caisse ; télécharger quittances et reçus de son périmètre ; utiliser l'espace agent mobile.

Refusé : contre-passation (demande à l'ADMIN avec motif), honoraires, taux de mandat, dépenses, reversements, relances, mises en demeure, import, paramètres, utilisateurs, sauvegardes, licence, tableau de bord.

### D6 — Sécurité

- Double authentification **obligatoire** pour tout compte `ENCAISSEUR`, `GESTIONNAIRE_BIENS` et `COMPTABLE` (la double authentification est aujourd'hui facultative).
- Désactiver un employé ou retirer ses affectations **révoque ses sessions** (refresh tokens) immédiatement.
- Créer, modifier, affecter et retirer un employé produit un événement d'audit avec avant/après.
- Le périmètre se lit à chaque requête (jamais copié dans le JWT) : un retrait d'affectation agit sans attendre l'expiration du jeton.

### D7 — Page « Équipe » (web)

Menu Administration > Équipe (ADMIN) : liste des employés (nom, contact, rôle, immeubles affectés, dernier encaissement, actif), création/édition, affectation multiple (cases à cocher sur les immeubles, recherche), fiche employé (encaissements récents, caisse du jour, journal d'audit). Elle remplace la création brute de Paramètres > Utilisateurs pour le personnel ; cet écran garde les autres rôles (locataires, prestataires…).

### D8 — Licence

Inchangée (palier par nombre de lots). Un plafond d'employés serait une décision commerciale séparée.

## Hors périmètre de cette décision

Éditeur de permissions libre, clôture de caisse avec écart et plafond de saisie, résumé quotidien au propriétaire : suites possibles (phase D), sans changer le modèle ci-dessus.

## Risques

- **Fuite par oubli de filtre** : le point le plus risqué. Parades : refus par défaut (D1), un test « interdit hors périmètre » par route ouverte à un rôle restreint, et un test de matrice qui échoue si une route ouverte à `ENCAISSEUR` n'est pas listée avec son filtre.
- Le **mobile** met en cache locataires et baux (`cache_terrain`) : il doit être vidé quand les affectations changent (déjà vidé à la déconnexion), et ne charger que le périmètre.
- Les **statistiques agrégées** (tableau de bord) ne sont pas ouvertes aux rôles restreints en phase A, pour éviter une fuite par totaux.

## Phase B — `GESTIONNAIRE_BIENS` (étape 12.3)

Même mécanisme (rôle restreint, affectations, refus par défaut, 404 hors périmètre, double authentification obligatoire). `Role.restreintAuxAffectations()` couvre les deux rôles ; `PerimetreAccesSecurite` n'a pas changé.

**Autorisé, dans son périmètre :**

- lire immeubles, lots (et leurs photos) ;
- **baux** : lister, voir, créer, modifier (brouillon), supprimer (brouillon), activer, résilier, renouveler, documents et aperçu PDF ; la création vérifie que le lot **et** le locataire sont dans son périmètre ;
- **locataires** : lister, voir, créer, modifier, archiver, garants, pièces d'identité (et leurs fichiers) ;
- **relances** : arriérés, historique, relance manuelle, mise en demeure (et PDF) ; lecture des paramètres de relance (pas leur modification) ;
- **signalements** : lister, voir, changer le statut (et photos) ;
- paiements, échéances, documents de paiement, caisse (les siens), comme l'encaisseur.

**Refusé :** créer ou modifier immeubles et lots (le palier de licence compte les lots), contre-passation, reversements, dépenses, propriétaires et prestataires, modèle de bail, paramètres, utilisateurs, import, conformité, sauvegardes, licence, tableau de bord, exécution des relances automatiques.

**Visibilité des locataires (décidée) :** un locataire est visible s'il a (ou a eu) un bail dans le périmètre **ou** si l'employé l'a créé lui-même (colonne `locataire.cree_par`, V24). Sans cela, il ne retrouverait pas la fiche qu'il vient de créer avant d'y rattacher un bail. Un locataire créé par un ADMIN reste invisible tant qu'aucun bail ne le rattache au périmètre. La règle vit dans `tiers.infra.VisibiliteLocataires` ; `LecteurTiers.locataireVisible` la met à disposition de `bail`.

**Point d'attention :** `BailService.charger` applique le périmètre à tous les chemins (lecture, modification, activation, résiliation, PDF) ; une nouvelle opération sur les baux doit passer par lui.
