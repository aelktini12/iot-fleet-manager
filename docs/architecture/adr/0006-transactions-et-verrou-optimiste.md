# ADR-0006 — Transactions et verrou optimiste

- **Statut** : Proposed (Phase 0)
- **Date** : 2026-08

## Contexte

Deux faiblesses de concurrence dans le service actuel :

1. **Aucun `@Transactional`.** Dans `updateDevice`, le `findById` et le `save` s'exécutent dans **deux transactions séparées**. Deux mises à jour concurrentes du même device peuvent s'écraser (*lost update*).
2. **Check-then-act sur le doublon.** `createDevice` vérifie `existsBySerialNumber` **puis** `save`. Entre les deux, deux requêtes concurrentes peuvent passer. La contrainte `unique` rattrape l'intégrité, mais l'exception `DataIntegrityViolationException` **n'est pas gérée** → le second appel renvoie un **500** au lieu d'un 409.

## Décision

1. Annoter les méthodes lecture-puis-écriture du service avec **`@Transactional`** (atomicité + contexte de persistance sur toute l'opération).
2. Ajouter un champ **`@Version`** sur l'entité `Device` → **verrouillage optimiste** : un conflit d'écriture concurrente lève `OptimisticLockException`.
3. Compléter `GlobalExceptionHandler` :
   - `DataIntegrityViolationException` → **409** (message métier propre).
   - `OptimisticLockException` / `ObjectOptimisticLockingFailureException` → **409** (conflit de version).

La vérification applicative `existsBySerialNumber` **reste** (message d'erreur explicite pour le cas courant), mais elle ne remplace pas la contrainte en base — les deux sont complémentaires.

## Conséquences

- ✅ Atomicité des opérations lecture-écriture ; plus de *lost update* silencieux.
- ✅ Doublon concurrent correctement traduit en 409.
- ✅ Adapté à un registre où plusieurs opérateurs agissent en parallèle.
- ⚠️ Le client doit savoir **rejouer** une opération en cas de 409/conflit de version (comportement à documenter côté front).
