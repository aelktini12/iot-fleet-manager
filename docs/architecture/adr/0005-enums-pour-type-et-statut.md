# ADR-0005 — Enums pour `type` et `status` (+ machine à états)

- **Statut** : Proposed (Phase 0, puis Phase 4 pour la machine à états)
- **Date** : 2026-08

## Contexte

`type` et `status` sont aujourd'hui des `String` validées par **regex** (`^(SENSOR|ACTUATOR|GATEWAY)$`, valeurs libres pour le statut). Cela pose deux problèmes :

- Pas de typage fort : faute de frappe possible, pas d'autocomplétion, validation dispersée.
- Le principe **Open/Closed** est violé : ajouter un type ou une règle de transition impose de **modifier** le service et l'annotation `@Pattern`.
- Incohérence UX : le front crée les devices en `ONLINE`, or un `ONLINE` ne peut pas être supprimé → device immédiatement indésupprimable.

## Décision

1. **Phase 0** — Remplacer les `String` par des **enums** `DeviceType { SENSOR, ACTUATOR, GATEWAY }` et `DeviceStatus { ONLINE, OFFLINE, WARNING }`. Sérialisation JSON **identique** aux valeurs actuelles (contrat inchangé pour le front).
2. **Phase 4** — Modéliser le statut comme une **machine à états** : chaque état porte ses transitions autorisées et sa capacité de suppression (`status.allowsDeletion()` au lieu d'un `if` en dur). Traiter alors l'incohérence « créé ONLINE → indésupprimable » (création en `OFFLINE` ou transition explicite).
3. Le comportement spécifique par type sera géré par un **Strategy** (`DeviceTypeHandler` injecté par Spring), pour rester fermé à la modification : ajouter un type = ajouter une classe.

## Conséquences

- ✅ Typage fort, plus de regex, erreurs détectées à la compilation.
- ✅ Prérequis au pattern State (transitions) et au Strategy (comportement par type).
- ✅ Mécanisme d'extensibilité proche d'un système à plugins (cœur = contrat, implémentations branchées).
- ⚠️ Enum en base : stocker en **`STRING`** (`@Enumerated(EnumType.STRING)`), jamais en ordinal, pour la robustesse aux réordonnancements.
