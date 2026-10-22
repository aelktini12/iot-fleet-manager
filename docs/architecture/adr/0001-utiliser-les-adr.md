# ADR-0001 — Utiliser des ADR pour tracer les décisions

- **Statut** : Accepted
- **Date** : 2026-08

## Contexte

Le projet évolue d'un simple registre vers une plateforme IoT complète, par phases. De nombreux choix structurants (persistance, protocole de télémétrie, sécurité…) seront faits dans le temps, souvent avec des compromis. Sans trace, la raison d'un choix se perd, et on rejoue les mêmes débats.

## Décision

Consigner chaque décision d'architecture significative dans un **Architecture Decision Record** versionné avec le code, dans `docs/architecture/adr/`. Format léger inspiré de MADR (Contexte / Décision / Conséquences / Statut). Un ADR est immuable une fois accepté ; une révision crée un nouvel ADR qui supersède l'ancien.

## Conséquences

- ✅ La raison de chaque choix est traçable et revisitable.
- ✅ Onboarding facilité ; utile en entretien/portfolio pour démontrer le raisonnement.
- ⚠️ Discipline requise : penser à écrire l'ADR au moment de la décision.
