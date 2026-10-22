# ADR-0010 — Reporter la sécurité JWT après le câblage front/back

- **Statut** : Accepted
- **Date** : 2026-08

## Contexte

Le backend n'a **aucune sécurité** aujourd'hui (tous les endpoints ouverts). La cible est Spring Security + **JWT**, avec autorisations par rôle (opérateur = lecture, administrateur = écriture/suppression).

La question n'est pas *si* mais *quand*. Introduire le JWT dès la Phase 0 protégerait tous les endpoints ; or le front actuel n'a **pas d'écran de login** et tomberait aussitôt en **401**, cassant l'application qui tourne aujourd'hui.

## Décision

**Reporter** l'introduction de la sécurité JWT à **après** le câblage front/back de bout en bout (une fois le front consolidé en Phase 1). Toute la Phase 0 reste **non-cassante** pour le front.

Quand la sécurité sera introduite, elle le sera en **coordonnant** front (écran de login, stockage et envoi du token, gestion du 401) et back (filtre JWT, rôles) dans la même itération.

## Conséquences

- ✅ Le front continue de fonctionner pendant tout le durcissement backend.
- ✅ La sécurité est ajoutée proprement, front et back ensemble, plutôt que dans la précipitation.
- ⚠️ Fenêtre pendant laquelle l'API reste ouverte : **acceptable** en développement local uniquement, jamais exposée publiquement sans auth.
- ⚠️ En attendant, sortir malgré tout les **secrets** (mot de passe DB) du dépôt dès la Phase 0 — c'est indépendant de l'auth applicative.
