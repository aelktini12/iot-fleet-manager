# Architecture Decision Records (ADR)

Ce dossier consigne les **décisions d'architecture structurantes** du projet IoT Fleet Manager.
Chaque ADR est immuable une fois *Accepted* : si une décision est révisée, on crée un **nouvel** ADR qui **supersède** l'ancien (on ne réécrit pas l'historique).

Format : inspiré de [MADR](https://adr.github.io/madr/) — Contexte / Décision / Conséquences / Statut.

## Statuts possibles

- **Accepted** — décision prise et appliquée (ou en cours d'application).
- **Proposed** — décision retenue pour la cible, pas encore implémentée.
- **Superseded by ADR-XXXX** — remplacée par une décision ultérieure.

## Index

| # | Titre | Statut |
|---|-------|--------|
| [0001](./0001-utiliser-les-adr.md) | Utiliser des ADR pour tracer les décisions | Accepted |
| [0002](./0002-architecture-en-couches.md) | Architecture en couches (monolithe modulaire) | Accepted |
| [0003](./0003-postgresql-pour-le-registre.md) | PostgreSQL pour le registre | Accepted |
| [0004](./0004-dto-et-mapper.md) | Introduire des DTO et un mapper | Proposed |
| [0005](./0005-enums-pour-type-et-statut.md) | Enums pour `type` et `status` (+ machine à états) | Proposed |
| [0006](./0006-transactions-et-verrou-optimiste.md) | Transactions et verrou optimiste | Proposed |
| [0007](./0007-separer-registre-et-telemetrie.md) | Séparer registre et télémétrie | Proposed |
| [0008](./0008-mqtt-pour-l-ingestion.md) | MQTT pour l'ingestion de télémétrie | Proposed |
| [0009](./0009-timescaledb-pour-la-telemetrie.md) | TimescaleDB pour le stockage time-series | Proposed |
| [0010](./0010-reporter-la-securite-jwt.md) | Reporter la sécurité JWT après le câblage front/back | Accepted |
