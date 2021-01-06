package no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger;

public enum Dekningsgrad {

    GRAD80(80),
    GRAD100(100);

    private final int kode;

    Dekningsgrad(int kode) {
        this.kode = kode;
    }
}
