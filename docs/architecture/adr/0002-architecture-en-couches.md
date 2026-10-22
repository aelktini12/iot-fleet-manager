# ADR-0002 — Architecture en couches (monolithe modulaire)

- **Statut** : Accepted
- **Date** : 2026-08

## Contexte

Le domaine actuel comporte **une seule entité** (`Device`) et un CRUD REST. Il faut choisir un style d'architecture qui soit propre, testable, et qui ne surcharge pas le projet d'indirection inutile.

Options envisagées :
1. **Architecture en couches** (controller / service / repository / model).
2. **Architecture hexagonale** (ports & adapters) complète dès maintenant.
3. **Microservices** (un service par capacité).

## Décision

Adopter une **architecture en couches** classique, en **monolithe modulaire** :

```
DeviceController → DeviceService → DeviceRepository → Device (entité)
```

- Le controller n'expose et ne délègue (aucune logique métier).
- Le service porte les règles métier et orchestre.
- Le repository isole la persistance.
- Injection **par constructeur** partout, champs `final`.

## Justification

- Pour une entité unique, l'hexagonal complet ajouterait des ports/adaptateurs sans bénéfice réel → **sur-ingénierie**.
- Les microservices sur un domaine aussi petit produiraient un **monolithe distribué** (inconvénients des deux mondes).
- Le découpage pertinent à l'échelle n'est pas « un service par couche » mais **registre vs télémétrie** (voir [ADR-0007](./0007-separer-registre-et-telemetrie.md)).

## Conséquences

- ✅ Séparation des responsabilités nette, code testable sans conteneur Spring.
- ✅ Bascule vers l'hexagonal peu coûteuse le jour venu, car les couches sont déjà propres (introduire un `DeviceRepositoryPort` côté domaine, le repository Spring Data devenant l'adaptateur).
- ⚠️ La dépendance actuelle du service vers `JpaRepository` (abstraction du framework) n'est pas une vraie **inversion de dépendance** : on a l'injection de dépendances, pas encore l'inversion au sens strict. Assumé à ce stade.
