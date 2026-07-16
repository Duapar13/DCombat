# Dossier `libs/`

Même API Spigot que pour DFaction (26.1.2). Le dépôt Maven local (`~/.m2`)
est **partagé par machine, pas par projet** : si tu as déjà fait la procédure
pour DFaction sur cette machine, tu n'as rien à refaire ici, `mvn` trouvera
`org.spigotmc:spigot-api:26.1.2-R0.1-SNAPSHOT` tout seul.

Sinon, voir `DFaction/libs/README.md` pour la procédure complète
(BuildTools + extraction + `mvn install:install-file`), à faire une seule
fois par machine.
