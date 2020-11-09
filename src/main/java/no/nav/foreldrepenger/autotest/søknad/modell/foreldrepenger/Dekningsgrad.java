package no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum Dekningsgrad {

    GRAD80(80),
    GRAD100(100);

    private final int kode;

    Dekningsgrad(int kode) {
        this.kode = kode;
    }
}
