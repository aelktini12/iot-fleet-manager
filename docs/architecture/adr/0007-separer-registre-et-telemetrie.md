# ADR-0007 — Séparer registre et télémétrie

- **Statut** : Proposed (Phase 2)
- **Date** : 2026-08

## Contexte

L'ambition est une plateforme IoT qui ingère la télémétrie de milliers de devices (ex. mesure toutes les 10 s). Deux profils de charge s'opposent :

| | Registre | Télémétrie |
|---|----------|------------|
| Écritures | Peu fréquentes | **Massives** |
| Données | Relationnelles, mutables | Immuables, horodatées |
| Cohérence | Forte | Éventuelle acceptable |
| Requêtes | Par clé/critère | Par **intervalle de temps** |

Les faire cohabiter dans le même service et la même base ferait porter au registre une charge d'écriture pour laquelle il n'est pas conçu.

## Décision

**Séparer** deux capacités :

- **Registre de flotte** — service actuel, PostgreSQL, cohérence forte.
- **Ingestion de télémétrie** — service dédié, consommant un flux MQTT, stockant en time-series (voir [ADR-0008](./0008-mqtt-pour-l-ingestion.md), [ADR-0009](./0009-timescaledb-pour-la-telemetrie.md)).

Le service d'ingestion **lit** le registre (pour associer une mesure à un device connu) mais ne partage pas sa base.

## Décision différée (pas d'emblée)

Ce découpage n'est **pas** appliqué avant la Phase 2. Tant qu'il n'y a pas de charge de télémétrie réelle, il serait prématuré. On mesure d'abord où ça casse, puis on découpe.

## Conséquences

- ✅ Chaque service dimensionné pour son profil de charge.
- ✅ Découplage producteurs (devices) / consommateurs via le broker.
- ⚠️ Coût : cohérence éventuelle entre les deux systèmes, plus de composants à exploiter, complexité opérationnelle accrue.
