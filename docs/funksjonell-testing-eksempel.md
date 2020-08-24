# Eksempel - Docker for funksjonelle og ikke tekniske
Her vil det bli presentert en fremgangsmåte for de funksjonelle og ikke tekniske for å få opp alt som trengs for å kjøre 
gjennom testene. Dette er gjort med fokus på at det skal være enkelt og at det ikke skal kreve en spesielt god PC (passer for deg med 8GB RAM eller mindre).
Denne fremgangsmåten vil sette opp den "minste verdikjeden". Den minste verdikjeden betyr at Docker setter bare opp de 
nødvendige applikasjonene som trengs for å kunne kjøre testene. 

*   Minste verdikjede for FPSAK: postgres, oracle, fpabakus, vtp og fpfrontend.
*   IKKE minste verdikjede for FPSAK: postgres, oracle, fpabakus, vtp, fpfrontend, fpformidling, ...


## Eksempel 1: Setter opp den minste verdikjeden (oracle, postgres, vtp, fpabakus, fpsak, fpfrontend)
Følg disse stegene for å sette opp (den minste) verdikjeden:

1) Åpne terminalvinduet i Intellij eller på maskinen.
2) Sørg for at du befinner deg i _fpsak-autotest_ prosjektet (For Mac `pwd`, for Windows `echo %cd%`). Hvis du ikke befinner
deg i _fpsak-autotest_-prosjektet, kjør følgende kommando:

    `cd VEIEN/TIL/fpsak-autotest`

3) Videre så går du til mappen _"lokal-utvikling"_. Skriv inn:

    `cd lokal-utvikling`
    
4) Kjøre scriptet "_testing-for-funksjonelle.sh_" for å starte opp applikasjonene:
    1) For Mac skriv følgende i terminalen: `./testing-for-funksjonelle.sh`
    2) For Windows skriv følgende i terminalen: `sh testing-for-funksjonelle.sh`

Etter en 1-2 minutter så vil alt være oppe og klar til bruk. Kjør dermed ønskede tester.

For å benytte deg av frontend bruker du denne linken: http://localhost:9000/fpsak/
