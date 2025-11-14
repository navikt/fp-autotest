Skripts for bumping av versjoner og endring av nais-filer i bulk.

Installer just (`brew install just`) og kjør `just` for en liste av kommandoer. Det ser f.eks. slik ut:

```
Available recipes:
    default
    bootstrap                    # Installerer avhengigheter
    logback_uten_team_logs mappe # Finner logback.xml-filer uten team-logs
    oppdater_repoliste           # Oppdaterer repos.txt
    hvilken_bom                  # Finner versjoner av fp-parent/fp-bom brukt
```

Du kjører oppdater_repoliste slik: `just oppdater_repoliste`.
Eventuelt kan du kjøre filene i denne mappen direkte, men da må du ordne avhengighetene selv.

Nyttig i kombinasjon med denne i en loop over alle repoer:

```
if ! git diff --quiet; then
    git stage --all
    git commit -m "EN COMMIT-MELDING HER"
    git push
fi
```
