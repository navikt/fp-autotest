package no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum MorsAktivitet {
    ARBEID,
    UTDANNING,
    KVALPROG("morsaktivitet.kvalprog"),
    INTROPROG("morsaktivitet.introprog"),
    TRENGER_HJELP("morsaktivitet.sykdom"),
    INNLAGT,
    ARBEID_OG_UTDANNING,
    SAMTIDIGUTTAK("morsaktivitet.samtidig"),
    UFØRE;

    private final String key;

    public String getKey() {
        return key;
    }

    MorsAktivitet() {
        this(null);
    }

    MorsAktivitet(String key) {
        this.key = key;
    }
}
