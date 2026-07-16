# DCombat

**Tag de combat PvP anti-combat-log, kills, morts, séries et classement.**
La brique de base du volet PvP de la suite : les autres plugins PvP
(DBounty, DRank, DScoreboard...) s'appuient sur ses statistiques via DAPI
plutôt que de les recalculer chacun de leur côté.

## Fonctionnalités

- **Tag de combat** : toute frappe PvP (corps-à-corps ou projectile tiré
  par un joueur) tague l'attaquant et la victime pour
  `combat.tag-duration-seconds` (15s par défaut, renouvelé à chaque coup).
- **Anti-combat-log** : se déconnecter alors qu'on est tagué tue le
  joueur (perte d'objets, comme une mort normale) si
  `combat.kill-on-combat-log` est activé (par défaut).
- **Suivi des kills/morts/séries** : chaque PvP concluant incrémente les
  kills du tueur et la série en cours (remise à zéro à sa prochaine
  mort), et les morts de la victime. La meilleure série est conservée.
- **Annonce de série** : diffuse un message serveur tous les
  `combat.streak-announce-every` kills consécutifs (5 par défaut, 0 pour
  désactiver).
- **`/kills [joueur]`** : kills, morts, ratio K/D, série actuelle et
  meilleure série.
- **`/pvptop [kills|deaths|streak]`** : classement des 10 meilleurs.
- Stockage YAML local par défaut, ou MySQL — même pattern que les autres
  plugins `D(nom)`. Le tag de combat lui-même reste en mémoire (état
  transitoire, comme les demandes `/tpa` de DCore).

## Intégration DAPI

DCombat **ne dépend pas** de DAPI (`softdepend: [DAPI]`, comme DCore et
DClass) : toutes ses fonctions marchent sans lui.

- **Fournit `CombatService`** : un futur plugin peut savoir si un joueur
  est en combat, ou consulter ses kills/morts/série, sans dépendre du
  code de DCombat. `DBounty` en aura besoin pour détecter un kill
  réclamant une prime, `DRank`/`DScoreboard` pour afficher des stats PvP.
- Ne consomme aucun service DAPI pour le moment.

### Autres idées d'interconnexion possibles

- `DCore` pourrait un jour consulter `CombatService.isTagged()` avant
  d'autoriser `/home`/`/warp`/`/spawn`/`/tpa` pendant un combat — pas
  encore branché : DCore a été livré avant DCombat, et le brancher plus
  tard demande de garder l'appel isolé pour ne pas casser le
  fonctionnement de DCore quand DCombat n'est pas installé (voir le
  README de DAPI sur les dépendances douces).

## Commandes

| Commande | Description |
|---|---|
| `/kills [joueur]` | Statistiques PvP d'un joueur (soi-même par défaut). |
| `/pvptop [kills\|deaths\|streak]` | Classement des 10 meilleurs (kills par défaut). |

## Permissions

| Permission | Défaut | Description |
|---|---|---|
| `dcombat.use` | `true` | Consulter ses statistiques PvP et le classement. |

## Configuration (`config.yml`)

```yaml
storage:
  type: local   # local ou mysql
  mysql:
    host: localhost
    port: 3306
    database: dcombat
    username: root
    password: ""

combat:
  tag-duration-seconds: 15
  kill-on-combat-log: true
  streak-announce-every: 5
```

## Compiler le projet

Dépend de l'API Spigot 26.1.2 et, en `provided`, de DAPI (facultatif à
l'exécution) :

```
cd ../DAPI && mvn install
cd ../DCombat && mvn clean package
```

Voir [`libs/README.md`](libs/README.md) pour la mise en place de l'API
Spigot.

## Roadmap / idées d'extension

- Intégration DCore décrite ci-dessus (bloquer les téléportations en combat).
- Zones "safe" (spawn, warps) où le tag ne s'applique pas, via `RegionService`
  (DGuard).
- Récompenses en argent par kill, via `EconomyService` (DEconomy).

## Licence

MIT — voir [`LICENSE`](LICENSE).
