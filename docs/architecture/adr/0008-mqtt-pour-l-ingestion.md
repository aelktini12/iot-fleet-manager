# ADR-0008 — MQTT pour l'ingestion de télémétrie

- **Statut** : Proposed (Phase 2)
- **Date** : 2026-08

## Contexte

Les devices sur le terrain doivent remonter leur télémétrie de façon fiable, sur des réseaux potentiellement instables et à fort volume. Le REST synchrone n'est pas adapté à ce profil (surcharge à grand débit, mal taillé pour des connexions intermittentes).

Options : REST synchrone, **MQTT**, AMQP, gRPC streaming.

## Décision

Utiliser **MQTT** comme protocole d'ingestion, avec un **broker Mosquitto** en entrée.

- Topics par device : `devices/{serialNumber}/telemetry`.
- Le service d'ingestion **souscrit** aux topics et traite les messages.
- **Kafka** pourra être introduit **en tampon** entre le broker et l'ingestion **si** le débit le justifie (absorption des pics, découplage, rejouabilité) — pas d'emblée.

## Justification

- MQTT est le **standard de l'IoT** : léger, publish/subscribe, conçu pour des réseaux contraints et intermittents (QoS, sessions persistantes, last will).
- Mosquitto : broker simple, éprouvé, idéal pour démarrer et pour un déploiement local/edge.

## Conséquences

- ✅ Ingestion adaptée à l'échelle et aux réseaux instables.
- ✅ Découplage fort device ↔ plateforme.
- ⚠️ Nouveau composant à exploiter (broker) : sécurité (TLS, authentification des devices), supervision.
- ⚠️ Introduire Kafka trop tôt serait de la sur-ingénierie ; décision conditionnée à une mesure de charge réelle.
