# ADR-0009 — TimescaleDB pour le stockage time-series

- **Statut** : Proposed (Phase 2)
- **Date** : 2026-08

## Contexte

La télémétrie produit des données **horodatées, immuables, en fort volume**, interrogées principalement par intervalle de temps et par agrégats (moyenne/min/max sur une fenêtre). PostgreSQL classique gère mal ce profil à grande échelle (partitionnement manuel, index qui gonflent, agrégations coûteuses).

Options : PostgreSQL nu, **TimescaleDB**, InfluxDB.

## Décision

Utiliser **TimescaleDB** (extension time-series de PostgreSQL) pour stocker la télémétrie, dans une base **distincte** de celle du registre.

- Table des mesures en **hypertable** (partitionnement temporel automatique).
- Agrégats continus pour les vues du dashboard.
- Politiques de rétention/compression sur les données anciennes.

## Justification

- Reste sur **l'écosystème PostgreSQL** déjà maîtrisé (SQL, drivers, outillage) → courbe d'apprentissage réduite vs InfluxDB.
- Partitionnement temporel, compression et agrégats continus natifs.

## Conséquences

- ✅ Requêtes par fenêtre temporelle performantes ; montée en charge maîtrisée.
- ✅ Continuité technique avec [ADR-0003](./0003-postgresql-pour-le-registre.md).
- ⚠️ Deux bases à exploiter (registre + télémétrie) : sauvegardes, supervision distinctes.
- ⚠️ Si un besoin très spécifique émergeait (débits extrêmes), réévaluer InfluxDB via un nouvel ADR.
