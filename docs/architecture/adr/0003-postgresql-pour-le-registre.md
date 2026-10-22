# ADR-0003 — PostgreSQL pour le registre

- **Statut** : Accepted
- **Date** : 2026-08

## Contexte

Le registre stocke l'inventaire des devices : données **fortement relationnelles**, contraintes d'**intégrité** (unicité du numéro de série), volume modéré, lectures/écritures équilibrées avec **cohérence forte** attendue.

## Décision

Utiliser **PostgreSQL** comme base du registre, accédée via **Spring Data JPA** (requêtes dérivées).

## Justification

- Modèle relationnel adapté à des données structurées avec contraintes (unicité, futurs liens registre ↔ groupes/sites).
- Contrainte `unique` en base = garantie d'intégrité au niveau du stockage.
- Écosystème mature, gratuit, bien outillé.

## Conséquences

- ✅ Intégrité garantie par la base, pas seulement par le code applicatif.
- ✅ Chemin d'évolution direct vers **TimescaleDB** (extension de PostgreSQL) pour la télémétrie — mêmes outils, même langage (voir [ADR-0009](./0009-timescaledb-pour-la-telemetrie.md)).
- ⚠️ PostgreSQL classique n'est **pas** adapté à la télémétrie haute fréquence : ce profil de charge sera traité séparément (voir [ADR-0007](./0007-separer-registre-et-telemetrie.md)).
- ⚠️ `spring.jpa.hibernate.ddl-auto=update` est acceptable en dev **uniquement** ; en production, migrations versionnées via **Flyway** + `ddl-auto=validate`.
