# ADR-0004 — Introduire des DTO et un mapper

- **Statut** : Proposed (Phase 0)
- **Date** : 2026-08

## Contexte

Actuellement, l'entité JPA `Device` est **sérialisée directement** en JSON et acceptée telle quelle en entrée. Cette classe cumule quatre responsabilités : mapping persistance, modèle métier, contrat d'API, schéma de documentation (`@Schema`). Conséquences observées :

- Tout champ ajouté en base **fuit** automatiquement dans l'API.
- Impossible d'avoir des règles de validation **différentes** en création et en mise à jour (le PUT exige un `serialNumber` valide alors que le service l'ignore à l'update).
- L'`id` généré par la base est **exposé et acceptable en entrée** : un client peut poster un `id`.
- Le PUT actuel est en réalité un **PATCH** (mise à jour partielle : name/type/status seulement).

Le package `dto/` existe déjà mais est **vide**.

## Décision

Introduire des **DTO dédiés par opération** et un **mapper** :

- `DeviceCreateRequest` — champs et validations propres à la création.
- `DeviceUpdateRequest` — champs modifiables (dont `latitude`/`longitude`, aujourd'hui bloqués).
- `DeviceResponse` — vue exposée, **contrat stable** (mêmes champs qu'aujourd'hui pour ne pas casser le front : `id, serialNumber, name, type, status, lastCommunication, latitude, longitude`).
- Un mapper (manuel dans un premier temps, MapStruct possible ensuite).

## Conséquences

- ✅ Contrat d'API découplé du schéma de persistance ; plus de fuite de champs.
- ✅ Validations différenciées création/mise à jour ; `id` non acceptable en entrée.
- ✅ Le front Angular n'est **pas** impacté : `DeviceResponse` conserve la forme JSON actuelle.
- ⚠️ Un peu plus de code (DTO + mapping) — compensé par la clarté du contrat.
