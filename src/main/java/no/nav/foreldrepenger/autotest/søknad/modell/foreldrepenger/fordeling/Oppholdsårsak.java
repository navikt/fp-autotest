package no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum Oppholdsårsak {
    INGEN,
    UTTAK_MØDREKVOTE_ANNEN_FORELDER("periode.opphold.uttakkvoteannen"),
    UTTAK_FEDREKVOTE_ANNEN_FORELDER("periode.opphold.uttakkvoteannen"),
    UTTAK_FORELDREPENGER_ANNEN_FORELDER,
    UTTAK_FELLESP_ANNEN_FORELDER("periode.opphold.uttakfellesannen");

    private final String key;

    public String getKey() {
        return key;
    }

    Oppholdsårsak() {
        this(null);
    }

    Oppholdsårsak(String key) {
        this.key = key;
    }
}
